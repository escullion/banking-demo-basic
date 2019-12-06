package com.eamonscullion.bankingdemobasic.account.model.dto;

import com.eamonscullion.bankingdemobasic.transaction.model.dto.TransactionDTO;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

/**
 * Used for returning an overview of an account.
 */
@Data
public class AccountDTO {
  private String firstName;
  private String lastName;
  private String ssn;
  private BigDecimal balance;
  private List<TransactionDTO> recentTransactions;
}
