# Banking Demo Basic

Covers the basic functionality for handling accounts and processing transactions, secured using JWT.

## Pre-requisites / Technologies used

- Java 8
- Spring boot 2.2
- Gradle 5
- H2 in-memory database
- Lombok, to enable you need to install lombok IDE plugin, and enable annotation processing in your IDE settings
- Groovy Spock for BDD-based unit testing
- RestAssured for BDD-based integration testing REST services
- SonarQube for static analysis

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
The app can be used through Swagger UI at `http://localhost:8080/swagger-ui.html`. See Swagger for more detailed instructions.

### Steps to use the app:
1) Run the app using the above instructions.
2) Create an account via POST request, which returns login credentials:
`http://localhost:8080/swagger-ui.html#/Accounts/openAccountUsingPOST`
3) Login in using credentials obtained from previous step, which will return a JWT token: 
`http://localhost:8080/swagger-ui.html#/Authentication/loginUsingPOST`
4) You can use the `Authorize` button, enter the token in the format `Bearer <token>`.
5) You will now have access to the app!

## Running SonarQube locally
1) Start Docker client (Docker for Mac, Docker for Windows or Docker machine)
2) Run script to start SonarQube container: 
`sh ./scripts/run-sonarqube.sh`
3) After building, run sonarqube gradle task to publish reports:
`./gradlew sonarqube`
4) Navigate to `localhost:9000` to view the reports
5) Login with default credentials `username:admin|password:admin`
