package com.eamonscullion.bankingdemobasic.account.model.dto;

import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * Used to open a new account.
 */
@Data
public class CreateAccountDTO {

  @NotNull
  private String firstName;

  @NotNull
  private String lastName;

  @NotNull
  private String ssn;
}
