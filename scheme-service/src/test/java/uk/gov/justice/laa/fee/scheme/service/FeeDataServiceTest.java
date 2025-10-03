package uk.gov.justice.laa.fee.scheme.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.POLICE_STATION;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.entity.FeeSchemesEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.enums.FeeType;
import uk.gov.justice.laa.fee.scheme.enums.Region;
import uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.repository.FeeRepository;

@ExtendWith(MockitoExtension.class)
class FeeDataServiceTest {

  @Mock
  FeeRepository feeRepository;

  @InjectMocks
  private FeeDataService feeDataService;


  @Test
  void test_whenFeeSchemeIdAndFeeCodePresentInDatabase_shouldReturnValidResponse() {

    FeeCalculationRequest feeData = getFeeCalculationRequest();

    FeeSchemesEntity feeSchemesEntity = FeeSchemesEntity.builder().schemeCode("POL_FS2022")
        .validFrom(LocalDate.of(2022,1,1)).build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("INVC")
        .feeSchemeCode(feeSchemesEntity)
        .profitCostLimit(new BigDecimal("123.56"))
        .fixedFee(new BigDecimal("200.56"))
        .categoryType(POLICE_STATION)
        .feeType(FeeType.FIXED)
        .build();

    when(feeRepository.findByFeeCode(any())).thenReturn(List.of(feeEntity));

    FeeEntity feeEntityResponse = feeDataService.getFeeEntity(feeData);

    assertThat(feeEntityResponse).isNotNull();
    assertThat(feeEntityResponse.getFeeCode()).isEqualTo("INVC");

  }

  @Test
  void test_whenMultipleRecordsPresentInFeeTable_shouldReturnValidResponse() {

    FeeCalculationRequest feeData = getFeeCalculationRequest();

    FeeSchemesEntity feeSchemesEntity1 = FeeSchemesEntity.builder().schemeCode("POL_FS2022")
        .validFrom(LocalDate.of(2022,1,1)).build();

    FeeEntity feeEntity1 = FeeEntity.builder()
        .feeCode("INVC")
        .feeSchemeCode(feeSchemesEntity1)
        .profitCostLimit(new BigDecimal("123.56"))
        .fixedFee(new BigDecimal("200.56"))
        .categoryType(POLICE_STATION)
        .feeType(FeeType.FIXED)
        .build();

    FeeSchemesEntity feeSchemesEntity2 = FeeSchemesEntity.builder().schemeCode("POL_FS2016")
        .validFrom(LocalDate.of(2016,1,1)).build();

    FeeEntity feeEntity2 = FeeEntity.builder()
        .feeCode("INVC")
        .feeSchemeCode(feeSchemesEntity2)
        .profitCostLimit(new BigDecimal("123.56"))
        .fixedFee(new BigDecimal("200.56"))
        .categoryType(POLICE_STATION)
        .feeType(FeeType.FIXED)
        .build();

    when(feeRepository.findByFeeCode(any())).thenReturn(List.of(feeEntity1, feeEntity2));

    FeeEntity feeEntityResponse = feeDataService.getFeeEntity(feeData);

    assertThat(feeEntityResponse).isNotNull();
    assertThat(feeEntityResponse.getFeeCode()).isEqualTo("INVC");
  }

