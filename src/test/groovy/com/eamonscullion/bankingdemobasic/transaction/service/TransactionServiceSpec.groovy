package com.eamonscullion.bankingdemobasic.transaction.service

import com.eamonscullion.bankingdemobasic.account.model.entity.Account
import com.eamonscullion.bankingdemobasic.account.repository.AccountRepository
import com.eamonscullion.bankingdemobasic.app.exception.CustomException
import com.eamonscullion.bankingdemobasic.transaction.model.dto.TransactionDTO
import com.eamonscullion.bankingdemobasic.transaction.model.entity.Transaction
import com.eamonscullion.bankingdemobasic.transaction.model.enums.TransactionType
import com.eamonscullion.bankingdemobasic.transaction.repository.TransactionRepository
import org.modelmapper.ModelMapper
import org.springframework.http.HttpStatus
import spock.lang.Specification

class TransactionServiceSpec extends Specification {

  private TransactionRepository transactionRepository = Mock()
  private AccountRepository accountRepository = Mock()
  private ModelMapper modelMapper = new ModelMapper()
  private TransactionService transactionService = new TransactionService(transactionRepository, accountRepository, modelMapper)

  def "Process deposit transaction successfully"() {
    given: "Test data set up"
      Long accountNumber = 1L
      Long transactionId = 10L
      Account account = new Account(accountNumber: accountNumber, balance: BigDecimal.ONE)
      TransactionDTO dto = new TransactionDTO(accountNumber: accountNumber, type: TransactionType.DEPOSIT, amount: BigDecimal.ONE, description: "deposit")
      Transaction savedTransaction = new Transaction(id: transactionId, amount: dto.amount, account: account)
    and: "Mock valid get account query"
      1 * accountRepository.findByAccountNumber(accountNumber) >> Optional.of(account)
    and: "Mock valid save transaction"
      1 * transactionRepository.save(_ as Transaction) >> savedTransaction
    when: "Process transaction called"
      def response = transactionService.processTransaction(dto)
    then: "Transaction saved successfully"
      response != null
      response.transactionId == transactionId
    and: "Account saved with updated balance"
      1 * accountRepository.save(_ as Account)
  }

  def "Process checks transaction successfully"() {
    given: "Test data set up"
      Long accountNumber = 1L
      Long transactionId = 10L
      Account account = new Account(accountNumber: accountNumber, balance: BigDecimal.ONE)
      TransactionDTO dto = new TransactionDTO(accountNumber: accountNumber, type: TransactionType.CHECKS, amount: BigDecimal.ONE, description: "checks")
      Transaction savedTransaction = new Transaction(id: transactionId, amount: dto.amount, account: account)
    and: "Mock valid get account query"
      1 * accountRepository.findByAccountNumber(accountNumber) >> Optional.of(account)
    and: "Mock valid save transaction"
      1 * transactionRepository.save(_ as Transaction) >> savedTransaction
    when: "Process transaction called"
      def response = transactionService.processTransaction(dto)
    then: "Transaction saved successfully"
      response != null
      response.transactionId == transactionId
    and: "Account saved with updated balance"
      1 * accountRepository.save(_ as Account)
  }

  def "Process withdrawal transaction successfully"() {
    given: "Test data set up"
      Long accountNumber = 1L
      Long transactionId = 10L
      Account account = new Account(accountNumber: accountNumber, balance: BigDecimal.ONE)
      TransactionDTO dto = new TransactionDTO(accountNumber: accountNumber, type: TransactionType.WITHDRAWAL, amount: BigDecimal.ONE, description: "withdrawal")
      Transaction savedTransaction = new Transaction(id: transactionId, amount: dto.amount, account: account)
    and: "Mock valid get account query"
      1 * accountRepository.findByAccountNumber(accountNumber) >> Optional.of(account)
    and: "Mock valid save transaction"
      1 * transactionRepository.save(_ as Transaction) >> savedTransaction
    when: "Process transaction called"
      def response = transactionService.processTransaction(dto)
    then: "Transaction saved successfully"
      response != null
      response.transactionId == transactionId
    and: "Account saved with updated balance"
      1 * accountRepository.save(_ as Account)
  }

  def "Process debit transaction successfully"() {
    given: "Test data set up"
      Long accountNumber = 1L
      Long transactionId = 10L
      Account account = new Account(accountNumber: accountNumber, balance: BigDecimal.ONE)
      TransactionDTO dto = new TransactionDTO(accountNumber: accountNumber, type: TransactionType.DEBIT, amount: BigDecimal.ONE, description: "debit")
      Transaction savedTransaction = new Transaction(id: transactionId, amount: dto.amount, account: account)
    and: "Mock valid get account query"
      1 * accountRepository.findByAccountNumber(accountNumber) >> Optional.of(account)
    and: "Mock valid save transaction"
      1 * transactionRepository.save(_ as Transaction) >> savedTransaction
    when: "Process transaction called"
      def response = transactionService.processTransaction(dto)
    then: "Transaction saved successfully"
      response != null
      response.transactionId == transactionId
    and: "Account saved with updated balance"
      1 * accountRepository.save(_ as Account)
  }

  def "Process withdrawal transaction with account with zero balance fails"() {
    given: "Test data set up"
      Long accountNumber = 1L
      Account account = new Account(accountNumber: accountNumber, balance: BigDecimal.ZERO)
      TransactionDTO dto = new TransactionDTO(accountNumber: accountNumber, type: TransactionType.WITHDRAWAL, amount: BigDecimal.ONE, description: "withdrawal")
    and: "Mock valid get account query"
      1 * accountRepository.findByAccountNumber(accountNumber) >> Optional.of(account)
    when: "Process transaction called"
      transactionService.processTransaction(dto)
    then: "Expect exception thrown"
      def ex = thrown(CustomException)
      ex.getMessage() == "Cannot withdraw from account, balance is negative or zero"
      ex.getHttpStatus() == HttpStatus.CONFLICT
    and: "Expect save transaction not called"
      0 * transactionRepository.save(_ as Transaction)
  }

  def "Process withdrawal transaction with amount higher than balance fails"() {
    given: "Test data set up"
      Long accountNumber = 1L
      Account account = new Account(accountNumber: accountNumber, balance: BigDecimal.ONE)
      TransactionDTO dto = new TransactionDTO(accountNumber: accountNumber, type: TransactionType.WITHDRAWAL, amount: BigDecimal.TEN, description: "withdrawal")
    and: "Mock valid get account query"
      1 * accountRepository.findByAccountNumber(accountNumber) >> Optional.of(account)
    when: "Process transaction called"
      transactionService.processTransaction(dto)
    then: "Expect exception thrown"
      def ex = thrown(CustomException)
      ex.getMessage() == "Cannot withdraw from account, transaction is higher than available balance"
      ex.getHttpStatus() == HttpStatus.CONFLICT
    and: "Expect save transaction not called"
      0 * transactionRepository.save(_ as Transaction)
  }

  def "Process transaction with invalid account number fails" () {
    given: "Test data set up"
      Long accountNumber = 1L
      TransactionDTO dto = new TransactionDTO(accountNumber: accountNumber, type: TransactionType.WITHDRAWAL, amount: BigDecimal.TEN, description: "withdrawal")
    and: "Mock valid get account query"
      1 * accountRepository.findByAccountNumber(accountNumber) >> Optional.empty()
    when: "Process transaction called"
      transactionService.processTransaction(dto)
    then: "Expect exception thrown"
      def ex = thrown(CustomException)
      ex.getMessage() == "Account Number doesn't exist"
      ex.getHttpStatus() == HttpStatus.NOT_FOUND
  }
}
