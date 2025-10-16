package uk.gov.justice.laa.fee.scheme.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.POLICE_STATION;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.entity.FeeSchemesEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.enums.FeeType;
import uk.gov.justice.laa.fee.scheme.enums.Region;
import uk.gov.justice.laa.fee.scheme.exception.FeeNotFoundException;
import uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil;
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

    FeeEntity feeEntityResponse = feeDataService.getFeeEntity(feeData);

    assertThat(feeEntityResponse).isNotNull();
    assertThat(feeEntityResponse.getFeeCode()).isEqualTo("INVC");
    assertThat(feeEntityResponse.getFixedFee()).isEqualTo("1256.66");
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

    FeeEntity feeEntityResponse = feeDataService.getFeeEntity(feeData);

    assertThat(feeEntityResponse).isNotNull();
    assertThat(feeEntityResponse.getFeeCode()).isEqualTo("INVC");
    assertThat(feeEntityResponse.getFixedFee()).isEqualTo("200.56");
  }

  @Test
  @Disabled
  void getFeeEntity_whenNoRecordReturnedAfterFilteringFeeEntityList_shouldThrowException() {

    FeeCalculationRequest feeData = getFeeCalculationRequest();

    LocalDate claimStartDate = FeeCalculationUtil.getFeeClaimStartDate(POLICE_STATION, feeData);

    FeeSchemesEntity feeSchemesEntity = FeeSchemesEntity.builder().schemeCode("POL_FS2025")
        .validFrom(LocalDate.of(2025, 10, 1)).build();

    FeeEntity feeEntity = policeStationFeeEntity(feeSchemesEntity);

    when(feeRepository.findByFeeCode(any())).thenReturn(List.of(feeEntity));

    assertThatThrownBy(() -> feeDataService.getFeeEntity(feeData))
        .hasMessageContaining(String.format("Fee not found for feeCode: %s and startDate: %s", "INVC", claimStartDate));
  }

  @Test
  @Disabled
  void test_whenNoRecordPresentInFeeTable_shouldThrowException() {

    FeeCalculationRequest feeData = getFeeCalculationRequest();

    when(feeRepository.findByFeeCode(any())).thenReturn(List.of());

    assertThatThrownBy(() -> feeDataService.getFeeEntity(feeData))
        .hasMessageContaining(String.format("Fee not found for feeCode: %s and startDate: %s", "INVC", feeData.getStartDate()));
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
  @Disabled
  void getFeeEntity_whenFamilyCategoryAndLondonRateIsMissing_shouldReturnCorrectFeeEntity() {
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

    FeeNotFoundException feeNotFoundException = assertThrows(FeeNotFoundException.class, () -> {
      feeDataService.getFeeEntity(feeCalculationRequest);
    });
    assertThat(feeNotFoundException.getMessage()).isEqualTo("Fee not found for feeCode: FPB010 and startDate: 2025-02-11");
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