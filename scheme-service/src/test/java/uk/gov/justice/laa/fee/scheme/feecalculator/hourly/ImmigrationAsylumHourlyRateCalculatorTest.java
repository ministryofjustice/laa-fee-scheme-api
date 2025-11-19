package uk.gov.justice.laa.fee.scheme.feecalculator.hourly;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.IMMIGRATION_ASYLUM;
import static uk.gov.justice.laa.fee.scheme.enums.FeeType.HOURLY;
import static uk.gov.justice.laa.fee.scheme.enums.WarningType.WARN_IMM_ASYLM_DETENTION_TRAVEL;
import static uk.gov.justice.laa.fee.scheme.enums.WarningType.WARN_IMM_ASYLM_DISB_LEGAL_HELP;
import static uk.gov.justice.laa.fee.scheme.enums.WarningType.WARN_IMM_ASYLM_JR_FORM_FILLING;
import static uk.gov.justice.laa.fee.scheme.enums.WarningType.WARN_IMM_ASYLM_PRIOR_AUTH_CLR;
import static uk.gov.justice.laa.fee.scheme.enums.WarningType.WARN_IMM_ASYLM_PRIOR_AUTH_INTERIM;
import static uk.gov.justice.laa.fee.scheme.enums.WarningType.WARN_IMM_ASYLM_PRIOR_AUTH_LEGAL_HELP;
import static uk.gov.justice.laa.fee.scheme.enums.WarningType.WARN_IMM_ASYLM_SUM_OVER_LIMIT_LEGAL_HELP;
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
import uk.gov.justice.laa.fee.scheme.enums.WarningType;
import uk.gov.justice.laa.fee.scheme.feecalculator.BaseFeeCalculatorTest;
import uk.gov.justice.laa.fee.scheme.model.BoltOnFeeDetails;
import uk.gov.justice.laa.fee.scheme.model.BoltOnType;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner;

@ExtendWith(MockitoExtension.class)
class ImmigrationAsylumHourlyRateCalculatorTest extends BaseFeeCalculatorTest {
  private static final boolean VAT = true;
  private static final boolean NO_VAT = false;

  private static final String AUTHORITY = "priorAuth";
  private static final String NO_AUTHORITY = null;

  @InjectMocks
  ImmigrationAsylumHourlyRateCalculator immigrationAsylumHourlyRateCalculator;

  @ParameterizedTest
  @MethodSource("feeTestDataLegalHelp")
  void calculateFee_whenLegalHelp_shouldReturnFeeCalculationResponse(String feeCode, boolean vatIndicator, String priorAuthority,
                                                                     double netProfitCosts, double netDisbursement, double disbursementVat,
                                                                     double expectedTotal, double expectedCalculatedVat,
                                                                     double expectedHourlyTotal, double expectedNetProfitCosts,
                                                                     double expectedNetDisbursement, List<WarningType> expectedWarnings) {

    mockVatRatesService(vatIndicator);

    FeeCalculationRequest
        feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode(feeCode)
        .startDate(LocalDate.of(2025, 5, 11))
        .netProfitCosts(netProfitCosts)
        .netDisbursementAmount(netDisbursement)
        .disbursementVatAmount(disbursementVat)
        .vatIndicator(vatIndicator)
        .immigrationPriorAuthorityNumber(priorAuthority)
        .build();

    FeeEntity feeEntity = buildFeeEntity();

    FeeCalculationResponse result = immigrationAsylumHourlyRateCalculator.calculate(feeCalculationRequest, feeEntity);

    assertThat(result).isNotNull();
    assertThat(result.getFeeCode()).isEqualTo(feeCode);
    assertThat(result.getSchemeId()).isEqualTo("IMM_ASYLM_FS2023");
    assertWarnings(result.getValidationMessages(), expectedWarnings);

    FeeCalculation feeCalculation = result.getFeeCalculation();
    assertThat(feeCalculation).isNotNull();
    assertThat(feeCalculation.getTotalAmount()).isEqualTo(expectedTotal);
    assertThat(feeCalculation.getVatIndicator()).isEqualTo(vatIndicator);
    assertThat(feeCalculation.getVatRateApplied()).isEqualTo(vatIndicator ? 20.0 : null);
    assertThat(feeCalculation.getCalculatedVatAmount()).isEqualTo(expectedCalculatedVat);
    assertThat(feeCalculation.getDisbursementAmount()).isEqualTo(expectedNetDisbursement);
    assertThat(feeCalculation.getRequestedNetDisbursementAmount()).isEqualTo(netDisbursement);
    assertThat(feeCalculation.getDisbursementVatAmount()).isEqualTo(disbursementVat);
    assertThat(feeCalculation.getHourlyTotalAmount()).isEqualTo(expectedHourlyTotal);
    assertThat(feeCalculation.getNetProfitCostsAmount()).isEqualTo(expectedNetProfitCosts);
    assertThat(feeCalculation.getRequestedNetProfitCostsAmount()).isEqualTo(netProfitCosts);
  }

