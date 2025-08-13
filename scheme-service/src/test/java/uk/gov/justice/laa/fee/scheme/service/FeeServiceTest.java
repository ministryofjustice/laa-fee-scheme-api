package uk.gov.justice.laa.fee.scheme.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.fee.scheme.feecalculators.CalculationType.MEDIATION;

import java.math.BigDecimal;
import java.time.LocalDate;
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
  void getFeeCalculation_shouldReturnExpectedCalculation_mediation() {
    FeeSchemesEntity feeSchemesEntity = new FeeSchemesEntity();
    feeSchemesEntity.setSchemeCode("MED_FS2013");
    feeSchemesEntity.setSchemeName("mediation fee scheme 2013");
    feeSchemesEntity.setValidFrom(LocalDate.parse("2013-04-01"));
    feeSchemesEntity.setValidTo(null);
    when(feeSchemesRepository.findValidSchemeForDate(any(), any())).thenReturn(Optional.of(feeSchemesEntity));

    FeeEntity feeEntity = new FeeEntity();
    feeEntity.setFeeCode("MED1");
    feeEntity.setMediationSessionOne(new BigDecimal(50));
    feeEntity.setMediationSessionTwo(new BigDecimal(100));
    feeEntity.setCalculationType(MEDIATION);
    when(feeRepository.findByFeeCodeAndFeeSchemeCode_SchemeCode(any(), any())).thenReturn(Optional.of(feeEntity));

    FeeCalculationRequest requestDto = new FeeCalculationRequest();
    requestDto.setFeeCode("MED1");
    requestDto.setStartDate(LocalDate.of(2025, 7, 29));
    requestDto.setNetDisbursementAmount(70.75);
    requestDto.setDisbursementVatAmount(20.15);
    requestDto.setVatIndicator(true);
    requestDto.setNumberOfMediationSessions(2);
    FeeCalculationResponse response = feeService.getFeeCalculation(requestDto);

    assertNotNull(response);
    assertEquals("MED1", response.getFeeCode());

    FeeCalculation calculation = response.getFeeCalculation();
    assertNotNull(calculation);
    assertEquals(120.75, calculation.getSubTotal());
    assertEquals(150.90, calculation.getTotalAmount());
  }
}