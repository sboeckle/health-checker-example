package io.vertx.examples.webapiservice.models;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.codegen.annotations.Fluent;
import io.vertx.core.json.JsonObject;

@DataObject(generateConverter = true, publicConverter = false)
public class Service {

  private String id;
  private String url;
  private String userId;
  private boolean isOk;

  public Service(
    String id,
    String url,
    String userId,
    boolean isOk
  ) {
    this.id = id;
    this.url = url;
    this.userId = userId;
    this.isOk = isOk;
  }

  public Service(JsonObject json) {
    ServiceConverter.fromJson(json, this);
  }

  public Service(Service other) {
    this.id = other.getId();
    this.url = other.getUrl();
    this.userId = other.getUserId();
    this.isOk = other.getIsOk();
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

  @Fluent public Service setIsOk(boolean isOk){
    this.isOk = isOk;
    return this;
  }
  public boolean getIsOk() {
    return this.isOk;
  }
}
