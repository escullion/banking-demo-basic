package com.eamonscullion.bankingdemobasic.account.model.entity;

import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SequenceGenerator(name = "seq", initialValue = 10000000)
public class Account {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
  private Long accountNumber;

  @NotNull
  private String firstName;

  @NotNull
  private String lastName;

  @NotNull
  private String pin;

  @NotNull
  private String ssn;

  @NotNull
  private BigDecimal balance;
}
