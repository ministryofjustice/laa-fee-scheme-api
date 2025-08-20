package uk.gov.justice.laa.fee.scheme.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.fee.scheme.feecalculator.CalculationType.COMMUNITY_CARE;
import static uk.gov.justice.laa.fee.scheme.feecalculator.CalculationType.IMMIGRATION_ASYLUM_FIXED_FEE;
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
  void getFeeCalculation_shouldReturnExpectedCalculation_communityCare() {
    String feeCode = "COM";

    FeeSchemesEntity feeSchemesEntity = FeeSchemesEntity.builder()
        .schemeCode("COM_FS2013")
        .schemeName("Community Care Fee Scheme 2013")
        .validFrom(LocalDate.parse("2013-04-01"))
        .build();
    when(feeSchemesRepository.findValidSchemeForDate(any(), any(), any())).thenReturn(List.of(feeSchemesEntity));

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode(feeCode)
        .fixedFee(new BigDecimal("79.80"))
        .calculationType(COMMUNITY_CARE)
        .build();
    when(feeRepository.findByFeeCodeAndFeeSchemeCode(any(), any())).thenReturn(Optional.of(feeEntity));

    FeeCalculationRequest request = FeeCalculationRequest.builder()
        .feeCode(feeCode)
        .startDate(LocalDate.of(2025, 5, 12))
        .vatIndicator(true)
        .netDisbursementAmount(45.16)
        .disbursementVatAmount(9.03)
        .build();

    FeeCalculationResponse response = feeService.getFeeCalculation(request);

    assertFeeCalculation(response, "COM", 124.96, 149.95);
  }

  @Test
  void getFeeCalculation_shouldReturnExpectedCalculation_mediation() {
    FeeSchemesEntity feeSchemesEntity = FeeSchemesEntity.builder()
        .schemeCode("MED_FS2013")
        .schemeName("mediation fee scheme 2013")
        .validFrom(LocalDate.parse("2013-04-01"))
        .build();
    when(feeSchemesRepository.findValidSchemeForDate(any(), any(), any())).thenReturn(List.of(feeSchemesEntity));

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("MED1")
        .mediationSessionOne(new BigDecimal(50))
        .mediationSessionTwo(new BigDecimal(100))
        .calculationType(MEDIATION)
        .build();
    when(feeRepository.findByFeeCodeAndFeeSchemeCode(any(), any())).thenReturn(Optional.of(feeEntity));

    FeeCalculationRequest request = FeeCalculationRequest.builder()
        .feeCode("MED1")
        .startDate(LocalDate.of(2025, 7, 29))
        .netDisbursementAmount(70.75)
        .disbursementVatAmount(20.15)
        .vatIndicator(true)
        .numberOfMediationSessions(2)
        .build();

    FeeCalculationResponse response = feeService.getFeeCalculation(request);

    assertFeeCalculation(response, "MED1", 170.75, 210.90);
  }

  @Test
  void getFeeCalculation_shouldReturnExpectedCalculation_immigrationAsylumFixedFee() {
    FeeSchemesEntity feeSchemesEntity = FeeSchemesEntity.builder()
        .schemeCode("I&A_FS2020")
        .schemeName("Standard Fee - Immigration CLR (2c + advocacy substantive hearing fee)")
        .validFrom(LocalDate.parse("2025-04-01"))
        .build();
    when(feeSchemesRepository.findValidSchemeForDate(any(), any(), any())).thenReturn(List.of(feeSchemesEntity));

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("IMCC")
        .fixedFee(new BigDecimal("764.00"))
        .disbursementLimit(new BigDecimal("600"))
        .oralCmrhBoltOn(new BigDecimal("166"))
        .telephoneCmrhBoltOn(new BigDecimal("90"))
        .adjornHearingBoltOn(new BigDecimal("161"))
        .calculationType(IMMIGRATION_ASYLUM_FIXED_FEE)
        .build();
    when(feeRepository.findByFeeCodeAndFeeSchemeCode(any(), any())).thenReturn(Optional.of(feeEntity));

    FeeCalculationRequest request = FeeCalculationRequest.builder()
        .feeCode("IMCC")
        .startDate(LocalDate.of(2025, 7, 29))
        .netDisbursementAmount(70.75)
        .disbursementVatAmount(20.15)
        .vatIndicator(true)
        .build();

    FeeCalculationResponse response = feeService.getFeeCalculation(request);

    assertFeeCalculation(response, "IMCC", 834.75, 1007.70);
  }

  private void assertFeeCalculation(FeeCalculationResponse response, String feeCode, double subTotal, double total) {
    assertThat(response).isNotNull();
    assertThat(response.getFeeCode()).isEqualTo(feeCode);

    FeeCalculation calculation = response.getFeeCalculation();
    assertThat(calculation).isNotNull();
    assertThat(calculation.getSubTotal()).isEqualTo(subTotal);
    assertThat(calculation.getTotalAmount()).isEqualTo(total);
  }

}