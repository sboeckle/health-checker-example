package io.vertxwebapiservice.services.impl;

import io.vertx.core.VertxException;
import io.vertx.ext.web.client.WebClient;
import io.vertxwebapiservice.models.Service;
import io.vertxwebapiservice.persistence.ServicePersistence;
import io.vertxwebapiservice.services.ServicesPoller;

import java.util.Date;
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
    services.forEach(s -> makeServiceCheck(s));
  }

  private void setServiceStatus(Service service, boolean isOk) {
    persistence.updateStatus(service.getId(), service, isOk, new Date().toString());
  }

  private void makeServiceCheck(Service service) {
    try{
      client.getAbs(service.getUrl())
          .send()
          .onSuccess(res -> {
            System.out.println("Received response with status code" + res.statusCode());
            setServiceStatus(service, true);
          })
          .onFailure(err -> {
            System.out.println("Something went wrong " + err.getMessage());
            setServiceStatus(service, false);
          });
    } catch(VertxException ve) {
      System.out.println("Something went wrong " + ve.getMessage());
      setServiceStatus(service, false);
    }
  }
}
