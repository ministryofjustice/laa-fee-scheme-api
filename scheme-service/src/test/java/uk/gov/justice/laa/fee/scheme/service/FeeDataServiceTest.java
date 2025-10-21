package uk.gov.justice.laa.fee.scheme.service;

import static org.assertj.core.api.Assertions.assertThat;
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
import uk.gov.justice.laa.fee.scheme.enums.FeeType;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.repository.FeeRepository;

@ExtendWith(MockitoExtension.class)
class FeeDataServiceTest {

  @Mock
  FeeRepository feeRepository;

  @InjectMocks
  private FeeDataService feeDataService;

  @Test
  void getFeeEntities_shouldReturnValidResponse() {

    FeeCalculationRequest feeCalculationRequest = buildFeeCalculationRequest();

    FeeEntity feeEntity = buildFeeEntity();

    when(feeRepository.findByFeeCode("INVC")).thenReturn(List.of(feeEntity));

    List<FeeEntity> result = feeDataService.getFeeEntities("INVC");

    assertThat(result).containsExactly(feeEntity);
  }

  private static FeeEntity buildFeeEntity() {
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
    return feeEntity;
  }

  private static FeeCalculationRequest buildFeeCalculationRequest() {
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