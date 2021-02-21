package io.vertxwebapiservice.persistence;

import io.vertx.core.Vertx;
import io.vertxwebapiservice.models.Service;
import io.vertxwebapiservice.persistence.impl.ServicePersistenceImpl;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * This interface represents a persistence layer of your application
 *
 * @author slinkydeveloper
 */
public interface ServicePersistence {

  /**
   * Factory method to instantiate ServicePersistence
   *
   * @return
   */
  static ServicePersistence create(Vertx vertx) {
    return new ServicePersistenceImpl(vertx);
  }

  List<Service> getFilteredServices(Predicate<Service> p);

  Optional<List<Service>> getServices();

  Optional<Service> getService(String serviceId);

  Service addService(Service t);

  boolean removeService(String serviceId);

  boolean updateService(String serviceId, Service service);
}
