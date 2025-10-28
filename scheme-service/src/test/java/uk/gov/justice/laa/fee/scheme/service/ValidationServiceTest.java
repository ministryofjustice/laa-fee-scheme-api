package uk.gov.justice.laa.fee.scheme.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.fee.scheme.enums.CaseType.CIVIL;
import static uk.gov.justice.laa.fee.scheme.enums.CaseType.CRIME;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.DISCRIMINATION;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.MAGS_COURT_DESIGNATED;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.POLICE_STATION;
import static uk.gov.justice.laa.fee.scheme.enums.ErrorType.ERR_ALL_FEE_CODE;
import static uk.gov.justice.laa.fee.scheme.enums.ErrorType.ERR_CIVIL_START_DATE;
import static uk.gov.justice.laa.fee.scheme.enums.ErrorType.ERR_CIVIL_START_DATE_TOO_OLD;
import static uk.gov.justice.laa.fee.scheme.enums.ErrorType.ERR_CRIME_REP_ORDER_DATE;
import static uk.gov.justice.laa.fee.scheme.enums.ErrorType.ERR_CRIME_UFN_DATE;
import static uk.gov.justice.laa.fee.scheme.enums.ErrorType.ERR_CRIME_UFN_MISSING;
import static uk.gov.justice.laa.fee.scheme.enums.ErrorType.ERR_FAMILY_LONDON_RATE;
import static uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner.TypeEnum.WARNING;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.entity.FeeSchemesEntity;
import uk.gov.justice.laa.fee.scheme.enums.AreaOfLawType;
import uk.gov.justice.laa.fee.scheme.enums.CaseType;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.enums.ErrorType;
import uk.gov.justice.laa.fee.scheme.enums.FeeType;
import uk.gov.justice.laa.fee.scheme.enums.Region;
import uk.gov.justice.laa.fee.scheme.exception.ValidationException;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner;

@ExtendWith(MockitoExtension.class)
class ValidationServiceTest {

  @Mock
  FeeDetailsService feeDetailsService;

  @InjectMocks
  private ValidationService validationService;


  @Test
  public void getValidFeeEntity_whenSingleFeeEntity_shouldReturnValidResponse() {
    FeeCalculationRequest feeCalculationRequest = getFeeCalculationRequest();

    FeeSchemesEntity feeSchemesEntity = FeeSchemesEntity.builder().schemeCode("POL_FS2022")
        .validFrom(LocalDate.of(2022, 1, 1)).build();

    List<FeeEntity> feeEntityList = List.of(policeStationFeeEntity(feeSchemesEntity));

    FeeEntity result = validationService.getValidFeeEntity(feeEntityList, feeCalculationRequest);

    assertThat(result).isNotNull();
    assertThat(result.getFeeCode()).isEqualTo("INVC");
    assertThat(result.getFixedFee()).isEqualTo("200.56");
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

    FeeEntity result = validationService.getValidFeeEntity(feeEntityList, feeCalculationRequest);

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

    FeeEntity result = validationService.getValidFeeEntity(feeEntityList, feeCalculationRequest);

    assertThat(result).isNotNull();
    assertThat(result.getFeeCode()).isEqualTo("INVC");
    assertThat(result.getFixedFee()).isEqualTo("1256.66");
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

    FeeEntity result = validationService.getValidFeeEntity(feeEntityList, feeCalculationRequest);

    assertThat(result).isNotNull();
    assertThat(result.getFeeCode()).isEqualTo("INVC");
    assertThat(result.getFixedFee()).isEqualTo("200.56");
    assertThat(result.getFeeScheme().getSchemeCode()).isEqualTo("POL_FS2022");
  }

  @Test
  public void getValidFeeEntity_whenGivenInvalidFeeCode_throwsException() {
    List<FeeEntity> feeEntityList = List.of();
    FeeCalculationRequest feeCalculationRequest = getFeeCalculationRequest();

    assertThatThrownBy(() -> validationService.getValidFeeEntity(feeEntityList, feeCalculationRequest))
        .isInstanceOf(ValidationException.class)
        .hasFieldOrPropertyWithValue("error", ERR_ALL_FEE_CODE)
        .hasMessage("ERRALL1 - Enter a valid Fee Code.");

  }

