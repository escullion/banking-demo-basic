package com.eamonscullion.bankingdemobasic.traits

import javax.validation.ConstraintViolation
import javax.validation.Validation
import javax.validation.Validator
import javax.validation.ValidatorFactory
import java.util.stream.Collectors

trait ConstraintViolationTrait {
  static final String NOT_NULL_MESSAGE = "must not be null"

  ValidatorFactory factory = Validation.buildDefaultValidatorFactory()
  Validator validator = factory.getValidator()

  boolean validateViolations(Set expectedViolations, Set<ConstraintViolation> constraintViolations, String message) {
    def result = constraintViolations.stream()
            .peek { violation -> assert violation.message == message }
            .map { violation -> violation.propertyPath.toString() }
            .collect(Collectors.toSet())
    return expectedViolations == result
  }
}