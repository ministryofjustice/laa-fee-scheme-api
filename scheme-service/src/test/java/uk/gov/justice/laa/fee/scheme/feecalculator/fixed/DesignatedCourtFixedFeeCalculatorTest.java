package uk.gov.justice.laa.fee.scheme.feecalculator.fixed;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.MAGISTRATES_COURT;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.YOUTH_COURT;
import static uk.gov.justice.laa.fee.scheme.enums.FeeType.FIXED;
import static uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner.TypeEnum.WARNING;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.Nested;
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
import uk.gov.justice.laa.fee.scheme.enums.CourtDesignationType;
import uk.gov.justice.laa.fee.scheme.enums.WarningType;
import uk.gov.justice.laa.fee.scheme.feecalculator.BaseFeeCalculatorTest;
import uk.gov.justice.laa.fee.scheme.feecalculator.fixed.standard.DesignatedCourtFixedFeeCalculator;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner;

@ExtendWith(MockitoExtension.class)
class DesignatedCourtFixedFeeCalculatorTest extends BaseFeeCalculatorTest {

  @InjectMocks
  DesignatedCourtFixedFeeCalculator calculator;

  @Nested
  class DesignatedTest {

    static Stream<Arguments> testDataMagsDesignated() {
      return Stream.of(
          argumentsDesignated("PROJ5, designated, including VAT", "PROJ5", true,
              463.22, 57.2),
          argumentsDesignated("PROJ5, designated, excluding VAT", "PROJ5", false,
              406.02, 0)
      );
    }

    static Stream<Arguments> testDataYouthDesignated() {
      return Stream.of(
          argumentsDesignated("YOUL1, designated, including VAT", "YOUL1", true,
              1489.4, 228.23),
          argumentsDesignated("YOUL1, designated, excluding VAT", "YOUL1", false,
              1261.17, 0)
      );
    }

    private static Arguments argumentsDesignated(String scenario, String feeCode, boolean vat, double expectedTotal,
                                                 double expectedVat) {
      return Arguments.of(scenario, feeCode, vat, expectedTotal, expectedVat);
    }

    private FeeCalculationRequest buildRequestDesignated(String feeCode, boolean vatIndicator) {
      return FeeCalculationRequest.builder()
          .feeCode(feeCode)
          .claimId("claim_123")
          .representationOrderDate(LocalDate.of(2025, 7, 29))
          .netDisbursementAmount(100.00)
          .disbursementVatAmount(20.00)
          .vatIndicator(vatIndicator)
          .caseConcludedDate(LocalDate.of(2025, 10, 30))
          .build();
    }

    private FeeCalculationResponse buildExpectedResponseDesignated(
        String feeCode,
        String schemeId,
        double fixedFee,
        FeeCalculationRequest request,
        boolean vatIndicator,
        double expectedTotal,
        double expectedVat
    ) {
      FeeCalculation expectedCalculation = FeeCalculation.builder()
          .totalAmount(expectedTotal)
          .vatIndicator(vatIndicator)
          .vatRateApplied(vatIndicator ? 20.0 : null)
          .disbursementAmount(request.getNetDisbursementAmount())
          .requestedNetDisbursementAmount(request.getNetDisbursementAmount())
          .disbursementVatAmount(request.getDisbursementVatAmount())
          .requestedDisbursementVatAmount(request.getDisbursementVatAmount())
          .fixedFeeAmount(fixedFee)
          .calculatedVatAmount(expectedVat)
          .build();

      return FeeCalculationResponse.builder()
          .feeCode(feeCode)
          .schemeId(schemeId)
          .claimId(request.getClaimId())
          .feeCalculation(expectedCalculation)
          .build();
    }

