package io.vertx.examples.webapiservice.services.impl;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.examples.webapiservice.models.Service;
import io.vertx.examples.webapiservice.persistence.ServicePersistence;
import io.vertx.examples.webapiservice.services.ServicesManagerService;
import io.vertx.examples.webapiservice.services.ServicesPoller;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.predicate.ResponsePredicate;

import java.util.List;
import java.util.Optional;

public class ServicesPollerImpl implements ServicesPoller {
  ServicePersistence persistence;
  WebClient client;
  public ServicesPollerImpl(ServicePersistence persistence, WebClient client) {
    this.persistence = persistence;
    this.client = client;
  }

  @Override
  public void poll() {
    Optional<List<Service>> servicesOpt = persistence.getServices();
    List<Service> services = servicesOpt.get();
    services.forEach(s -> makeCall(s));
  }

  private void makeCall(Service service) {
    System.out.println("calling " + service.getUrl());
//    HttpRequest req = client.get(service.getUrl()).expect(ResponsePredicate.SC_OK);
//    client.get(service.getUrl()).send(ar -> {
//      if (ar.succeeded()) {
//        HttpResponse<Buffer> response = ar.result();
//        System.out.println("Got HTTP response with status " + response.statusCode());
//      } else {
//        ar.cause().printStackTrace();
//      }
//    });
//    client.requestAbs(HttpMethod.GET, service.getUrl(), res -> {
//      System.out.println(res.toString());
//    });
  }
}
