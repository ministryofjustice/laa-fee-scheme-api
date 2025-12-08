package uk.gov.justice.laa.fee.scheme.feecalculator.util;

import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.ADVICE_ASSISTANCE_ADVOCACY;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.COMMUNITY_CARE;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.MAGISTRATES_COURT;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.MEDIATION;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.POLICE_STATION;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.enums.ClaimStartDateType;
import uk.gov.justice.laa.fee.scheme.exception.CaseConcludedDateRequiredException;
import uk.gov.justice.laa.fee.scheme.exception.StartDateRequiredException;
import uk.gov.justice.laa.fee.scheme.model.BoltOnFeeDetails;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;

class FeeCalculationUtilTest {

  @Test
  void getFeeClaimStartDate_returnsStartDate() {
    FeeCalculationRequest feeDataRequest = FeeCalculationRequest.builder()
        .startDate(LocalDate.of(2022, 12, 1))
        .build();

    LocalDate result = FeeCalculationUtil.getFeeClaimStartDate(COMMUNITY_CARE, feeDataRequest);

    assertThat(result).isEqualTo(LocalDate.of(2022, 12, 1));
  }

  @Test
  void getFeeClaimStartDate_returnsUfnDate() {
    FeeCalculationRequest feeDataRequest = FeeCalculationRequest.builder()
        .uniqueFileNumber("011222/456")
        .build();

    LocalDate result = FeeCalculationUtil.getFeeClaimStartDate(POLICE_STATION, feeDataRequest);

    assertThat(result).isEqualTo(LocalDate.of(2022, 12, 1));
  }

  @Test
  void getFeeClaimStartDate_returnsRepOrder() {
    FeeCalculationRequest feeDataRequest = FeeCalculationRequest.builder()
        .representationOrderDate(LocalDate.of(2022, 12, 1))
        .build();

    LocalDate result = FeeCalculationUtil.getFeeClaimStartDate(MAGISTRATES_COURT, feeDataRequest);

    assertThat(result).isEqualTo(LocalDate.of(2022, 12, 1));
  }

  @Test
  void getFeeClaimStartDate_returnsCaseConcludedDate() {
    FeeCalculationRequest feeDataRequest = FeeCalculationRequest.builder()
        .caseConcludedDate(LocalDate.of(2022, 12, 1))
        .build();

    LocalDate result = FeeCalculationUtil.getFeeClaimStartDate(ADVICE_ASSISTANCE_ADVOCACY, feeDataRequest);

    assertThat(result).isEqualTo(LocalDate.of(2022, 12, 1));
  }

  @ParameterizedTest
  @CsvSource({
      "COMMUNITY_CARE, CASE_START_DATE",
      "POLICE_STATION, UFN",
      "MAGISTRATES_COURT, REP_ORDER_DATE",
  })
  void getFeeClaimStartDateType_givenCategoryType_returnsClaimStartType(CategoryType categoryType, ClaimStartDateType expectedResult) {
    FeeCalculationRequest feeDataRequest = getFeeCalculationRequest();
    ClaimStartDateType result = FeeCalculationUtil.getFeeClaimStartDateType(categoryType, feeDataRequest);

    assertThat(result).isEqualTo(expectedResult);
  }

  @ParameterizedTest
  @EnumSource(value = CategoryType.class, names = {
      "MAGISTRATES_COURT", "YOUTH_COURT"
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
      "ADVOCACY_APPEALS_REVIEWS, PROH1, 2025-11-11, 090925/abc, 2025-11-11",
      "ADVOCACY_APPEALS_REVIEWS, PROH2, 2025-11-11, 090925/abc, 2025-11-11",
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
        FeeCalculationUtil.getFeeClaimStartDate(POLICE_STATION, request)
    );
  }

  @Test
  void calculateVatAmount_shouldReturnResult() {
    BigDecimal totalAmount = FeeCalculationUtil.calculateVatAmount(new BigDecimal("170.50"),
        new BigDecimal("20.00"));

    assertThat(totalAmount).isEqualTo(new BigDecimal("34.10"));
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

  @Test
  void filterBoltOnFeeDetails_andNullForRequest() {
    BoltOnFeeDetails boltOnFeeDetails = BoltOnFeeDetails.builder()
        .boltOnTotalFeeAmount(null)
        .boltOnAdjournedHearingCount(null)
        .boltOnAdjournedHearingFee(null)
        .boltOnCmrhOralCount(null)
        .boltOnCmrhOralFee(null)
        .boltOnCmrhTelephoneCount(null)
        .boltOnCmrhTelephoneFee(null)
        .boltOnHomeOfficeInterviewCount(null)
        .boltOnHomeOfficeInterviewFee(null)
        .boltOnSubstantiveHearingFee(null)
        .build();

    BoltOnFeeDetails boltOnFeeDetailResponse = FeeCalculationUtil.filterBoltOnFeeDetails(boltOnFeeDetails);

    assertThat(boltOnFeeDetailResponse).isNull();
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
  void shouldThrowException_ifStartDateIsNull() {
    FeeCalculationRequest request = getFeeCalculationRequest();
    request.setStartDate(null);

    assertThrows(StartDateRequiredException.class, () ->
        FeeCalculationUtil.getFeeClaimStartDate(MEDIATION, request)
    );
  }

  @Test
  void shouldThrowException_ifCaseConcludedDateIsNull() {
    FeeCalculationRequest request = getFeeCalculationRequest();
    request.setCaseConcludedDate(null);

    assertThrows(CaseConcludedDateRequiredException.class, () ->
        FeeCalculationUtil.getFeeClaimStartDate(ADVICE_ASSISTANCE_ADVOCACY, request)
    );
  }
}