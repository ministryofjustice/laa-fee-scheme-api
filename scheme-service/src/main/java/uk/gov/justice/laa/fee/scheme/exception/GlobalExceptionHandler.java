package uk.gov.justice.laa.fee.scheme.exception;

import java.time.OffsetDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import uk.gov.justice.laa.fee.scheme.model.ErrorResponse;
import uk.gov.justice.laa.fee.scheme.sentry.TestSentryException;

/**
 * Global exception handler for our controllers.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Global exception handler for HttpMessageNotReadableException exception.
   * Duplicate fields, malformed request, type mismatch.
   */
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
    return handleException(ex, HttpStatus.BAD_REQUEST);
  }

  /**
   * Handle missing request fields.
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentException(MethodArgumentNotValidException ex) {
    return handleException(ex, HttpStatus.BAD_REQUEST);
  }

  /**
   * Global exception handler for CategoryCodeNotFoundException exception.
   */
  @ExceptionHandler(CategoryCodeNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleCategoryOfLawNotFound(CategoryCodeNotFoundException ex) {
    return handleException(ex, HttpStatus.NOT_FOUND);
  }

  /**
   * Global exception handler for FeeNotFoundException exception.
   */
  @ExceptionHandler(FeeNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleFeeCodeNotfound(FeeNotFoundException ex) {
    return handleException(ex, HttpStatus.NOT_FOUND);
  }

  /**
   * Global exception handler for InvalidMediationSessionException exception.
   */
  @ExceptionHandler(InvalidMediationSessionException.class)
  public ResponseEntity<ErrorResponse> handleInvalidMediationSession(InvalidMediationSessionException ex) {
    return handleException(ex, HttpStatus.BAD_REQUEST);
  }

  /**
   * Global exception handler for FeeNotFoundException exception.
   */
  @ExceptionHandler(PoliceStationFeeNotFoundException.class)
  public ResponseEntity<ErrorResponse> handlePoliceStationFeeNotfound(PoliceStationFeeNotFoundException ex) {
    return handleException(ex, HttpStatus.NOT_FOUND);
  }

  /**
   * Global exception handler for TestSentryException exception. (To test Sentry integration only, will be removed later)
   */
  @ExceptionHandler(TestSentryException.class)
  public ResponseEntity<ErrorResponse> testSentry(TestSentryException ex) {
    return handleException(ex, HttpStatus.INTERNAL_SERVER_ERROR);
  }


  private ResponseEntity<ErrorResponse> handleException(Throwable ex, HttpStatus status) {
    ErrorResponse errorResponse = new ErrorResponse()
        .timestamp(OffsetDateTime.now())
        .status(status.value())
        .error(status.getReasonPhrase())
        .message(ex.getMessage());

    return new ResponseEntity<>(errorResponse, status);
  }
}