# Banking Demo Basic

Covers the basic functionality for handling accounts and processing transactions.

## Pre-requisites

- Java 8
- Spring boot 2.2
- Lombok - Install lombok IDE plugin, and enable annotation processing in your IDE
- Groovy Spock for unit testing
- RestAssured for integration test requests

## Building and running the app
Building the app:
```bash
./gradlew clean build
```

Running the app:
```
./gradlew bootRun
```

Running all tests (unit and integration):
```
./gradlew test
```

Running only integration tests:
```
./gradlew integTest
```

## App Usage
The app can be used through Swagger UI at `http://localhost:8080/swagger-ui.html`.

Swagger UI displays detailed instructions for how to the app.

## Running SonarQube locally
1) Start Docker client (Docker for Mac, Docker for Windows or Docker machine)
2) Run script to start SonarQube container: 
`sh ./scripts/run-sonarqube.sh`
3) After building, run sonarqube gradle task to publish reports:
`./gradlew sonarqube`
4) Navigate to `localhost:9000` to view the reports
5) Login with default credentials `username:admin|password:admin`