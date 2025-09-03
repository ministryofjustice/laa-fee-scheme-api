package uk.gov.justice.laa.fee.scheme.feecalculator;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.justice.laa.fee.scheme.feecalculator.CalculationType.IMMIGRATION_ASYLUM_HOURLY_RATE;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.entity.FeeSchemesEntity;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;

class ImmigrationAsylumHourlyRateCalculatorTest {

  static Stream<Arguments> feeTestData() {
    return Stream.of(
        Arguments.of("IAXL", false, null, 166.25, 123.38, 24.67,          // under profit costs and disbursements limits (No VAT)
            382.19, 0, 234.14, 166.25, 123.38, List.of()),
        Arguments.of("IAXL", false, "priorAuth", 919.16, 123.38, 24.67,   // over profit costs limit with prior auth (No VAT)
            1135.10, 0, 987.05, 919.16, 123.38, List.of()),
        Arguments.of("IAXL", false, null, 919.16, 123.38, 24.67,          // over profit costs limit without prior auth (No VAT)
            1015.94, 0, 867.89, 800.00, 123.38, List.of("warning net profit costs")),
        Arguments.of("IAXL", false, "priorAuth", 166.25, 425.17, 85.03,   // over disbursements limit with prior auth (No VAT)
            744.34, 0, 234.14, 166.25, 425.17, List.of()),
        Arguments.of("IAXL", false, null, 166.25, 425.17, 85.03,          // over disbursements limit without prior auth (No VAT)
            719.17, 0, 234.14, 166.25, 400.00, List.of("warning net disbursements")),
        Arguments.of("IAXL", false, "priorAuth", 919.16, 425.17, 85.03,   // over profit costs and disbursements limits with prior auth (No VAT)
            1497.25, 0, 987.05, 919.16, 425.17, List.of()),
        Arguments.of("IAXL", false, null, 919.16, 425.17, 85.03,          // over profit costs and disbursements limits without prior auth (No VAT)
            1352.92, 0, 867.89, 800.00, 400.00, List.of("warning net profit costs", "warning net disbursements")),

        Arguments.of("IAXL", true, null, 166.25, 123.38, 24.67,          // under profit costs and disbursements limits (VAT applied)
            429.02, 46.83, 234.14, 166.25, 123.38, List.of()),
        Arguments.of("IAXL", true, "priorAuth", 919.16, 123.38, 24.67,   // over profit costs limit with prior auth (VAT applied)
            1332.51, 197.41, 987.05, 919.16, 123.38, List.of()),
        Arguments.of("IAXL", true, null, 919.16, 123.38, 24.67,          // over profit costs limit without prior auth (VAT applied)
            1189.52, 173.58, 867.89, 800.00, 123.38, List.of("warning net profit costs")),
        Arguments.of("IAXL", true, "priorAuth", 166.25, 425.17, 85.03,   // over disbursements limit with prior auth (VAT applied)
            791.17, 46.83, 234.14, 166.25, 425.17, List.of()),
        Arguments.of("IAXL", true, null, 166.25, 425.17, 85.03,          // over disbursements limit without prior auth (VAT applied)
            766.00, 46.83, 234.14, 166.25, 400.00, List.of("warning net disbursements")),
        Arguments.of("IAXL", true, "priorAuth", 919.16, 425.17, 85.03,   // over profit costs and disbursements limits with prior auth (VAT applied)
            1694.66, 197.41, 987.05, 919.16, 425.17, List.of()),
        Arguments.of("IAXL", true, null, 919.16, 425.17, 85.03,          // over profit costs and disbursements limits without prior auth (VAT applied)
            1526.50, 173.58, 867.89, 800.0, 400.00, List.of("warning net profit costs", "warning net disbursements")),

        Arguments.of("IMXL", false, null, 166.25, 123.38, 24.67,          // IMXL
            382.19, 0, 234.14, 166.25, 123.38, List.of())
    );
  }

