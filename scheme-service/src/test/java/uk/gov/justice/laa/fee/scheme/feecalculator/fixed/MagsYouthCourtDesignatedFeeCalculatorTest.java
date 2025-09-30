package uk.gov.justice.laa.fee.scheme.feecalculator.fixed;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.MAGS_COURT_UNDESIGNATED;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.YOUTH_COURT_UNDESIGNATED;
import static uk.gov.justice.laa.fee.scheme.enums.FeeType.FIXED;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.entity.FeeSchemesEntity;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;

@ExtendWith(MockitoExtension.class)
class MagsYouthCourtDesignatedFeeCalculatorTest {

  @InjectMocks
  MagsYouthCourtDesignatedFeeCalculator calculator;

  static Stream<Arguments> testDataMags() {
    return Stream.of(
        arguments("PROJ5, designated, including VAT", "PROJ5", true, 463.22, 57.2),
        arguments("PROJ5, designated, excluding VAT", "PROJ5", false, 406.02, 0)
    );
  }

  static Stream<Arguments> testDataYouth() {
    return Stream.of(
        arguments("YOUL1, designated, including VAT", "YOUL1", true, 1489.4, 228.23),
        arguments("YOUL1, designated, excluding VAT", "YOUL1", false, 1261.17, 0)
    );
  }

  private static Arguments arguments(String scenario, String feeCode, boolean vat, double expectedTotal, double expectedVat) {
    return Arguments.of(scenario, feeCode, vat, expectedTotal, expectedVat);
  }

  private FeeCalculationRequest buildRequest(String feeCode, boolean vatIndicator) {
    return FeeCalculationRequest.builder()
        .feeCode(feeCode)
        .claimId("claim_123")
        .startDate(LocalDate.of(2025, 7, 29))
        .netDisbursementAmount(100.00)
        .disbursementVatAmount(20.00)
        .vatIndicator(vatIndicator)
        .build();
  }

  private FeeCalculationResponse buildExpectedResponse(
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
        .vatRateApplied(20.0)
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
  @MethodSource("testDataMags")
  void calculate_when_MagistratesCourt_Undesignated(
      String description,
      String feeCode,
      boolean vatIndicator,
      double expectedTotal,
      double expectedVat
  ) {
    FeeCalculationRequest feeCalculationRequest = buildRequest(feeCode, vatIndicator);

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode(feeCode)
        .feeSchemeCode(FeeSchemesEntity.builder().schemeCode("MAGS_COURT_FS2022").build())
        .fixedFee(new BigDecimal("286.02"))
        .categoryType(MAGS_COURT_UNDESIGNATED)
        .feeType(FIXED)
        .build();

    FeeCalculationResponse response = calculator.calculate(feeCalculationRequest, feeEntity);

    FeeCalculationResponse expected = buildExpectedResponse(feeCode, "MAGS_COURT_FS2022", 286.02,
        feeCalculationRequest, vatIndicator, expectedTotal, expectedVat);

    assertThat(response).usingRecursiveComparison().isEqualTo(expected);
  }

  @ParameterizedTest()
  @MethodSource("testDataYouth")
  void calculate_when_YouthCourt_Undesignated(
      String description,
      String feeCode,
      boolean vatIndicator,
      double expectedTotal,
      double expectedVat
  ) {
    FeeCalculationRequest feeCalculationRequest = buildRequest(feeCode, vatIndicator);

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode(feeCode)
        .feeSchemeCode(FeeSchemesEntity.builder().schemeCode("YOUTH_COURT_FS2024").build())
        .fixedFee(new BigDecimal("1141.17"))
        .categoryType(YOUTH_COURT_UNDESIGNATED)
        .feeType(FIXED)
        .build();

    FeeCalculationResponse response = calculator.calculate(feeCalculationRequest, feeEntity);

    FeeCalculationResponse expected = buildExpectedResponse(feeCode, "YOUTH_COURT_FS2024", 1141.17,
        feeCalculationRequest, vatIndicator, expectedTotal, expectedVat);

    assertThat(response).usingRecursiveComparison().isEqualTo(expected);
  }
}