    @ParameterizedTest()
    @MethodSource("testDataMagsDesignated")
    void calculate_when_MagistratesCourt_designated(
        String description,
        String feeCode,
        boolean vatIndicator,
        double expectedTotal,
        double expectedVat
    ) {
      mockVatRatesService(vatIndicator);

      FeeCalculationRequest feeCalculationRequest = buildRequestDesignated(feeCode, vatIndicator);

      FeeEntity feeEntity = FeeEntity.builder()
          .feeCode(feeCode)
          .feeScheme(FeeSchemesEntity.builder().schemeCode("MAGS_COURT_FS2022").build())
          .fixedFee(new BigDecimal("286.02"))
          .categoryType(MAGISTRATES_COURT)
          .courtDesignationType(CourtDesignationType.DESIGNATED)
          .feeType(FIXED)
          .build();

      FeeCalculationResponse response = calculator.calculate(feeCalculationRequest, feeEntity);

      FeeCalculationResponse expected = buildExpectedResponseDesignated(feeCode, "MAGS_COURT_FS2022", 286.02,
          feeCalculationRequest, vatIndicator, expectedTotal, expectedVat);

      assertThat(response).usingRecursiveComparison().isEqualTo(expected);
    }

    @ParameterizedTest()
    @MethodSource("testDataYouthDesignated")
    void calculate_when_YouthCourt_designated(
        String description,
        String feeCode,
        boolean vatIndicator,
        double expectedTotal,
        double expectedVat
    ) {
      mockVatRatesService(vatIndicator);

      FeeCalculationRequest feeCalculationRequest = buildRequestDesignated(feeCode, vatIndicator);

      FeeEntity feeEntity = FeeEntity.builder()
          .feeCode(feeCode)
          .feeScheme(FeeSchemesEntity.builder().schemeCode("YOUTH_COURT_FS2024").build())
          .fixedFee(new BigDecimal("1141.17"))
          .categoryType(YOUTH_COURT)
          .courtDesignationType(CourtDesignationType.DESIGNATED)
          .feeType(FIXED)
          .build();

      FeeCalculationResponse response = calculator.calculate(feeCalculationRequest, feeEntity);

      FeeCalculationResponse expected = buildExpectedResponseDesignated(feeCode, "YOUTH_COURT_FS2024", 1141.17,
          feeCalculationRequest, vatIndicator, expectedTotal, expectedVat);

      assertThat(response).usingRecursiveComparison().isEqualTo(expected);
    }
  }


  @ParameterizedTest
  @CsvSource(value = {
      "false, 500, 170.13", // No VAT
      "true, 500, 180.13", // VAT applied
      "true, null, 180.13" // No escape threshold limit
  }, nullValues = "null")
  void calculate_shouldReturnFeeCalculationResponse_withDisbursementLimitWarning(boolean vatIndicator, String escapeThreshold, double expectedTotal) {
    mockVatRatesService(vatIndicator);

    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode("PROJ5")
        .startDate(LocalDate.of(2025, 5, 12))
        .vatIndicator(vatIndicator)
        .netDisbursementAmount(100.11)
        .disbursementVatAmount(22.22)
        .caseConcludedDate(LocalDate.of(2025, 5, 12))
        .build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("PROJ5")
        .feeScheme(FeeSchemesEntity.builder().schemeCode("MAGS_COURT_FS2022").build())
        .fixedFee(new BigDecimal("50.00"))
        .escapeThresholdLimit(escapeThreshold != null ? new BigDecimal(escapeThreshold) : null)
        .categoryType(MAGISTRATES_COURT)
        .build();

    FeeCalculationResponse result = calculator.calculate(feeCalculationRequest, feeEntity);

    ValidationMessagesInner validationMessage =
        ValidationMessagesInner.builder()
            .message(WarningType.WARN_DISBURSEMENT_VAT_CAPPED.getMessage())
            .code(WarningType.WARN_DISBURSEMENT_VAT_CAPPED.getCode())
            .type(WARNING)
            .build();

    assertThat(result.getValidationMessages()).containsExactly(validationMessage);
    assertThat(result).isNotNull();
    assertThat(result.getFeeCode()).isEqualTo("PROJ5");
    assertThat(result.getFeeCalculation()).isNotNull();
    assertThat(result.getFeeCalculation().getTotalAmount()).isEqualTo(expectedTotal);
  }

  @Test
  void getSupportedCategories_shouldReturnEmptySet() {

    Set<CategoryType> result = calculator.getSupportedCategories();

    assertThat(result).isEmpty();
  }
}