  static Stream<Arguments> feeTestDataLegalHelp() {
    return Stream.of(
        // under profit costs and disbursements limits
        Arguments.of("IAXL", NO_VAT, NO_AUTHORITY, 166.25, 123.38, 24.67,
            314.3, 0, 289.63, 166.25, 123.38, List.of()),
        Arguments.of("IAXL", VAT, NO_AUTHORITY, 166.25, 123.38, 24.67,
            347.55, 33.25, 289.63, 166.25, 123.38, List.of()),

        // over profit costs limit "with" prior authority
        Arguments.of("IAXL", NO_VAT, AUTHORITY, 919.16, 123.38, 24.67,
            1067.21, 0, 1042.54, 919.16, 123.38, List.of()),
        Arguments.of("IAXL", VAT, AUTHORITY, 919.16, 123.38, 24.67,
            1251.04, 183.83, 1042.54, 919.16, 123.38, List.of()),

        // over profit costs limit "without" prior authority
        Arguments.of("IAXL", NO_VAT, NO_AUTHORITY, 919.16, 123.38, 24.67,
            948.05, 0, 923.38, 800, 123.38, List.of(WARN_IMM_ASYLM_PRIOR_AUTH_LEGAL_HELP)),
        Arguments.of("IAXL", VAT, NO_AUTHORITY, 919.16, 123.38, 24.67,
            1108.05, 160, 923.38, 800, 123.38, List.of(WARN_IMM_ASYLM_PRIOR_AUTH_LEGAL_HELP)),

        // over disbursements limit "with" prior authority
        Arguments.of("IAXL", NO_VAT, AUTHORITY, 166.25, 425.17, 85.03,
            676.45, 0, 591.42, 166.25, 425.17, List.of()),
        Arguments.of("IAXL", VAT, AUTHORITY, 166.25, 425.17, 85.03,
            709.7, 33.25, 591.42, 166.25, 425.17, List.of()),

        // over disbursements limit "without" prior authority
        Arguments.of("IAXL", NO_VAT, NO_AUTHORITY, 166.25, 425.17, 85.03,
            651.28, 0, 566.25, 166.25, 400, List.of(WARN_IMM_ASYLM_DISB_LEGAL_HELP)),
        Arguments.of("IAXL", VAT, NO_AUTHORITY, 166.25, 425.17, 85.03,
            684.53, 33.25, 566.25, 166.25, 400, List.of(WARN_IMM_ASYLM_DISB_LEGAL_HELP)),

        // over profit costs and disbursements limits "with" prior authority
        Arguments.of("IAXL", NO_VAT, AUTHORITY, 919.16, 425.17, 85.03,
            1429.36, 0, 1344.33, 919.16, 425.17, List.of()),
        Arguments.of("IAXL", VAT, AUTHORITY, 919.16, 425.17, 85.03,
            1613.19, 183.83, 1344.33, 919.16, 425.17, List.of()),

        // over profit costs and disbursements limits "without" prior authority
        Arguments.of("IAXL", NO_VAT, null, 919.16, 425.17, 85.03,
            1285.03, 0, 1200, 800, 400, List.of(WARN_IMM_ASYLM_PRIOR_AUTH_LEGAL_HELP, WARN_IMM_ASYLM_DISB_LEGAL_HELP)),
        Arguments.of("IAXL", VAT, null, 919.16, 425.17, 85.03,
            1445.03, 160, 1200, 800, 400, List.of(WARN_IMM_ASYLM_PRIOR_AUTH_LEGAL_HELP, WARN_IMM_ASYLM_DISB_LEGAL_HELP)),

        // IMXL
        Arguments.of("IMXL", NO_VAT, NO_AUTHORITY, 166.25, 123.38, 24.67,
            314.3, 0, 289.63, 166.25, 123.38, List.of())
    );
  }

