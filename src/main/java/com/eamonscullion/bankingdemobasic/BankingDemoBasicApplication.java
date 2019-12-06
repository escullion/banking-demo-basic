package com.eamonscullion.bankingdemobasic;

import com.eamonscullion.bankingdemobasic.account.model.entity.Account;
import com.eamonscullion.bankingdemobasic.account.repository.AccountRepository;
import com.eamonscullion.bankingdemobasic.transaction.model.entity.Transaction;
import com.eamonscullion.bankingdemobasic.transaction.model.enums.TransactionType;
import com.eamonscullion.bankingdemobasic.transaction.repository.TransactionRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@RequiredArgsConstructor
@EnableTransactionManagement
public class BankingDemoBasicApplication implements CommandLineRunner {

  private final AccountRepository accountRepository;
  private final TransactionRepository transactionRepository;
  private final PasswordEncoder passwordEncoder;

  public static void main(String[] args) {
    SpringApplication.run(BankingDemoBasicApplication.class, args);
  }

  @Override
  public void run(String... params) {
  	// Set up test account
    Account testAccount = Account.builder()
      .pin(passwordEncoder.encode("1111"))
      .firstName("test")
      .lastName("account")
      .ssn("1111")
      .balance(BigDecimal.valueOf(6L))
      .build();
    Account existingAccount = accountRepository.save(testAccount);

    // Set up test transactions
    for (int i = 0; i < 6; i++) {
      Transaction transaction = Transaction.builder()
        .amount(BigDecimal.ONE)
        .type(TransactionType.DEPOSIT)
        .account(existingAccount)
        .date(LocalDateTime.now())
        .description("test")
        .build();
      transactionRepository.save(transaction);
    }
  }
}