  @ParameterizedTest
  @MethodSource("feeTestData")
  void getFee_shouldReturnFeeCalculationResponse_legalHelp(String feeCode, boolean vatIndicator, String priorAuthority,
                                                           double netProfitCosts, double netDisbursement, double disbursementVat,
                                                           double expectedTotal, double expectedCalculatedVat,
                                                           double expectedHourlyTotal, double expectedNetProfitCosts,
                                                           double expectedNetDisbursement, List<String> expectedWarnings) {

    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode(feeCode)
        .startDate(LocalDate.of(2025, 5, 11))
        .netProfitCosts(netProfitCosts)
        .jrFormFilling(67.89)
        .netDisbursementAmount(netDisbursement)
        .disbursementVatAmount(disbursementVat)
        .vatIndicator(vatIndicator)
        .immigrationPriorAuthorityNumber(priorAuthority)
        .build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode(feeCode)
        .feeSchemeCode(FeeSchemesEntity.builder().schemeCode("I&A_FS2013").build())
        .calculationType(IMMIGRATION_ASYLUM_HOURLY_RATE)
        .profitCostLimit(new BigDecimal("800.00"))
        .disbursementLimit(new BigDecimal("400.00"))
        .build();

    FeeCalculationResponse result = ImmigrationAsylumHourlyRateCalculator.getFee(feeEntity, feeCalculationRequest);

    assertThat(result).isNotNull();
    assertThat(result.getFeeCode()).isEqualTo(feeCode);
    assertThat(result.getSchemeId()).isEqualTo("I&A_FS2013");
    assertThat(result.getFeeCalculation()).isNotNull();
    assertThat(result.getFeeCalculation().getTotalAmount()).isEqualTo(expectedTotal);
    assertThat(result.getFeeCalculation().getVatIndicator()).isEqualTo(vatIndicator);
    assertThat(result.getFeeCalculation().getVatRateApplied()).isEqualTo(20.0);
    assertThat(result.getFeeCalculation().getCalculatedVatAmount()).isEqualTo(expectedCalculatedVat);
    assertThat(result.getFeeCalculation().getDisbursementAmount()).isEqualTo(expectedNetDisbursement);
    assertThat(result.getFeeCalculation().getDisbursementVatAmount()).isEqualTo(disbursementVat);
    assertThat(result.getFeeCalculation().getHourlyTotalAmount()).isEqualTo(expectedHourlyTotal);
    assertThat(result.getFeeCalculation().getNetProfitCostsAmount()).isEqualTo(expectedNetProfitCosts);
    assertThat(result.getFeeCalculation().getJrFormFillingAmount()).isEqualTo(67.89);
    assertThat(result.getWarnings()).isEqualTo(expectedWarnings);
  }

  static Stream<Arguments> feeTestDataClr() {
    return Stream.of(
        Arguments.of("IAXC", false, null, 407.21, 1600.00,         // under total limit (No VAT)
            952.46, 0, 845.06, List.of()),
        Arguments.of("IAXC", false, "priorAuth", 1345.62, 1600.00, // over total limit with prior auth (No VAT)
            1890.87, 0, 1783.47, List.of()),
        Arguments.of("IAXC", false, null, 1345.62, 1600.00,        // over total limit without prior auth (No VAT)
            1707.4, 0, 1600.00, List.of("warning total limit")),
        Arguments.of("IAXC", true, null, 407.21, 1600.00,          // under total limit (VAT applied)
            1121.47, 169.01, 845.06, List.of()),
        Arguments.of("IAXC", true, "priorAuth", 1345.62, 1600.00,   // over total limit with prior auth (VAT applied)
            2247.56, 356.69, 1783.47, List.of()),
        Arguments.of("IAXC", true, null, 1345.62, 1600.00,          // over total limit without prior auth (VAT applied)
            2027.4, 320, 1600.00, List.of("warning total limit")),

        Arguments.of("IRAR", true, null, 2100.46, 1600.00,          // IRAR (has no limit)
            3153.37, 507.66, 2538.31, List.of()),


        Arguments.of("IMXC", true, null, 632.97, 1200.00,           // IMXC
            1392.38, 214.16, 1070.82, List.of())
    );
  }

