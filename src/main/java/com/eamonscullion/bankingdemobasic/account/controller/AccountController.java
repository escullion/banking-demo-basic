package com.eamonscullion.bankingdemobasic.account.controller;

import com.eamonscullion.bankingdemobasic.account.model.dto.AccountDTO;
import com.eamonscullion.bankingdemobasic.account.model.dto.BalanceProjection;
import com.eamonscullion.bankingdemobasic.account.model.dto.CreateAccountDTO;
import com.eamonscullion.bankingdemobasic.account.model.dto.CreatedAccountDTO;
import com.eamonscullion.bankingdemobasic.account.service.AccountService;
import com.eamonscullion.bankingdemobasic.security.service.AuthenticationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Api(tags = "Accounts")
@RequiredArgsConstructor
@RequestMapping("accounts")
public class AccountController {

  private final AccountService accountService;
  private final AuthenticationService authenticationService;

  @PostMapping
  @ApiOperation(value = "Open a new account")
  public ResponseEntity<CreatedAccountDTO> openAccount(@RequestBody @Valid CreateAccountDTO dto) {
    log.info("Received request to open a new account");
    return ResponseEntity.ok(accountService.openAccount(dto));
  }

  @GetMapping
  @ApiOperation(value = "Get an overview for the authenticated account")
  public ResponseEntity<AccountDTO> getAccount() {
    log.info("Received request to retrieve an account overview");
    AccountDTO response = accountService.getAccountOverview(authenticationService.getCurrentUserFromSession());
    return ResponseEntity.ok(response);
  }

  @DeleteMapping
  @ApiOperation(value = "Close the authenticated account")
  public void closeAccount() {
    log.info("Received request to close an account");
    accountService.closeAccount(authenticationService.getCurrentUserFromSession());
  }

  @GetMapping("balance")
  @ApiOperation(value = "Get the account balance for the authenticated account")
  public ResponseEntity<BalanceProjection> getAccountBalance() {
    log.info("Received request to retrieve an account's balance");
    return accountService.getAccountBalance(authenticationService.getCurrentUserFromSession())
      .map(ResponseEntity::ok)
      .orElseGet(() -> ResponseEntity.notFound().build());
  }
}
