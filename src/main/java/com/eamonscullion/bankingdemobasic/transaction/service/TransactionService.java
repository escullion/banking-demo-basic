package com.eamonscullion.bankingdemobasic.transaction.service;

import com.eamonscullion.bankingdemobasic.account.model.entity.Account;
import com.eamonscullion.bankingdemobasic.account.repository.AccountRepository;
import com.eamonscullion.bankingdemobasic.app.exception.CustomException;
import com.eamonscullion.bankingdemobasic.transaction.model.dto.CreatedTransactionDTO;
import com.eamonscullion.bankingdemobasic.transaction.model.entity.Transaction;
import com.eamonscullion.bankingdemobasic.transaction.model.dto.TransactionDTO;
import com.eamonscullion.bankingdemobasic.transaction.repository.TransactionRepository;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TransactionService {

  private final TransactionRepository transactionRepository;
  private final AccountRepository accountRepository;
  private final ModelMapper modelMapper;

  public CreatedTransactionDTO processTransaction(TransactionDTO dto) {
    Account account = getAccount(dto.getAccountNumber());
    Transaction transaction = modelMapper.map(dto, Transaction.class);
    transaction.setAccount(account);

    switch (transaction.getType()) {
      case DEPOSIT:
      case CHECKS:
        log.debug("Processing a debit/check transaction for account number {}", dto.getAccountNumber());
        return processDeposit(transaction);
      case WITHDRAWAL:
      case DEBIT:
        log.debug("Processing a withdrawal/debit transaction for account number {}", dto.getAccountNumber());
        return processWithdrawal(transaction);
      default:
        log.error("Failed to match a transaction type");
        throw new CustomException("Failed to process transaction", HttpStatus.BAD_REQUEST);
    }
  }

  private CreatedTransactionDTO processDeposit(Transaction transaction) {
    Account account = transaction.getAccount();
    Transaction newTransaction = transactionRepository.save(transaction);
    log.debug("Transaction successfully processed with id {} for account {}", newTransaction.getId(), account.getAccountNumber());

    BigDecimal newBalance = account.getBalance().add(newTransaction.getAmount());
    updateBalance(account, newBalance);
    return modelMapper.map(newTransaction, CreatedTransactionDTO.class);
  }

  private void updateBalance(Account account, BigDecimal newBalance) {
    account.setBalance(newBalance);
    accountRepository.save(account);
    log.debug("Balance successfully updated for account {}", account.getAccountNumber());
  }

  private CreatedTransactionDTO processWithdrawal(Transaction transaction) {
    Account account = transaction.getAccount();
    if (account.getBalance().signum() < 1) {
      log.error("Failed to withdraw from account, current balance is negative or zero");
      throw new CustomException("Cannot withdraw from account, balance is negative or zero", HttpStatus.CONFLICT);
    }

    if (account.getBalance().compareTo(transaction.getAmount()) < 0) {
      log.error("Failed to withdraw from account, transaction amount is higher than balance");
      throw new CustomException("Cannot withdraw from account, transaction is higher than available balance", HttpStatus.CONFLICT);
    }

    Transaction newTransaction = transactionRepository.save(transaction);
    log.debug("Transaction successfully processed with id {} for account {}", newTransaction.getId(), account.getAccountNumber());

    BigDecimal newBalance = account.getBalance().subtract(newTransaction.getAmount());
    updateBalance(account, newBalance);
    return modelMapper.map(newTransaction, CreatedTransactionDTO.class);
  }

  private Account getAccount(Long accountNumber) {
    return accountRepository.findByAccountNumber(accountNumber)
      .orElseThrow(() -> new CustomException("Account Number doesn't exist", HttpStatus.NOT_FOUND));
  }

}
