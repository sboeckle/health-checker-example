package io.vertxwebapiservice.services;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertxwebapiservice.models.Service;
import io.vertxwebapiservice.persistence.ServicePersistence;
import io.vertxwebapiservice.services.impl.ServicesManagerServiceImpl;
import io.vertx.ext.web.api.service.ServiceRequest;
import io.vertx.ext.web.api.service.ServiceResponse;
import io.vertx.ext.web.api.service.WebApiServiceGen;

import java.util.List;

/**
 * This interface describes the Transactions Manager Service. Note that all methods has same name of corresponding operation id
 *
 */
@WebApiServiceGen
public interface ServicesManagerService {

  static ServicesManagerService create(ServicePersistence persistence) {
    return new ServicesManagerServiceImpl(persistence);
  }

  void getServiceList(ServiceRequest request, Handler<AsyncResult<ServiceResponse>> resultHandler);

  void getServiceListFiltered(
      String userId,
      ServiceRequest request, Handler<AsyncResult<ServiceResponse>> resultHandler);

  void getService(
    String serviceId,
    ServiceRequest request, Handler<AsyncResult<ServiceResponse>> resultHandler);

  void createService(
      Service body,
      ServiceRequest request, Handler<AsyncResult<ServiceResponse>> resultHandler);

  void updateService(
    String serviceId,
    Service body,
    ServiceRequest request, Handler<AsyncResult<ServiceResponse>> resultHandler);

  void deleteService(
    String serviceId,
    ServiceRequest request, Handler<AsyncResult<ServiceResponse>> resultHandler);

}
