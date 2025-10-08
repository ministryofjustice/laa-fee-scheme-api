package uk.gov.justice.laa.fee.scheme.feecalculator.hourly;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.IMMIGRATION_ASYLUM;
import static uk.gov.justice.laa.fee.scheme.enums.FeeType.HOURLY;
import static uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner.TypeEnum.WARNING;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
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
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner;

@ExtendWith(MockitoExtension.class)
class ImmigrationAsylumHourlyRateCalculatorTest {

  @InjectMocks
  ImmigrationAsylumHourlyRateCalculator immigrationAsylumHourlyRateCalculator;

  static Stream<Arguments> feeTestData() {
    return Stream.of(
        // under profit costs and disbursements limits (No VAT)
        Arguments.of("IAXL", false, null, 166.25, 123.38, 24.67,
            314.3, 0, 289.63, 166.25, 123.38, List.of()),
        // over profit costs limit with prior auth (No VAT)
        Arguments.of("IAXL", false, "priorAuth", 919.16, 123.38, 24.67,
            1067.21, 0, 1042.54, 919.16, 123.38, List.of()),
        // over profit costs limit without prior auth (No VAT)
        Arguments.of("IAXL", false, null, 919.16, 123.38, 24.67,
            948.05, 0, 923.38, 800.00, 123.38, List.of("warning net profit costs")),
        // over disbursements limit with prior auth (No VAT)
        Arguments.of("IAXL", false, "priorAuth", 166.25, 425.17, 85.03,
            676.45, 0, 591.42, 166.25, 425.17, List.of()),
        // over disbursements limit without prior auth (No VAT)
        Arguments.of("IAXL", false, null, 166.25, 425.17, 85.03,
            651.28, 0, 566.25, 166.25, 400.00, List.of("warning net disbursements")),
        // over profit costs and disbursements limits with prior auth (No VAT)
        Arguments.of("IAXL", false, "priorAuth", 919.16, 425.17, 85.03,
            1429.36, 0, 1344.33, 919.16, 425.17, List.of()),
        // over profit costs and disbursements limits without prior auth (No VAT)
        Arguments.of("IAXL", false, null, 919.16, 425.17, 85.03,
            1285.03, 0, 1200, 800.00, 400.00, List.of("warning net profit costs", "warning net disbursements")),
        // under profit costs and disbursements limits (VAT applied)
        Arguments.of("IAXL", true, null, 166.25, 123.38, 24.67,
            347.55, 33.25, 289.63, 166.25, 123.38, List.of()),
        // over profit costs limit with prior auth (VAT applied)
        Arguments.of("IAXL", true, "priorAuth", 919.16, 123.38, 24.67,
            1251.04, 183.83, 1042.54, 919.16, 123.38, List.of()),
        // over profit costs limit without prior auth (VAT applied)
        Arguments.of("IAXL", true, null, 919.16, 123.38, 24.67,
            1108.05, 160, 923.38, 800.00, 123.38, List.of("warning net profit costs")),
        // over disbursements limit with prior auth (VAT applied)
        Arguments.of("IAXL", true, "priorAuth", 166.25, 425.17, 85.03,
            709.7, 33.25, 591.42, 166.25, 425.17, List.of()),
        // over disbursements limit without prior auth (VAT applied)
        Arguments.of("IAXL", true, null, 166.25, 425.17, 85.03,
            684.53, 33.25, 566.25, 166.25, 400.00, List.of("warning net disbursements")),
        // over profit costs and disbursements limits with prior auth (VAT applied)
        Arguments.of("IAXL", true, "priorAuth", 919.16, 425.17, 85.03,
            1613.19, 183.83, 1344.33, 919.16, 425.17, List.of()),
        // over profit costs and disbursements limits without prior auth (VAT applied)
        Arguments.of("IAXL", true, null, 919.16, 425.17, 85.03,
            1445.03, 160.00, 1200.00, 800.0, 400.00, List.of("warning net profit costs", "warning net disbursements")),

        // IMXL
        Arguments.of("IMXL", false, null, 166.25, 123.38, 24.67,
            314.3, 0, 289.63, 166.25, 123.38, List.of()),

        // IA100
        Arguments.of("IA100", false, null, 166.25, 123.38, 24.67,
            314.3, 0, 289.63, 166.25, 123.38, List.of())
    );
  }

  @ParameterizedTest
  @MethodSource("feeTestData")
  void getFee_whenLegalHelpFeeCode_shouldReturnFeeCalculationResponse(String feeCode, boolean vatIndicator, String priorAuthority,
                                                                      double netProfitCosts, double netDisbursement, double disbursementVat,
                                                                      double expectedTotal, double expectedCalculatedVat,
                                                                      double expectedHourlyTotal, double expectedNetProfitCosts,
                                                                      double expectedNetDisbursement, List<String> expectedWarnings) {

    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode(feeCode)
        .startDate(LocalDate.of(2025, 5, 11))
        .netProfitCosts(netProfitCosts)
        .netDisbursementAmount(netDisbursement)
        .disbursementVatAmount(disbursementVat)
        .vatIndicator(vatIndicator)
        .immigrationPriorAuthorityNumber(priorAuthority)
        .build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode(feeCode)
        .feeScheme(FeeSchemesEntity.builder().schemeCode("IMM_ASYLM_FS2023").build())
        .categoryType(IMMIGRATION_ASYLUM)
        .feeType(HOURLY)
        .profitCostLimit(new BigDecimal("800.00"))
        .disbursementLimit(new BigDecimal("400.00"))
        .build();

    FeeCalculationResponse result = immigrationAsylumHourlyRateCalculator.calculate(feeCalculationRequest, feeEntity);

    List<ValidationMessagesInner> validationMessages = expectedWarnings.stream()
        .map(i -> ValidationMessagesInner.builder()
            .message(i)
            .type(WARNING)
            .build())
        .toList();

    assertThat(result).isNotNull();
    assertThat(result.getFeeCode()).isEqualTo(feeCode);
    assertThat(result.getSchemeId()).isEqualTo("IMM_ASYLM_FS2023");
    assertThat(result.getFeeCalculation()).isNotNull();
    assertThat(result.getFeeCalculation().getTotalAmount()).isEqualTo(expectedTotal);
    assertThat(result.getFeeCalculation().getVatIndicator()).isEqualTo(vatIndicator);
    assertThat(result.getFeeCalculation().getVatRateApplied()).isEqualTo(20.0);
    assertThat(result.getFeeCalculation().getCalculatedVatAmount()).isEqualTo(expectedCalculatedVat);
    assertThat(result.getFeeCalculation().getDisbursementAmount()).isEqualTo(expectedNetDisbursement);
    assertThat(result.getFeeCalculation().getRequestedNetDisbursementAmount()).isEqualTo(netDisbursement);
    assertThat(result.getFeeCalculation().getDisbursementVatAmount()).isEqualTo(disbursementVat);
    assertThat(result.getFeeCalculation().getHourlyTotalAmount()).isEqualTo(expectedHourlyTotal);
    assertThat(result.getFeeCalculation().getNetProfitCostsAmount()).isEqualTo(expectedNetProfitCosts);
    assertThat(result.getFeeCalculation().getRequestedNetProfitCostsAmount()).isEqualTo(netProfitCosts);
    assertThat(result.getValidationMessages())
        .usingRecursiveComparison()
        .isEqualTo(validationMessages);
  }

  @Test
  void getSupportedCategories_shouldReturnEmptySet() {
    Set<CategoryType> result = immigrationAsylumHourlyRateCalculator.getSupportedCategories();

    assertThat(result).isEmpty();
  }
}