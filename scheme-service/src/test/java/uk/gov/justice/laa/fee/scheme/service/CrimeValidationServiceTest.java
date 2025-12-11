package uk.gov.justice.laa.fee.scheme.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.ADVICE_ASSISTANCE_ADVOCACY;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.ADVOCACY_APPEALS_REVIEWS;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.MAGISTRATES_COURT;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.POLICE_STATION;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.SENDING_HEARING;
import static uk.gov.justice.laa.fee.scheme.enums.ErrorType.ERR_ALL_FEE_CODE;
import static uk.gov.justice.laa.fee.scheme.enums.ErrorType.ERR_CRIME_REP_ORDER_DATE;
import static uk.gov.justice.laa.fee.scheme.enums.ErrorType.ERR_CRIME_REP_ORDER_DATE_MISSING;
import static uk.gov.justice.laa.fee.scheme.enums.ErrorType.ERR_CRIME_UFN_DATE;
import static uk.gov.justice.laa.fee.scheme.enums.ErrorType.ERR_CRIME_UFN_MISSING;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.entity.FeeSchemesEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.enums.CourtDesignationType;
import uk.gov.justice.laa.fee.scheme.enums.FeeType;
import uk.gov.justice.laa.fee.scheme.exception.ValidationException;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;

@ExtendWith(MockitoExtension.class)
class CrimeValidationServiceTest {

  @InjectMocks
  private CrimeValidationService crimeValidationService;

  private static FeeCalculationRequest getFeeCalculationRequest() {
    return FeeCalculationRequest.builder()
        .feeCode("INVC")
        .startDate(LocalDate.of(2022, 1, 1))
        .vatIndicator(Boolean.TRUE)
        .policeStationSchemeId("1003")
        .policeStationId("NA2093")
        .uniqueFileNumber("010122/456")
        .netDisbursementAmount(50.50)
        .disbursementVatAmount(20.15)
        .build();
  }

  private FeeEntity policeStationFeeEntity(FeeSchemesEntity feeSchemesEntity) {
    return FeeEntity.builder()
        .feeCode("INVC")
        .feeScheme(feeSchemesEntity)
        .profitCostLimit(new BigDecimal("123.56"))
        .fixedFee(new BigDecimal("200.56"))
        .categoryType(POLICE_STATION)
        .feeType(FeeType.FIXED)
        .build();
  }

  @Test
  void getValidFeeEntity_whenSingleFeeEntity_shouldReturnValidResponse() {
    FeeCalculationRequest feeCalculationRequest = getFeeCalculationRequest();

    FeeSchemesEntity feeSchemesEntity = FeeSchemesEntity.builder().schemeCode("POL_FS2022")
        .validFrom(LocalDate.of(2022, 1, 1)).build();

    List<FeeEntity> feeEntityList = List.of(policeStationFeeEntity(feeSchemesEntity));

    FeeEntity result = crimeValidationService.getValidFeeEntity(feeEntityList, feeCalculationRequest);

    assertThat(result).isNotNull();
    assertThat(result.getFeeCode()).isEqualTo("INVC");
    assertThat(result.getFixedFee()).isEqualTo("200.56");
    assertThat(result.getFeeScheme().getSchemeCode()).isEqualTo("POL_FS2022");
  }

  @Test
  void getValidFeeEntity_whenMultipleFeeEntitiesAndFeeSchemeEnded_shouldReturnValidResponse() {

    FeeCalculationRequest feeCalculationRequest = getFeeCalculationRequest();

    FeeSchemesEntity feeSchemesEntity1 = FeeSchemesEntity.builder().schemeCode("POL_FS2022")
        .validFrom(LocalDate.of(2022, 1, 1)).validTo(LocalDate.of(2023, 1, 1)).build();

    FeeEntity feeEntity1 = FeeEntity.builder()
        .feeCode("INVC")
        .feeScheme(feeSchemesEntity1)
        .profitCostLimit(new BigDecimal("954.56"))
        .fixedFee(new BigDecimal("1256.66"))
        .categoryType(POLICE_STATION)
        .feeType(FeeType.FIXED)
        .build();

    FeeSchemesEntity feeSchemesEntity2 = FeeSchemesEntity.builder().schemeCode("POL_FS2016")
        .validFrom(LocalDate.of(2016, 1, 1)).build();

    FeeEntity feeEntity2 = FeeEntity.builder()
        .feeCode("INVC")
        .feeScheme(feeSchemesEntity2)
        .profitCostLimit(new BigDecimal("422.56"))
        .fixedFee(new BigDecimal("1698.38"))
        .categoryType(POLICE_STATION)
        .feeType(FeeType.FIXED)
        .build();

    List<FeeEntity> feeEntityList = List.of(feeEntity1, feeEntity2);

    FeeEntity result = crimeValidationService.getValidFeeEntity(feeEntityList, feeCalculationRequest);

    assertThat(result).isNotNull();
    assertThat(result.getFeeCode()).isEqualTo("INVC");
    assertThat(result.getFixedFee()).isEqualTo("1256.66");
    assertThat(result.getFeeScheme().getSchemeCode()).isEqualTo("POL_FS2022");
  }