  @Test
  void getValidFeeEntity_whenCivilFeeCodeAndStartDateIsInvalid_shouldThrowException() {

    when(feeDetailsService.getCaseType("DISC")).thenReturn(CIVIL);

    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode("DISC")
        .startDate(LocalDate.of(2025, 5, 1))
        .netDisbursementAmount(50.50)
        .disbursementVatAmount(20.15)
        .build();

    FeeSchemesEntity feeSchemesEntity = FeeSchemesEntity.builder().schemeCode("DISC_FS2013")
        .validFrom(LocalDate.of(2025, 3, 1))
        .validTo(LocalDate.of(2025, 4, 1))
        .build();

    FeeEntity feeEntity = fixedFeeEntity("DISC", DISCRIMINATION, feeSchemesEntity);

    List<FeeEntity> feeEntityList = List.of(feeEntity);

    assertThatThrownBy(() -> validationService.getValidFeeEntity(feeEntityList, feeCalculationRequest))
        .isInstanceOf(ValidationException.class)
        .hasFieldOrPropertyWithValue("error", ERR_CIVIL_START_DATE)
        .hasMessage("ERRCIV1 - Fee Code is not valid for the Case Start Date.");
  }


  @Test
  void getValidFeeEntity_whenCivilFeeCodeAndDateTooFarInPast_shouldThrowException() {
    when(feeDetailsService.getCaseType("DISC")).thenReturn(CIVIL);

    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode("DISC")
        .startDate(LocalDate.of(2025, 5, 1))
        .netDisbursementAmount(50.50)
        .disbursementVatAmount(20.15)
        .build();

    FeeSchemesEntity feeSchemesEntity = FeeSchemesEntity.builder().schemeCode("FEE_SCHEME_2025")
        .validFrom(LocalDate.of(2025, 10, 1)).build();

    FeeEntity feeEntity = fixedFeeEntity("DISC", DISCRIMINATION, feeSchemesEntity);

    List<FeeEntity> feeEntityList = List.of(feeEntity);


    assertThatThrownBy(() -> validationService.getValidFeeEntity(feeEntityList, feeCalculationRequest))
        .isInstanceOf(ValidationException.class)
        .hasFieldOrPropertyWithValue("error", ERR_CIVIL_START_DATE_TOO_OLD)
        .hasMessage("ERRCIV2 - Case Start Date is too far in the past.");
  }

  @ParameterizedTest
  @CsvSource({
      "IACC, IMMIGRATION_ASYLUM, IMM_ASYLM_FS2020, 2020-04-01, 2013-04-29, ERR_IMM_ASYLUM_BETWEEN_DATE",
      "IACE, IMMIGRATION_ASYLUM, IMM_ASYLM_FS2023, 2023-04-01, 2020-04-29, ERR_IMM_ASYLUM_AFTER_DATE",

      "IACC, IMMIGRATION_ASYLUM, IMM_ASYLM_FS2020, 2020-04-01, 2011-04-29, ERR_CIVIL_START_DATE_TOO_OLD",
      "IACA, IMMIGRATION_ASYLUM, IMM_ASYLM_FS2013, 2013-04-01, 2011-04-29, ERR_CIVIL_START_DATE_TOO_OLD",
      "IACE, IMMIGRATION_ASYLUM, IMM_ASYLM_FS2023, 2023-04-01, 2011-04-29, ERR_CIVIL_START_DATE_TOO_OLD",
      "IDAS2, IMMIGRATION_ASYLUM, IMM_ASYLM_FS2013, 2013-04-01, 2011-04-29, ERR_CIVIL_START_DATE_TOO_OLD",
  })
  void getValidFeeEntity_whenCivilImmigrationAsylumFeeCodeAndDateTooFarInPast_shouldThrowException(String feeCode, CategoryType categoryType,
                                                                                                   String feeScheme, LocalDate feeSchemeDate,
                                                                                                   LocalDate claimStartDate, ErrorType expectedError) {
    when(feeDetailsService.getCaseType(feeCode)).thenReturn(CIVIL);

    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode(feeCode)
        .startDate(claimStartDate)
        .netDisbursementAmount(50.50)
        .disbursementVatAmount(20.15)
        .build();

    FeeSchemesEntity feeSchemesEntity = FeeSchemesEntity.builder().schemeCode(feeScheme)
        .validFrom(feeSchemeDate).build();

    FeeEntity feeEntity = fixedFeeEntity(feeCode, categoryType, feeSchemesEntity);

    List<FeeEntity> feeEntityList = List.of(feeEntity);

    assertThatThrownBy(() -> validationService.getValidFeeEntity(feeEntityList, feeCalculationRequest))
        .isInstanceOf(ValidationException.class)
        .hasFieldOrPropertyWithValue("error", expectedError)
        .hasMessageContaining(expectedError.getMessage());
  }

