package com.eamonscullion.bankingdemobasic.account

import com.eamonscullion.bankingdemobasic.AbstractWebIntegrationSpec
import com.eamonscullion.bankingdemobasic.account.model.dto.AccountDTO
import com.eamonscullion.bankingdemobasic.account.model.dto.CreateAccountDTO
import com.eamonscullion.bankingdemobasic.account.model.dto.CreatedAccountDTO
import com.eamonscullion.bankingdemobasic.account.model.entity.Account
import com.eamonscullion.bankingdemobasic.account.repository.AccountRepository
import com.eamonscullion.bankingdemobasic.traits.AuthenticationTrait
import io.restassured.RestAssured
import org.apache.http.HttpStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder

import static org.hamcrest.CoreMatchers.containsString

class AccountControllerSpec extends AbstractWebIntegrationSpec implements AuthenticationTrait {

  @Autowired
  private AccountRepository accountRepository

  @Autowired
  private PasswordEncoder passwordEncoder

  private static final ACCOUNTS = "accounts"
  private Account account
  private CreatedAccountDTO created

  def setup() {
    CreateAccountDTO dto = new CreateAccountDTO(firstName: "first", lastName: "last", ssn: "1111")
    created = RestAssured.given().body(dto).post("/accounts").as(CreatedAccountDTO)
    account = accountRepository.findByAccountNumber(created.accountNumber).get()
  }

  def "When GET account is called Then expect successful response"() {
    given: "Account is logged in and authenticated"
      def request = authenticate(created.accountNumber, created.pin)
    when: "Get Account is called"
      def response = request.when().get(ACCOUNTS)
    then: "Expect successful response"
      response.then().statusCode(HttpStatus.SC_OK)
    and: "Expect account returned"
      AccountDTO accountDTO = response.as(AccountDTO)
      account.firstName == accountDTO.firstName
  }

  def "When POST account is called then expect account created"() {
    given: "Account is logged in and authenticated"
      def request = authenticate(created.accountNumber, created.pin)
    and: "Request body is set up"
      request.body(new CreateAccountDTO(firstName: "first", lastName: "last", ssn: "ssn"))
    when: "Open Account is called"
      def response = request.when().post(ACCOUNTS)
    then: "Expect successful response"
      response.then().statusCode(HttpStatus.SC_OK)
    and: "Expect account number and pin returned"
      response.then().body(containsString("accountNumber"))
      response.then().body(containsString("pin"))
  }

  def "When POST account is called with invalid request then expect validation error"() {
    given: "Account is logged in and authenticated"
      def request = authenticate(created.accountNumber, created.pin)
    and: "Invalid request body is set up"
      request.given().body(new CreateAccountDTO())
    when: "Open Account is called"
      def response = request.when().post(ACCOUNTS)
    then: "Expect bad request response"
      response.then().statusCode(HttpStatus.SC_BAD_REQUEST)
    and: "Expect account number and pin returned"
      response.then().body(containsString("firstName: must not be null"))
      response.then().body(containsString("lastName: must not be null"))
      response.then().body(containsString("ssn: must not be null"))
  }

  def "When DELETE account is called then expect account closed"() {
    given: "Account is logged in and authenticated"
      def request = authenticate(created.accountNumber, created.pin)
    when: "Delete Account is called"
      def response = request.when().delete(ACCOUNTS)
    then: "Expect successful response"
      response.then().statusCode(HttpStatus.SC_OK)
    and: "Expect account is not present in database"
      !accountRepository.findByAccountNumber(account.accountNumber).isPresent()
  }
}