  @Test
  void getValidFeeEntity_whenMultipleFeeEntities_returnsValidFeeEntity() {

    FeeCalculationRequest feeCalculationRequest = getFeeCalculationRequest();

    FeeSchemesEntity feeSchemesEntity1 = FeeSchemesEntity.builder().schemeCode("POL_FS2022")
        .validFrom(LocalDate.of(2022, 1, 1)).build();

    FeeEntity feeEntity1 = policeStationFeeEntity(feeSchemesEntity1);

    FeeSchemesEntity feeSchemesEntity2 = FeeSchemesEntity.builder().schemeCode("POL_FS2016")
        .validFrom(LocalDate.of(2016, 1, 1)).build();

    FeeEntity feeEntity2 = policeStationFeeEntity(feeSchemesEntity2);

    List<FeeEntity> feeEntityList = List.of(feeEntity1, feeEntity2);

    FeeEntity result = crimeValidationService.getValidFeeEntity(feeEntityList, feeCalculationRequest);

    assertThat(result).isNotNull();
    assertThat(result.getFeeCode()).isEqualTo("INVC");
    assertThat(result.getFixedFee()).isEqualTo("200.56");
    assertThat(result.getFeeScheme().getSchemeCode()).isEqualTo("POL_FS2022");
  }

  @Test
  void getValidFeeEntity_whenSingleFeeEntity_And_StartDateIsGreaterThanFeeSchemeStartDate_shouldReturnValidResponse() {

    FeeCalculationRequest feeCalculationRequest = getFeeCalculationRequest();

    FeeSchemesEntity feeSchemesEntity = FeeSchemesEntity.builder().schemeCode("POL_FS2022")
        .validFrom(LocalDate.of(2021, 12, 31)).build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("INVC")
        .feeScheme(feeSchemesEntity)
        .profitCostLimit(new BigDecimal("123.56"))
        .fixedFee(new BigDecimal("200.56"))
        .categoryType(POLICE_STATION)
        .feeType(FeeType.FIXED)
        .build();

    List<FeeEntity> feeEntityList = List.of(feeEntity);

    FeeEntity result = crimeValidationService.getValidFeeEntity(feeEntityList, feeCalculationRequest);

    assertThat(result).isNotNull();
    assertThat(result.getFeeCode()).isEqualTo("INVC");
    assertThat(result.getFixedFee()).isEqualTo("200.56");
    assertThat(result.getFeeScheme().getSchemeCode()).isEqualTo("POL_FS2022");
  }

  @Test
  void getValidFeeEntity_whenAdviceAssistanceAdvocacyClaimSubmitted_shouldReturnValidResponse() {

    FeeCalculationRequest feeCalculationRequest = getFeeCalculationRequest();
    feeCalculationRequest.setFeeCode("PROD");
    feeCalculationRequest.setCaseConcludedDate(LocalDate.of(2021, 12, 31));
    FeeSchemesEntity feeSchemesEntity = FeeSchemesEntity.builder().schemeCode("AAA_FS2016")
        .validFrom(LocalDate.of(2021, 12, 31)).build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("PROD")
        .feeScheme(feeSchemesEntity)
        .categoryType(ADVICE_ASSISTANCE_ADVOCACY)
        .feeType(FeeType.HOURLY)
        .build();

    List<FeeEntity> feeEntityList = List.of(feeEntity);

    FeeEntity result = crimeValidationService.getValidFeeEntity(feeEntityList, feeCalculationRequest);

    assertThat(result).isNotNull();
    assertThat(result.getFeeCode()).isEqualTo("PROD");
    assertThat(result.getFeeScheme().getSchemeCode()).isEqualTo("AAA_FS2016");
  }