  @ParameterizedTest
  @MethodSource("feeTestDataIA100")
  void calculateFee_whenLegalHelpIA100_shouldReturnFeeCalculationResponse(String feeCode, boolean vatIndicator, String priorAuthority,
                                                                          double netProfitCosts, double netDisbursement, double disbursementVat,
                                                                          double expectedTotal, double expectedCalculatedVat,
                                                                          double expectedHourlyTotal, List<WarningType> expectedWarnings) {

    mockVatRatesService(vatIndicator);

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
        .totalLimit(new BigDecimal("100.00"))
        .build();

    FeeCalculationResponse result = immigrationAsylumHourlyRateCalculator.calculate(feeCalculationRequest, feeEntity);

    assertThat(result).isNotNull();
    assertThat(result.getFeeCode()).isEqualTo(feeCode);
    assertThat(result.getSchemeId()).isEqualTo("IMM_ASYLM_FS2023");
    assertWarnings(result.getValidationMessages(), expectedWarnings);

    assertFeeCalculation(result.getFeeCalculation(), expectedTotal, vatIndicator,
        expectedCalculatedVat, netDisbursement, disbursementVat,
        expectedHourlyTotal, netProfitCosts, null, null);
  }

  static Stream<Arguments> feeTestDataIA100() {
    return Stream.of(
        // IA100 under total limit (No VAT)
        Arguments.of("IA100", false, null, 23.99, 55.60, 11.12,
            90.71, 0, 79.59, List.of()),
        // IA100 under total limit (VAT applied)
        Arguments.of("IA100", true, null, 23.99, 55.60, 11.12,
            95.51, 4.80, 79.59, List.of()),

        // IA100 over total limit with prior auth (No VAT)
        Arguments.of("IA100", false, "priorAuth", 65.21, 55.60, 11.12,
            131.93, 0, 120.81, List.of()),
        // IA100 over total limit with prior auth (VAT applied)
        Arguments.of("IA100", true, "priorAuth", 65.21, 55.60, 11.12,
            144.97, 13.04, 120.81, List.of()),

        // IA100 over total limit without prior auth (No VAT)
        Arguments.of("IA100", false, null, 65.21, 55.60, 11.12,
            111.12, 0, 100, List.of(WARN_IMM_ASYLM_SUM_OVER_LIMIT_LEGAL_HELP)),
        // IA100 over total limit without prior auth (VAT applied)
        Arguments.of("IA100", true, null, 65.21, 55.60, 11.12,
            124.16, 13.04, 100, List.of(WARN_IMM_ASYLM_SUM_OVER_LIMIT_LEGAL_HELP))
    );
  }

