package com.eamonscullion.bankingdemobasic.transaction.model.dto;

import com.eamonscullion.bankingdemobasic.app.validation.ExternalCall;
import com.eamonscullion.bankingdemobasic.transaction.model.enums.TransactionType;
import java.math.BigDecimal;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.Data;

@Data
public class TransactionDTO {

  @NotNull(groups = ExternalCall.class)
  private Long accountNumber;

  @NotNull
  private TransactionType type;

  @NotNull
  @Positive
  private BigDecimal amount;

  @NotNull
  private String description;
}