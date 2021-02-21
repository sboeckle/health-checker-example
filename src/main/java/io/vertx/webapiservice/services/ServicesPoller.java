package io.vertxwebapiservice.services;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.http.HttpClient;
import io.vertxwebapiservice.persistence.ServicePersistence;
import io.vertxwebapiservice.services.impl.ServicesManagerServiceImpl;
import io.vertxwebapiservice.services.impl.ServicesPollerImpl;

@ProxyGen
public interface ServicesPoller {

  static ServicesPoller create(ServicePersistence persistence, HttpClient client){
    return new ServicesPollerImpl(persistence, client);
  }

  void poll();
}
