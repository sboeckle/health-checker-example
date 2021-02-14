package io.vertx.examples.webapiservice.services;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.examples.webapiservice.models.Service;
import io.vertx.examples.webapiservice.persistence.ServicePersistence;
import io.vertx.examples.webapiservice.services.impl.ServicesManagerServiceImpl;
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
    String transactionId,
    Service body,
    ServiceRequest request, Handler<AsyncResult<ServiceResponse>> resultHandler);

  void deleteService(
    String transactionId,
    ServiceRequest request, Handler<AsyncResult<ServiceResponse>> resultHandler);

}