  @Test
  void test_whenClaimReceivedWithInvalidFeeCode_And_Valid_RepOrderDate_shouldThrowException() {

    FeeCalculationRequest feeCalculationRequest = getFeeCalculationRequest();
    feeCalculationRequest.setFeeCode("XYZ");
    feeCalculationRequest.setRepresentationOrderDate(LocalDate.of(2022, 11, 1));
    FeeSchemesEntity feeSchemesEntity = FeeSchemesEntity.builder().schemeCode("SEND_HEAR_FS2022")
        .validFrom(LocalDate.of(2021, 12, 31)).build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("XYZ")
        .feeScheme(feeSchemesEntity)
        .fixedFee(new BigDecimal("208.61"))
        .categoryType(SENDING_HEARING)
        .feeType(FeeType.FIXED)
        .build();

    List<FeeEntity> feeEntityList = List.of(feeEntity);

    assertThatThrownBy(() -> crimeValidationService.getValidFeeEntity(feeEntityList, feeCalculationRequest))
        .isInstanceOf(ValidationException.class)
        .hasFieldOrPropertyWithValue("error", ERR_CRIME_REP_ORDER_DATE)
        .hasMessage("ERRCRM12 - Fee Code is not valid for the Representation Order Date provided.");
  }


  @Test
  void getValidFeeEntity_whenGivenInvalidFeeCode_throwsException() {
    List<FeeEntity> feeEntityList = List.of();
    FeeCalculationRequest feeCalculationRequest = getFeeCalculationRequest();

    assertThatThrownBy(() -> crimeValidationService.getValidFeeEntity(feeEntityList, feeCalculationRequest))
        .isInstanceOf(ValidationException.class)
        .hasFieldOrPropertyWithValue("error", ERR_ALL_FEE_CODE)
        .hasMessage("ERRALL1 - Enter a valid Fee Code.");

  }

  @Test
  void getValidFeeEntity_whenCrimeFeeCodeAndUfnStartDateIsInvalid_shouldThrowException() {
    FeeCalculationRequest feeCalculationRequest = getFeeCalculationRequest();

    FeeSchemesEntity feeSchemesEntity = FeeSchemesEntity.builder().schemeCode("POL_FS2025")
        .validFrom(LocalDate.of(2025, 10, 1)).build();

    FeeEntity feeEntity = policeStationFeeEntity(feeSchemesEntity);

    List<FeeEntity> feeEntityList = List.of(feeEntity);

    assertThatThrownBy(() -> crimeValidationService.getValidFeeEntity(feeEntityList, feeCalculationRequest))
        .isInstanceOf(ValidationException.class)
        .hasFieldOrPropertyWithValue("error", ERR_CRIME_UFN_DATE)
        .hasMessage("ERRCRM1 - Fee Code is not valid for the Case Start Date.");
  }

  @Test
  void getValidFeeEntity_whenCrimeFeeCodeAndUfnIsMissing_shouldThrowException() {
    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode("INVC")
        .startDate(LocalDate.of(2022, 1, 1))
        .vatIndicator(Boolean.TRUE)
        .policeStationSchemeId("1003")
        .policeStationId("NA2093")
        .netDisbursementAmount(50.50)
        .disbursementVatAmount(20.15)
        .build();

    FeeSchemesEntity feeSchemesEntity = FeeSchemesEntity.builder().schemeCode("POL_FS2025")
        .validFrom(LocalDate.of(2025, 10, 1)).build();

    FeeEntity feeEntity = policeStationFeeEntity(feeSchemesEntity);

    List<FeeEntity> feeEntityList = List.of(feeEntity);

    assertThatThrownBy(() -> crimeValidationService.getValidFeeEntity(feeEntityList, feeCalculationRequest))
        .isInstanceOf(ValidationException.class)
        .hasFieldOrPropertyWithValue("error", ERR_CRIME_UFN_MISSING)
        .hasMessage("ERRCRM7 - Enter a UFN.");
  }