  @ParameterizedTest
  @MethodSource("warningTestDataLegalHelp")
  void calculateFee_whenLegalHelpFeeCodeAndGivenUnexpectedField_shouldReturnWarning(Double detentionTravelAndWaitingCosts,
                                                                                    Double jrFormFilling, List<WarningType> expectedWarnings) {
    mockVatRatesService(false);

    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode("IAXL")
        .startDate(LocalDate.of(2025, 5, 11))
        .netProfitCosts(166.25)
        .netDisbursementAmount(123.38)
        .disbursementVatAmount(24.67)
        .detentionTravelAndWaitingCosts(detentionTravelAndWaitingCosts)
        .jrFormFilling(jrFormFilling)
        .vatIndicator(false)
        .build();

    FeeEntity feeEntity = buildFeeEntity();

    FeeCalculationResponse result = immigrationAsylumHourlyRateCalculator.calculate(feeCalculationRequest, feeEntity);

    assertThat(result).isNotNull();
    assertWarnings(result.getValidationMessages(), expectedWarnings);
  }

  static Stream<Arguments> warningTestDataLegalHelp() {
    return Stream.of(
        // Legal Help
        Arguments.of(314.3, null, List.of(WARN_IMM_ASYLM_DETENTION_TRAVEL)),
        Arguments.of(null, 55.34, List.of(WARN_IMM_ASYLM_JR_FORM_FILLING)),
        Arguments.of(314.3, 55.34, List.of(WARN_IMM_ASYLM_DETENTION_TRAVEL, WARN_IMM_ASYLM_JR_FORM_FILLING))
    );
  }

  @ParameterizedTest
  @MethodSource("feeTestDataClr")
  void calculateFee_whenClr_shouldReturnFeeCalculationResponse(String feeCode, boolean vatIndicator, String priorAuthority,
                                                               double netProfitCosts, double netCostOfCounsel, double netDisbursement,
                                                               double disbursementVat, double expectedTotal, double expectedCalculatedVat,
                                                               double expectedHourlyTotal, List<WarningType> expectedWarnings) {

    mockVatRatesService(vatIndicator);

    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode(feeCode)
        .startDate(LocalDate.of(2025, 5, 11))
        .netProfitCosts(netProfitCosts)
        .netCostOfCounsel(netCostOfCounsel)
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
        .totalLimit(new BigDecimal("1600.00"))
        .build();

    FeeCalculationResponse result = immigrationAsylumHourlyRateCalculator.calculate(feeCalculationRequest, feeEntity);

    assertThat(result).isNotNull();
    assertThat(result.getFeeCode()).isEqualTo(feeCode);
    assertThat(result.getSchemeId()).isEqualTo("IMM_ASYLM_FS2023");
    assertWarnings(result.getValidationMessages(), expectedWarnings);

    assertFeeCalculation(result.getFeeCalculation(), expectedTotal, vatIndicator,
        expectedCalculatedVat, netDisbursement, disbursementVat,
        expectedHourlyTotal, netProfitCosts, netCostOfCounsel, null);
  }

  static Stream<Arguments> feeTestDataClr() {
    return Stream.of(
        // under total limit
        Arguments.of("IAXC", NO_VAT, NO_AUTHORITY, 486.78, 611.25, 152.34, 30.46,
            1280.83, 0, 1250.37, List.of()),
        Arguments.of("IAXC", VAT, NO_AUTHORITY, 486.78, 611.25, 152.34, 30.46,
            1500.44, 219.61, 1250.37, List.of()),

        // over total limit "with" prior authority
        Arguments.of("IAXC", NO_VAT, AUTHORITY, 486.78, 1008.17, 152.34, 30.46,
            1677.75, 0, 1647.29, List.of()),
        Arguments.of("IAXC", VAT, AUTHORITY, 486.78, 1008.17, 152.34, 30.46,
            1976.74, 298.99, 1647.29, List.of()),

        // over total "without" prior authority
        Arguments.of("IAXC", NO_VAT, NO_AUTHORITY, 486.78, 1008.17, 152.34, 30.46,
            1630.46, 0, 1600, List.of(WARN_IMM_ASYLM_PRIOR_AUTH_CLR)),
        Arguments.of("IAXC", VAT, NO_AUTHORITY, 486.78, 1008.17, 152.34, 30.46,
            1929.45, 298.99, 1600, List.of(WARN_IMM_ASYLM_PRIOR_AUTH_CLR)),

        // IMXC
        Arguments.of("IMXC", NO_VAT, NO_AUTHORITY, 486.78, 611.25, 152.34, 30.46,
            1280.83, 0, 1250.37, List.of()),

        // IRAR
        Arguments.of("IRAR", NO_VAT, NO_AUTHORITY, 486.78, 1008.17, 152.34, 30.46,
            1677.75, 0, 1647.29, List.of())
    );
  }

