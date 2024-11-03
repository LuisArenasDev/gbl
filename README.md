# Device Service

## Requirement
* Java 17 or higher
* MySQL
* Maven
* Docker


## Build project
- Run `mvn clean package -Dmaven.test.skip=true`

## Setup and run using Docker
* Create docker image
```
  docker build -t device-service .
```

* Run docker-compose file
```
docker-compose up --build
```

## Test application using Swagger
you can test the application using swagger UI:
http://localhost:8080/swagger-ui/index.html 

## Run unit and integration tests
Run `mvn clean test`
