package uk.gov.justice.laa.fee.scheme.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.MAGS_COURT_DESIGNATED;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.POLICE_STATION;
import static uk.gov.justice.laa.fee.scheme.enums.ValidationError.ERRALL1;
import static uk.gov.justice.laa.fee.scheme.enums.ValidationError.ERRCIV1;
import static uk.gov.justice.laa.fee.scheme.enums.ValidationError.ERRCIV2;
import static uk.gov.justice.laa.fee.scheme.enums.ValidationError.ERRCRM1;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.entity.FeeSchemesEntity;
import uk.gov.justice.laa.fee.scheme.enums.AreaOfLawType;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.enums.FeeType;
import uk.gov.justice.laa.fee.scheme.enums.Region;
import uk.gov.justice.laa.fee.scheme.exception.ValidationException;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.repository.FeeRepository;

@ExtendWith(MockitoExtension.class)
class FeeDataServiceTest {

  @Mock
  FeeRepository feeRepository;

  @Mock
  FeeDetailsService feeDetailsService;

  @InjectMocks
  private FeeDataService feeDataService;

  private static FeeCalculationRequest getFeeCalculationRequest() {
    return FeeCalculationRequest.builder()
        .feeCode("INVC")
        .startDate(LocalDate.of(2017, 7, 29))
        .vatIndicator(Boolean.TRUE)
        .policeStationSchemeId("1003")
        .policeStationId("NA2093")
        .uniqueFileNumber("010122/456")
        .netDisbursementAmount(50.50)
        .disbursementVatAmount(20.15)
        .build();
  }

  @Test
  void getFeeEntity_whenFeeSchemeIdAndFeeCodePresentInDatabase_shouldReturnValidResponse() {

    FeeCalculationRequest feeData = getFeeCalculationRequest();

    FeeSchemesEntity feeSchemesEntity = FeeSchemesEntity.builder().schemeCode("POL_FS2022")
        .validFrom(LocalDate.of(2022, 1, 1)).build();

    FeeEntity feeEntity = policeStationFeeEntity(feeSchemesEntity);

    when(feeRepository.findByFeeCode(any())).thenReturn(List.of(feeEntity));
    when(feeDetailsService.getAreaOfLaw("INVC")).thenReturn(AreaOfLawType.CRIME_LOWER);

    FeeEntity feeEntityResponse = feeDataService.getFeeEntity(feeData);

    assertThat(feeEntityResponse).isNotNull();
    assertThat(feeEntityResponse.getFeeCode()).isEqualTo("INVC");

  }

  @Test
  void getFeeEntity_whenMultipleRecordsPresentInFeeTable_shouldReturnValidResponse() {

    FeeCalculationRequest feeData = getFeeCalculationRequest();

    FeeSchemesEntity feeSchemesEntity1 = FeeSchemesEntity.builder().schemeCode("POL_FS2022")
        .validFrom(LocalDate.of(2022, 1, 1)).build();

    FeeEntity feeEntity1 = policeStationFeeEntity(feeSchemesEntity1);

    FeeSchemesEntity feeSchemesEntity2 = FeeSchemesEntity.builder().schemeCode("POL_FS2016")
        .validFrom(LocalDate.of(2016, 1, 1)).build();

    FeeEntity feeEntity2 = policeStationFeeEntity(feeSchemesEntity2);

    when(feeRepository.findByFeeCode(any())).thenReturn(List.of(feeEntity1, feeEntity2));
    when(feeDetailsService.getAreaOfLaw("INVC")).thenReturn(AreaOfLawType.CRIME_LOWER);

    FeeEntity feeEntityResponse = feeDataService.getFeeEntity(feeData);

    assertThat(feeEntityResponse).isNotNull();
    assertThat(feeEntityResponse.getFeeCode()).isEqualTo("INVC");
  }

  @Test
  void getFeeEntity_whenMultipleRecordsPresentInFeeTableAndFeeSchemeEnded_shouldReturnValidResponse() {

    FeeCalculationRequest feeData = getFeeCalculationRequest();

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

    when(feeRepository.findByFeeCode(any())).thenReturn(List.of(feeEntity1, feeEntity2));
    when(feeDetailsService.getAreaOfLaw("INVC")).thenReturn(AreaOfLawType.CRIME_LOWER);

    FeeEntity feeEntityResponse = feeDataService.getFeeEntity(feeData);

    assertThat(feeEntityResponse).isNotNull();
    assertThat(feeEntityResponse.getFeeCode()).isEqualTo("INVC");
    assertThat(feeEntityResponse.getFixedFee()).isEqualTo("1256.66");
  }

