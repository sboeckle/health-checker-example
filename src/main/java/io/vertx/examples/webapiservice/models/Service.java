package io.vertx.examples.webapiservice.models;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.codegen.annotations.Fluent;
import io.vertx.core.json.JsonObject;

@DataObject(generateConverter = true, publicConverter = false)
public class Service {

  private String name;
  private String id;
  private String url;
  private String userId;
  private String status;
  private String createdAt;
  private String updatedAt;

  public Service(
    String id,
    String url,
    String userId,
    String status
  ) {
    this.id = id;
    this.url = url;
    this.userId = userId;
    this.status = status;
  }

  public Service(JsonObject json) {
    ServiceConverter.fromJson(json, this);
  }

  public Service(Service other) {
    this.id = other.getId();
    this.name = other.name;
    this.url = other.getUrl();
    this.userId = other.getUserId();
    this.status = other.getStatus();
    this.createdAt = other.getCreatedAt();
    this.updatedAt = other.getUpdatedAt();
  }

  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    ServiceConverter.toJson(this, json);
    return json;
  }

  @Fluent public Service setId(String id){
    this.id = id;
    return this;
  }
  public String getId() {
    return this.id;
  }

  @Fluent public Service setUrl(String url){
    this.url = url;
    return this;
  }
  public String getUrl() {
    return this.url;
  }

  @Fluent public Service setUserId(String userId){
    this.userId = userId;
    return this;
  }
  public String getUserId() {
    return this.userId;
  }

  @Fluent
  public Service setName(String name) {
    this.name = name;
    return this;
  }
  public String getName() {
    return name;
  }

  @Fluent
  public Service setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
    return this;
  }
  public String getCreatedAt() {return createdAt;}

  @Fluent
  public Service setUpdatedAt(String updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }
  public String getUpdatedAt() {return updatedAt;}

  @Fluent
  public Service setStatus(String status) {
    this.status = status;
    return this;
  }
  public String getStatus() {return status;}




}
