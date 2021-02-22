package io.vertxwebapiservice.persistence.impl;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertxwebapiservice.models.Service;
import io.vertxwebapiservice.persistence.ServicePersistence;

import java.nio.file.FileAlreadyExistsException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.json.JSONObject;

public class ServicePersistenceImpl implements ServicePersistence {

    private Vertx vertx;
    private final String fileName = "services.serialized";
    private Map <String, Service> services;

    public ServicePersistenceImpl(Vertx vertx) {
        this.vertx = vertx;
        services = new HashMap<String, Service>();
        initPersistence();
        readFromFile();
    }

    private void initPersistence() {
      try {
        Files.createFile(Path.of(fileName));
      } catch (FileAlreadyExistsException e) {
        System.out.println("Persistence file already exists");
      } catch (IOException ioe) {
        System.err.println("Error reading from persistence file");
        ioe.printStackTrace();
      }
    }

    private void readFromFile() {
        try {
          String servicesAsString = Files.readString(Path.of(fileName));
          if (servicesAsString.equals("")) {
            servicesAsString = "{}";
          }
          JSONObject jsonO = new JSONObject(servicesAsString);
          jsonO.keySet().forEach(k -> {
            JSONObject o = (JSONObject) jsonO.get(k);
            Service s = new Service(new JsonObject(o.toString()));
            services.put(k, s);
          });
        } catch (IOException e) {
          System.err.println("Error reading from persistence file");
          e.printStackTrace();
        }
    }

    private void saveToFile() {
      // use JSONObject for simple parsing of Map -> String
      JSONObject jsonO = new JSONObject(services);
      JsonObject json = new JsonObject(jsonO.toString());
      vertx.fileSystem().writeFile(
          fileName,
          Buffer.buffer(json.toString()),
          result -> {
            if (result.succeeded()) {
              System.out.println("success: write persistence file");
            } else {
              System.err.println("failed: write persistence file");
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
      t.setCreatedAt(now.toString())
          .setUpdatedAt(now.toString());
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
    public boolean updateService(String serviceId, Service service, boolean updateMeta) {
      Service current = services.get(serviceId);
      if (current == null) { return false; }
      service.setCreatedAt(current.getCreatedAt());
      service.setUpdatedAt(new Date().toString());
      if(!current.getUrl().equals(service.getUrl())) {
        service.setLastCheckedAt(null);
        service.setStatus(null);
      }
      services.replace(serviceId, service);
      saveToFile();
      return true;
    }

  @Override
    public void updateStatus(String serviceId, Service service, boolean isOk, String checkedAt) {
      Service current = services.get(serviceId);
      if (current == null) {
        System.err.println("failed: trying to update status of not found service");
        return;
      }
      service.setStatus(isOk ? "OK" : "FAIL");
      service.setLastCheckedAt(checkedAt);
      services.replace(serviceId, service);
      saveToFile();
    }
}
