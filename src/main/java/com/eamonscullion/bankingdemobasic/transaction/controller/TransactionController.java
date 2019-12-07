package com.eamonscullion.bankingdemobasic.transaction.controller;

import com.eamonscullion.bankingdemobasic.app.validation.ExternalCall;
import com.eamonscullion.bankingdemobasic.security.service.AuthenticationService;
import com.eamonscullion.bankingdemobasic.transaction.model.dto.CreatedTransactionDTO;
import com.eamonscullion.bankingdemobasic.transaction.model.dto.TransactionDTO;
import com.eamonscullion.bankingdemobasic.transaction.service.TransactionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@Api(tags = "Transactions")
@RequestMapping("transactions")
public class TransactionController {

  private final TransactionService transactionService;
  private final AuthenticationService authenticationService;

  @PostMapping
  @ApiOperation(value = "Process a transaction for the authenticated account, and returns the transaction ID")
  public ResponseEntity<CreatedTransactionDTO>processTransaction(@RequestBody @Valid TransactionDTO dto) {
    log.info("Request received to process a transaction");
    dto.setAccountNumber(authenticationService.getCurrentUserFromSession());
    return ResponseEntity.ok(transactionService.processTransaction(dto));
  }

  @PostMapping("external")
  @Validated(ExternalCall.class)
  @ApiOperation(value = "Process a transaction for an external source, and returns the transaction ID")
  public ResponseEntity<CreatedTransactionDTO> processExternalTransaction(@RequestBody @Valid TransactionDTO dto) {
    log.info("Request received to process an external transaction");
    return ResponseEntity.ok(transactionService.processTransaction(dto));
  }
}