package uk.gov.justice.laa.fee.scheme.feecalculator.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.COMMUNITY_CARE;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.MENTAL_HEALTH;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.entity.FeeSchemesEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.model.BoltOnFeeDetails;
import uk.gov.justice.laa.fee.scheme.model.BoltOnType;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;

class FeeCalculationUtilTest {

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
        .feeSchemeCode(FeeSchemesEntity.builder().schemeCode("FEE_SCHEME_CODE").build())
        .categoryType(MENTAL_HEALTH)
        .fixedFee(new BigDecimal("59.62"))
        .adjornHearingBoltOn(boltOnFee)
        .build();

    FeeCalculationResponse response = FeeCalculationUtil.calculate(feeEntity, feeCalculationRequest);

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
        .feeSchemeCode(FeeSchemesEntity.builder().schemeCode("FEE_SCHEME_CODE").build())
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
        .feeSchemeCode(FeeSchemesEntity.builder().schemeCode("MHL_FS2013").build())
        .fixedFee(fixedFee)
        .adjornHearingBoltOn(new BigDecimal(100))
        .categoryType(MENTAL_HEALTH)
        .build();

    FeeCalculationResponse response = FeeCalculationUtil.calculate(feeEntity, feeCalculationRequest);

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
  void testIsFixedFeeWhenFixed() {
    Assertions.assertTrue(FeeCalculationUtil.isFixedFee("FIXED"));
  }

  @Test
  void testIsFixedFeeWhenNotFixed() {
    Assertions.assertFalse(FeeCalculationUtil.isFixedFee("HOURLY"));
  }

  @Test
  void testIsFixedFeeWhenNull() {
    assertThrows(NullPointerException.class, () -> {
      FeeCalculationUtil.isFixedFee(null);
    });
  }

  @ParameterizedTest
  @EnumSource(value = CategoryType.class, names = {
      "POLICE_STATION", "PRISON_LAW"
  })
  void calculate_ReturnDateFromUniqueFileNumber_forPoliceAndPrisonLaw(CategoryType categoryType) {
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
  @EnumSource(value = CategoryType.class, names = {
      "IMMIGRATION_ASYLUM", "COMMUNITY_CARE", "EDUCATION",
      "MENTAL_HEALTH", "WELFARE_BENEFITS", "MEDIATION"
  })
  void shouldReturnStartDate_forOtherCategories(CategoryType categoryType) {
    FeeCalculationRequest feeDataRequest = getFeeCalculationRequest();
    LocalDate startDate = LocalDate.of(2024, 7, 20);
    feeDataRequest.setStartDate(startDate);
    LocalDate result = FeeCalculationUtil.getFeeClaimStartDate(categoryType, feeDataRequest);

    assertEquals(startDate, result);
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
}