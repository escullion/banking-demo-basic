package com.eamonscullion.bankingdemobasic.security.service;

import com.eamonscullion.bankingdemobasic.app.exception.CustomException;
import com.eamonscullion.bankingdemobasic.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

  private final JwtTokenProvider jwtTokenProvider;
  private final AuthenticationManager authenticationManager;

  public String login(Long accountNumber, String pin) {
    try {
      authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(accountNumber, pin));
      return jwtTokenProvider.createToken(accountNumber);
    } catch (AuthenticationException ex) {
      log.error("Failed to authenticate account {}, invalid account number or pin", accountNumber);
      throw new CustomException("Invalid account number/pin supplied", HttpStatus.BAD_REQUEST);
    }
  }

  public Long getCurrentUserFromSession() {
    final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication.getPrincipal() instanceof User) {
      User userPrincipal = (User) authentication.getPrincipal();
      return Long.valueOf(userPrincipal.getUsername());
    } else {
      log.error("Failed to retrieve account number from session");
      throw new CustomException("Unable to retrieve user from the current session.", HttpStatus.UNAUTHORIZED);
    }
  }

  public String refresh() {
    return jwtTokenProvider.createToken(getCurrentUserFromSession());
  }
}