  @ParameterizedTest
  @CsvSource({
      "IACC, IMMIGRATION_ASYLUM, IMM_ASYLM_FS2020, 2020-04-01, 2023-04-29, 2024-04-29, ERR_IMM_ASYLUM_BETWEEN_DATE",
      "IACA, IMMIGRATION_ASYLUM, IMM_ASYLM_FS2020, 2020-04-01, 2023-04-29, 2024-04-29, ERR_IMM_ASYLUM_BEFORE_DATE",
  })
  void getValidFeeEntity_whenCivilImmigrationAsylumFeeCodeAndStartDateIsInvalid_shouldThrowException(String feeCode, CategoryType categoryType,
                                                                                                   String feeScheme, LocalDate feeSchemeStartDate,
                                                                                                   LocalDate feeSchemeEndDate,
                                                                                                   LocalDate claimStartDate, ErrorType expectedError) {
    when(feeDetailsService.getCaseType(feeCode)).thenReturn(CIVIL);

    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode(feeCode)
        .startDate(claimStartDate)
        .netDisbursementAmount(50.50)
        .disbursementVatAmount(20.15)
        .build();

    FeeSchemesEntity feeSchemesEntity = FeeSchemesEntity.builder().schemeCode(feeScheme)
        .validFrom(feeSchemeStartDate)
        .validTo(feeSchemeEndDate).build();

    FeeEntity feeEntity = fixedFeeEntity(feeCode, categoryType, feeSchemesEntity);

    List<FeeEntity> feeEntityList = List.of(feeEntity);

    assertThatThrownBy(() -> validationService.getValidFeeEntity(feeEntityList, feeCalculationRequest))
        .isInstanceOf(ValidationException.class)
        .hasFieldOrPropertyWithValue("error", expectedError)
        .hasMessageContaining(expectedError.getMessage());
  }

  @Test
  void getValidFeeEntity_whenCrimeFeeCodeAndUfnStartDateIsInvalid_shouldThrowException() {
    when(feeDetailsService.getCaseType("INVC")).thenReturn(CRIME);

    FeeCalculationRequest feeCalculationRequest = getFeeCalculationRequest();

    FeeSchemesEntity feeSchemesEntity = FeeSchemesEntity.builder().schemeCode("POL_FS2025")
        .validFrom(LocalDate.of(2025, 10, 1)).build();

    FeeEntity feeEntity = policeStationFeeEntity(feeSchemesEntity);

    List<FeeEntity> feeEntityList = List.of(feeEntity);

    assertThatThrownBy(() -> validationService.getValidFeeEntity(feeEntityList, feeCalculationRequest))
        .isInstanceOf(ValidationException.class)
        .hasFieldOrPropertyWithValue("error", ERR_CRIME_UFN_DATE)
        .hasMessage("ERRCRM1 - Fee Code is not valid for the Case Start Date.");
  }

  @Test
  void getValidFeeEntity_whenCrimeFeeCodeAndUfnIsMissing_shouldThrowException() {
    when(feeDetailsService.getCaseType("INVC")).thenReturn(CRIME);

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

    assertThatThrownBy(() -> validationService.getValidFeeEntity(feeEntityList, feeCalculationRequest))
        .isInstanceOf(ValidationException.class)
        .hasFieldOrPropertyWithValue("error", ERR_CRIME_UFN_MISSING)
        .hasMessage("ERRCRM7 - Enter a UFN.");
  }

