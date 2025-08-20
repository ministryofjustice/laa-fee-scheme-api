package uk.gov.justice.laa.fee.scheme.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.fee.scheme.feecalculator.CalculationType.CLAIMS_PUBLIC_AUTHORITIES;
import static uk.gov.justice.laa.fee.scheme.feecalculator.CalculationType.CLINICAL_NEGLIGENCE;
import static uk.gov.justice.laa.fee.scheme.feecalculator.CalculationType.COMMUNITY_CARE;
import static uk.gov.justice.laa.fee.scheme.feecalculator.CalculationType.MEDIATION;
import static uk.gov.justice.laa.fee.scheme.testutility.TestDataUtility.buildFeeEntity;
import static uk.gov.justice.laa.fee.scheme.testutility.TestDataUtility.buildFeeSchemesEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
    FeeSchemesEntity feeSchemesEntity = FeeSchemesEntity.builder().schemeCode("scheme123").build();
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
    FeeSchemesEntity feeSchemesEntity = buildFeeSchemesEntity("MED_FS2013", "Mediation Fee Scheme 2013", LocalDate.parse("2013-04-01"));
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

  @ParameterizedTest
  @MethodSource("testDataOtherCivil")
  void getFeeCalculation_shouldReturnExpectedCalculation_otherCivil(String feeCode, FeeSchemesEntity feeSchemesEntity, FeeEntity feeEntity,
                                                                    double expectedSubTotal, double expectedTotal) {

    when(feeSchemesRepository.findValidSchemeForDate(any(), any(), any())).thenReturn(List.of(feeSchemesEntity));
    when(feeRepository.findByFeeCodeAndFeeSchemeCode(any(), any())).thenReturn(Optional.of(feeEntity));

    FeeCalculationRequest request = FeeCalculationRequest.builder()
        .feeCode(feeCode)
        .startDate(LocalDate.of(2025, 3, 5))
        .vatIndicator(true)
        .netDisbursementAmount(62.93)
        .disbursementVatAmount(12.59)
        .build();

    FeeCalculationResponse response = feeService.getFeeCalculation(request);

    assertFeeCalculation(response, feeCode, expectedSubTotal, expectedTotal);
  }

  static Stream<Arguments> testDataOtherCivil() {
    return Stream.of(
        Arguments.of("CAPA", // Claims Against Public Authorities
            buildFeeSchemesEntity("CAPA_FS2013", "Claims Against Public Authorities Fee Scheme 2013", LocalDate.parse("2013-04-01")),
            buildFeeEntity("CAPA", new BigDecimal("235.00"), CLAIMS_PUBLIC_AUTHORITIES),
            297.93, 357.52),
        Arguments.of("CLIN", // Clinical Negligence
            buildFeeSchemesEntity("CLIN_FS2013", "Clinical Negligence Fee Scheme 2013", LocalDate.parse("2013-04-01")),
            buildFeeEntity("CLIN", new BigDecimal("420.00"), CLINICAL_NEGLIGENCE),
            482.93, 579.52),
        Arguments.of("COM", // Community Care
            buildFeeSchemesEntity("COM_FS2013", "Community Care Fee Scheme 2013", LocalDate.parse("2013-04-01")),
            buildFeeEntity("CAPA", new BigDecimal("79.00"), COMMUNITY_CARE),
            141.93, 170.32),
        Arguments.of("DEBT", // Community Care
            buildFeeSchemesEntity("DEBT_FS2013", "Debt Fee Scheme 2013", LocalDate.parse("2013-04-01")),
            buildFeeEntity("DEBT", new BigDecimal("133.00"), COMMUNITY_CARE),
            195.93, 235.12)
    );
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