package uk.gov.justice.laa.fee.scheme.exception;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner.TypeEnum.ERROR;

import java.time.OffsetDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import uk.gov.justice.laa.fee.scheme.enums.ErrorCode;
import uk.gov.justice.laa.fee.scheme.model.ErrorResponse;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner;

/**
 * Global exception handler for our controllers.
 */
@RestControllerAdvice
@Slf4j
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
   * Global exception handler for HttpMessageNotReadableException exception.
   * Duplicate fields, malformed request, type mismatch.
   */
  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
    return handleException(ex, HttpStatus.METHOD_NOT_ALLOWED);
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
  @ExceptionHandler({CategoryCodeNotFoundException.class})
  public ResponseEntity<ErrorResponse> handleCategoryOfLawNotFound(CategoryCodeNotFoundException ex) {
    return handleException(ex, HttpStatus.NOT_FOUND);
  }

  /**
   * Global exception handler for ValidationException exception.
   */
  @ExceptionHandler(ValidationException.class)
  public ResponseEntity<FeeCalculationResponse> handleValidationException(ValidationException ex) {
    ErrorCode error = ex.getError();
    FeeContext context = ex.getContext();

    log.error("Validation error with message: {} :: {feeCode={}, startDate={}}",
        ex.getMessage(), context.feeCode(), context.startDate(), ex);

    ValidationMessagesInner validationMessages = ValidationMessagesInner.builder()
        .type(ERROR)
        .code(error.getCode())
        .message(error.getMessage()).build();

    FeeCalculationResponse feeCalculationResponse = FeeCalculationResponse.builder()
        .feeCode(context.feeCode())
        .schemeId(context.schemeId())
        .claimId(context.claimId())
        .validationMessages(List.of(validationMessages)).build();

    return ResponseEntity.ok(feeCalculationResponse);
  }

  /**
   * Global exception handler for PoliceStationFeeNotFoundException exception.
   */
  @ExceptionHandler(PoliceStationFeeNotFoundException.class)
  public ResponseEntity<ErrorResponse> handlePoliceStationFeeNotfound(PoliceStationFeeNotFoundException ex) {
    return handleException(ex, HttpStatus.NOT_FOUND);
  }

  /**
   * Global exception handler for all other exceptions.
   */
  @ExceptionHandler(Exception.class)
  @ResponseStatus(INTERNAL_SERVER_ERROR)
  public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
    return handleException(ex, INTERNAL_SERVER_ERROR);
  }


  private ResponseEntity<ErrorResponse> handleException(Throwable ex, HttpStatus status) {
    log.error("Error occurred :: {error={}, status={}, message={}}", status.getReasonPhrase(), status.value(), ex.getMessage(), ex);

    ErrorResponse errorResponse = new ErrorResponse()
        .timestamp(OffsetDateTime.now())
        .status(status.value())
        .error(status.getReasonPhrase())
        .message(ex.getMessage());

    return new ResponseEntity<>(errorResponse, status);
  }
}