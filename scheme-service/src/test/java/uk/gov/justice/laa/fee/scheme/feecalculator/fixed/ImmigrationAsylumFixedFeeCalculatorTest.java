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

  public static Stream<Arguments> testDataWithDisbursementWithinLimit() {
    return Stream.of(
        arguments("IACA, Has Vat, eligible for disbursement, below limit",
            "IACA", true, null, 399, 50, BigDecimal.valueOf(600),
            50, 50, 1274.0, 399, 512,
            137.5, 75.5),
        arguments("IACA, Has Vat, eligible for disbursement, above limit, with prior auth",
            "IACA", true, "hasPriorAuth", 800, 100, BigDecimal.valueOf(600),
            50, 50, 1725.0, 800, 512,
            137.5, 75.5),
        arguments("IACA, No Vat, eligible for disbursement, below limit",
            "IACA", false, null, 399, 50, BigDecimal.valueOf(600),
            50, 50, 1136.50, 399, 512,
            0, 75.5),
        arguments("IACA, No Vat, eligible for disbursement, above limit, with prior auth",
            "IACA", false, "hasPriorAuth", 800, 100, BigDecimal.valueOf(600),
            50, 50, 1587.50, 800, 512,
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
  @MethodSource("testDataWithDisbursementWithinLimit")
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
        .feeScheme(FeeSchemesEntity.builder().schemeCode("IMM_ASYLM_FS2023").build())
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
      "IDAS1, 1110.6, 1000.0",
      "IDAS2, 1110.6, 1000.0"
  })
  void calculate_whenImmigrationAndAsylum_withNoDisbursementLimit(String feeCode, double expectedTotal,
                                                                      double requestedDisbursementAmount) {

    FeeCalculationRequest feeCalculationRequest = buildRequest(feeCode, requestedDisbursementAmount);
    FeeEntity feeEntity = buildFeeEntity(feeCode, BigDecimal.valueOf(75.5), null);
    FeeCalculationResponse response = immigrationAsylumFixedFeeCalculator.calculate(feeCalculationRequest, feeEntity);

    assertNotNull(response.getFeeCalculation());
    assertThat(response.getValidationMessages()).isEqualTo(new ArrayList<>());
    assertThat(response.getFeeCode()).isEqualTo(feeCode);
    assertThat(response.getFeeCalculation().getTotalAmount()).isEqualTo(expectedTotal);
  }

  @ParameterizedTest
  @CsvSource({
      "IALB, 510.6, 1000.0, 400.0, TEMPORARY WARIA 2 MESSAGE",
      "IMLB, 510.6, 1000.0, 400.0, TEMPORARY WARIA 2 MESSAGE",
      "IACE, 710.6, 1000.0, 600.0, TEMPORARY WARIA 1 MESSAGE"
  })
  void calculate_whenImmigrationAndAsylum_withDisbursementBeyondLimit(String feeCode, double expectedTotal,
                                                                      double requestedDisbursementAmount,
                                                                      double disbursementLimit, String warningMessage) {

    FeeCalculationRequest feeCalculationRequest = buildRequest(feeCode, requestedDisbursementAmount);
    FeeEntity feeEntity = buildFeeEntity(feeCode, BigDecimal.valueOf(75.5), BigDecimal.valueOf(disbursementLimit));
    FeeCalculationResponse response = immigrationAsylumFixedFeeCalculator.calculate(feeCalculationRequest, feeEntity);

    ValidationMessagesInner validationMessage = ValidationMessagesInner.builder()
        .message(warningMessage)
        .type(WARNING)
        .build();

    assertNotNull(response.getFeeCalculation());
    assertThat(response.getValidationMessages()).containsExactly(validationMessage);
    assertThat(response.getFeeCode()).isEqualTo(feeCode);
    assertThat(response.getFeeCalculation().getTotalAmount()).isEqualTo(expectedTotal);
  }

  @Test
  void getSupportedCategories_shouldReturnEmptySet() {
    Set<CategoryType> result = immigrationAsylumFixedFeeCalculator.getSupportedCategories();
    assertThat(result).isEmpty();
  }

  private FeeCalculationRequest buildRequest(String feeCode, double netDisbursementAmount) {
    return FeeCalculationRequest.builder()
        .feeCode(feeCode)
        .claimId("claim_123")
        .startDate(LocalDate.of(2025, 7, 29))
        .netDisbursementAmount(netDisbursementAmount)
        .disbursementVatAmount(20.0)
        .vatIndicator(true)
        .immigrationPriorAuthorityNumber(null)
        .build();
  }

  private FeeEntity buildFeeEntity(String feeCode, BigDecimal fixedFee, BigDecimal disbursementLimit) {
    return FeeEntity.builder()
        .feeCode(feeCode)
        .feeScheme(FeeSchemesEntity.builder().schemeCode("IMM_ASYLM_FS2023").build())
        .fixedFee(fixedFee)
        .categoryType(IMMIGRATION_ASYLUM)
        .feeType(FIXED)
        .disbursementLimit(disbursementLimit)
        .build();
  }
}
