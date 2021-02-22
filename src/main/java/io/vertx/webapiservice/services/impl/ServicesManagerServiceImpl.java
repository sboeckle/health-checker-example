package io.vertxwebapiservice.services.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertxwebapiservice.models.Service;
import io.vertxwebapiservice.persistence.ServicePersistence;
import io.vertxwebapiservice.services.ServicesManagerService;
import io.vertx.ext.web.api.service.ServiceRequest;
import io.vertx.ext.web.api.service.ServiceResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ServicesManagerServiceImpl implements ServicesManagerService {

  private final ServicePersistence persistence;

  public ServicesManagerServiceImpl(ServicePersistence persistence) {
    this.persistence = persistence;
  }

  @Override
  public void getServiceList(ServiceRequest request, Handler<AsyncResult<ServiceResponse>> resultHandler) {
    Optional<List<Service>> optResults = persistence.getServices();
    resultHandler.handle(Future.succeededFuture(
        ServiceResponse.completedWithJson(
            new JsonArray(optResults.get().stream().map(Service::toJson).collect(Collectors.toList()))
        )
    ));
  }

  @Override
  public void getServiceListFiltered(
    String userId,
    ServiceRequest request, Handler<AsyncResult<ServiceResponse>> resultHandler) {
    List<Service> results = persistence.getFilteredServices(this.constructFilterPredicate(userId));
    resultHandler.handle(Future.succeededFuture(
      ServiceResponse.completedWithJson(
        new JsonArray(results.stream().map(Service::toJson).collect(Collectors.toList()))
      )
    ));
  }

  @Override
  public void createService(
    Service body,
    ServiceRequest request, Handler<AsyncResult<ServiceResponse>> resultHandler) {
    try{
      Service serviceAdded = persistence.addService(body);
      resultHandler.handle(Future.succeededFuture(ServiceResponse.completedWithJson(serviceAdded.toJson())));
    } catch (IllegalArgumentException iae){
      resultHandler.handle(Future.succeededFuture(new ServiceResponse().setStatusCode(400).setStatusMessage(iae.getMessage())));
    }
  }

  @Override
  public void getService(
    String serviceId,
    ServiceRequest request, Handler<AsyncResult<ServiceResponse>> resultHandler) {
    Optional<Service> t = persistence.getService(serviceId);
    if (t.isPresent())
      resultHandler.handle(Future.succeededFuture(ServiceResponse.completedWithJson(t.get().toJson())));
    else
      resultHandler.handle(Future.succeededFuture(new ServiceResponse().setStatusCode(404).setStatusMessage("Not Found")));
  }

  @Override
  public void updateService(
    String serviceId,
    Service body,
    ServiceRequest request, Handler<AsyncResult<ServiceResponse>> resultHandler) {
    if (persistence.updateService(serviceId, body, true))
      resultHandler.handle(Future.succeededFuture(ServiceResponse.completedWithJson(body.toJson())));
    else
      resultHandler.handle(Future.succeededFuture(new ServiceResponse().setStatusCode(404).setStatusMessage("Not Found")));
  }

  @Override
  public void deleteService(
    String serviceId,
    ServiceRequest request, Handler<AsyncResult<ServiceResponse>> resultHandler) {
    if (persistence.removeService(serviceId))
      resultHandler.handle(Future.succeededFuture(new ServiceResponse().setStatusCode(204).setStatusMessage("No Content")));
    else
      resultHandler.handle(Future.succeededFuture(new ServiceResponse().setStatusCode(404).setStatusMessage("Not Found")));
  }

  private Predicate<Service> constructFilterPredicate(String userId) {
    //TBD  need multiple operators? if not simplify
    List<Predicate<Service>> predicates = new ArrayList<>();
    if (userId != null) {
      predicates.add(service -> userId.equals(service.getUserId()));
    }
    // Elegant predicates combination
    return predicates.stream().reduce(service -> true, Predicate::and);
  }

}
