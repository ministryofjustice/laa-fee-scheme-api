package uk.gov.justice.laa.fee.scheme.exception;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import uk.gov.justice.laa.fee.scheme.controller.FeeCalculationController;
import uk.gov.justice.laa.fee.scheme.model.ErrorResponse;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

  private GlobalExceptionHandler globalExceptionHandler;

  @BeforeEach
  void setup() {
    globalExceptionHandler = new GlobalExceptionHandler();
  }

  @Test
  void handleHttpMessageNotReadable() {
    HttpMessageNotReadableException exception = new HttpMessageNotReadableException("Duplicate field 'feeCode'", null, null);

    ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleHttpMessageNotReadable(exception);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getStatus()).isEqualTo(400);
    assertThat(response.getBody().getMessage()).contains("Duplicate field 'feeCode'");
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
    assertThat(response.getBody().getStatus()).isEqualTo(400);
    assertThat(response.getBody().getMessage()).contains("Fee code must not be blank");
  }

  @Test
  void handleCategoryCodeNotFound() {
    CategoryCodeNotFoundException exception = new CategoryCodeNotFoundException("FEE123");

    ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleCategoryOfLawNotFound(exception);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getStatus()).isEqualTo(404);
    assertThat(response.getBody().getMessage()).isEqualTo("Category of law code not found for fee code: FEE123");
  }

  @Test
  void handleFeeEntityNotfoundForScheme() {
    LocalDate date = LocalDate.of(2025, 2, 20);
    FeeNotFoundException exception = new FeeNotFoundException("FEE123", date);

    ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleFeeCodeNotfound(exception);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getStatus()).isEqualTo(404);
    assertThat(response.getBody().getMessage())
        .isEqualTo("Fee not found for fee code - FEE123, with start date - 2025-02-20");
  }

  @Test
  void handleInvalidMediationSession() {
    InvalidMediationSessionException exception = new InvalidMediationSessionException("FEE123");

    ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleInvalidMediationSession(exception);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getStatus()).isEqualTo(400);
    assertThat(response.getBody().getMessage())
        .isEqualTo("Invalid mediation session for fee code - FEE123: numberOfMediationSessions required");
  }

  @Test
  void handlePoliceStationFeeEntityNotfoundForPoliceStationId() {
    LocalDate date = LocalDate.of(2025, 2, 20);
    PoliceStationFeeNotFoundException exception = new PoliceStationFeeNotFoundException("NE021", date);

    ResponseEntity<ErrorResponse> response = globalExceptionHandler.handlePoliceStationFeeNotfound(exception);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getStatus()).isEqualTo(404);
    assertThat(response.getBody().getMessage())
        .isEqualTo("Police Station Fee not found for Police Station Id - NE021, with case start date - 2025-02-20");
  }


  @Test
  void handlePoliceStationFeeEntityNotfoundForPoliceStationSchemeId() {
    LocalDate date = LocalDate.of(2025, 2, 20);
    PoliceStationFeeNotFoundException exception = new PoliceStationFeeNotFoundException("1004");

    ResponseEntity<ErrorResponse> response = globalExceptionHandler.handlePoliceStationFeeNotfound(exception);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getStatus()).isEqualTo(404);
    assertThat(response.getBody().getMessage())
        .isEqualTo("Police Station Fee not found for Police Station Scheme Id - 1004");
  }

  @Test
  void handlePoliceStationFeeCalculationNotImplementedForPoliceStationOtherFeeCode() {
    PoliceStationFeeNotFoundException exception = new PoliceStationFeeNotFoundException("INVM", "1004");

    ResponseEntity<ErrorResponse> response = globalExceptionHandler.handlePoliceStationFeeNotfound(exception);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getStatus()).isEqualTo(404);
    assertThat(response.getBody().getMessage())
        .isEqualTo("Calculation Logic for Police Station Other Fee not implemented, Fee Code - INVM, Police Station Scheme Id - 1004");
  }
}