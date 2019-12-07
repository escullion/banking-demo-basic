package com.eamonscullion.bankingdemobasic.traits

import io.restassured.RestAssured
import io.restassured.specification.RequestSpecification

trait AuthenticationTrait {
  String AUTHORIZATION = "AUTHORIZATION"

  String login(Long accountNumber, String pin) {
    return RestAssured.given()
            .queryParam("accountNumber", accountNumber)
            .queryParam("pin", pin)
            .post("auth/login").asString()
  }

  RequestSpecification authenticate(Long accountNumber, String pin) {
    String token = login(accountNumber, pin)
    return RestAssured.given().header(AUTHORIZATION, "Bearer " + token)
  }
}