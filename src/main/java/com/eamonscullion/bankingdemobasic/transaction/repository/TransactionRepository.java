package com.eamonscullion.bankingdemobasic.transaction.repository;

import com.eamonscullion.bankingdemobasic.transaction.model.entity.Transaction;
import java.util.List;
import org.springframework.data.repository.Repository;

/**
 * Extending from base Repository to prevent unwanted operations e.g delete
 */
public interface TransactionRepository extends Repository<Transaction, Long> {
  Transaction save(Transaction transaction);
  List<Transaction> findTop5ByAccount_AccountNumberOrderByDateDesc(Long accountNumber);
}
