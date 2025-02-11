# Boot Data Keyset Pagination with Blaze Persistence

## Overview

This project extends the concept of keyset pagination with an additional library, Blaze Persistence, providing simpler syntax for complex dynamic queries.

## Features

* Efficient pagination for large datasets by fetching only necessary data
* Dynamic query capabilities
* Advanced filtering options

### Format code

```shell
./mvnw spotless:apply
```

### Run tests

```shell
./mvnw clean verify
```

### Run locally

```shell
docker-compose -f docker/docker-compose.yml up -d
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

### Using Testcontainers at Development Time
You can run `TestApplication.java` from your IDE directly.
You can also run the application using Maven as follows:

```shell
./mvnw spring-boot:test-run
```


### Useful Links
* Swagger UI: http://localhost:8080/swagger-ui.html
* Actuator Endpoint: http://localhost:8080/actuator
