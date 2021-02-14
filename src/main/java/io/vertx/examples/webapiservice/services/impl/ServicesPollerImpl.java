package io.vertx.examples.webapiservice.services.impl;

import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpMethod;
import io.vertx.examples.webapiservice.models.Service;
import io.vertx.examples.webapiservice.persistence.ServicePersistence;
import io.vertx.examples.webapiservice.services.ServicesPoller;

import java.util.List;
import java.util.Optional;

public class ServicesPollerImpl implements ServicesPoller {
  ServicePersistence persistence;
  HttpClient client;
  public ServicesPollerImpl(ServicePersistence persistence, HttpClient client) {
    this.persistence = persistence;
    this.client = client;
  }

  @Override
  public void poll() {
    Optional<List<Service>> servicesOpt = persistence.getServices();
    List<Service> services = servicesOpt.get();
    services.forEach(s -> makeServiceCheck(s));
  }

  private void setServiceStatus(Service service, boolean isOk) {
    service.setIsOk(isOk);
    persistence.updateService(service.getId(), service);
  }

  private void makeServiceCheck(Service service) {
    System.out.println("calling " + service.getUrl());
    client.request(HttpMethod.GET,80, service.getUrl(), "/", ar1 -> {
      if (ar1.succeeded()) {
        HttpClientRequest request = ar1.result();
        request.send(ar2 -> {
          if (ar2.succeeded()) {
            HttpClientResponse response = ar2.result();
            System.out.println("Received response with status code " + response.statusCode());
            setServiceStatus(service, true);
          } else {
            ar2.cause().printStackTrace();
            setServiceStatus(service, false);
          }
        });
      } else {
        ar1.cause().printStackTrace();
        setServiceStatus(service, false);
      }
    });
  }
}