  @Test
  void getFeeEntity_whenMultipleRecordsPresentInFeeTableAndFeeSchemeEndedForMagCourt_shouldReturnValidResponse() {

    FeeCalculationRequest feeData = getFeeCalculationRequest();
    feeData.setFeeCode("PROJ7");
    feeData.setRepresentationOrderDate(LocalDate.of(2022, 9, 29));
    feeData.setDisbursementVatAmount(15.50);
    feeData.setNetDisbursementAmount(20.00);

    FeeSchemesEntity feeSchemesEntity1 = FeeSchemesEntity.builder().schemeCode("MAGS_COURT_FS2016")
        .validFrom(LocalDate.of(2016, 4, 1)).validTo(LocalDate.of(2022, 9, 29)).build();

    FeeEntity feeEntity1 = FeeEntity.builder()
        .feeCode("PROJ7")
        .feeScheme(feeSchemesEntity1)
        .fixedFee(new BigDecimal("471.81"))
        .categoryType(MAGS_COURT_DESIGNATED)
        .feeType(FeeType.FIXED)
        .build();

    FeeSchemesEntity feeSchemesEntity2 = FeeSchemesEntity.builder().schemeCode("MAGS_COURT_FS2022")
        .validFrom(LocalDate.of(2016, 1, 1)).build();

    FeeEntity feeEntity2 = FeeEntity.builder()
        .feeCode("PROJ7")
        .feeScheme(feeSchemesEntity2)
        .fixedFee(new BigDecimal("542.58"))
        .categoryType(MAGS_COURT_DESIGNATED)
        .feeType(FeeType.FIXED)
        .build();

    when(feeRepository.findByFeeCode(any())).thenReturn(List.of(feeEntity1, feeEntity2));
    when(feeDetailsService.getAreaOfLaw("PROJ7")).thenReturn(AreaOfLawType.CRIME_LOWER);

    FeeEntity feeEntityResponse = feeDataService.getFeeEntity(feeData);

    assertThat(feeEntityResponse).isNotNull();
    assertThat(feeEntityResponse.getFeeCode()).isEqualTo("PROJ7");
    assertThat(feeEntityResponse.getFixedFee()).isEqualTo("471.81");
    assertThat(feeEntityResponse.getFeeScheme().getSchemeCode()).isEqualTo("MAGS_COURT_FS2016");
  }

  @Test
  void getFeeEntity_whenMultipleRecordsPresentInFeeTableForMagCourt_shouldReturnValidResponse() {

    FeeCalculationRequest feeData = getFeeCalculationRequest();
    feeData.setFeeCode("PROJ7");
    feeData.setRepresentationOrderDate(LocalDate.of(2022, 9, 30));
    feeData.setDisbursementVatAmount(15.50);
    feeData.setNetDisbursementAmount(20.00);

    FeeSchemesEntity feeSchemesEntity1 = FeeSchemesEntity.builder().schemeCode("MAGS_COURT_FS2016")
        .validFrom(LocalDate.of(2016, 4, 1)).validTo(LocalDate.of(2022, 9, 29)).build();

    FeeEntity feeEntity1 = FeeEntity.builder()
        .feeCode("PROJ7")
        .feeScheme(feeSchemesEntity1)
        .fixedFee(new BigDecimal("471.81"))
        .categoryType(MAGS_COURT_DESIGNATED)
        .feeType(FeeType.FIXED)
        .build();

    FeeSchemesEntity feeSchemesEntity2 = FeeSchemesEntity.builder().schemeCode("MAGS_COURT_FS2022")
        .validFrom(LocalDate.of(2016, 1, 1)).build();

    FeeEntity feeEntity2 = FeeEntity.builder()
        .feeCode("PROJ7")
        .feeScheme(feeSchemesEntity2)
        .fixedFee(new BigDecimal("542.58"))
        .categoryType(MAGS_COURT_DESIGNATED)
        .feeType(FeeType.FIXED)
        .build();

    when(feeRepository.findByFeeCode(any())).thenReturn(List.of(feeEntity1, feeEntity2));
    when(feeDetailsService.getAreaOfLaw("PROJ7")).thenReturn(AreaOfLawType.CRIME_LOWER);

    FeeEntity feeEntityResponse = feeDataService.getFeeEntity(feeData);

    assertThat(feeEntityResponse).isNotNull();
    assertThat(feeEntityResponse.getFeeCode()).isEqualTo("PROJ7");
    assertThat(feeEntityResponse.getFixedFee()).isEqualTo("542.58");
    assertThat(feeEntityResponse.getFeeScheme().getSchemeCode()).isEqualTo("MAGS_COURT_FS2022");
  }

