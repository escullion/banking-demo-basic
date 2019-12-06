package com.eamonscullion.bankingdemobasic.app.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolationException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {

  /**
   * Handle any uncaught exceptions
   */
  @ExceptionHandler(Exception.class)
  private ResponseEntity<ErrorDTO> handleException(Exception exception) {

    // Log the complete Stacktrace if there is another cause
    if (exception.getCause() != null) {
      log.error("An error occurred: " + exception.getMessage(), exception);
    } else {
      log.error("An error occurred: " + exception.getMessage());
    }

    // Use response code and reason if annotated at the exception class level
    MergedAnnotations mergedAnnotations = MergedAnnotations.from(exception.getClass());
    if (mergedAnnotations.isPresent(ResponseStatus.class)) {
      ResponseStatus responseStatus = (ResponseStatus) mergedAnnotations.get(ResponseStatus.class);
      return ResponseEntity.status(responseStatus.code()).body(new ErrorDTO(exception.getMessage()));
    }
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorDTO(exception.getMessage()));
  }

  /**
   * Handles business rule related issues
   */
  @ExceptionHandler(CustomException.class)
  public ResponseEntity<Object> handleCustom(CustomException ex) {
    log.warn("CustomException occurred: " + ex);
    return ResponseEntity.status(ex.getHttpStatus()).body(new ErrorDTO(ex.getMessage()));
  }

  /**
   * Handles errors related to illegal arguments
   */
  @ExceptionHandler(IllegalArgumentException.class)
  private ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException ex) {
    log.warn("IllegalArgumentException occurred: " + ex);
    return ResponseEntity.badRequest().body(new ErrorDTO(ex.getMessage()));
  }

  /**
   * Handles errors related to JSON parsing issues
   */
  @ExceptionHandler(HttpMessageNotReadableException.class)
  private ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
    log.warn("HttpMessageNotReadableException occured: " + ex);

    Throwable throwable = ex.getMostSpecificCause();
    if (throwable instanceof InvalidFormatException) {
      InvalidFormatException exception = (InvalidFormatException) throwable;
      return ResponseEntity.badRequest().body(new ErrorDTO(exception.getOriginalMessage()));
    }
    return ResponseEntity.badRequest().build();
  }

  /**
   * Handles bean validation issues triggered by {@link javax.validation.Valid}
   */
  @ExceptionHandler({ConstraintViolationException.class})
  private ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex) {
    log.warn("ConstraintViolationException occurred: " + ex);

    List<String> errors = ex.getConstraintViolations().stream()
      .map(error -> buildValidationResponse(String.valueOf(error.getPropertyPath()), error.getMessage()))
      .collect(Collectors.toList());
    return ResponseEntity.badRequest().body(errors);
  }

  /**
   * Handles bean validation issues that occurs at method level
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  private ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
    log.warn("MethodArgumentNotValidException occurs: " + ex);

    List<String> errors = ex.getBindingResult().getFieldErrors().stream()
      .map(error -> buildValidationResponse(error.getField(), error.getDefaultMessage()))
      .collect(Collectors.toList());
    return ResponseEntity.badRequest().body(errors);
  }

  private String buildValidationResponse(String key, String value) {
    return key + ": " + value;
  }

  @Data
  private class ErrorDTO {
    private final String message;
  }
}
