## Health Checker Example

Simple service for periodically polling of urls.
A CRUD REST API is provided for handling multiple services.
A React app os provided as simple UI.

## Service

- The REST Service is implemented with vert.x and built on top of the vert.x web api service example [vertx-web-api-service](https://vertx.io/docs/vertx-web-api-service/java/)
- The `WebApiServiceMainVerticle` is the main class, it creates and handles
  - `ServiceManagerService` - a router with routes based on the `src/main/resources/openapi.json` specification using `OpenAPI3RouterFactory`
    - Handles the Services that should be polled. Does this with a injected persistance (currently a Map that is saved to file on writes) that can be exchanged later
    - saves JSON in `services.serialized` file
  - `ServicePoller` - periodically polling defined services
    - Queries the Services from the ServiceManagerService, polls their urls updates the services accordingly
    - currently, it polls every 3000ms

Notes

- Poller writes status of services `status` ('OK','FAIL') after polling as well as updates the `lastCheckedAt` field

## UI

A simple react UI was generated for being able to work with the REST Service.
It offers CRUD operations on the services API via a simple table (MaterialTable)

## Run it

Prerequisites:

- maven
- java
- npm & node

**Service**
```bash
mvn clean package
java -jar target/health-checker-service-4.0.2-fat.jar
```
- service is started and API is served on http://localhost:8080/api
  - check CLI's logs for success (-> `Succeeded in deploying verticle`)
  - when failing services are saved then the log can be spammed with excetions for that case delete content of `services.serialized` file before starting again
- check `src/main/resources/openapi.json` for available routes

**UI**

```bash
cd ./health-checker-app
npm i
npm start
```
-> browser should start on http://localhost:3000