  @Test
  void getFeeEntity_whenSingleRecordOfFeePresentInFeeTable_And_StartDateIsGreaterThanFeeSchemeStartDate_shouldReturnValidResponse() {

    FeeCalculationRequest feeData = getFeeCalculationRequest();

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

    when(feeRepository.findByFeeCode(any())).thenReturn(List.of(feeEntity));
    when(feeDetailsService.getAreaOfLaw("INVC")).thenReturn(AreaOfLawType.CRIME_LOWER);

    FeeEntity feeEntityResponse = feeDataService.getFeeEntity(feeData);

    assertThat(feeEntityResponse).isNotNull();
    assertThat(feeEntityResponse.getFeeCode()).isEqualTo("INVC");
    assertThat(feeEntityResponse.getFixedFee()).isEqualTo("200.56");
  }

  @Test
  void getFeeEntity_whenSingleRecordOfFeePresentInFeeTable_shouldReturnValidResponse() {

    FeeCalculationRequest feeData = getFeeCalculationRequest();

    FeeSchemesEntity feeSchemesEntity = FeeSchemesEntity.builder().schemeCode("POL_FS2022")
        .validFrom(LocalDate.of(2022, 1, 1)).build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("INVC")
        .feeScheme(feeSchemesEntity)
        .profitCostLimit(new BigDecimal("123.56"))
        .fixedFee(new BigDecimal("200.56"))
        .categoryType(POLICE_STATION)
        .feeType(FeeType.FIXED)
        .build();

    when(feeRepository.findByFeeCode(any())).thenReturn(List.of(feeEntity));
    when(feeDetailsService.getAreaOfLaw("INVC")).thenReturn(AreaOfLawType.CRIME_LOWER);

    FeeEntity feeEntityResponse = feeDataService.getFeeEntity(feeData);

    assertThat(feeEntityResponse).isNotNull();
    assertThat(feeEntityResponse.getFeeCode()).isEqualTo("INVC");
    assertThat(feeEntityResponse.getFixedFee()).isEqualTo("200.56");
  }

  @ParameterizedTest
  @CsvSource({
      "DISC, DISCRIMINATION, LEGAL_HELP",
      "ASSA, MEDIATION, MEDIATION",
  })
  void getFeeEntity_whenCivilFeeCodeAndStartDateIsInvalid_shouldThrowException(String feeCode, CategoryType categoryType,
                                                                               AreaOfLawType areaOfLawType) {

    FeeCalculationRequest feeData = FeeCalculationRequest.builder()
        .feeCode(feeCode)
        .startDate(LocalDate.of(2025, 5, 1))
        .netDisbursementAmount(50.50)
        .disbursementVatAmount(20.15)
        .build();

    FeeSchemesEntity feeSchemesEntity = FeeSchemesEntity.builder().schemeCode("DISC_FS2013")
        .validFrom(LocalDate.of(2025, 3, 1))
        .validTo(LocalDate.of(2025, 4, 1))
        .build();

    FeeEntity feeEntity = fixedFeeEntity(feeCode, categoryType, feeSchemesEntity);

    when(feeRepository.findByFeeCode(feeCode)).thenReturn(List.of(feeEntity));
    when(feeDetailsService.getAreaOfLaw(feeCode)).thenReturn(areaOfLawType);

    assertThatThrownBy(() -> feeDataService.getFeeEntity(feeData))
        .isInstanceOf(ValidationException.class)
        .hasFieldOrPropertyWithValue("error", ERRCIV1)
        .hasMessage("Fee Code is not valid for Case Start Date.");
  }

  @ParameterizedTest
  @CsvSource({
      "DISC, DISCRIMINATION, LEGAL_HELP",
      "ASSA, MEDIATION, MEDIATION",
  })
  void getFeeEntity_whenCivilFeeCodeAndDateTooFarInPast_shouldThrowException(String feeCode, CategoryType categoryType,
                                                                             AreaOfLawType areaOfLawType) {

    FeeCalculationRequest feeData = FeeCalculationRequest.builder()
        .feeCode(feeCode)
        .startDate(LocalDate.of(2025, 5, 1))
        .netDisbursementAmount(50.50)
        .disbursementVatAmount(20.15)
        .build();

    FeeSchemesEntity feeSchemesEntity = FeeSchemesEntity.builder().schemeCode("FEE_SCHEME_2025")
        .validFrom(LocalDate.of(2025, 10, 1)).build();

    FeeEntity feeEntity = fixedFeeEntity(feeCode, categoryType, feeSchemesEntity);

    when(feeRepository.findByFeeCode(feeCode)).thenReturn(List.of(feeEntity));
    when(feeDetailsService.getAreaOfLaw(feeCode)).thenReturn(areaOfLawType);

    assertThatThrownBy(() -> feeDataService.getFeeEntity(feeData))
        .isInstanceOf(ValidationException.class)
        .hasFieldOrPropertyWithValue("error", ERRCIV2)
        .hasMessage("Case Start Date is too far in the past.");
  }