  @ParameterizedTest
  @MethodSource(value = {"warningTestDataClr", "warningTestDataClrInterim"})
  void calculateFee_whenClrAndGivenUnexpectedField_shouldReturnWarning(String feeCode, Double detentionTravelAndWaitingCosts,
                                                                       Double jrFormFilling, List<WarningType> expectedWarnings) {

    mockVatRatesService(false);

    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode(feeCode)
        .startDate(LocalDate.of(2025, 5, 11))
        .netProfitCosts(486.78)
        .netCostOfCounsel(611.25)
        .netDisbursementAmount(152.34)
        .disbursementVatAmount(30.46)
        .detentionTravelAndWaitingCosts(detentionTravelAndWaitingCosts)
        .jrFormFilling(jrFormFilling)
        .vatIndicator(false)
        .build();

    FeeEntity feeEntity = buildFeeEntity();

    FeeCalculationResponse result = immigrationAsylumHourlyRateCalculator.calculate(feeCalculationRequest, feeEntity);

    assertThat(result).isNotNull();
    assertWarnings(result.getValidationMessages(), expectedWarnings);
  }

  static Stream<Arguments> warningTestDataClr() {
    return Stream.of(
        Arguments.of("IAXC", 314.3, null, List.of(WARN_IMM_ASYLM_DETENTION_TRAVEL)),
        Arguments.of("IAXC", null, 55.34, List.of(WARN_IMM_ASYLM_JR_FORM_FILLING)),
        Arguments.of("IAXC", 314.3, 55.34, List.of(WARN_IMM_ASYLM_DETENTION_TRAVEL, WARN_IMM_ASYLM_JR_FORM_FILLING))
    );
  }

  static Stream<Arguments> warningTestDataClrInterim() {
    return Stream.of(
        Arguments.of("IACD", 314.3, null, List.of(WARN_IMM_ASYLM_DETENTION_TRAVEL)),
        Arguments.of("IACD", null, 55.34, List.of(WARN_IMM_ASYLM_JR_FORM_FILLING)),
        Arguments.of("IACD", 314.3, 55.34, List.of(WARN_IMM_ASYLM_DETENTION_TRAVEL, WARN_IMM_ASYLM_JR_FORM_FILLING))
    );
  }

  @ParameterizedTest
  @MethodSource("feeTestDataClrInterim")
  void calculateFee_whenClrInterim_shouldReturnFeeCalculationResponse(String feeCode, boolean vatIndicator, String priorAuthority, BoltOnType requestedBoltOns,
                                                                      double netProfitCosts, double netCostOfCounsel, double netDisbursement,
                                                                      double disbursementVat, double expectedTotal, double expectedCalculatedVat,
                                                                      double expectedHourlyTotal, List<WarningType> expectedWarnings) {

    mockVatRatesService(vatIndicator);
    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode(feeCode)
        .startDate(LocalDate.of(2025, 5, 11))
        .netProfitCosts(netProfitCosts)
        .netCostOfCounsel(netCostOfCounsel)
        .boltOns(requestedBoltOns)
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
        .adjornHearingBoltOn(new BigDecimal("50"))
        .oralCmrhBoltOn(new BigDecimal("166"))
        .telephoneCmrhBoltOn(new BigDecimal("90"))
        .substantiveHearingBoltOn(new BigDecimal("302"))
        .totalLimit(new BigDecimal("1600.00"))
        .build();

    FeeCalculationResponse result = immigrationAsylumHourlyRateCalculator.calculate(feeCalculationRequest, feeEntity);

    assertThat(result).isNotNull();
    assertThat(result.getFeeCode()).isEqualTo(feeCode);
    assertThat(result.getSchemeId()).isEqualTo("IMM_ASYLM_FS2023");
    assertWarnings(result.getValidationMessages(), expectedWarnings);

    BoltOnFeeDetails expectedBoltOnFeeDetails = requestedBoltOns == null ? null :
          expectedBoltOnFeeDetails(requestedBoltOns.getBoltOnSubstantiveHearing());

    assertFeeCalculation(result.getFeeCalculation(), expectedTotal, vatIndicator,
        expectedCalculatedVat, netDisbursement, disbursementVat,
        expectedHourlyTotal, netProfitCosts, netCostOfCounsel, expectedBoltOnFeeDetails);
  }

