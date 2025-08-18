package uk.gov.justice.laa.fee.scheme.exceptions;

import java.time.OffsetDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import uk.gov.justice.laa.fee.scheme.model.ErrorResponse;

/**
 * Global exception handler for our controllers.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Global exception handler for CategoryCodeNotFoundException exception.
   */
  @ExceptionHandler(CategoryCodeNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleCategoryOfLawNotFound(CategoryCodeNotFoundException ex) {
    ErrorResponse errorResponse = new ErrorResponse()
        .timestamp(OffsetDateTime.now())
        .status(HttpStatus.NOT_FOUND.value())
        .error(HttpStatus.NOT_FOUND.getReasonPhrase())
        .message(ex.getMessage());

    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
  }

  /**
   * Global exception handler for FeeNotFoundException exception.
   */
  @ExceptionHandler(FeeNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleFeeCodeNotfound(FeeNotFoundException ex) {
    ErrorResponse errorResponse = new ErrorResponse()
        .timestamp(OffsetDateTime.now())
        .status(HttpStatus.NOT_FOUND.value())
        .error(HttpStatus.NOT_FOUND.getReasonPhrase())
        .message(ex.getMessage());

    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
  }
}