  @Test
  void getValidFeeEntity_whenCrimeFeeCodeAndRepOrderDateIsInvalid_shouldThrowException() {
    when(feeDetailsService.getCaseType("PROJ5")).thenReturn(CRIME);

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
        .categoryType(MAGS_COURT_DESIGNATED)
        .feeType(FeeType.FIXED).build();

    List<FeeEntity> feeEntityList = List.of(feeEntity);

    assertThatThrownBy(() -> validationService.getValidFeeEntity(feeEntityList, feeCalculationRequest))
        .isInstanceOf(ValidationException.class)
        .hasFieldOrPropertyWithValue("error", ERR_CRIME_REP_ORDER_DATE)
        .hasMessage("ERRCRM12 - Fee Code is not valid for the Case Start Date.");
  }

  @ParameterizedTest()
  @CsvSource(value = {
      "true, LONDON, FAM_LON_FS2011",
      "false, NON_LONDON, FAM_NON_FS2011"
  })
  void getValidFeeEntity_whenFamilyCategoryAndGivenLondonRate_shouldReturnCorrectFeeEntity(Boolean isLondonRate,
                                                                                           Region expectedRegion,
                                                                                           String expectedFeeScheme) {

    FeeSchemesEntity feeSchemesEntity = FeeSchemesEntity.builder().schemeCode("FAM_LON_FS2011")
        .validFrom(LocalDate.of(2011, 1, 1)).build();

    FeeSchemesEntity feeSchemesEntity2 = FeeSchemesEntity.builder().schemeCode("FAM_NON_FS2011")
        .validFrom(LocalDate.of(2011, 1, 1)).build();

    FeeEntity feeEntity1 = familyFeeEntity(feeSchemesEntity, Region.LONDON);

    FeeEntity feeEntity2 = familyFeeEntity(feeSchemesEntity2, Region.NON_LONDON);

    List<FeeEntity> feeEntityList = List.of(feeEntity1, feeEntity2);

    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode("FPB010")
        .startDate(LocalDate.of(2025, 2, 11))
        .vatIndicator(Boolean.TRUE)
        .netDisbursementAmount(50.50)
        .disbursementVatAmount(20.15)
        .londonRate(isLondonRate)
        .build();

    FeeEntity result = validationService.getValidFeeEntity(feeEntityList, feeCalculationRequest);

    assertThat(result).isNotNull();
    assertThat(result.getFeeCode()).isEqualTo("FPB010");
    assertThat(result.getFixedFee()).isEqualTo("150");
    assertThat(result.getFeeScheme().getSchemeCode()).isEqualTo(expectedFeeScheme);
    assertThat(result.getRegion()).isEqualTo(expectedRegion);
  }

  @Test
  void getValidFeeEntity_whenFamilyCategoryAndLondonRateIsMissing_shouldThrowException() {
    when(feeDetailsService.getCaseType("FPB010")).thenReturn(CIVIL);

    FeeSchemesEntity feeSchemesEntity = FeeSchemesEntity.builder().schemeCode("FAM_LON_FS2011")
        .validFrom(LocalDate.of(2011, 1, 1)).build();

    FeeSchemesEntity feeSchemesEntity2 = FeeSchemesEntity.builder().schemeCode("FAM_NON_FS2011")
        .validFrom(LocalDate.of(2011, 1, 1)).build();

    FeeEntity feeEntity1 = familyFeeEntity(feeSchemesEntity, Region.LONDON);

    FeeEntity feeEntity2 = familyFeeEntity(feeSchemesEntity2, Region.NON_LONDON);

    List<FeeEntity> feeEntityList = List.of(feeEntity1, feeEntity2);

    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode("FPB010")
        .startDate(LocalDate.of(2025, 2, 11))
        .vatIndicator(Boolean.TRUE)
        .netDisbursementAmount(50.50)
        .disbursementVatAmount(20.15)
        .build();

    assertThatThrownBy(() -> validationService.getValidFeeEntity(feeEntityList, feeCalculationRequest))
        .isInstanceOf(ValidationException.class)
        .hasFieldOrPropertyWithValue("error", ERR_FAMILY_LONDON_RATE)
        .hasMessage("ERRFAM1 - London/non-London rate must be entered for the Fee Code used.");
  }