  static Stream<Arguments> feeTestDataClrInterim() {
    return Stream.of(
        // under total limit
        Arguments.of("IACD", NO_VAT, NO_AUTHORITY, buildBoltOn(true), 486.78, 611.25, 152.34, 30.46,
            2054.83, 0, 2024.37, List.of()),
        Arguments.of("IACD", VAT, NO_AUTHORITY, buildBoltOn(true), 486.78, 611.25, 152.34, 30.46,
            2429.24, 374.41, 2024.37, List.of()),

        // over total "with" prior authority
        Arguments.of("IACD", NO_VAT, AUTHORITY, buildBoltOn(true), 486.78, 1008.17, 152.34, 30.46,
            2451.75, 0, 2421.29, List.of()),
        Arguments.of("IACD", VAT, AUTHORITY, buildBoltOn(true), 486.78, 1008.17, 152.34, 30.46,
            2905.54, 453.79, 2421.29, List.of()),

        // over total limit "without" prior authority
        Arguments.of("IACD", NO_VAT, NO_AUTHORITY, buildBoltOn(true), 486.78, 1008.17, 152.34, 30.46,
            2404.46, 0, 2374, List.of(WARN_IMM_ASYLM_PRIOR_AUTH_INTERIM)),
        Arguments.of("IACD", VAT, NO_AUTHORITY, buildBoltOn(true), 486.78, 1008.17, 152.34, 30.46,
            2858.25, 453.79, 2374, List.of(WARN_IMM_ASYLM_PRIOR_AUTH_INTERIM)),

        // substantive hearing bolt on = false
        Arguments.of("IACD", NO_VAT, NO_AUTHORITY, buildBoltOn(false), 486.78, 611.25, 152.34, 30.46,
            1752.83, 0, 1722.37, List.of()),

        // no bolt ons
        Arguments.of("IACD", NO_VAT, NO_AUTHORITY, null, 486.78, 611.25, 152.34, 30.46,
            1280.83, 0, 1250.37, List.of()),

        // IMCD
        Arguments.of("IMCD", NO_VAT, NO_AUTHORITY, buildBoltOn(true), 486.78, 611.25, 152.34, 30.46,
            2054.83, 0, 2024.37, List.of())
    );
  }

  @Test
  void getSupportedCategories_shouldReturnEmptySet() {
    Set<CategoryType> result = immigrationAsylumHourlyRateCalculator.getSupportedCategories();

    assertThat(result).isEmpty();
  }

  @Test
  void shouldThrowException_whenCategoryTypeIsUnsupported() {
    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder().feeCode("AAAA").build();
    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("AAAA")
        .build();

    assertThatThrownBy(() -> immigrationAsylumHourlyRateCalculator.calculate(feeCalculationRequest, feeEntity))
        .isInstanceOf(IllegalArgumentException.class).hasMessage("Fee code not supported: AAAA");
  }