  @Test
  void getFeeEntity_whenCrimeFeeCodeAndStartDateIsInvalid_shouldThrowException() {

    FeeCalculationRequest feeData = getFeeCalculationRequest();

    FeeSchemesEntity feeSchemesEntity = FeeSchemesEntity.builder().schemeCode("POL_FS2025")
        .validFrom(LocalDate.of(2025, 10, 1)).build();

    FeeEntity feeEntity = policeStationFeeEntity(feeSchemesEntity);

    when(feeRepository.findByFeeCode(any())).thenReturn(List.of(feeEntity));
    when(feeDetailsService.getAreaOfLaw("INVC")).thenReturn(AreaOfLawType.CRIME_LOWER);

    assertThatThrownBy(() -> feeDataService.getFeeEntity(feeData))
        .isInstanceOf(ValidationException.class)
        .hasFieldOrPropertyWithValue("error", ERRCRM1)
        .hasMessage("Fee Code is not valid for Case Start Date.");
  }

  @Test
  void test_whenNoRecordPresentInFeeTable_shouldThrowException() {

    FeeCalculationRequest feeData = getFeeCalculationRequest();

    when(feeRepository.findByFeeCode("INVC")).thenReturn(List.of());

    assertThatThrownBy(() -> feeDataService.getFeeEntity(feeData))
        .isInstanceOf(ValidationException.class)
        .hasFieldOrPropertyWithValue("error", ERRALL1)
        .hasMessage("Enter a valid Fee Code.");
  }

  @ParameterizedTest()
  @CsvSource(value = {
      "true, LONDON",
      "false, NON_LONDON",
  })
  void getFeeEntity_whenFamilyCategoryAndGivenLondonRate_shouldReturnCorrectFeeEntity(Boolean isLondonRate, Region expectedRegion) {

    FeeSchemesEntity feeSchemesEntity = FeeSchemesEntity.builder().schemeCode("FAM_LON_FS2011")
        .validFrom(LocalDate.of(2011, 1, 1)).build();

    FeeSchemesEntity feeSchemesEntity2 = FeeSchemesEntity.builder().schemeCode("FAM_NON_FS2011")
        .validFrom(LocalDate.of(2011, 1, 1)).build();

    FeeEntity feeEntity = familyFeeEntity(feeSchemesEntity, Region.LONDON);

    FeeEntity feeEntity2 = familyFeeEntity(feeSchemesEntity2, Region.NON_LONDON);

    when(feeRepository.findByFeeCode(any())).thenReturn(List.of(feeEntity, feeEntity2));

    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode("FPB010")
        .startDate(LocalDate.of(2025, 2, 11))
        .vatIndicator(Boolean.TRUE)
        .netDisbursementAmount(50.50)
        .disbursementVatAmount(20.15)
        .londonRate(isLondonRate)
        .build();

    FeeEntity feeEntityResponse = feeDataService.getFeeEntity(feeCalculationRequest);

    assertThat(feeEntityResponse).isNotNull();
    assertThat(feeEntityResponse.getFeeCode()).isEqualTo("FPB010");
    assertThat(feeEntityResponse.getFixedFee()).isEqualTo(new BigDecimal("150"));
    assertThat(feeEntityResponse.getRegion()).isEqualTo(expectedRegion);
  }

  @Test
  void getFeeEntity_whenFamilyCategoryAndLondonRateIsMissing_shouldThrowException() {
    FeeSchemesEntity feeSchemesEntity = FeeSchemesEntity.builder().schemeCode("FAM_LON_FS2011")
        .validFrom(LocalDate.of(2011, 1, 1)).build();

    FeeSchemesEntity feeSchemesEntity2 = FeeSchemesEntity.builder().schemeCode("FAM_NON_FS2011")
        .validFrom(LocalDate.of(2011, 1, 1)).build();

    FeeEntity feeEntity = familyFeeEntity(feeSchemesEntity, Region.LONDON);

    FeeEntity feeEntity2 = familyFeeEntity(feeSchemesEntity2, Region.NON_LONDON);

    when(feeRepository.findByFeeCode(any())).thenReturn(List.of(feeEntity, feeEntity2));

    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode("FPB010")
        .startDate(LocalDate.of(2025, 2, 11))
        .vatIndicator(Boolean.TRUE)
        .netDisbursementAmount(50.50)
        .disbursementVatAmount(20.15)
        .build();

    assertThatThrownBy(() -> feeDataService.getFeeEntity(feeCalculationRequest))
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("Fee Code is not valid for Case Start Date.");
  }

  private FeeEntity fixedFeeEntity(String feeCode, CategoryType categoryType, FeeSchemesEntity feeSchemesEntity) {
    return  FeeEntity.builder()
        .feeCode(feeCode)
        .feeScheme(feeSchemesEntity)
        .categoryType(categoryType)
        .feeType(FeeType.FIXED)
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