  @ParameterizedTest
  @CsvSource({
      "PROE1, MAGISTRATES_COURT",
      "PROF1, MAGISTRATES_COURT",
      "YOUE4, YOUTH_COURT",
      "YOUF4, YOUTH_COURT"
  })
  void getValidFeeEntity_whenCriminalProceedingsAndRepOrderDateIsMissing_shouldThrowException(String feeCode, String categoryType) {
    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode(feeCode)
        .startDate(LocalDate.of(2025, 1, 1))
        .vatIndicator(Boolean.TRUE)
        .netDisbursementAmount(50.50)
        .disbursementVatAmount(20.15)
        .build();

    CategoryType courtCategoryType = CategoryType.valueOf(categoryType);

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode(feeCode)
        .feeScheme(FeeSchemesEntity.builder().build())
        .fixedFee(new BigDecimal("200"))
        .categoryType(courtCategoryType)
        .courtDesignationType(CourtDesignationType.DESIGNATED)
        .feeType(FeeType.FIXED).build();

    List<FeeEntity> feeEntityList = List.of(feeEntity);

    assertThatThrownBy(() -> crimeValidationService.getValidFeeEntity(feeEntityList, feeCalculationRequest))
        .isInstanceOf(ValidationException.class)
        .hasFieldOrPropertyWithValue("error", ERR_CRIME_REP_ORDER_DATE_MISSING)
        .hasMessage("ERRCRM8 - Enter a representation order date.");
  }

  @Test
  void getValidFeeEntity_whenCrimeFeeCodeAndRepOrderDateIsInvalid_shouldThrowException() {
    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode("PROJ5")
        .representationOrderDate(LocalDate.of(2022, 1, 1))
        .uniqueFileNumber("010525/456")
        .vatIndicator(Boolean.TRUE)
        .netDisbursementAmount(50.50)
        .disbursementVatAmount(20.15)
        .build();

    FeeSchemesEntity feeSchemesEntity = FeeSchemesEntity.builder().schemeCode("MAGS_COURT_FS2025")
        .validFrom(LocalDate.of(2025, 10, 1)).build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("PROJ5")
        .feeScheme(feeSchemesEntity)
        .fixedFee(new BigDecimal("200"))
        .categoryType(MAGISTRATES_COURT)
        .courtDesignationType(CourtDesignationType.DESIGNATED)
        .feeType(FeeType.FIXED).build();

    List<FeeEntity> feeEntityList = List.of(feeEntity);

    assertThatThrownBy(() -> crimeValidationService.getValidFeeEntity(feeEntityList, feeCalculationRequest))
        .isInstanceOf(ValidationException.class)
        .hasFieldOrPropertyWithValue("error", ERR_CRIME_REP_ORDER_DATE)
        .hasMessage("ERRCRM12 - Fee Code is not valid for the Representation Order Date provided.");
  }

  @Test
  void getValidFeeEntity_whenAdvocacyAssistanceFeeCodeAndRepOrderDateUfn_null_shouldThrowException() {
    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode("PROH")
        .representationOrderDate(null)
        .uniqueFileNumber(null)
        .vatIndicator(Boolean.TRUE)
        .netDisbursementAmount(50.50)
        .disbursementVatAmount(20.15)
        .build();

    FeeSchemesEntity feeSchemesEntity = FeeSchemesEntity.builder().schemeCode("MAGS_COURT_FS2025")
        .validFrom(LocalDate.of(2025, 10, 1)).build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("PROH")
        .feeScheme(feeSchemesEntity)
        .fixedFee(new BigDecimal("200"))
        .categoryType(ADVOCACY_APPEALS_REVIEWS)
        .feeType(FeeType.FIXED).build();

    List<FeeEntity> feeEntityList = List.of(feeEntity);

    assertThatThrownBy(() -> crimeValidationService.getValidFeeEntity(feeEntityList, feeCalculationRequest))
        .isInstanceOf(ValidationException.class)
        .hasFieldOrPropertyWithValue("error", ERR_CRIME_UFN_MISSING)
        .hasMessage("ERRCRM7 - Enter a UFN.");
  }

  @ParameterizedTest
  @CsvSource({  // null → blank
      "PROH,''",       // empty → blank
      "PROH, '   '",   // non-blank → false
      "PROH1,''",       // empty → blank
      "PROH1, '   '",   // non-blank → false
      "PROH2,''",       // empty → blank
      "PROH2, '   '"   // non-blank → false
  })
  void getValidFeeEntity_whenAdvocacyAssistanceFeeCodeAndRepOrderDateUfn_EmptyValueSupplied_shouldThrowException(String feeCode,
                                                                                                                 String ufn) {
    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode(feeCode)
        .representationOrderDate(null)
        .uniqueFileNumber(ufn)
        .vatIndicator(Boolean.TRUE)
        .netDisbursementAmount(50.50)
        .disbursementVatAmount(20.15)
        .build();

    FeeSchemesEntity feeSchemesEntity = FeeSchemesEntity.builder().schemeCode("MAGS_COURT_FS2025")
        .validFrom(LocalDate.of(2025, 10, 1)).build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode(feeCode)
        .feeScheme(feeSchemesEntity)
        .fixedFee(new BigDecimal("200"))
        .categoryType(ADVOCACY_APPEALS_REVIEWS)
        .feeType(FeeType.FIXED).build();

    List<FeeEntity> feeEntityList = List.of(feeEntity);

    assertThatThrownBy(() -> crimeValidationService.getValidFeeEntity(feeEntityList, feeCalculationRequest))
        .isInstanceOf(ValidationException.class)
        .hasFieldOrPropertyWithValue("error", ERR_CRIME_UFN_MISSING)
        .hasMessage("ERRCRM7 - Enter a UFN.");
  }


