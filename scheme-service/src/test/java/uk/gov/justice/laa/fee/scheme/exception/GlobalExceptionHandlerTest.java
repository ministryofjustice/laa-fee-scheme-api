package uk.gov.justice.laa.fee.scheme.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.justice.laa.fee.scheme.enums.ErrorType.ERR_ALL_FEE_CODE;
import static uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner.TypeEnum.ERROR;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import uk.gov.justice.laa.fee.scheme.controller.FeeCalculationController;
import uk.gov.justice.laa.fee.scheme.model.ErrorResponse;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

  private final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

  @Test
  void handleHttpMessageNotReadable() {
    HttpMessageNotReadableException exception = new HttpMessageNotReadableException("Duplicate field 'feeCode'", null, null);

    ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleHttpMessageNotReadable(exception);

    assertErrorResponse(response, HttpStatus.BAD_REQUEST, "Duplicate field 'feeCode'");
  }

  @Test
  void handleMethodArgumentException() throws NoSuchMethodException {
    MethodParameter methodParameter = new MethodParameter(
        FeeCalculationController.class.getMethod("getFeeCalculation", FeeCalculationRequest.class), 0);

    FeeCalculationRequest request = new FeeCalculationRequest();
    BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(request, "feeCalculationRequest");
    bindingResult.rejectValue("feeCode", "NotBlank", "Fee code must not be blank");

    MethodArgumentNotValidException exception = new MethodArgumentNotValidException(methodParameter, bindingResult);

    ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleMethodArgumentException(exception);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getTimestamp()).isNotNull();
    assertThat(response.getBody().getError()).isEqualTo("Bad Request");
    assertThat(response.getBody().getStatus()).isEqualTo(400);
    assertThat(response.getBody().getMessage()).contains("Fee code must not be blank");
  }

  @Test
  void handleCategoryCodeNotFound() {
    CategoryCodeNotFoundException exception = new CategoryCodeNotFoundException("FEE123");

    ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleCategoryOfLawNotFound(exception);

    assertErrorResponse(response, HttpStatus.NOT_FOUND, "Category of law code not found for feeCode: FEE123");
  }

  @Test
  void handleValidationException() {
    FeeContext feeContext = new FeeContext("FEE123", LocalDate.of(2020, 3, 1), "claim_123");
    ValidationException exception = new ValidationException(ERR_ALL_FEE_CODE, feeContext);

    ResponseEntity<FeeCalculationResponse> response = globalExceptionHandler.handleValidationException(exception);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    FeeCalculationResponse feeCalculationResponse = response.getBody();
    assertThat(feeCalculationResponse.getFeeCode()).isEqualTo("FEE123");
    assertThat(feeCalculationResponse.getClaimId()).isEqualTo("claim_123");

    ValidationMessagesInner validationMessage = ValidationMessagesInner.builder()
        .code("ERRALL1")
        .type(ERROR)
        .message("Enter a valid Fee Code.")
        .build();
    assertThat(feeCalculationResponse.getValidationMessages()).containsExactly(validationMessage);

    assertThat(feeCalculationResponse.getFeeCalculation()).isNull();
  }

  @Test
  void handleHttpRequestMethodNotSupported() {
    HttpRequestMethodNotSupportedException exception = new HttpRequestMethodNotSupportedException("GET");

    ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleHttpRequestMethodNotSupported(exception);

    assertErrorResponse(response, HttpStatus.METHOD_NOT_ALLOWED, "Request method 'GET' is not supported");
  }

  @Test
  void handleGenericExceptionFound() {
    RuntimeException exception = new RuntimeException("some error");

    ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleGenericException(exception);

    assertErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, "some error");
  }

  @Test
  void handleStartDateNotProvided() {
    StartDateRequiredException exception = new StartDateRequiredException("FEE123");

    ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleStartDateNotProvided(exception);

    assertErrorResponse(response, HttpStatus.NOT_FOUND, "Start Date is required for feeCode: FEE123");
  }

  @Test
  void handleCaseConcludedDateNotProvided() {
    CaseConcludedDateRequiredException exception = new CaseConcludedDateRequiredException("FEE123");

    ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleCaseConcludedDateNotProvided(exception);

    assertErrorResponse(response, HttpStatus.NOT_FOUND, "Case Concluded Date is required for feeCode: FEE123");
  }

  @Test
  void handleDateTimeParsingIssue() {
    DateTimeParseException exception = new DateTimeParseException("ext '541116' could not be parsed: "
        + "Invalid value for DayOfMonth (valid values 1 - 28/31): 54", "541116", 1);

    ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleDateTimeParsingIssue(exception);

    assertErrorResponse(response,
        HttpStatus.NOT_FOUND, "ext '541116' could not be parsed: Invalid value for DayOfMonth "
            + "(valid values 1 - 28/31): 54");
  }

  private void assertErrorResponse(ResponseEntity<ErrorResponse> response, HttpStatus expectedStatus,
                                   String expectedMessage) {
    assertThat(response.getStatusCode()).isEqualTo(expectedStatus);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getTimestamp()).isNotNull();
    assertThat(response.getBody().getError()).isEqualTo(expectedStatus.getReasonPhrase());
    assertThat(response.getBody().getStatus()).isEqualTo(expectedStatus.value());
    assertThat(response.getBody().getMessage()).isEqualTo(expectedMessage);
  }
}