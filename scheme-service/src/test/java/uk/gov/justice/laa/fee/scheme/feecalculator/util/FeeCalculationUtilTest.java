package uk.gov.justice.laa.fee.scheme.feecalculator.util;

import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.COMMUNITY_CARE;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.FAMILY;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.MENTAL_HEALTH;
import static uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner.TypeEnum.WARNING;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.entity.FeeSchemesEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.enums.FeeType;
import uk.gov.justice.laa.fee.scheme.enums.LimitType;
import uk.gov.justice.laa.fee.scheme.model.BoltOnFeeDetails;
import uk.gov.justice.laa.fee.scheme.model.BoltOnType;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner;

class FeeCalculationUtilTest {

  private static final String ESCAPE_CASE_WARNING_CODE_DESCRIPTION = "123warning";

  @CsvSource(value = {
      "false, null, null, 94.96", // false VAT indicator
      "true, null, null, 106.88", // true VAT indicator
      "false, 2, 22.15, 139.26", // false VAT indicator with bolt ons
      "true, 2, 22.15, 160.04", // true VAT indicator with bolt ons
  }, nullValues = {"null"})
  @ParameterizedTest
  void calculate_givenFeeEntity_returnsFeeCalculationResponse(Boolean vatIndicator, Integer noBoltOns, BigDecimal boltOnFee,
                                                              double expectedTotal) {
    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode("FEE1")
        .startDate(LocalDate.of(2025, 1, 1))
        .vatIndicator(vatIndicator)
        .netDisbursementAmount(29.45)
        .disbursementVatAmount(5.89)
        .boltOns(noBoltOns != null ? BoltOnType.builder().boltOnAdjournedHearing(noBoltOns).build() : null)
        .build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("FEE1")
        .feeScheme(FeeSchemesEntity.builder().schemeCode("FEE_SCHEME_CODE").build())
        .categoryType(MENTAL_HEALTH)
        .fixedFee(new BigDecimal("59.62"))
        .adjornHearingBoltOn(boltOnFee)
        .build();

    FeeCalculationResponse response = FeeCalculationUtil.calculate(feeCalculationRequest, feeEntity);

    assertThat(response).isNotNull();
    assertThat(response.getFeeCode()).isEqualTo("FEE1");

    FeeCalculation calculation = response.getFeeCalculation();
    assertThat(calculation).isNotNull();
    assertThat(calculation.getTotalAmount()).isEqualTo(expectedTotal);
  }

  @Test
  void calculate_givenFixedFee_returnsFeeCalculationResponse() {
    BigDecimal fixedFee = new BigDecimal("59.62");
    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode("FEE1")
        .startDate(LocalDate.of(2025, 1, 1))
        .vatIndicator(true)
        .netDisbursementAmount(29.45)
        .disbursementVatAmount(5.89)
        .build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeScheme(FeeSchemesEntity.builder().schemeCode("FEE_SCHEME_CODE").build())
        .categoryType(COMMUNITY_CARE)
        .build();

    FeeCalculationResponse response = FeeCalculationUtil.calculate(fixedFee, feeCalculationRequest, feeEntity);

    assertThat(response).isNotNull();
    assertThat(response.getFeeCode()).isEqualTo("FEE1");

    FeeCalculation calculation = response.getFeeCalculation();
    assertThat(calculation).isNotNull();
    assertThat(calculation.getTotalAmount()).isEqualTo(106.88);
  }

  @Test
  void calculate_givenFixedFeeWithBoltOns_returnsFeeCalculationResponse() {
    BigDecimal fixedFee = new BigDecimal("263.00");
    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode("MHL02")
        .claimId("claim_123")
        .startDate(LocalDate.of(2025, 1, 1))
        .vatIndicator(true)
        .netDisbursementAmount(29.45)
        .disbursementVatAmount(5.89)
        .boltOns(BoltOnType.builder()
            .boltOnAdjournedHearing(1)
            .build())
        .build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("MHL02")
        .feeScheme(FeeSchemesEntity.builder().schemeCode("MHL_FS2013").build())
        .fixedFee(fixedFee)
        .adjornHearingBoltOn(new BigDecimal(100))
        .categoryType(MENTAL_HEALTH)
        .build();

    FeeCalculationResponse response = FeeCalculationUtil.calculate(feeCalculationRequest, feeEntity);

    FeeCalculation expectedCalculation = FeeCalculation.builder()
        .totalAmount(470.94)
        .vatIndicator(true)
        .vatRateApplied(20.0)
        .disbursementAmount(29.45)
        .requestedNetDisbursementAmount(29.45)
        .disbursementVatAmount(5.89)
        .fixedFeeAmount(263.00)
        .calculatedVatAmount(72.60)
        .boltOnFeeDetails(BoltOnFeeDetails.builder()
            .boltOnTotalFeeAmount(100.00)
            .boltOnAdjournedHearingCount(1)
            .boltOnAdjournedHearingFee(100.00)
            .build())
        .build();

    FeeCalculationResponse expectedResponse = FeeCalculationResponse.builder()
        .feeCode("MHL02")
        .schemeId("MHL_FS2013")
        .claimId("claim_123")
        .validationMessages(new ArrayList<>())
        .escapeCaseFlag(false)
        .feeCalculation(expectedCalculation)
        .build();

    assertThat(response)
        .usingRecursiveComparison()
        .isEqualTo(expectedResponse);

  }

