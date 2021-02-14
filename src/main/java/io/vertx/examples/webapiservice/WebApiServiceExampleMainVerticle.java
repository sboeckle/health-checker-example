package io.vertx.examples.webapiservice;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.examples.webapiservice.persistence.ServicePersistence;
import io.vertx.examples.webapiservice.services.ServicesManagerService;
import io.vertx.examples.webapiservice.services.ServicesPoller;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.openapi.RouterBuilder;
import io.vertx.serviceproxy.ServiceBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebApiServiceExampleMainVerticle extends AbstractVerticle {

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
    WebClient client = WebClient.create(vertx);
    ServicesPoller servicesPoller = ServicesPoller.create(persistence, client);
    vertx.setPeriodic(3000, id -> servicesPoller.poll());
    consumer = serviceBinder
        .setAddress("services_poller.myapp")
        .register(ServicesPoller.class, servicesPoller);
  }

  /**
   * This method constructs the router factory, mounts services and handlers and starts the http server with built router
   * @return
   */
  private Future<Void> startHttpServer() {
    return RouterBuilder.create(this.vertx, "openapi.json")
      .onFailure(Throwable::printStackTrace) // In case the contract loading failed print the stacktrace
      .compose(routerBuilder -> {
        // Mount services on event bus based on extensions
        routerBuilder.mountServicesFromExtensions();

        // Generate the router
        Router router = routerBuilder.createRouter();
        router.errorHandler(400, ctx -> {
          LOG.debug("Bad Request", ctx.failure());
        });
        server = vertx.createHttpServer(new HttpServerOptions().setPort(8080).setHost("localhost")).requestHandler(router);
        return server.listen().mapEmpty();
      });
  }

  @Override
  public void start(Promise<Void> promise) {
    serviceBinder = new ServiceBinder(vertx);
    ServicePersistence persistence = ServicePersistence.create();
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
    vertx.deployVerticle(new WebApiServiceExampleMainVerticle());
  }

}