package com.eamonscullion.bankingdemobasic.security.controller;

import com.eamonscullion.bankingdemobasic.security.service.AuthenticationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@Api(tags = "Authentication")
public class AuthenticationController {

  private final AuthenticationService authenticationService;

  @PostMapping("login")
  @ApiOperation(value = "Login with account number and pin to retrieve a JWT token")
  public String login(@RequestParam Long accountNumber, @RequestParam String pin){
    log.info("Received request to login in account number {}", accountNumber);
    return authenticationService.login(accountNumber, pin);
  }

  @GetMapping("refresh")
  @ApiOperation(value = "Refresh the JWT token for the authenticated account")
  public String refresh() {
    return authenticationService.refresh();
  }
}
