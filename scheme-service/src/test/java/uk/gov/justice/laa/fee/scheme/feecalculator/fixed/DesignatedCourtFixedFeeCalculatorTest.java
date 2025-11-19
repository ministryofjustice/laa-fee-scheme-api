package uk.gov.justice.laa.fee.scheme.feecalculator.fixed;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.MAGISTRATES_COURT;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.YOUTH_COURT;
import static uk.gov.justice.laa.fee.scheme.enums.FeeType.FIXED;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.stream.Stream;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.entity.FeeSchemesEntity;
import uk.gov.justice.laa.fee.scheme.enums.CourtDesignationType;
import uk.gov.justice.laa.fee.scheme.feecalculator.BaseFeeCalculatorTest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;

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
    void calculate_when_MagistratesCourt_Undesignated(
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
    void calculate_when_YouthCourt_Undesignated(
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

}