  @ParameterizedTest
  @ValueSource(strings = {"INVB1", "INVB2", "PROT", "PROU", "PROW", "PRIA", "PRIB1", "PRIB2", "PRIC1", "PRIC2",
      "PRID1", "PRID2", "PRID1", "PRID2", "PRIE1", "PRIE2" })
  public void checkForWarnings_whenGivenCrimeFeeCodeAndNetTravelCosts_returnsWarnings(String feeCode) {
    when(feeDetailsService.getCaseType(feeCode)).thenReturn(CRIME);

    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode(feeCode)
        .netTravelCosts(100.0)
        .build();

    List<ValidationMessagesInner> result = validationService.checkForWarnings(feeCalculationRequest);

    assertThat(result).containsExactly(ValidationMessagesInner.builder()
        .type(WARNING)
        .code("WARCRM1")
        .message("Cost not included. Travel costs cannot be claimed with Fee Code used.")
        .build());
  }

  @ParameterizedTest
  @ValueSource(strings = {"INVB1", "INVB2", "PROT", "PROU", "PROW" })
  public void checkForWarnings_whenGivenCrimeFeeCodeAndNetWaitingCosts_returnsWarnings(String feeCode) {
    when(feeDetailsService.getCaseType(feeCode)).thenReturn(CRIME);

    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode(feeCode)
        .netWaitingCosts(100.0)
        .build();

    List<ValidationMessagesInner> result = validationService.checkForWarnings(feeCalculationRequest);

    assertThat(result).containsExactly(ValidationMessagesInner.builder()
        .type(WARNING)
        .code("WARCRM2")
        .message("Cost not included. Waiting costs cannot be claimed with Fee Code used.")
        .build());
  }

  @Test
  public void checkForWarnings_whenGivenCrimeFeeCodeAndTravelCosts_returnsNoWarnings() {
    when(feeDetailsService.getCaseType("INVC")).thenReturn(CRIME);

    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode("INVC")
        .netWaitingCosts(100.0)
        .build();

    List<ValidationMessagesInner> result = validationService.checkForWarnings(feeCalculationRequest);

    assertThat(result).isEmpty();
  }

  @Test
  public void checkForWarnings_whenGivenCrimeFeeCodeAndNetWaitingCosts_returnsNoWarnings() {
    when(feeDetailsService.getCaseType("INVC")).thenReturn(CRIME);

    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode("INVC")
        .netTravelCosts(100.0)
        .build();

    List<ValidationMessagesInner> result = validationService.checkForWarnings(feeCalculationRequest);

    assertThat(result).isEmpty();
  }

  @Test
  public void checkForWarnings_whenGivenCivilFeeCode_returnsNoWarnings() {
    when(feeDetailsService.getCaseType("INVC")).thenReturn(CRIME);

    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode("INVC")
        .netTravelCosts(100.0)
        .build();

    List<ValidationMessagesInner> result = validationService.checkForWarnings(feeCalculationRequest);

    assertThat(result).isEmpty();
  }

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

  private FeeEntity fixedFeeEntity(String feeCode, CategoryType categoryType, FeeSchemesEntity feeSchemesEntity) {
    return FeeEntity.builder()
        .feeCode(feeCode)
        .feeScheme(feeSchemesEntity)
        .categoryType(categoryType)
        .feeType(FeeType.FIXED)
        .build();
  }

  private FeeEntity familyFeeEntity(FeeSchemesEntity feeSchemesEntity, Region region) {
    return FeeEntity.builder()
        .feeCode("FPB010")
        .feeScheme(feeSchemesEntity)
        .fixedFee(new BigDecimal("150"))
        .escapeThresholdLimit(new BigDecimal("300"))
        .region(region)
        .categoryType(CategoryType.FAMILY)
        .feeType(FeeType.FIXED)
        .build();
  }
}