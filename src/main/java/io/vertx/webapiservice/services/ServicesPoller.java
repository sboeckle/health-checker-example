package io.vertxwebapiservice.services;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.ext.web.client.WebClient;
import io.vertxwebapiservice.persistence.ServicePersistence;
import io.vertxwebapiservice.services.impl.ServicesPollerImpl;

@ProxyGen
public interface ServicesPoller {

  static ServicesPoller create(ServicePersistence persistence, WebClient client){
    return new ServicesPollerImpl(persistence, client);
  }

  void poll();
}