  @Test
  void should_returnValidationWarningMessageInResponse_when_total_fee_exceeds_escape_threshold_limit_for_family() {
    BigDecimal fixedFee = new BigDecimal("263.00");
    BigDecimal escapeThresoldLimit = new BigDecimal("550.00");

    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode("FPB010")
        .claimId("claim_124")
        .startDate(LocalDate.of(2025, 1, 1))
        .vatIndicator(true)
        .netDisbursementAmount(29.45)
        .disbursementVatAmount(1005.89)
        .build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("FPB010")
        .feeScheme(FeeSchemesEntity.builder().schemeCode("FAM_NON_LON_FS2011").build())
        .fixedFee(fixedFee)
        .escapeThresholdLimit(escapeThresoldLimit)
        .categoryType(FAMILY)
        .build();

    FeeCalculationResponse response = FeeCalculationUtil.calculate(feeCalculationRequest, feeEntity);

    FeeCalculation expectedCalculation = FeeCalculation.builder()
        .totalAmount(1350.94)
        .vatIndicator(true)
        .vatRateApplied(20.0)
        .disbursementAmount(29.45)
        .requestedNetDisbursementAmount(29.45)
        .disbursementVatAmount(1005.89)
        .fixedFeeAmount(263.00)
        .calculatedVatAmount(52.60)
        .build();


    List<ValidationMessagesInner> validationMessages = new ArrayList<>();

    validationMessages.add(ValidationMessagesInner.builder()
        .message(ESCAPE_CASE_WARNING_CODE_DESCRIPTION)
        .type(WARNING)
        .build());
    FeeCalculationResponse expectedResponse = FeeCalculationResponse.builder()
        .feeCode("FPB010")
        .schemeId("FAM_NON_LON_FS2011")
        .claimId("claim_124")
        .validationMessages(validationMessages)
        .escapeCaseFlag(true)
        .feeCalculation(expectedCalculation)
        .build();

    assertThat(response)
        .usingRecursiveComparison()
        .isEqualTo(expectedResponse);

  }

  @ParameterizedTest
  @CsvSource(value = {
      "FIXED, true",
      "HOURLY, false",
      "null, false"
  }, nullValues = {"null"})
  void testIsFixedFee(FeeType feeType, boolean expected) {
    Assertions.assertEquals(expected, FeeCalculationUtil.isFixedFee(feeType));
  }

  @ParameterizedTest
  @EnumSource(value = CategoryType.class, names = {
      "ASSOCIATED_CIVIL", "POLICE_STATION", "PRISON_LAW"
  })
  void calculate_ReturnDateFromUniqueFileNumber_forAssociatedCivilAndPoliceAndPrisonLaw(CategoryType categoryType) {
    FeeCalculationRequest feeDataRequest = getFeeCalculationRequest();
    LocalDate result = FeeCalculationUtil.getFeeClaimStartDate(categoryType, feeDataRequest);

    assertEquals(LocalDate.of(2022, 12, 1), result);
  }

  @ParameterizedTest
  @EnumSource(value = CategoryType.class, names = {
      "MAGS_COURT_DESIGNATED", "MAGS_COURT_UNDESIGNATED",
      "YOUTH_COURT_DESIGNATED", "YOUTH_COURT_UNDESIGNATED"
  })
  void calculate_ReturnRepresentationOrderDate_forMagistratesAndYouthCourts(CategoryType categoryType) {
    FeeCalculationRequest feeDataRequest = getFeeCalculationRequest();
    LocalDate repOrderDate = LocalDate.of(2023, 12, 12);
    feeDataRequest.setRepresentationOrderDate(repOrderDate);
    LocalDate result = FeeCalculationUtil.getFeeClaimStartDate(categoryType, feeDataRequest);

    assertEquals(repOrderDate, result);
  }

