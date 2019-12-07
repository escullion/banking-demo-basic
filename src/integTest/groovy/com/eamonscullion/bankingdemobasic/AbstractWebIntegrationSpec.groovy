package com.eamonscullion.bankingdemobasic

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import io.restassured.RestAssured
import io.restassured.builder.RequestSpecBuilder
import io.restassured.config.ObjectMapperConfig
import io.restassured.filter.log.LogDetail
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

import static io.restassured.config.HttpClientConfig.httpClientConfig
import static org.springframework.http.HttpHeaders.CONTENT_TYPE
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE

/**
 * Base class to derive concrete web test classes from.
 */
//@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AbstractWebIntegrationSpec extends Specification {

  @LocalServerPort
  public int port

  def setup() {
    RestAssured.config = RestAssured.config().httpClient(httpClientConfig().reuseHttpClientInstance())
    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
    RestAssured.requestSpecification = requestSpecification()
  }

  def requestSpecification() {
    return new RequestSpecBuilder()
            .setPort(port)
            .addHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .log(LogDetail.ALL)
            .build()
  }


}

