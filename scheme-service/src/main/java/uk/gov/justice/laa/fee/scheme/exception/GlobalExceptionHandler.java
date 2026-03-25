package uk.gov.justice.laa.fee.scheme.exception;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner.TypeEnum.ERROR;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tools.jackson.core.exc.StreamReadException;
import tools.jackson.databind.exc.InvalidFormatException;
import tools.jackson.databind.exc.MismatchedInputException;
import uk.gov.justice.laa.fee.scheme.enums.ErrorType;
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
   *
   * @param ex the exception thrown when the request body is not readable or cannot be parsed.
   * @return the error response and a 400 Bad Request status code.
   */
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
    HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
    String errorMessage = switch (ex.getCause()) {
      case InvalidFormatException ife -> String.format("Invalid value: %s for field: %s expects a %s",
          ife.getValue(), ife.getPath().isEmpty() ? "unknown" : ife.getPath().getFirst().getPropertyName(),
          ife.getTargetType().getSimpleName());
      case MismatchedInputException mie -> String.format("Invalid value for field: %s expects a %s",
          mie.getPath().isEmpty() ? "unknown" : mie.getPath().getFirst().getPropertyName(), mie.getTargetType().getSimpleName());
      case StreamReadException ignored -> "Request body is invalid JSON";
      case null, default -> "Request body is not readable";
    };

    log.error("Request not readable error [status={}, error={}, message={}]", httpStatus.value(),
        httpStatus.getReasonPhrase(), errorMessage, ex);

    return getErrorResponse(httpStatus, errorMessage);
  }

  /**
   * Global exception handler for MethodArgumentNotValidException exception.
   * Validation errors for @Valid annotated request bodies.
   *
   * @param ex the exception thrown when validation on an argument annotated with @Valid fails.
   * @return the error response and a 400 Bad Request status code.
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentException(MethodArgumentNotValidException ex) {

    // extract field errors
    String errorMessage = ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .map(error -> error.getField() + ": " + error.getDefaultMessage())
        .collect(Collectors.joining("; "));

    HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
    log.error("Request not valid error [status={}, error={}, message={}]", httpStatus.value(),
        httpStatus.getReasonPhrase(), errorMessage, ex);

    return getErrorResponse(httpStatus, errorMessage);
  }

  /**
   * Global exception handler for HttpRequestMethodNotSupportedException exception.
   * Unsupported HTTP method used in the request.
   *
   * @param ex the exception thrown when an HTTP request method is not supported by the endpoint.
   * @return the error response and a 405 Method Not Allowed status code.
   */
  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
    return handleException("HTTP request method not supported error", ex, HttpStatus.METHOD_NOT_ALLOWED);
  }

  /**
   * Global exception handler for CategoryCodeNotFoundException exception.
   * Category code provided in the request does not exist.
   *
   * @param ex the exception thrown when a category code is not found.
   * @return the error response and a 404 Not Found status code.
   */
  @ExceptionHandler(CategoryCodeNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleCategoryOfLawNotFound(CategoryCodeNotFoundException ex) {
    return handleException("Category code not found error", ex, HttpStatus.NOT_FOUND);
  }

  /**
   * Global exception handler for StartDateRequiredException exception.
   * Start date is required for fee calculation but was not provided in the request.
   *
   * @param ex the exception thrown when start date is required but not provided.
   * @return the error response and a 404 Not Found status code.
   */
  @ExceptionHandler(StartDateRequiredException.class)
  public ResponseEntity<ErrorResponse> handleStartDateNotProvided(StartDateRequiredException ex) {
    return handleException("Start date required error", ex, HttpStatus.BAD_REQUEST);
  }

  /**
   * Global exception handler for CaseConcludedDateRequiredException exception.
   * Case concluded date is required for fee calculation but was not provided in the request.
   *
   * @param ex the exception thrown when case concluded date is required but not provided.
   * @return the error response and a 404 Not Found status code.
   */
  @ExceptionHandler(CaseConcludedDateRequiredException.class)
  public ResponseEntity<ErrorResponse> handleCaseConcludedDateNotProvided(CaseConcludedDateRequiredException ex) {
    return handleException("Case concluded date required error", ex, HttpStatus.BAD_REQUEST);
  }

  /**
   * Global exception handler for DateTimeParseException exception.
   * Date string provided in the request cannot be parsed.
   *
   * @param ex the exception thrown when a date string cannot be parsed.
   * @return the error response and a 400 Bad Request status code.
   */
  @ExceptionHandler({DateTimeParseException.class})
  public ResponseEntity<ErrorResponse> handleDateTimeParsingIssue(DateTimeParseException ex) {
    return handleException("Date time parse error", ex, HttpStatus.BAD_REQUEST);
  }

  /**
   * Global exception handler for NumberFormatException exception.
   * Number string provided in the request cannot be parsed.
   *
   * @param ex the exception thrown when a number string cannot be parsed.
   * @return the error response and a 400 Bad Request status code.
   */
  @ExceptionHandler(NumberFormatException.class)
  public ResponseEntity<ErrorResponse> handleNumberException(NumberFormatException ex) {
    return handleException("Number format error", ex, HttpStatus.BAD_REQUEST);
  }

  /**
   * Global exception handler for ValidationException exception.
   *
   * @param ex the exception thrown when validation errors occur during fee calculation.
   * @return the success response with validation messages and a 200 OK status code.
   */
  @ExceptionHandler(ValidationException.class)
  public ResponseEntity<FeeCalculationResponse> handleValidationException(ValidationException ex) {
    ErrorType error = ex.getError();
    FeeContext context = ex.getContext();

    log.error("Validation error [message={}]", ex.getMessage(), ex);

    ValidationMessagesInner validationMessages = ValidationMessagesInner.builder()
        .type(ERROR)
        .code(error.getCode())
        .message(error.getMessage()).build();

    FeeCalculationResponse feeCalculationResponse = FeeCalculationResponse.builder()
        .feeCode(context.feeCode())
        .claimId(context.claimId())
        .validationMessages(List.of(validationMessages)).build();

    return ResponseEntity.ok(feeCalculationResponse);
  }

  /**
   * Global exception handler for all other exceptions.
   *
   * @param ex the exception thrown when an unexpected error occurs.
   * @return the error response and a 500 Internal Server Error status code.
   */
  @ExceptionHandler(Exception.class)
  @ResponseStatus(INTERNAL_SERVER_ERROR)
  public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
    return handleException("Unexpected error", ex, INTERNAL_SERVER_ERROR);
  }

  private ResponseEntity<ErrorResponse> handleException(String errorMessagePrefix, Throwable ex, HttpStatus httpStatus) {
    log.error("{} [status={}, error={}, message={}]", errorMessagePrefix, httpStatus.value(),
        httpStatus.getReasonPhrase(), ex.getMessage(), ex);

    return getErrorResponse(httpStatus, ex.getMessage());
  }

  private ResponseEntity<ErrorResponse> getErrorResponse(HttpStatus httpStatus, String message) {
    ErrorResponse errorResponse = new ErrorResponse()
        .timestamp(OffsetDateTime.now())
        .status(httpStatus.value())
        .error(httpStatus.getReasonPhrase())
        .message(message);

    return new ResponseEntity<>(errorResponse, httpStatus);
  }
}