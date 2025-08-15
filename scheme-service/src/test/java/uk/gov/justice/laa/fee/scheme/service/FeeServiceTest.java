package uk.gov.justice.laa.fee.scheme.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.fee.scheme.feecalculator.CalculationType.MEDIATION;

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
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.repository.FeeRepository;
import uk.gov.justice.laa.fee.scheme.repository.FeeSchemesRepository;

@ExtendWith(MockitoExtension.class)
class FeeServiceTest {

  @InjectMocks
  private FeeService feeService;
  @Mock
  FeeRepository feeRepository;
  @Mock
  FeeSchemesRepository feeSchemesRepository;

  @Test
  void shouldThrowException_feeSchemeNotFoundForDate() {
    FeeCalculationRequest requestDto = FeeCalculationRequest.builder()
        .feeCode("FEE123")
        .startDate(LocalDate.of(2025, 7, 29))
        .netDisbursementAmount(70.75)
        .disbursementVatAmount(20.15)
        .vatIndicator(true)
        .numberOfMediationSessions(2)
        .build();

    when(feeSchemesRepository.findValidSchemeForDate(any(), any(), any())).thenReturn(List.of());

    assertThatThrownBy(() -> feeService.getFeeCalculation(requestDto))
        .hasMessageContaining("Fee not found for fee code FEE123, with start date 2025-07-29");
  }

  @Test
  void shouldThrowException_feeEntityNotFoundForSchemeId() {
    FeeSchemesEntity feeSchemesEntity = new FeeSchemesEntity();
    feeSchemesEntity.setSchemeCode("scheme123");

    when(feeSchemesRepository.findValidSchemeForDate(any(), any(), any())).thenReturn(List.of(feeSchemesEntity));
    when(feeRepository.findByFeeCodeAndFeeSchemeCode("FEE123", feeSchemesEntity))
        .thenReturn(Optional.empty());

    FeeCalculationRequest requestDto = FeeCalculationRequest.builder()
        .feeCode("FEE123")
        .startDate(LocalDate.of(2025, 7, 29))
        .netDisbursementAmount(70.75)
        .disbursementVatAmount(20.15)
        .vatIndicator(true)
        .numberOfMediationSessions(2)
        .build();

    assertThatThrownBy(() -> feeService.getFeeCalculation(requestDto))
        .hasMessageContaining("Fee not found for fee code FEE123, with start date 2025-07-29");

  }

  @Test
  void getFeeCalculation_shouldReturnExpectedCalculation_mediation() {
    FeeSchemesEntity feeSchemesEntity = FeeSchemesEntity.builder()
        .schemeCode("MED_FS2013")
        .schemeName("mediation fee scheme 2013")
        .validFrom(LocalDate.parse("2013-04-01"))
        .validTo(null)
        .build();
    when(feeSchemesRepository.findValidSchemeForDate(any(), any(), any())).thenReturn(List.of(feeSchemesEntity));

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("MED1")
        .mediationSessionOne(new BigDecimal(50))
        .mediationSessionTwo(new BigDecimal(100))
        .calculationType(MEDIATION)
        .build();
    when(feeRepository.findByFeeCodeAndFeeSchemeCode(any(), any())).thenReturn(Optional.of(feeEntity));

    FeeCalculationRequest requestDto = FeeCalculationRequest.builder()
        .feeCode("MED1")
        .startDate(LocalDate.of(2025, 7, 29))
        .netDisbursementAmount(70.75)
        .disbursementVatAmount(20.15)
        .vatIndicator(true)
        .numberOfMediationSessions(2)
        .build();
    FeeCalculationResponse response = feeService.getFeeCalculation(requestDto);

    assertNotNull(response);
    assertEquals("MED1", response.getFeeCode());

    FeeCalculation calculation = response.getFeeCalculation();
    assertNotNull(calculation);
    assertEquals(170.75, calculation.getSubTotal());
    assertEquals(210.90, calculation.getTotalAmount());
  }
}