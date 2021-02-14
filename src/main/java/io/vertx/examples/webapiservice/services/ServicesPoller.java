package io.vertx.examples.webapiservice.services;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.http.HttpClient;
import io.vertx.examples.webapiservice.persistence.ServicePersistence;
import io.vertx.examples.webapiservice.services.impl.ServicesManagerServiceImpl;
import io.vertx.examples.webapiservice.services.impl.ServicesPollerImpl;

@ProxyGen
public interface ServicesPoller {

  static ServicesPoller create(ServicePersistence persistence, HttpClient client){
    return new ServicesPollerImpl(persistence, client);
  }

  void poll();
}
