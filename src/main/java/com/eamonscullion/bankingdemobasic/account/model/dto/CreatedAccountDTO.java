package com.eamonscullion.bankingdemobasic.account.model.dto;

import lombok.Data;

/**
 * Response when an account is created.
 */
@Data
public class CreatedAccountDTO {
  private String accountNumber;
  private String pin;
}
