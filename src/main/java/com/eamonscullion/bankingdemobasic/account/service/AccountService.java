package com.eamonscullion.bankingdemobasic.account.service;

import com.eamonscullion.bankingdemobasic.account.model.dto.AccountDTO;
import com.eamonscullion.bankingdemobasic.account.model.dto.BalanceProjection;
import com.eamonscullion.bankingdemobasic.account.model.dto.CreateAccountDTO;
import com.eamonscullion.bankingdemobasic.account.model.dto.CreatedAccountDTO;
import com.eamonscullion.bankingdemobasic.account.model.entity.Account;
import com.eamonscullion.bankingdemobasic.account.repository.AccountRepository;
import com.eamonscullion.bankingdemobasic.app.exception.CustomException;
import com.eamonscullion.bankingdemobasic.transaction.model.dto.TransactionDTO;
import com.eamonscullion.bankingdemobasic.transaction.repository.TransactionRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.utility.RandomString;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AccountService {

  private final AccountRepository accountRepository;
  private final ModelMapper modelMapper;
  private final PasswordEncoder passwordEncoder;
  private final TransactionRepository transactionRepository;

  public CreatedAccountDTO openAccount(CreateAccountDTO dto) {
    Account account = modelMapper.map(dto, Account.class);
    String pin = RandomString.make(4);
    account.setPin(passwordEncoder.encode(pin));
    account.setBalance(BigDecimal.ZERO);

    CreatedAccountDTO createdAccount = modelMapper.map(accountRepository.save(account), CreatedAccountDTO.class);
    log.debug("Successfully created an account with account number {}", createdAccount.getAccountNumber());
    createdAccount.setPin(pin);
    return createdAccount;
  }

  public void closeAccount(Long accountNumber) {
    Account account = accountRepository.findByAccountNumber(accountNumber)
      .orElseThrow(() -> new CustomException("Account number doesn't exist", HttpStatus.NO_CONTENT));

    if (account.getBalance().signum() < 0) {
      log.error("Cannot close account with account number {}, balance is negative", accountNumber);
      throw new CustomException("Cannot close account, balance is negative", HttpStatus.CONFLICT);
    }
    accountRepository.deleteById(accountNumber);
    log.debug("Successfully closed an account with account number {}", accountNumber);
  }

  public AccountDTO getAccountOverview(Long accountNumber) {
    Account account = accountRepository.findByAccountNumber(accountNumber)
      .orElseThrow(() -> new CustomException("Account number doesn't exist", HttpStatus.NO_CONTENT));
    AccountDTO dto = modelMapper.map(account, AccountDTO.class);
    log.debug("Successfully retrieve account overview for account number {}", accountNumber);
    dto.setRecentTransactions(getRecentTransactions(accountNumber));
    return dto;
  }

  public Optional<BalanceProjection> getAccountBalance(Long accountNumber) {
    return accountRepository.findBalanceByAccountNumber(accountNumber);
  }

  private List<TransactionDTO> getRecentTransactions(Long accountNumber) {
    return transactionRepository.findTop5ByAccount_AccountNumberOrderByDateDesc(accountNumber)
      .stream()
      .map(transaction -> modelMapper.map(transaction, TransactionDTO.class))
      .collect(Collectors.toList());
  }
}
