package com.eamonscullion.bankingdemobasic.account.controller

import com.eamonscullion.bankingdemobasic.account.model.dto.CreateAccountDTO
import com.eamonscullion.bankingdemobasic.account.service.AccountService
import com.eamonscullion.bankingdemobasic.security.service.AuthenticationService
import spock.lang.Specification

class AccountControllerSpec extends Specification {

  AccountService accountService = Mock()
  AuthenticationService authenticationService = Mock()
  AccountController accountController = new AccountController(accountService, authenticationService)

  def "Open account returns 200 OK status code" () {
    given: ""
      CreateAccountDTO createAccountDTO = new CreateAccountDTO(firstName: "first", lastName: "last", ssn: "12345678")
    when: ""
      def response = accountController.openAccount()
    then: ""

  }

  def "Given account is not found, return 204 no content" () {
    given: ""

    when: ""

    then: ""

  }
}
