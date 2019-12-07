package com.eamonscullion.bankingdemobasic.account.service

import com.eamonscullion.bankingdemobasic.account.model.dto.AccountDTO
import com.eamonscullion.bankingdemobasic.account.model.dto.BalanceProjection
import com.eamonscullion.bankingdemobasic.account.model.dto.CreateAccountDTO
import com.eamonscullion.bankingdemobasic.account.model.entity.Account
import com.eamonscullion.bankingdemobasic.account.repository.AccountRepository
import com.eamonscullion.bankingdemobasic.app.exception.CustomException
import com.eamonscullion.bankingdemobasic.transaction.model.entity.Transaction
import com.eamonscullion.bankingdemobasic.transaction.model.enums.TransactionType
import com.eamonscullion.bankingdemobasic.transaction.repository.TransactionRepository
import org.modelmapper.ModelMapper
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import spock.lang.Specification

class AccountServiceSpec extends Specification {

  private AccountRepository accountRepository = Mock()
  private ModelMapper modelMapper = new ModelMapper()
  private PasswordEncoder passwordEncoder = Mock()
  private TransactionRepository transactionRepository = Mock()
  private AccountService accountService = new AccountService(accountRepository, modelMapper, passwordEncoder, transactionRepository)

  def "Account opened successfully"() {
    given: "Test data set up"
      CreateAccountDTO dto = new CreateAccountDTO(firstName: "first", lastName: "last", ssn: "ssn")
      Account account = new Account(accountNumber: 1)
    and: "Mock valid responses"
      1 * passwordEncoder.encode(_ as String) >> "encodedPassword"
      1 * accountRepository.save(_ as Account) >> account
    when: "Open Account called"
      def response = accountService.openAccount(dto)
    then: "Account opened successfully"
      response != null
      response.accountNumber == account.accountNumber
  }

  def "Account closed successfully"() {
    given: "Test data set up"
      Long accountNumber = 1L
      Account account = new Account(accountNumber: accountNumber, balance: BigDecimal.ONE)
    and: "Mock valid get account query"
      1 * accountRepository.findByAccountNumber(accountNumber) >> Optional.of(account)
    when: "Close account called"
      accountService.closeAccount(accountNumber)
    then: "Expect delete called successfully"
      1 * accountRepository.deleteById(accountNumber)
  }

  def "Account closed with invalid account number throws exception"() {
    given: "Test data set up"
      Long accountNumber = 1
    and: "Mock empty response"
      1 * accountRepository.findByAccountNumber(accountNumber) >> Optional.empty()
    when: "Close account called"
      accountService.closeAccount(accountNumber)
    then: "Exception thrown"
      def ex = thrown(CustomException)
      ex.getMessage() == "Account number doesn't exist"
      ex.getHttpStatus() == HttpStatus.NOT_FOUND
    and: "Expect delete not called"
      0 * accountRepository.deleteById(accountNumber)
  }

  def "Account closed with overdrawn account throws exception"() {
    given: "Test data set up"
      Long accountNumber = 1L
      Account account = new Account(accountNumber: accountNumber, balance: BigDecimal.valueOf(-1L))
    and: "Mock valid get account query"
      1 * accountRepository.findByAccountNumber(accountNumber) >> Optional.of(account)
    when: "Close account called"
      accountService.closeAccount(accountNumber)
    then: "Exception thrown"
      def ex = thrown(CustomException)
      ex.getMessage() == "Cannot close account, balance is negative"
      ex.getHttpStatus() == HttpStatus.CONFLICT
    and: "Expect delete not called"
      0 * accountRepository.deleteById(accountNumber)
  }

  def "Get account overview successfully"() {
    given: "Test data set up"
      Long accountNumber = 1L
      Account account = new Account(accountNumber: accountNumber, balance: BigDecimal.valueOf(1L))
    and: "Mock valid get account query"
      1 * accountRepository.findByAccountNumber(accountNumber) >> Optional.of(account)
    and: "Mock valid transaction query"
      Transaction transaction = new Transaction(id: 10L, type: TransactionType.DEPOSIT, amount: BigDecimal.ONE, description: "test", account: account)
      1 * transactionRepository.findTop5ByAccount_AccountNumberOrderByDateDesc(accountNumber) >> Arrays.asList(transaction)
    when: "Get account overview called"
      AccountDTO response = accountService.getAccountOverview(accountNumber)
    then: "Expect account overview returned successfully"
      response != null
      !response.recentTransactions.isEmpty()
  }

  def "Get account overview with invalid account number throws exception"() {
    given: "Test data set up"
      Long accountNumber = 1L
    and: "Mock empty response from get account query"
      1 * accountRepository.findByAccountNumber(accountNumber) >> Optional.empty()
    when: "Get account overview called"
      accountService.getAccountOverview(accountNumber)
    then: "Exception thrown"
      def ex = thrown(CustomException)
      ex.getMessage() == "Account number doesn't exist"
      ex.getHttpStatus() == HttpStatus.NOT_FOUND
    and: "Expect transaction query not called"
      0 * transactionRepository.findTop5ByAccount_AccountNumberOrderByDateDesc(accountNumber)
  }

  def "Get account overview with no transactions returns empty transactions"() {
    given: "Test data set up"
      Long accountNumber = 1L
      Account account = new Account(accountNumber: accountNumber, balance: BigDecimal.valueOf(1L))
    and: "Mock valid get account query"
      1 * accountRepository.findByAccountNumber(accountNumber) >> Optional.of(account)
    and: "Mock valid transaction query"
      1 * transactionRepository.findTop5ByAccount_AccountNumberOrderByDateDesc(accountNumber) >> Arrays.asList()
    when: "Get account overview called"
      AccountDTO response = accountService.getAccountOverview(accountNumber)
    then: "Expect account overview returned successfully"
      response != null
      response.recentTransactions.isEmpty()
  }

  def "Get account balance successfully"() {
    given: "Test data set up"
      Long accountNumber = 1L
    and: "Mock valid get account balance query"
      BalanceProjection balanceProjection = Mock()
      1 * accountRepository.findBalanceByAccountNumber(accountNumber) >> Optional.of(balanceProjection)
    when: "Get account balance is called"
      Optional<BalanceProjection> response = accountService.getAccountBalance(accountNumber)
    then: "Account Balance is returned successfully"
      response.isPresent()
      response.get() == balanceProjection
  }

  def "Get account balance with invalid account number returns empty optional"() {
    given: "Test data set up"
      Long accountNumber = 1L
    and: "Mock valid get account balance query"
      1 * accountRepository.findBalanceByAccountNumber(accountNumber) >> Optional.empty()
    when: "Get account balance is called"
      Optional<BalanceProjection> response = accountService.getAccountBalance(accountNumber)
    then: "Empty response is returned"
      !response.isPresent()
  }
}
