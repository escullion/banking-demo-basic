package com.eamonscullion.bankingdemobasic.transaction.model.entity;

import com.eamonscullion.bankingdemobasic.account.model.entity.Account;
import com.eamonscullion.bankingdemobasic.transaction.model.enums.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @CreationTimestamp
  @Column(nullable = false, updatable = false)
  private LocalDateTime date;

  @NotNull
  private TransactionType type;

  @NotNull
  private BigDecimal amount;

  @NotNull
  private String description;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "account_number")
  private Account account;

}
