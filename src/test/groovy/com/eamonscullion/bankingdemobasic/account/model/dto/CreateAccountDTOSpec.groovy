package com.eamonscullion.bankingdemobasic.account.model.dto

import com.eamonscullion.bankingdemobasic.traits.ConstraintViolationTrait
import spock.lang.Specification

import javax.validation.ConstraintViolation

class CreateAccountDTOSpec extends Specification implements ConstraintViolationTrait {

  def "create account DTO validation with no constraint violations"() {
    given: "populated completion criteria"
      CreateAccountDTO createAccountDTO = new CreateAccountDTO(firstName: "first", lastName: "last", ssn: "snn")
    when: "validate is called"
      Set<ConstraintViolation<CreateAccountDTO>> violations = validator.validate(createAccountDTO)
    then: "expect no violations"
      violations.isEmpty()
  }

  def "create account DTO validation with not null constraint violations"() {
    given: "populated completion criteria"
      CreateAccountDTO createAccountDTO = new CreateAccountDTO()
    when: "validate is called"
      Set<ConstraintViolation<CreateAccountDTO>> violations = validator.validate(createAccountDTO)
    then: "expect violations returned"
      !violations.isEmpty()
    and: "correct violations are returned"
      Set<String> expectedViolations = Arrays.asList("firstName", "lastName", "ssn")
      assert validateViolations(expectedViolations, violations, NOT_NULL_MESSAGE)
  }
}
