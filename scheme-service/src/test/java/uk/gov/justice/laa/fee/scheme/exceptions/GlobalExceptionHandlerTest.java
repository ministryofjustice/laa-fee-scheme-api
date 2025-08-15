package uk.gov.justice.laa.fee.scheme.exceptions;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.justice.laa.fee.scheme.model.ErrorResponse;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

  private GlobalExceptionHandler globalExceptionHandler;

  @BeforeEach
  void setup() {
    globalExceptionHandler = new GlobalExceptionHandler();
  }

  @Test
  void handleCategoryCodeNotFound() {
    CategoryCodeNotFoundException exception = new CategoryCodeNotFoundException("FEE123");

    ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleCategoryOfLawNotFound(exception);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getStatus()).isEqualTo(404);
    assertThat(response.getBody().getMessage()).isEqualTo("Category of code not found for fee: FEE123");
  }

  @Test
  void handleFeeSchemeNotFoundForDate() {
    LocalDate date = LocalDate.of(2025, 2, 20);

    FeeSchemeNotFoundForDateException exception = new FeeSchemeNotFoundForDateException("FEE123", date);

    ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleFeeSchemeNotFoundForDate(exception);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getStatus()).isEqualTo(404);
    assertThat(response.getBody().getMessage()).isEqualTo(String.format("No fee scheme found for fee FEE123, with date %s", date));
  }

  @Test
  void handleFeeEntityNotfoundForScheme() {
    FeeEntityNotFoundException exception = new FeeEntityNotFoundException("FEE123", "schemeId");

    ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleFeeEntityNotfoundForScheme(exception);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getStatus()).isEqualTo(404);
    assertThat(response.getBody().getMessage()).isEqualTo("Fee entity not found for fee FEE123, and schemeId schemeId");
  }
}