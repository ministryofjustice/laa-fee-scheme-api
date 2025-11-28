package uk.gov.justice.laa.fee.scheme.feecalculator.disbursement;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.MENTAL_HEALTH;
import static uk.gov.justice.laa.fee.scheme.enums.FeeType.DISB_ONLY;

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
class MentalHealthDisbursementOnlyCalculatorTest {

  @InjectMocks
  MentalHealthDisbursementOnlyCalculator mentalHealthDisbursementOnlyCalculator;


  @Test
  void calculate_whenMentalHealthClaimSubmittedOnlyForDisbursement_returnSuccess() {

    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode("MHLDIS")
        .claimId("claim_123")
        .startDate(LocalDate.of(2025, 7, 29))
        .netDisbursementAmount(1200.0)
        .disbursementVatAmount(150.0)
        .build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("MHLDIS")
        .feeScheme(FeeSchemesEntity.builder().schemeCode("MHL_DISB_FS2013").build())
        .categoryType(MENTAL_HEALTH)
        .feeType(DISB_ONLY)
        .build();

    FeeCalculationResponse response = mentalHealthDisbursementOnlyCalculator.calculate(feeCalculationRequest, feeEntity);

    FeeCalculation expectedCalculation = FeeCalculation.builder()
        .totalAmount(1350.0)
        .disbursementAmount(1200.0)
        .requestedNetDisbursementAmount(1200.0)
        .disbursementVatAmount(150.0)
        .build();

    FeeCalculationResponse expectedResponse = FeeCalculationResponse.builder()
        .feeCode("MHLDIS")
        .schemeId("MHL_DISB_FS2013")
        .claimId("claim_123")
        .feeCalculation(expectedCalculation)
        .build();

    assertThat(response)
        .usingRecursiveComparison()
        .isEqualTo(expectedResponse);
  }

}