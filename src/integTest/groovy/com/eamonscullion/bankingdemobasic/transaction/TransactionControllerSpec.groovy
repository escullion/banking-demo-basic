package com.eamonscullion.bankingdemobasic.transaction

import com.eamonscullion.bankingdemobasic.AbstractWebIntegrationSpec
import com.eamonscullion.bankingdemobasic.account.model.dto.CreateAccountDTO
import com.eamonscullion.bankingdemobasic.account.model.dto.CreatedAccountDTO
import com.eamonscullion.bankingdemobasic.account.model.entity.Account
import com.eamonscullion.bankingdemobasic.account.repository.AccountRepository
import com.eamonscullion.bankingdemobasic.traits.AuthenticationTrait
import com.eamonscullion.bankingdemobasic.transaction.model.dto.TransactionDTO
import com.eamonscullion.bankingdemobasic.transaction.model.enums.TransactionType
import com.eamonscullion.bankingdemobasic.transaction.repository.TransactionRepository
import io.restassured.RestAssured
import org.apache.http.HttpStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder

import static org.hamcrest.CoreMatchers.containsString

class TransactionControllerSpec extends AbstractWebIntegrationSpec implements AuthenticationTrait {

  @Autowired
  private AccountRepository accountRepository

  @Autowired
  private TransactionRepository transactionRepository

  @Autowired
  private PasswordEncoder passwordEncoder

  private static final TRANSACTIONS = "transactions"
  private Account account
  private CreatedAccountDTO created

  def setup() {
    CreateAccountDTO dto = new CreateAccountDTO(firstName: "first", lastName: "last", ssn: "1111")
    created = RestAssured.given().body(dto).post("/accounts").as(CreatedAccountDTO)
    account = accountRepository.findByAccountNumber(created.accountNumber).get()
  }

  def "When process deposit transaction called then expect success"() {
    given: "Account is logged in and authenticated"
      def request = authenticate(created.accountNumber, created.pin)
    and: "Request body is set up"
      request.body(new TransactionDTO(type: TransactionType.DEPOSIT, amount: BigDecimal.ONE, description: "deposit"))
    when: "Process transaction is called"
      def response = request.post(TRANSACTIONS)
    then: "Expect successful response"
      response.then().statusCode(HttpStatus.SC_OK)
    and: "Expect transaction ID returned"
      response.then().body(containsString("transactionId"))
  }

  def "When process withdrawal transaction called then expect success"() {
    given: "Account is logged in and authenticated"
      def request = authenticate(created.accountNumber, created.pin)
    and: "Account has sufficient balance"
      def setUpRequest = request
      setUpRequest.body(new TransactionDTO(type: TransactionType.DEPOSIT, amount: BigDecimal.ONE, description: "deposit"))
      setUpRequest.post(TRANSACTIONS)
    and: "Request body is set up"
      request.body(new TransactionDTO(type: TransactionType.WITHDRAWAL, amount: BigDecimal.ONE, description: "withdrawal"))
    when: "Process transaction is called"
      def response = request.post(TRANSACTIONS)
    then: "Expect successful response"
      response.then().statusCode(HttpStatus.SC_OK)
    and: "Expect transaction ID returned"
      response.then().body(containsString("transactionId"))
  }

  def "When process transaction called with invalid data then expect validation errors"() {
    given: "Account is logged in and authenticated"
      def request = authenticate(created.accountNumber, created.pin)
    and: "Invalid request body is set up"
      request.body(new TransactionDTO())
    when: "Process transaction is called"
      def response = request.post(TRANSACTIONS)
    then: "Expect unsuccessful response"
      response.then().statusCode(HttpStatus.SC_BAD_REQUEST)
    and: "Expect validation errors returned"
      response.then().body(containsString("type: must not be null"))
      response.then().body(containsString("amount: must not be null"))
      response.then().body(containsString("description: must not be null"))
  }

  def "When process debit external transaction called then expect success"() {
    given: "Account is logged in and authenticated"
      def request = authenticate(created.accountNumber, created.pin)
    and: "Account has sufficient balance"
      def setUpRequest = request
      setUpRequest.body(new TransactionDTO(type: TransactionType.DEPOSIT, amount: BigDecimal.ONE, description: "deposit"))
      setUpRequest.post(TRANSACTIONS)
    and: "Request body is set up"
      request.body(new TransactionDTO(accountNumber: account.accountNumber, type: TransactionType.DEBIT,
              amount: BigDecimal.ONE, description: "debit"))
    when: "Process transaction is called"
      def response = request.post(TRANSACTIONS + "/external")
    then: "Expect successful response"
      response.then().statusCode(HttpStatus.SC_OK)
    and: "Expect transaction ID returned"
      response.then().body(containsString("transactionId"))
  }

  def "When process checks external transaction called then expect success"() {
    given: "Account is logged in and authenticated"
      def request = authenticate(created.accountNumber, created.pin)
    and: "Request body is set up"
      request.body(new TransactionDTO(accountNumber: account.accountNumber, type: TransactionType.CHECKS,
              amount: BigDecimal.ONE, description: "checks"))
    when: "Process transaction is called"
      def response = request.post(TRANSACTIONS + "/external")
    then: "Expect successful response"
      response.then().statusCode(HttpStatus.SC_OK)
    and: "Expect transaction ID returned"
      response.then().body(containsString("transactionId"))
  }

  def "When process external transaction called with invalid data then expect validation errors"() {
    given: "Account is logged in and authenticated"
      def request = authenticate(created.accountNumber, created.pin)
    and: "Invalid request body is set up"
      request.body(new TransactionDTO(type: TransactionType.DEBIT, amount: BigDecimal.ONE, description: "debit"))
    when: "Process external transaction is called"
      def response = request.post(TRANSACTIONS + "/external")
    then: "Expect unsuccessful response"
      response.then().statusCode(HttpStatus.SC_BAD_REQUEST)
    and: "Expect validation errors returned"
      response.then().body(containsString("accountNumber: must not be null"))
  }

  def "When process external transaction called with invalid account then expect 404"() {
    given: "Account is logged in and authenticated"
      def request = authenticate(created.accountNumber, created.pin)
    and: "Request body is set up"
      request.body(new TransactionDTO(accountNumber: 0, type: TransactionType.CHECKS,
              amount: BigDecimal.ONE, description: "checks"))
    when: "Process transaction is called"
      def response = request.post(TRANSACTIONS + "/external")
    then: "Expect successful response"
      response.then().statusCode(HttpStatus.SC_NOT_FOUND)
    and: "Expect transaction ID returned"
      response.then().body(containsString("Account Number doesn't exist"))
  }
}
