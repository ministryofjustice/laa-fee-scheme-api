package uk.gov.justice.laa.fee.scheme.feecalculator.fixed;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.IMMIGRATION_ASYLUM;
import static uk.gov.justice.laa.fee.scheme.enums.FeeType.FIXED;
import static uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner.TypeEnum.WARNING;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.entity.FeeSchemesEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.model.BoltOnFeeDetails;
import uk.gov.justice.laa.fee.scheme.model.BoltOnType;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner;

@ExtendWith(MockitoExtension.class)
class ImmigrationAsylumFixedFeeCalculatorTest {

  @InjectMocks
  ImmigrationAsylumFixedFeeCalculator immigrationAsylumFixedFeeCalculator;

  private static final String WARNING_CODE_DESCRIPTION = "123"; // clarify what description should be

  public static Stream<Arguments> testDataWithDisbursement() {
    return Stream.of(
        arguments("IACA, Has Vat, eligible for disbursement, below limit",
            "IACA", true, null, 399, 50, BigDecimal.valueOf(600),
            50, 50, 1274.0, 399, 512,
            137.5, 75.5),
        arguments("IACA, Has Vat, eligible for disbursement, above limit, with prior auth",
            "IACA", true, "hasPriorAuth", 800, 100, BigDecimal.valueOf(600),
            50, 50, 1725.0, 800, 512,
            137.5, 75.5),
        arguments("IACA, Has Vat, eligible for disbursement, above limit, without prior auth",
            "IACA", true, null, 800, 100, BigDecimal.valueOf(600),
            50, 50, 1525.0, 600, 512,
            137.5, 75.5),
        arguments("IACA, No Vat, eligible for disbursement, below limit",
            "IACA", false, null, 399, 50, BigDecimal.valueOf(600),
            50, 50, 1136.50, 399, 512,
            0, 75.5),
        arguments("IACA, No Vat, eligible for disbursement, above limit, with prior auth",
            "IACA", false, "hasPriorAuth", 800, 100, BigDecimal.valueOf(600),
            50, 50, 1587.50, 800, 512,
            0, 75.5),
        arguments("IACA, No Vat, eligible for disbursement, above limit, without prior auth",
            "IACA", false, null, 800, 100, BigDecimal.valueOf(600),
            50, 50, 1387.50, 600, 512,
            0, 75.5)
    );
  }

  private static Arguments arguments(String scenario, String feeCode, boolean vat, String priorAuthority, double netDisbursementAmount,
                                     double disbursementVatAmount, BigDecimal netDisbursementLimit, double detentionAndWaitingCosts,
                                     double jrFormfilling, double total, double expectedDisbursementAmount,
                                     double expectedTotalBoltOnFeeAmount, double expectedCalculatedVatAmount,
                                     double expectedFixedFeeAmount) {
    return Arguments.of(scenario, feeCode, vat, priorAuthority, netDisbursementAmount, disbursementVatAmount, netDisbursementLimit,
        detentionAndWaitingCosts, jrFormfilling, total, expectedDisbursementAmount, expectedTotalBoltOnFeeAmount,
        expectedCalculatedVatAmount, expectedFixedFeeAmount);
  }

