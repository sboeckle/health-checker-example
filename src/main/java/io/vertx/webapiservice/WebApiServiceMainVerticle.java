package io.vertxwebapiservice;

import io.vertx.core.*;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.http.*;
import io.vertx.core.json.JsonObject;
import io.vertxwebapiservice.persistence.ServicePersistence;
import io.vertxwebapiservice.services.ServicesManagerService;
import io.vertxwebapiservice.services.ServicesPoller;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.openapi.RouterBuilder;
import io.vertx.serviceproxy.ServiceBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class WebApiServiceMainVerticle extends AbstractVerticle {

  HttpServer server;
  ServiceBinder serviceBinder;

  MessageConsumer<JsonObject> consumer;
  Logger LOG = LoggerFactory.getLogger(this.getClass());

  private void startServicesManager(ServicePersistence persistence) {
    // Create service and mount to event bus
    ServicesManagerService servicesManagerService = ServicesManagerService.create(persistence);
    consumer = serviceBinder
        .setAddress("services_manager.myapp")
        .register(ServicesManagerService.class, servicesManagerService);
  }

  private void startServicesPoller(ServicePersistence persistence) {
    HttpClient client = vertx.createHttpClient();
    ServicesPoller servicesPoller = ServicesPoller.create(persistence, client);
    vertx.setPeriodic(3000, id -> servicesPoller.poll());
    consumer = serviceBinder
        .setAddress("services_poller.myapp")
        .register(ServicesPoller.class, servicesPoller);
  }

  /**
   * Configure router with CORS support for REST APIs. Allows * for now
   */
  private void configureCors(Router router) {
    Set<String> allowedHeaders = new HashSet<>();
    allowedHeaders.add("x-requested-with");
    allowedHeaders.add("Access-Control-Allow-Origin");
    allowedHeaders.add("origin");
    allowedHeaders.add("Content-Type");
    allowedHeaders.add("accept");

    Set<HttpMethod> allowedMethods = new HashSet<>();
    allowedMethods.add(HttpMethod.GET);
    allowedMethods.add(HttpMethod.POST);
    allowedMethods.add(HttpMethod.OPTIONS);
    allowedMethods.add(HttpMethod.DELETE);
    allowedMethods.add(HttpMethod.PATCH);
    allowedMethods.add(HttpMethod.PUT);
    router.route().handler(CorsHandler.create("*").allowedHeaders(allowedHeaders).allowedMethods(allowedMethods));
  }

  /**
   * This method constructs the router factory, mounts services and handlers and starts the http server with built router
   */
  private Future<Void> startHttpServer() {
    Router mainRouter = Router.router(vertx);
    configureCors(mainRouter);
    return RouterBuilder.create(this.vertx, "openapi.json")
      .onFailure(Throwable::printStackTrace) // In case the contract loading failed print the stacktrace
      .compose(routerBuilder -> {
        routerBuilder.mountServicesFromExtensions();
        Router apiRouter = routerBuilder.createRouter();
        apiRouter.errorHandler(400, ctx -> {
          LOG.debug("Bad Request", ctx.failure());
        });
        mainRouter.mountSubRouter("/", apiRouter);
        server = vertx.createHttpServer(new HttpServerOptions().setPort(8080).setHost("localhost")).requestHandler(mainRouter);
        return server.listen().mapEmpty();
      });
  }

  @Override
  public void start(Promise<Void> promise) {
    serviceBinder = new ServiceBinder(vertx);
    ServicePersistence persistence = ServicePersistence.create(vertx);
    startServicesManager(persistence);
    startServicesPoller(persistence);
    startHttpServer().onComplete(promise);
  }

  /**
   * This method closes the http server and unregister all services loaded to Event Bus
   */
  @Override
  public void stop(){
    this.server.close();
    consumer.unregister();
  }

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new WebApiServiceMainVerticle());
  }

}
