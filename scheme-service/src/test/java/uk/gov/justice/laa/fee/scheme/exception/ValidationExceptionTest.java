package uk.gov.justice.laa.fee.scheme.exception;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.fee.scheme.enums.ErrorType;

class ValidationExceptionTest {

  @Test
  public void testToString() {
    FeeContext feeContext = new FeeContext("FEE1", LocalDate.of(2015,1,1), "FeeScheme", "claim_123");

    ValidationException validationException = new ValidationException(ErrorType.ERR_ALL_FEE_CODE, feeContext);

    assertThat(validationException.toString())
        .contains("[error=ERR_ALL_FEE_CODE,context=FeeContext[feeCode=FEE1, startDate=2015-01-01, schemeId=FeeScheme, claimId=claim_123]]");
  }
}