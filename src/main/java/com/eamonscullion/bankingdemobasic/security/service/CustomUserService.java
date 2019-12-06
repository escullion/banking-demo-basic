package com.eamonscullion.bankingdemobasic.security.service;

import com.eamonscullion.bankingdemobasic.account.model.entity.Account;
import com.eamonscullion.bankingdemobasic.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserService implements UserDetailsService {

  private final AccountRepository accountRepository;

  @Override
  public UserDetails loadUserByUsername(String accountNumber) {
    Account account = accountRepository.findByAccountNumber(Long.valueOf(accountNumber))
      .orElseThrow(() -> new UsernameNotFoundException("Account '" + accountNumber + "' not found"));

    return User.builder()
      .username(accountNumber)
      .password(account.getPin())
      .authorities("Account Holder")
      .accountExpired(false)
      .accountLocked(false)
      .credentialsExpired(false)
      .disabled(false)
      .build();
  }

}