  private FeeEntity buildFeeEntity() {
    return FeeEntity.builder()
        .feeCode("IAXL")
        .feeScheme(FeeSchemesEntity.builder().schemeCode("IMM_ASYLM_FS2023").build())
        .categoryType(IMMIGRATION_ASYLUM)
        .feeType(HOURLY)
        .profitCostLimit(new BigDecimal("800.00"))
        .disbursementLimit(new BigDecimal("400.00"))
        .build();
  }

  private void assertFeeCalculation(FeeCalculation feeCalculation, Double total, Boolean vatIndicator,
                                    Double calculatedVat, Double netDisbursement, Double disbursementVat,
                                    Double hourlyTotal, Double netProfitCosts, Double netCostOfCounsel,
                                    BoltOnFeeDetails boltOnFeeDetails) {
    assertThat(feeCalculation).isNotNull();
    assertThat(feeCalculation.getTotalAmount()).isEqualTo(total);
    assertThat(feeCalculation.getVatIndicator()).isEqualTo(vatIndicator);
    assertThat(feeCalculation.getVatRateApplied()).isEqualTo(vatIndicator ? 20.0 : null);
    assertThat(feeCalculation.getCalculatedVatAmount()).isEqualTo(calculatedVat);
    assertThat(feeCalculation.getDisbursementAmount()).isEqualTo(netDisbursement);
    assertThat(feeCalculation.getRequestedNetDisbursementAmount()).isEqualTo(netDisbursement);
    assertThat(feeCalculation.getDisbursementVatAmount()).isEqualTo(disbursementVat);
    assertThat(feeCalculation.getHourlyTotalAmount()).isEqualTo(hourlyTotal);
    assertThat(feeCalculation.getNetProfitCostsAmount()).isEqualTo(netProfitCosts);
    assertThat(feeCalculation.getRequestedNetProfitCostsAmount()).isEqualTo(netProfitCosts);
    assertThat(feeCalculation.getNetCostOfCounselAmount()).isEqualTo(netCostOfCounsel);
    assertThat(feeCalculation.getBoltOnFeeDetails()).isEqualTo(boltOnFeeDetails);
  }

  private void assertWarnings(List<ValidationMessagesInner> resultMessages, List<WarningType> expectedWarnings) {
    List<ValidationMessagesInner> validationMessages = expectedWarnings.stream()
        .map(i -> ValidationMessagesInner.builder()
            .message(i.getMessage())
            .code(i.getCode())
            .type(WARNING)
            .build())
        .toList();

    assertThat(resultMessages)
        .usingRecursiveComparison()
        .isEqualTo(validationMessages);
  }

  private static BoltOnType buildBoltOn(boolean substantiveHearing) {
    return BoltOnType.builder()
        .boltOnAdjournedHearing(1)
        .boltOnCmrhOral(2)
        .boltOnCmrhTelephone(1)
        .boltOnSubstantiveHearing(substantiveHearing)
        .build();
  }

  private static BoltOnFeeDetails expectedBoltOnFeeDetails(Boolean substantiveHearing) {
    return Boolean.TRUE.equals(substantiveHearing) ?
       expectedBoltOnFeeDetails(302.0, 774.0) : expectedBoltOnFeeDetails(null, 472.0) ;
  }

  private static BoltOnFeeDetails expectedBoltOnFeeDetails(Double boltOnSubstantiveHearingFee, Double boltOnTotalFeeAmount) {
    return BoltOnFeeDetails.builder()
        .boltOnAdjournedHearingCount(1)
        .boltOnAdjournedHearingFee(50.0)
        .boltOnCmrhOralCount(2)
        .boltOnCmrhOralFee(332.0)
        .boltOnCmrhTelephoneCount(1)
        .boltOnCmrhTelephoneFee(90.0)
        .boltOnSubstantiveHearingFee(boltOnSubstantiveHearingFee)
        .boltOnTotalFeeAmount(boltOnTotalFeeAmount)
        .build();
  }
}