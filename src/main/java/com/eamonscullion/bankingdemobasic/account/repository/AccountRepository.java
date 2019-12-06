package com.eamonscullion.bankingdemobasic.account.repository;

import com.eamonscullion.bankingdemobasic.account.model.entity.Account;
import com.eamonscullion.bankingdemobasic.account.model.dto.BalanceProjection;
import java.util.Optional;
import org.springframework.data.repository.Repository;

/**
 * Extending Base Repository as we don't want to expose unwanted functionality.
 */
public interface AccountRepository extends Repository<Account, Long> {
  Account save(Account account);
  void deleteById(Long accountNumber);
  Optional<Account> findByAccountNumber(Long accountNumber);
  Optional<BalanceProjection> findBalanceByAccountNumber(Long accountNumber);
}