  @Test
  void getValidFeeEntity_whenAdvocacyAssistanceFeeCodeAndRepOrderDateSupplied_shouldReturnValidResponse() {
    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode("PROH")
        .representationOrderDate(LocalDate.of(2022, 12, 1))
        .uniqueFileNumber("010525/456")
        .vatIndicator(Boolean.TRUE)
        .netDisbursementAmount(50.50)
        .disbursementVatAmount(20.15)
        .caseConcludedDate(LocalDate.of(2022, 12, 11))
        .build();

    FeeSchemesEntity feeSchemesEntity = FeeSchemesEntity.builder().schemeCode("MAGS_COURT_FS2022")
        .validFrom(LocalDate.of(2022, 10, 1)).build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("PROH")
        .feeScheme(feeSchemesEntity)
        .fixedFee(new BigDecimal("200"))
        .categoryType(ADVOCACY_APPEALS_REVIEWS)
        .feeType(FeeType.FIXED).build();

    List<FeeEntity> feeEntityList = List.of(feeEntity);

    FeeEntity feeEntityResponse = crimeValidationService.getValidFeeEntity(feeEntityList, feeCalculationRequest);
    assertThat(feeEntityResponse).isNotNull();
    assertThat(feeEntityResponse.getFeeCode()).isEqualTo("PROH");
    assertThat(feeEntityResponse.getFixedFee()).isEqualTo("200");
    assertThat(feeEntityResponse.getFeeScheme().getSchemeCode()).isEqualTo("MAGS_COURT_FS2022");
  }


  @ParameterizedTest
  @CsvSource({
      "PROE1, 2025-11-12",
      "PROF4, 2025-11-12",
      "PROJ3, 2025-11-12",
      "YOUE2, 2025-11-12",
      "YOUE4, 2025-11-12",
      "YOUF4, 2025-11-12",
      "APPB, 2025-11-12",
      "PROW, 2025-11-12"
  })
  void testValidFeeCodes(String feeCode, String repDate) {
    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode(feeCode).representationOrderDate(LocalDate.parse(repDate)).build();
    assertTrue(crimeValidationService.isFeeCodeValidForRepOrderDate(feeCalculationRequest));
  }

  // Test invalid fee codes
  @ParameterizedTest
  @CsvSource({
      "INVALID, 2025-11-12",
      "PROX5, 2025-11-12",
      "YOUZ1, 2025-11-12",
      "APPZ, 2025-11-12"
  })
  void testInvalidFeeCodes(String feeCode, String repDate) {
    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode(feeCode).representationOrderDate(LocalDate.parse(repDate)).build();
    assertFalse(crimeValidationService.isFeeCodeValidForRepOrderDate(feeCalculationRequest));
  }

  // Test null repDate returns true
  @ParameterizedTest
  @ValueSource(strings = {"PROW", "APPB"})
  void testNullRepDate(String feeCode) {
    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode(feeCode).representationOrderDate(null).build();
    assertTrue(crimeValidationService.isFeeCodeValidForRepOrderDate(feeCalculationRequest));
  }

  @ParameterizedTest
  @CsvSource({
      "PROH, 2025-10-12, 2025-11-12",
      "PROH1, 2025-10-12, 2025-11-12",
      "PROH2, 2025-10-12, 2025-11-12",
  })
  void testValidAdvocacyAssistanceFeeCode(String feeCode, String caseConcludedDate) {
    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode(feeCode).caseConcludedDate(LocalDate.parse(caseConcludedDate)).build();

    boolean result = crimeValidationService.isFeeCodeValidForRepOrderDate(feeCalculationRequest);
    assertThat(result).isTrue();
  }
}
