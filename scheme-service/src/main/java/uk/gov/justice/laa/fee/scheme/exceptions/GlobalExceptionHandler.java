package uk.gov.justice.laa.fee.scheme.exceptions;

import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for our controllers.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Global exception handler for CategoryCodeNotFound exception.
   */
  @ExceptionHandler(CategoryCodeNotFound.class)
  public ResponseEntity<ErrorResponse> handleCategoryOfLawNotFound(CategoryCodeNotFound ex) {

    ErrorResponse errorResponse = new ErrorResponse(
        LocalDateTime.now(),
        HttpStatus.NOT_FOUND.value(),
        HttpStatus.NOT_FOUND.getReasonPhrase(),
        ex.getMessage());

    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
  }
}