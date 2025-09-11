package uk.gov.justice.laa.fee.scheme.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.POLICE_STATION;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.entity.FeeSchemesEntity;
import uk.gov.justice.laa.fee.scheme.enums.FeeType;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.repository.FeeRepository;
import uk.gov.justice.laa.fee.scheme.repository.FeeSchemesRepository;

@ExtendWith(MockitoExtension.class)
class FeeDataServiceTest {

  @Mock
  FeeRepository feeRepository;

  @Mock
  FeeSchemesRepository feeSchemesRepository;

  @InjectMocks
  private FeeDataService feeDataService;


  @Test
  void test_whenFeeSchemeIdAndFeeCodePresentInDatabase_shouldReturnValidResponse() {

    FeeCalculationRequest feeData = FeeCalculationRequest.builder()
        .feeCode("INVC")
        .startDate(LocalDate.of(2017, 7, 29))
        .vatIndicator(Boolean.TRUE)
        .policeStationSchemeId("1003")
        .policeStationId("NA2093")
        .netDisbursementAmount(50.50)
        .disbursementVatAmount(20.15)
        .build();

    FeeSchemesEntity feeSchemesEntity = FeeSchemesEntity.builder().schemeCode("POL_FS2022").build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("INVC")
        .feeSchemeCode(feeSchemesEntity)
        .profitCostLimit(new BigDecimal("123.56"))
        .fixedFee(new BigDecimal("200.56"))
        .categoryType(POLICE_STATION)
        .feeType(FeeType.FIXED)
        .build();

    when(feeSchemesRepository.findValidSchemeForDate(any(),
        any(),any())).thenReturn(List.of(feeSchemesEntity));
    when(feeRepository.findByFeeCodeAndFeeSchemeCode(any(),
        any())).thenReturn(Optional.of(feeEntity));

    FeeEntity feeEntityResponse = feeDataService.getFeeEntity(feeData);

    assertThat(feeEntityResponse).isNotNull();
    assertThat(feeEntityResponse.getFeeCode()).isEqualTo("INVC");

  }
}