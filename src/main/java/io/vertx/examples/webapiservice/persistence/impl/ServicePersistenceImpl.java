package io.vertx.examples.webapiservice.persistence.impl;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.examples.webapiservice.models.Service;
import io.vertx.examples.webapiservice.persistence.ServicePersistence;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.json.JSONObject;

public class ServicePersistenceImpl implements ServicePersistence {

    private Vertx vertx;
    private final String fileName = "services.serialized";
    private SortedMap <String, Service> services;

    public ServicePersistenceImpl(Vertx vertx) {
        this.vertx = vertx;
        services = new TreeMap<String, Service>();
        initPersistance();
    }

    private void initPersistance() {
        vertx.fileSystem().readFile(fileName, ar -> {
          if (ar.failed()) {
            System.out.println(ar.cause().getMessage());
          } else {
            System.out.println("WORKED SO FAR");
          }
        });
    }

    private void saveToFile() {
      JSONObject json = new JSONObject(services);
      System.out.println(json.toString());
      vertx.fileSystem().writeFile(
          fileName,
          Buffer.buffer(json.toString()),
          result -> {
            if (result.succeeded()) {
              System.out.println("wrote it" + result.toString());
            } else {
              System.err.println("did" +
                  " not write it");
            }
          });
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
        Date now = new Date();
        t.setCreatedAt(now.toString());
        t.setUpdatedAt(now.toString());
        services.put(t.getId(), t);
        saveToFile();
        return t;
      }

    @Override
    public boolean removeService(String serviceId) {
      Service t = services.remove(serviceId);
      if (t != null) {
        saveToFile();
        return true;
      }
      else return false;
    }

    @Override
    public boolean updateService(String serviceId, Service service) {
      service.setUpdatedAt(new Date().toString());
      Service t = services.replace(serviceId, service);
      if (t != null) {
        saveToFile();
        return true;
      }
      else return false;
    }
}
