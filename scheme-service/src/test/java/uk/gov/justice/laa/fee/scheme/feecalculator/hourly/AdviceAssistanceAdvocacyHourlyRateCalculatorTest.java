package uk.gov.justice.laa.fee.scheme.feecalculator.hourly;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.ADVICE_ASSISTANCE_ADVOCACY;
import static uk.gov.justice.laa.fee.scheme.enums.FeeType.HOURLY;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.entity.FeeSchemesEntity;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.service.VatRatesService;

@ExtendWith(MockitoExtension.class)
class AdviceAssistanceAdvocacyHourlyRateCalculatorTest {

  @Mock
  VatRatesService vatRatesService;

  @InjectMocks
  AdviceAssistanceAdvocacyHourlyRateCalculator adviceAssistanceAdvocacyHourlyRateCalculator;

  public static Stream<Arguments> testData() {
    return Stream.of(
        arguments("PROD, with VAT", "PROD", true, 100.0, 20.0, 290,
            180, 120, 118.0, 590.0, 828.0),
        arguments("PROD, without VAT", "PROD", false, 100.0, 20.0, 190,
            110, 150, 0.0, 450.0, 570.0)
    );
  }

  private static Arguments arguments(String scenario, String feeCode, boolean vat,
                                     double netDisbursementAmount, double disbursementVatAmount, double requestedProfitCosts,
                                     double requestedTravelCosts, double requestedWaitingCosts,
                                     double calculatedVatAmount, double hourlyTotalAmount, double expectedTotal) {
    return Arguments.of(scenario, feeCode, vat, netDisbursementAmount, disbursementVatAmount, requestedProfitCosts,
        requestedTravelCosts, requestedWaitingCosts, calculatedVatAmount, hourlyTotalAmount,
        expectedTotal);
  }

  @ParameterizedTest
  @MethodSource("testData")
  void calculate_whenAdviceAssistanceAdvocacy(
      String description,
      String feeCode,
      boolean vatIndicator,
      double netDisbursementAmount,
      double disbursementVatAmount,
      double requestedProfitCosts,
      double requestedTravelCosts,
      double requestedWaitingCosts,
      double calculatedVatAmount,
      double hourlyTotalAmount,
      double expectedTotal
  ) {

    mockVatRatesService(vatIndicator);

    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode(feeCode)
        .claimId("claim_123")
        .caseConcludedDate(LocalDate.of(2024, 7, 29))
        .netProfitCosts(requestedProfitCosts)
        .netDisbursementAmount(netDisbursementAmount)
        .disbursementVatAmount(disbursementVatAmount)
        .vatIndicator(vatIndicator)
        .netTravelCosts(requestedTravelCosts)
        .netWaitingCosts(requestedWaitingCosts)
        .build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode(feeCode)
        .feeScheme(FeeSchemesEntity.builder().schemeCode("AAA_FS2016").build())
        .categoryType(ADVICE_ASSISTANCE_ADVOCACY)
        .feeType(HOURLY)
        .build();

    FeeCalculation expectedCalculation = FeeCalculation.builder()
        .totalAmount(expectedTotal)
        .vatIndicator(vatIndicator)
        .vatRateApplied(vatIndicator ? 20.0 : null)
        .calculatedVatAmount(calculatedVatAmount)
        .disbursementAmount(netDisbursementAmount)
        .requestedNetDisbursementAmount(netDisbursementAmount)
        .disbursementVatAmount(disbursementVatAmount)
        .hourlyTotalAmount(hourlyTotalAmount)
        .netProfitCostsAmount(requestedProfitCosts)
        .requestedNetProfitCostsAmount(requestedProfitCosts)
        .netTravelCostsAmount(requestedTravelCosts)
        .netWaitingCostsAmount(requestedWaitingCosts)
        .build();


    FeeCalculationResponse response = adviceAssistanceAdvocacyHourlyRateCalculator.calculate(feeCalculationRequest, feeEntity);
    FeeCalculationResponse expectedResponse = FeeCalculationResponse.builder()
        .feeCode(feeCode)
        .schemeId("AAA_FS2016")
        .claimId("claim_123")
        .validationMessages(new ArrayList<>())
        .feeCalculation(expectedCalculation)
        .build();

    assertThat(response)
        .usingRecursiveComparison()
        .isEqualTo(expectedResponse);
  }

  private void mockVatRatesService(Boolean vatIndicator) {
    when(vatRatesService.getVatRateForDate(any(), any()))
        .thenReturn(vatIndicator ? new BigDecimal("20.00") : BigDecimal.ZERO);
  }
}