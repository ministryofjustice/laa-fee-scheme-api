package uk.gov.justice.laa.fee.scheme.feecalculator;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.justice.laa.fee.scheme.feecalculator.CalculationType.IMMIGRATION_ASYLUM_HOURLY_RATE;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.entity.FeeSchemesEntity;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;

class ImmigrationAsylumHourlyRateCalculatorTest {

  static Stream<Arguments> feeTestData() {
    return Stream.of(
        Arguments.of("IAXL", true, null, 166.25, 800.00,        // IAXL under profit costs limit
            429.02, 46.83, 234.14, 166.25, List.of()),
        Arguments.of("IAXL", true, "priorAuth", 919.16, 800.00, // IAXL over profit costs limit with prior auth
            1332.51, 197.41, 987.05, 919.16, List.of()),
        Arguments.of("IAXL", true, null, 919.16, 800.00,        // IAXL over profit costs limit without prior auth
            1189.52, 173.58, 867.89, 800.00, List.of("warning net profit costs")),
        Arguments.of("IMXL", true, null, 166.25, 800.00,        // IXML under profit costs limit
            429.02, 46.83, 234.14, 166.25, List.of()),
        Arguments.of("IMXL", true, "priorAuth", 536.71, 500.00, // IXML over profit costs limit with prior auth
            873.57, 120.92, 604.60, 536.71, List.of()),
        Arguments.of("IMXL", true, null, 536.71, 500.00,        // IXML over profit costs limit without prior auth
            829.52, 113.58, 567.89, 500.00, List.of("warning net profit costs"))

    );
  }

  @ParameterizedTest
  @MethodSource("feeTestData")
  void getFee_shouldReturnFeeCalculationResponse(String feeCode, boolean vatIndicator, String priorAuthority,
                                                             double netProfitCosts, double profitCostLimit,
                                                             double expectedTotal, double expectedCalculatedVat,
                                                             double expectedHourlyTotal, double expectedNetProfitCosts,
                                                             List<String> expectedWarnings) {

    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode(feeCode)
        .startDate(LocalDate.of(2025, 5, 11))
        .netProfitCosts(netProfitCosts)
        .jrFormFilling(67.89)
        .netDisbursementAmount(123.38)
        .disbursementVatAmount(24.67)
        .vatIndicator(vatIndicator)
        .immigrationPriorityAuthority(priorAuthority)
        .build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode(feeCode)
        .feeSchemeCode(FeeSchemesEntity.builder().schemeCode("I&A_FS2023").build())
        .calculationType(IMMIGRATION_ASYLUM_HOURLY_RATE)
        .profitCostLimit(new BigDecimal(profitCostLimit))
        .disbursementLimit(new BigDecimal("800.00"))
        .build();

    FeeCalculationResponse result = ImmigrationAsylumHourlyRateCalculator.getFee(feeEntity, feeCalculationRequest);

    assertThat(result).isNotNull();
    assertThat(result.getFeeCode()).isEqualTo(feeCode);
    assertThat(result.getSchemeId()).isEqualTo("I&A_FS2023");
    assertThat(result.getFeeCalculation()).isNotNull();
    assertThat(result.getFeeCalculation().getTotalAmount()).isEqualTo(expectedTotal);
    assertThat(result.getFeeCalculation().getVatIndicator()).isEqualTo(vatIndicator);
    assertThat(result.getFeeCalculation().getVatRateApplied()).isEqualTo(20.0);
    assertThat(result.getFeeCalculation().getCalculatedVatAmount()).isEqualTo(expectedCalculatedVat);
    assertThat(result.getFeeCalculation().getDisbursementAmount()).isEqualTo(123.38);
    assertThat(result.getFeeCalculation().getDisbursementVatAmount()).isEqualTo(24.67);
    assertThat(result.getFeeCalculation().getHourlyTotalAmount()).isEqualTo(expectedHourlyTotal);
    assertThat(result.getFeeCalculation().getNetProfitCostsAmount()).isEqualTo(expectedNetProfitCosts);
    assertThat(result.getFeeCalculation().getJrFormFillingAmount()).isEqualTo(67.89);
    assertThat(result.getWarnings()).isEqualTo(expectedWarnings);
  }


}