  @Test
  void test_whenMultipleRecordsPresentInFeeTableAndFeeSchemeEnded_shouldReturnValidResponse() {

    FeeCalculationRequest feeData = getFeeCalculationRequest();

    FeeSchemesEntity feeSchemesEntity1 = FeeSchemesEntity.builder().schemeCode("POL_FS2022")
        .validFrom(LocalDate.of(2022,1,1)).validTo(LocalDate.of(2023,1,1)).build();

    FeeEntity feeEntity1 = FeeEntity.builder()
        .feeCode("INVC")
        .feeSchemeCode(feeSchemesEntity1)
        .profitCostLimit(new BigDecimal("954.56"))
        .fixedFee(new BigDecimal("1256.66"))
        .categoryType(POLICE_STATION)
        .feeType(FeeType.FIXED)
        .build();

    FeeSchemesEntity feeSchemesEntity2 = FeeSchemesEntity.builder().schemeCode("POL_FS2016")
        .validFrom(LocalDate.of(2016,1,1)).build();

    FeeEntity feeEntity2 = FeeEntity.builder()
        .feeCode("INVC")
        .feeSchemeCode(feeSchemesEntity2)
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
  void test_whenSingleRecordOfFeePresentInFeeTable_And_StartDateIsGreaterThanFeeSchemeStartDate_shouldReturnValidResponse() {

    FeeCalculationRequest feeData = getFeeCalculationRequest();

    FeeSchemesEntity feeSchemesEntity = FeeSchemesEntity.builder().schemeCode("POL_FS2022")
        .validFrom(LocalDate.of(2021,12,31)).build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("INVC")
        .feeSchemeCode(feeSchemesEntity)
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
  void test_whenSingleRecordOfFeePresentInFeeTable_shouldReturnValidResponse() {

    FeeCalculationRequest feeData = getFeeCalculationRequest();

    FeeSchemesEntity feeSchemesEntity = FeeSchemesEntity.builder().schemeCode("POL_FS2022")
          .validFrom(LocalDate.of(2022,1,1)).build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("INVC")
        .feeSchemeCode(feeSchemesEntity)
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
  void test_whenNoRecordReturnedAfterFilteringFeeEntityList_shouldThrowException() {

    FeeCalculationRequest feeData = getFeeCalculationRequest();

    LocalDate claimStartDate = FeeCalculationUtil.getFeeClaimStartDate(POLICE_STATION, feeData);

    FeeSchemesEntity feeSchemesEntity = FeeSchemesEntity.builder().schemeCode("POL_FS2025")
        .validFrom(LocalDate.of(2025,10,1)).build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("INVC")
        .feeSchemeCode(feeSchemesEntity)
        .profitCostLimit(new BigDecimal("123.56"))
        .fixedFee(new BigDecimal("200.56"))
        .categoryType(POLICE_STATION)
        .feeType(FeeType.FIXED)
        .build();

    when(feeRepository.findByFeeCode(any())).thenReturn(List.of(feeEntity));

    assertThatThrownBy(() -> feeDataService.getFeeEntity(feeData))
        .hasMessageContaining(String.format("Fee not found for feeCode: %s and startDate: %s", "INVC", claimStartDate));
  }

  @Test
  void test_whenNoRecordPresentInFeeTable_shouldThrowException() {

    FeeCalculationRequest feeData = getFeeCalculationRequest();

    when(feeRepository.findByFeeCode(any())).thenReturn(List.of());

    assertThatThrownBy(() -> feeDataService.getFeeEntity(feeData))
        .hasMessageContaining(String.format("Fee not found for feeCode: %s and startDate: %s", "INVC", feeData.getStartDate()));
  }

  @Test
  void test_whenFilterByRegionAndLondonRateIsTrue_shouldReturnCorrectFeeEntity() {

    FeeSchemesEntity feeSchemesEntity = FeeSchemesEntity.builder().schemeCode("FAM_FS2011")
        .validFrom(LocalDate.of(2011,1,1)).build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("FPB010")
        .feeSchemeCode(feeSchemesEntity)
        .fixedFee(new BigDecimal("150"))
        .escapeThresholdLimit(new BigDecimal("300"))
        .region(Region.LONDON)
        .categoryType(CategoryType.FAMILY)
        .feeType(FeeType.FIXED)
        .build();

    when(feeRepository.findByFeeCode(any())).thenReturn(List.of(feeEntity));

    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode("FPB010")
        .startDate(LocalDate.of(2025, 2, 11))
        .vatIndicator(Boolean.TRUE)
        .netDisbursementAmount(50.50)
        .disbursementVatAmount(20.15)
        .londonRate(Boolean.TRUE)
        .build();

    FeeEntity feeEntityResponse = feeDataService.getFeeEntity(feeCalculationRequest);

    assertThat(feeEntityResponse).isNotNull();
    assertThat(feeEntityResponse.getFeeCode()).isEqualTo("FPB010");
    assertThat(feeEntityResponse.getFixedFee()).isEqualTo(new BigDecimal("150"));
    assertThat(feeEntityResponse.getRegion()).isEqualTo(Region.LONDON);
  }

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
}