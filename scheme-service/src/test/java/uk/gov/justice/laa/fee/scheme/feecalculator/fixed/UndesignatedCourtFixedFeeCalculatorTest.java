package uk.gov.justice.laa.fee.scheme.feecalculator.fixed;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.MAGISTRATES_COURT;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.YOUTH_COURT;
import static uk.gov.justice.laa.fee.scheme.enums.FeeType.FIXED;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.entity.FeeSchemesEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.enums.CourtDesignationType;
import uk.gov.justice.laa.fee.scheme.feecalculator.BaseFeeCalculatorTest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;

@ExtendWith(MockitoExtension.class)
class UndesignatedCourtFixedFeeCalculatorTest extends BaseFeeCalculatorTest {

  @InjectMocks
  UndesignatedCourtFixedFeeCalculator calculator;

  @Nested
  class UndesignatedTest {

    static Stream<Arguments> testDataMagsUndesignated() {
      return Stream.of(
          argumentsUndesignated("PROE1, Undesignated, including VAT", "PROE1", true,
              520.66, 66.78,
              50.0, 60.0, 50.0, 60.0),
          argumentsUndesignated("PROE1, Undesignated, excluding VAT", "PROE1", false,
              453.88, 0,
              50.0, 60.0, 50.0, 60.0),
          argumentsUndesignated("PROE1, Undesignated, no travel or waiting", "PROE1", true,
              388.66, 44.78,
              null, null, null, null)
      );
    }

    static Stream<Arguments> testDataYouthUndesignated() {
      return Stream.of(
          argumentsUndesignated("YOUE3, Undesignated, including VAT", "YOUE3", true,
              1355.95, 205.99,
              50.0, 60.0, 50.0, 60.0),
          argumentsUndesignated("YOUE3, Undesignated, excluding VAT", "YOUE3", false,
              1149.96, 0,
              50.0, 60.0, 50.0, 60.0),
          argumentsUndesignated("YOUE3, Undesignated, no travel or waiting", "YOUE3", true,
              1223.95, 183.99,
              null, null, null, null)
      );
    }

    private static Arguments argumentsUndesignated(String scenario, String feeCode, boolean vat, double expectedTotal,
                                                   double expectedVat, Double requestedNetTravel, Double requestedNetWaiting,
                                                   Double expectedNetTravel, Double expectedNetWaiting) {
      return Arguments.of(scenario, feeCode, vat, expectedTotal, expectedVat, requestedNetTravel,
          requestedNetWaiting, expectedNetTravel, expectedNetWaiting);
    }

    private FeeCalculationRequest buildRequestUndesignated(String feeCode, boolean vatIndicator, Double requestedNetTravel,
                                                           Double requestedNetWaiting) {
      return FeeCalculationRequest.builder()
          .feeCode(feeCode)
          .claimId("claim_123")
          .representationOrderDate(LocalDate.of(2025, 7, 29))
          .netDisbursementAmount(100.00)
          .disbursementVatAmount(20.00)
          .vatIndicator(vatIndicator)
          .netTravelCosts(requestedNetTravel)
          .netWaitingCosts(requestedNetWaiting)
          .build();
    }

    private FeeCalculationResponse buildExpectedResponseUndesignated(
        String feeCode,
        String schemeId,
        double fixedFee,
        FeeCalculationRequest request,
        boolean vatIndicator,
        double expectedTotal,
        double expectedVat,
        Double expectedNetTravel,
        Double expectedNetWaiting
    ) {
      FeeCalculation expectedCalculation = FeeCalculation.builder()
          .totalAmount(expectedTotal)
          .vatIndicator(vatIndicator)
          .vatRateApplied(vatIndicator ? 20.0 : null)
          .disbursementAmount(request.getNetDisbursementAmount())
          .requestedNetDisbursementAmount(request.getNetDisbursementAmount())
          .disbursementVatAmount(request.getDisbursementVatAmount())
          .netTravelCostsAmount(expectedNetTravel)
          .netWaitingCostsAmount(expectedNetWaiting)
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
    @MethodSource("testDataMagsUndesignated")
    void calculate_when_MagistratesCourt_Undesignated(
        String description,
        String feeCode,
        boolean vatIndicator,
        double expectedTotal,
        double expectedVat,
        Double requestedNetTravel,
        Double requestedNetWaiting,
        Double expectedNetTravel,
        Double expectedNetWaiting
    ) {

      mockVatRatesService(vatIndicator);

      FeeCalculationRequest feeCalculationRequest = buildRequestUndesignated(feeCode, vatIndicator, requestedNetTravel,
          requestedNetWaiting);

      FeeEntity feeEntity = FeeEntity.builder()
          .feeCode(feeCode)
          .feeScheme(FeeSchemesEntity.builder().schemeCode("MAGS_COURT_FS2022").build())
          .fixedFee(new BigDecimal("223.88"))
          .categoryType(MAGISTRATES_COURT)
          .courtDesignationType(CourtDesignationType.UNDESIGNATED)
          .feeType(FIXED)
          .build();

      FeeCalculationResponse response = calculator.calculate(feeCalculationRequest, feeEntity);

      FeeCalculationResponse expected = buildExpectedResponseUndesignated(feeCode, "MAGS_COURT_FS2022", 223.88,
          feeCalculationRequest, vatIndicator, expectedTotal, expectedVat, expectedNetTravel, expectedNetWaiting);

      assertThat(response).usingRecursiveComparison().isEqualTo(expected);
    }

    @ParameterizedTest()
    @MethodSource("testDataYouthUndesignated")
    void calculate_when_YouthCourt_Undesignated(
        String description,
        String feeCode,
        boolean vatIndicator,
        double expectedTotal,
        double expectedVat,
        Double requestedNetTravel,
        Double requestedNetWaiting,
        Double expectedNetTravel,
        Double expectedNetWaiting
    ) {

      mockVatRatesService(vatIndicator);

      FeeCalculationRequest feeCalculationRequest = buildRequestUndesignated(feeCode, vatIndicator, requestedNetTravel,
          requestedNetWaiting);

      FeeEntity feeEntity = FeeEntity.builder()
          .feeCode(feeCode)
          .feeScheme(FeeSchemesEntity.builder().schemeCode("YOUTH_COURT_FS2024").build())
          .fixedFee(new BigDecimal("919.96"))
          .categoryType(YOUTH_COURT)
          .courtDesignationType(CourtDesignationType.UNDESIGNATED)
          .feeType(FIXED)
          .build();

      FeeCalculationResponse response = calculator.calculate(feeCalculationRequest, feeEntity);

      FeeCalculationResponse expected = buildExpectedResponseUndesignated(feeCode, "YOUTH_COURT_FS2024", 919.96,
          feeCalculationRequest, vatIndicator, expectedTotal, expectedVat, expectedNetTravel, expectedNetWaiting);

      assertThat(response).usingRecursiveComparison().isEqualTo(expected);
    }

  }


  @Test
  void getSupportedCategories_shouldReturnEmptySet() {

    Set<CategoryType> result = calculator.getSupportedCategories();

    assertThat(result).isEmpty();
  }

}