  @ParameterizedTest
  @MethodSource("testDataWithDisbursement")
  void calculate_whenImmigrationAndAsylum_withDisbursement(
      String description,
      String feeCode,
      boolean vatIndicator,
      String immigrationPriorityAuthority,
      double netDisbursementAmount,
      double disbursementVatAmount,
      BigDecimal netDisbursementLimit,
      double detentionAndWaitingCosts,
      double jrFormfilling,
      double expectedTotal,
      double expectedDisbursementAmount,
      double expectedTotalBoltOnFeeAmount,
      double expectedCalculatedVat,
      double expectedFixedFee) {

    FeeCalculationRequest feeData = FeeCalculationRequest.builder()
        .feeCode(feeCode)
        .claimId("claim_123")
        .startDate(LocalDate.of(2025, 7, 29))
        .netDisbursementAmount(netDisbursementAmount)
        .disbursementVatAmount(disbursementVatAmount)
        .vatIndicator(vatIndicator)
        .immigrationPriorAuthorityNumber(immigrationPriorityAuthority)
        .boltOns(BoltOnType.builder()
            .boltOnCmrhOral(2)
            .boltOnCmrhTelephone(2)
            .build())
        .detentionAndWaitingCosts(detentionAndWaitingCosts)
        .jrFormFilling(jrFormfilling)
        .build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode(feeCode)
        .feeSchemeCode(FeeSchemesEntity.builder().schemeCode("IMM_ASYLM_FS2023").build())
        .fixedFee(new BigDecimal("75.50"))
        .categoryType(IMMIGRATION_ASYLUM)
        .feeType(FIXED)
        .disbursementLimit(netDisbursementLimit)
        .oralCmrhBoltOn(BigDecimal.valueOf(166))
        .telephoneCmrhBoltOn(BigDecimal.valueOf(90))
        .build();

    FeeCalculationResponse response = immigrationAsylumFixedFeeCalculator.calculate(feeData, feeEntity);

    FeeCalculation expectedCalculation = FeeCalculation.builder()
        .totalAmount(expectedTotal)
        .vatIndicator(vatIndicator)
        .vatRateApplied(20.0)
        .disbursementAmount(expectedDisbursementAmount)
        .requestedNetDisbursementAmount(feeData.getNetDisbursementAmount())
        .disbursementVatAmount(disbursementVatAmount)
        .detentionAndWaitingCostsAmount(detentionAndWaitingCosts)
        .jrFormFillingAmount(jrFormfilling)
        .boltOnFeeDetails(BoltOnFeeDetails.builder()
            .boltOnTotalFeeAmount(expectedTotalBoltOnFeeAmount)
            .boltOnCmrhOralCount(2)
            .boltOnCmrhOralFee(332.0)
            .boltOnCmrhTelephoneCount(2)
            .boltOnCmrhTelephoneFee(180.0)
            .build())
        .fixedFeeAmount(expectedFixedFee)
        .calculatedVatAmount(expectedCalculatedVat)
        .build();

    FeeCalculationResponse expectedResponse = FeeCalculationResponse.builder()
        .feeCode(feeCode)
        .schemeId("IMM_ASYLM_FS2023")
        .claimId("claim_123")
        .validationMessages(new ArrayList<>())
        .escapeCaseFlag(false) // hardcoded till escape logic implemented
        .feeCalculation(expectedCalculation)
        .build();

    assertThat(response)
        .usingRecursiveComparison()
        .isEqualTo(expectedResponse);
  }

  @ParameterizedTest
  @CsvSource({
      "IDAS1, true, 90.60",
      "IDAS1, false, 75.5",
      "IDAS2, true, 90.60",
      "IDAS2, false, 75.5"
  })
  void calculate_whenImmigrationAndAsylum_withoutDisbursement(String feeCode, boolean vatIndicator,
                                                              double expectedTotal) {

    FeeCalculationRequest feeData = FeeCalculationRequest.builder()
        .feeCode(feeCode)
        .claimId("claim_123")
        .startDate(LocalDate.of(2025, 7, 29))
        .netDisbursementAmount(null)
        .disbursementVatAmount(null)
        .vatIndicator(vatIndicator)
        .immigrationPriorAuthorityNumber(null)
        .boltOns(BoltOnType.builder().build())
        .build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode(feeCode)
        .feeSchemeCode(FeeSchemesEntity.builder().schemeCode("IMM_ASYLM_FS2023").build())
        .fixedFee(new BigDecimal("75.50"))
        .categoryType(IMMIGRATION_ASYLUM)
        .feeType(FIXED)
        .disbursementLimit(null)
        .build();

    FeeCalculationResponse response = immigrationAsylumFixedFeeCalculator.calculate(feeData, feeEntity);

    ValidationMessagesInner validationMessage = ValidationMessagesInner.builder()
        .message(WARNING_CODE_DESCRIPTION)
        .type(WARNING)
        .build();

    assertNotNull(response.getFeeCalculation());
    assertThat(response.getFeeCode()).isEqualTo(feeCode);
    assertThat(response.getFeeCalculation().getTotalAmount()).isEqualTo(expectedTotal);
    assertThat(response.getValidationMessages().getFirst()).isEqualTo(validationMessage);
  }

  @Test
  void getSupportedCategories_shouldReturnEmptySet() {
    Set<CategoryType> result = immigrationAsylumFixedFeeCalculator.getSupportedCategories();

    assertThat(result).isEmpty();
  }
}