  @ParameterizedTest
  @CsvSource(value = {
      "ADVOCACY_APPEALS_REVIEWS, PROH, 2025-11-11, 090925/abc, 2025-11-11",
      "ADVOCACY_APPEALS_REVIEWS, PROH, , 090925/abc, 2025-09-09",
      "ADVOCACY_APPEALS_REVIEWS, APPA, , 090925/abc, 2025-09-09",
      "ADVOCACY_APPEALS_REVIEWS, APPB, , 090925/abc, 2025-09-09"
  })
  void shouldReturnStartDate_forOtherCategories(String categoryType, String feeCode, String repOrderDate, String ufn,
                                                LocalDate expectedDate) {
    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode(feeCode)
        .claimId("claim_123")
        .representationOrderDate(nonNull(repOrderDate) ? LocalDate.parse(repOrderDate) : null)
        .uniqueFileNumber(ufn)
        .netProfitCosts(10.0)
        .netDisbursementAmount(10.0)
        .disbursementVatAmount(10.0)
        .vatIndicator(true)
        .netTravelCosts(10.0)
        .netWaitingCosts(10.0)
        .build();

    LocalDate result = FeeCalculationUtil.getFeeClaimStartDate(CategoryType.valueOf(categoryType), feeCalculationRequest);
    assertEquals(expectedDate, result);
  }


  @Test
  void shouldThrowException_ifUniqueFileNumberIsNullForPoliceStation() {
    FeeCalculationRequest request = getFeeCalculationRequest();
    request.setUniqueFileNumber(null);

    assertThrows(NullPointerException.class, () ->
        FeeCalculationUtil.getFeeClaimStartDate(CategoryType.POLICE_STATION, request)
    );
  }

  private static FeeCalculationRequest getFeeCalculationRequest() {
    return FeeCalculationRequest.builder()
        .feeCode("ABC")
        .startDate(LocalDate.of(2017, 7, 29))
        .vatIndicator(Boolean.TRUE)
        .policeStationSchemeId("1003")
        .policeStationId("NA2093")
        .uniqueFileNumber("011222/456")
        .netDisbursementAmount(50.50)
        .disbursementVatAmount(20.15)
        .representationOrderDate(LocalDate.of(2023, 12, 12))
        .build();
  }

  @Test
  void calculateTotalAmount_givenFeeAndDisbursements_returnsTotal() {
    BigDecimal totalAmount = FeeCalculationUtil.calculateTotalAmount(new BigDecimal("120.50"),
        new BigDecimal("24.10"),
        new BigDecimal("67.52"),
        new BigDecimal("13.50"));

    assertThat(totalAmount).isEqualTo(new BigDecimal("225.62"));
  }

  @Test
  void calculateTotalAmount_givenFee_returnsTotal() {
    BigDecimal totalAmount = FeeCalculationUtil.calculateTotalAmount(new BigDecimal("120.50"),
        new BigDecimal("24.10"));

    assertThat(totalAmount).isEqualTo(new BigDecimal("144.60"));
  }

  @ParameterizedTest
  @CsvSource({
      "99, false",
      "100, false",
      "101, true",
  })
  void isEscapedCase_returnsResult(BigDecimal amount, boolean expected) {
    boolean result = FeeCalculationUtil.isEscapedCase(amount, new BigDecimal("100"));

    assertThat(result).isEqualTo(expected);
  }

  @ParameterizedTest
  @MethodSource("limitTestData")
  void checkLimitAndCapIfExceeded_returnsResult(BigDecimal amount, String authority, BigDecimal expectedAmount,
                                                List<ValidationMessagesInner> expectedMessages) {

    LimitContext limitContext = new LimitContext(LimitType.TOTAL, new BigDecimal("100"), authority, "Warning message");
    List<ValidationMessagesInner> validationMessages = new ArrayList<>();

    BigDecimal result = FeeCalculationUtil.checkLimitAndCapIfExceeded(amount, limitContext, validationMessages);

    assertThat(result).isEqualTo(expectedAmount);

    assertThat(validationMessages).isEqualTo(expectedMessages);
  }

  public static Stream<Arguments> limitTestData() {
    return Stream.of(
        arguments(new BigDecimal("90"), null, new BigDecimal("90"), List.of()),
        arguments(new BigDecimal("200"), "priorAuth", new BigDecimal("200"), List.of()),
        arguments(new BigDecimal("200"), null, new BigDecimal("100"),
            List.of(ValidationMessagesInner.builder().type(WARNING).message("Warning message").build()))
    );
  }

}