  @ParameterizedTest
  @MethodSource("feeTestDataClr")
  void getFee_shouldReturnFeeCalculationResponse_clr(String feeCode, boolean vatIndicator, String priorAuthority,
                                                     double netProfitCosts, double totalLimit,
                                                     double expectedTotal, double expectedCalculatedVat,
                                                     double expectedHourlyTotal, List<String> expectedWarnings) {

    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode(feeCode)
        .startDate(LocalDate.of(2025, 5, 11))
        .netProfitCosts(netProfitCosts)
        .netCostOfCounsel(362.85)
        .jrFormFilling(75.00)
        .netDisbursementAmount(89.50)
        .disbursementVatAmount(17.90)
        .vatIndicator(vatIndicator)
        .immigrationPriorAuthorityNumber(priorAuthority)
        .build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode(feeCode)
        .feeSchemeCode(FeeSchemesEntity.builder().schemeCode("I&A_FS2013").build())
        .calculationType(IMMIGRATION_ASYLUM_HOURLY_RATE)
        .totalLimit(new BigDecimal(totalLimit))
        .build();

    FeeCalculationResponse result = ImmigrationAsylumHourlyRateCalculator.getFee(feeEntity, feeCalculationRequest);

    assertThat(result).isNotNull();
    assertThat(result.getFeeCode()).isEqualTo(feeCode);
    assertThat(result.getSchemeId()).isEqualTo("I&A_FS2013");
    assertThat(result.getWarnings()).isEqualTo(expectedWarnings);

    FeeCalculation feeCalculation = result.getFeeCalculation();
    assertThat(feeCalculation).isNotNull();
    assertThat(feeCalculation.getTotalAmount()).isEqualTo(expectedTotal);
    assertThat(feeCalculation.getVatIndicator()).isEqualTo(vatIndicator);
    assertThat(feeCalculation.getVatRateApplied()).isEqualTo(20.0);
    assertThat(feeCalculation.getCalculatedVatAmount()).isEqualTo(expectedCalculatedVat);
    assertThat(feeCalculation.getDisbursementAmount()).isEqualTo(89.50);
    assertThat(feeCalculation.getDisbursementVatAmount()).isEqualTo(17.90);
    assertThat(feeCalculation.getHourlyTotalAmount()).isEqualTo(expectedHourlyTotal);
    assertThat(feeCalculation.getNetProfitCostsAmount()).isEqualTo(netProfitCosts);
    assertThat(feeCalculation.getJrFormFillingAmount()).isEqualTo(75.00);

  }

  // @TODO: IA100 will move to Legal Help in future, hence why it is tested separately here
  static Stream<Arguments> feeTestDataIA100() {
    return Stream.of(
        Arguments.of(21.46, 62.10, 7.49, 37.45, List.of()), // under total limit
        Arguments.of(73.29, 137.16, 20, 100.0, List.of("warning total limit")) // over total limit
    );
  }
  @ParameterizedTest
  @MethodSource("feeTestDataIA100")
  void getFee_shouldReturnFeeCalculationResponse_IA100(double netProfitCosts,
                                                       double expectedTotal, double expectedCalculatedVat,
                                                       double expectedHourlyTotal, List<String> expectedWarnings) {

    String feeCode = "IA100";
    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode(feeCode)
        .startDate(LocalDate.of(2025, 5, 11))
        .netProfitCosts(netProfitCosts)
        .netCostOfCounsel(10.99)
        .jrFormFilling(5.00)
        .netDisbursementAmount(14.30)
        .disbursementVatAmount(2.86)
        .vatIndicator(true)
        .build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode(feeCode)
        .feeSchemeCode(FeeSchemesEntity.builder().schemeCode("I&A_FS2013").build())
        .calculationType(IMMIGRATION_ASYLUM_HOURLY_RATE)
        .totalLimit(new BigDecimal("100"))
        .build();

    FeeCalculationResponse result = ImmigrationAsylumHourlyRateCalculator.getFee(feeEntity, feeCalculationRequest);

    assertThat(result).isNotNull();
    assertThat(result.getFeeCode()).isEqualTo(feeCode);
    assertThat(result.getSchemeId()).isEqualTo("I&A_FS2013");
    assertThat(result.getWarnings()).isEqualTo(expectedWarnings);

    FeeCalculation feeCalculation = result.getFeeCalculation();
    assertThat(feeCalculation).isNotNull();
    assertThat(feeCalculation.getTotalAmount()).isEqualTo(expectedTotal);
    assertThat(feeCalculation.getVatIndicator()).isEqualTo(true);
    assertThat(feeCalculation.getVatRateApplied()).isEqualTo(20.0);
    assertThat(feeCalculation.getCalculatedVatAmount()).isEqualTo(expectedCalculatedVat);
    assertThat(feeCalculation.getDisbursementAmount()).isEqualTo(14.30);
    assertThat(feeCalculation.getDisbursementVatAmount()).isEqualTo(2.86);
    assertThat(feeCalculation.getHourlyTotalAmount()).isEqualTo(expectedHourlyTotal);
    assertThat(feeCalculation.getNetProfitCostsAmount()).isEqualTo(netProfitCosts);
    assertThat(feeCalculation.getJrFormFillingAmount()).isEqualTo(5.00);
  }
}