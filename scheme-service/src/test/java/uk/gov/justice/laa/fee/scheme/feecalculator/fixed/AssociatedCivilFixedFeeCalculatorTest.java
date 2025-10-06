package uk.gov.justice.laa.fee.scheme.feecalculator.fixed;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.ASSOCIATED_CIVIL;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.entity.FeeSchemesEntity;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;

@ExtendWith(MockitoExtension.class)
class AssociatedCivilFixedFeeCalculatorTest {

  @InjectMocks
  AssociatedCivilFixedFeeCalculator associatedCivilFixedFeeCalculator;

  @Test
  void calculate_shouldReturnFeeCalculationResponse() {
    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode("ASMS")
        .claimId("claim_123")
        .uniqueFileNumber("020416/001")
        .vatIndicator(true)
        .netDisbursementAmount(100.11)
        .disbursementVatAmount(20.22)
        .build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("ASMS")
        .feeScheme(FeeSchemesEntity.builder().schemeCode("ASSOC_FS2016").build())
        .fixedFee(new BigDecimal("50.00"))
        .categoryType(ASSOCIATED_CIVIL)
        .build();

    FeeCalculationResponse result = associatedCivilFixedFeeCalculator.calculate(feeCalculationRequest, feeEntity);

    assertThat(result).isNotNull();
    assertThat(result.getFeeCode()).isEqualTo("ASMS");
    assertThat(result.getClaimId()).isEqualTo("claim_123");
    assertThat(result.getSchemeId()).isEqualTo("ASSOC_FS2016");
    assertThat(result.getEscapeCaseFlag()).isFalse();

    FeeCalculation feeCalculation = result.getFeeCalculation();
    assertThat(feeCalculation).isNotNull();
    assertThat(feeCalculation.getTotalAmount()).isEqualTo(180.33);
    assertThat(feeCalculation.getVatIndicator()).isTrue();
    assertThat(feeCalculation.getVatRateApplied()).isEqualTo(20.0);
    assertThat(feeCalculation.getCalculatedVatAmount()).isEqualTo(10.0);
    assertThat(feeCalculation.getDisbursementAmount()).isEqualTo(100.11);
    assertThat(feeCalculation.getRequestedNetDisbursementAmount()).isEqualTo(100.11);
    assertThat(feeCalculation.getDisbursementVatAmount()).isEqualTo(20.22);
    assertThat(feeCalculation.getFixedFeeAmount()).isEqualTo(50);
  }
}