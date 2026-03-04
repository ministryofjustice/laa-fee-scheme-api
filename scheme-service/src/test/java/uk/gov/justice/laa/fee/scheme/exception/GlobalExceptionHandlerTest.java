package uk.gov.justice.laa.fee.scheme.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.justice.laa.fee.scheme.enums.ErrorType.ERR_ALL_FEE_CODE;
import static uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner.TypeEnum.ERROR;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
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

@ExtendWith(value = {MockitoExtension.class, OutputCaptureExtension.class})
class GlobalExceptionHandlerTest {

  private final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

  @Test
  void handleHttpMessageNotReadable(CapturedOutput capturedOutput) {
    HttpMessageNotReadableException exception = new HttpMessageNotReadableException("Duplicate field 'feeCode'", null, null);

    ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleHttpMessageNotReadable(exception);

    assertErrorResponse(response, HttpStatus.BAD_REQUEST, "Duplicate field 'feeCode'");
    assertThat(capturedOutput.getOut())
        .contains("Request not readable error [status=400, error=Bad Request, message=Duplicate field 'feeCode']");
  }

  @Test
  void handleMethodArgumentException(CapturedOutput capturedOutput) throws NoSuchMethodException {
    MethodParameter methodParameter = new MethodParameter(
        FeeCalculationController.class.getMethod("getFeeCalculation", FeeCalculationRequest.class), 0);

    BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new FeeCalculationRequest(), "feeCalculationRequest");
    bindingResult.rejectValue("feeCode", "NotBlank", "must not be blank");

    MethodArgumentNotValidException exception = new MethodArgumentNotValidException(methodParameter, bindingResult);

    ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleMethodArgumentException(exception);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getTimestamp()).isNotNull();
    assertThat(response.getBody().getError()).isEqualTo("Bad Request");
    assertThat(response.getBody().getStatus()).isEqualTo(400);
    assertThat(response.getBody().getMessage()).isEqualTo("feeCode: must not be blank");
    assertThat(capturedOutput.getOut())
        .contains("Request not valid error [status=400, error=Bad Request, message=feeCode: must not be blank]");
  }

  @Test
  void handleCategoryCodeNotFound(CapturedOutput capturedOutput) {
    CategoryCodeNotFoundException exception = new CategoryCodeNotFoundException("FEE123");

    ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleCategoryOfLawNotFound(exception);

    assertErrorResponse(response, HttpStatus.NOT_FOUND, "Category of law code not found for feeCode: FEE123");
    assertThat(capturedOutput.getOut())
        .contains("Category code not found error [status=404, error=Not Found, message=Category of law code not found for feeCode: FEE123]");
  }

  @Test
  void handleValidationException(CapturedOutput capturedOutput) {
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

    assertThat(capturedOutput.getOut()).contains("Validation error [message=ERRALL1 - Enter a valid Fee Code.]");
  }

  @Test
  void handleHttpRequestMethodNotSupported(CapturedOutput capturedOutput) {
    HttpRequestMethodNotSupportedException exception = new HttpRequestMethodNotSupportedException("GET");

    ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleHttpRequestMethodNotSupported(exception);

    assertErrorResponse(response, HttpStatus.METHOD_NOT_ALLOWED, "Request method 'GET' is not supported");
    assertThat(capturedOutput.getOut())
        .contains("HTTP request method not supported error [status=405, error=Method Not Allowed, message=Request method 'GET' is not supported]");
  }

  @Test
  void handleGenericExceptionFound(CapturedOutput capturedOutput) {
    RuntimeException exception = new RuntimeException("some error");

    ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleGenericException(exception);

    assertErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, "some error");
    assertThat(capturedOutput.getOut())
        .contains("Unexpected error [status=500, error=Internal Server Error, message=some error]");
  }

  @Test
  void handleStartDateNotProvided(CapturedOutput capturedOutput) {
    StartDateRequiredException exception = new StartDateRequiredException("FEE123");

    ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleStartDateNotProvided(exception);

    assertErrorResponse(response, HttpStatus.BAD_REQUEST, "Start Date is required for feeCode: FEE123");
    assertThat(capturedOutput.getOut())
        .contains("Start date required error [status=400, error=Bad Request, message=Start Date is required for feeCode: FEE123]");
  }

  @Test
  void handleCaseConcludedDateNotProvided(CapturedOutput capturedOutput) {
    CaseConcludedDateRequiredException exception = new CaseConcludedDateRequiredException("FEE123");

    ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleCaseConcludedDateNotProvided(exception);

    assertErrorResponse(response, HttpStatus.BAD_REQUEST, "Case Concluded Date is required for feeCode: FEE123");
    assertThat(capturedOutput.getOut())
        .contains("Case concluded date required error [status=400, error=Bad Request, message=Case Concluded Date is required for feeCode: FEE123]");
  }

  @Test
  void handleDateTimeParsingIssue(CapturedOutput capturedOutput) {
    DateTimeParseException exception = new DateTimeParseException("ext '541116' could not be parsed: "
                                                                  + "Invalid value for DayOfMonth (valid values 1 - 28/31): 54", "541116", 1);

    ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleDateTimeParsingIssue(exception);

    assertErrorResponse(response,
        HttpStatus.BAD_REQUEST, "ext '541116' could not be parsed: Invalid value for DayOfMonth "
                                + "(valid values 1 - 28/31): 54");
    assertThat(capturedOutput.getOut())
        .contains("Date time parse error [status=400, error=Bad Request, message=ext '541116' could not be parsed: "
                  + "Invalid value for DayOfMonth (valid values 1 - 28/31): 54]");
  }

  @Test
  void handleNumberFormatException(CapturedOutput capturedOutput) {
    NumberFormatException exception = new NumberFormatException("For input string: \"6/\"");

    ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleNumberException(exception);

    assertErrorResponse(response, HttpStatus.BAD_REQUEST, "For input string: \"6/\"");
    assertThat(capturedOutput.getOut())
        .contains("Number format error [status=400, error=Bad Request, message=For input string: \"6/\"]");
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