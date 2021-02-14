package io.vertx.examples.webapiservice.persistence.impl;

import io.vertx.examples.webapiservice.models.Service;
import io.vertx.examples.webapiservice.persistence.ServicePersistence;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ServicePersistenceImpl implements ServicePersistence {

    private Map<String, Service> services;

    public ServicePersistenceImpl() {
        services = new HashMap<>();
    }

    @Override
    public List<Service> getFilteredServices(Predicate<Service> p) {
        return services.values().stream().filter(p).collect(Collectors.toList());
    }

    @Override
    public Optional<List<Service>> getServices() {
      return Optional.of(new ArrayList<>(services.values()));
    }

  @Override
  public Optional<Service> getService(String serviceId) {
    return Optional.ofNullable(services.get(serviceId));
  }

  @Override
    public Service addService(Service t) {
      services.put(t.getId(), t);
      return t;
    }

    @Override
    public boolean removeService(String serviceId) {
      Service t = services.remove(serviceId);
      if (t != null) return true;
      else return false;
    }

    @Override
    public boolean updateService(String serviceId, Service service) {
      Service t = services.replace(serviceId, service);
      if (t != null) return true;
      else return false;
    }
}
