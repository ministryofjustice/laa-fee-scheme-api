package uk.gov.justice.laa.fee.scheme.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.fee.scheme.feecalculator.CalculationType.CLAIMS_PUBLIC_AUTHORITIES;
import static uk.gov.justice.laa.fee.scheme.feecalculator.CalculationType.CLINICAL_NEGLIGENCE;
import static uk.gov.justice.laa.fee.scheme.feecalculator.CalculationType.COMMUNITY_CARE;
import static uk.gov.justice.laa.fee.scheme.feecalculator.CalculationType.DEBT;
import static uk.gov.justice.laa.fee.scheme.feecalculator.CalculationType.DISCRIMINATION;
import static uk.gov.justice.laa.fee.scheme.feecalculator.CalculationType.HOUSING;
import static uk.gov.justice.laa.fee.scheme.feecalculator.CalculationType.HOUSING_HLPAS;
import static uk.gov.justice.laa.fee.scheme.feecalculator.CalculationType.IMMIGRATION_ASYLUM_FIXED_FEE;
import static uk.gov.justice.laa.fee.scheme.feecalculator.CalculationType.IMMIGRATION_ASYLUM_HOURLY_RATE;
import static uk.gov.justice.laa.fee.scheme.feecalculator.CalculationType.MEDIATION;
import static uk.gov.justice.laa.fee.scheme.feecalculator.CalculationType.MISCELLANEOUS;
import static uk.gov.justice.laa.fee.scheme.feecalculator.CalculationType.POLICE_STATION;
import static uk.gov.justice.laa.fee.scheme.feecalculator.CalculationType.PUBLIC_LAW;
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
import uk.gov.justice.laa.fee.scheme.entity.PoliceStationFeesEntity;
import uk.gov.justice.laa.fee.scheme.feecalculator.CalculationType;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.repository.FeeRepository;
import uk.gov.justice.laa.fee.scheme.repository.FeeSchemesRepository;
import uk.gov.justice.laa.fee.scheme.repository.PoliceStationFeesRepository;

@ExtendWith(MockitoExtension.class)
class FeeServiceTest {

  @Mock
  FeeRepository feeRepository;
  @Mock
  FeeSchemesRepository feeSchemesRepository;
  @Mock
  PoliceStationFeesRepository policeStationFeesRepository;
  @InjectMocks
  private FeeService feeService;

  static Stream<Arguments> testDataOtherCivil() {
    return Stream.of(
        Arguments.of("CAPA", // Claims Against Public Authorities
            "CAPA_FS2013", "CAPA Fee Scheme 2013", LocalDate.parse("2013-04-01"),
            new BigDecimal("235.00"), CLAIMS_PUBLIC_AUTHORITIES, 357.52),
        Arguments.of("CLIN", // Clinical Negligence
            "CLIN_FS2013", "CLIN Fee Scheme 2013", LocalDate.parse("2013-04-01"),
            new BigDecimal("420.00"), CLINICAL_NEGLIGENCE, 579.52),
        Arguments.of("COM", // Community Care
            "COM_FS2013", "COM Fee Scheme 2013", LocalDate.parse("2013-04-01"),
            new BigDecimal("79.00"), COMMUNITY_CARE, 170.32),
        Arguments.of("DEBT", // Debt
            "DEBT_FS2013", "DEBT Fee Scheme 2013", LocalDate.parse("2013-04-01"),
            new BigDecimal("133.00"), DEBT, 235.12),
        Arguments.of("ELA", // Housing - HLPAS
            "ELA_FS2024", "ELA Fee Scheme 2013", LocalDate.parse("2024-09-01"),
            new BigDecimal("209.00"), HOUSING_HLPAS, 326.32),
        Arguments.of("HOUS", // Housing
            "HOUS_FS2013", "HOUS Fee Scheme 2013", LocalDate.parse("2013-04-01"),
            new BigDecimal("98.00"), HOUSING, 193.12),
        Arguments.of("MISCCON", // Miscellaneous
            "MISCCON", "MISCCON Fee Scheme 2015", LocalDate.parse("2015-03-23"),
            new BigDecimal("375.00"), MISCELLANEOUS, 525.52),
        Arguments.of("PUB", // Public Law
            "PUB_FS2013", "PUB Fee Scheme 2015", LocalDate.parse("2013-04-01"),
            new BigDecimal("112.00"), PUBLIC_LAW, 209.92)
    );
  }

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
  void getFeeCalculation_shouldReturnExpectedCalculation_discrimination() {
    FeeSchemesEntity feeSchemesEntity = buildFeeSchemesEntity("DISC_FS2013",
        "Discrimination Fee Scheme 2013", LocalDate.parse("2013-04-01"));
    when(feeSchemesRepository.findValidSchemeForDate(any(), any(), any())).thenReturn(List.of(feeSchemesEntity));

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("DISC")
        .feeSchemeCode(FeeSchemesEntity.builder().schemeCode("DISC_FS2013").build())
        .escapeThresholdLimit(new BigDecimal("700.00"))
        .calculationType(DISCRIMINATION)
        .build();
    when(feeRepository.findByFeeCodeAndFeeSchemeCode(any(), any())).thenReturn(Optional.of(feeEntity));

    FeeCalculationRequest request = FeeCalculationRequest.builder()
        .feeCode("DISC")
        .startDate(LocalDate.of(2025, 7, 29))
        .netProfitCosts(250.25)
        .netCostOfCounsel(100.72)
        .travelAndWaitingCosts(30.34)
        .netDisbursementAmount(70.75)
        .disbursementVatAmount(20.15)
        .vatIndicator(true)
        .build();

    FeeCalculationResponse response = feeService.getFeeCalculation(request);

    assertFeeCalculation(response, "DISC", 548.47);
  }

  @Test
  void getFeeCalculation_shouldReturnExpectedCalculation_mediation() {
    FeeSchemesEntity feeSchemesEntity = buildFeeSchemesEntity("MED_FS2013",
        "Mediation Fee Scheme 2013", LocalDate.parse("2013-04-01"));
    when(feeSchemesRepository.findValidSchemeForDate(any(), any(), any())).thenReturn(List.of(feeSchemesEntity));

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("MED1")
        .feeSchemeCode(FeeSchemesEntity.builder().schemeCode("MED_FS2013").build())
        .mediationFeeLower(new BigDecimal(50))
        .mediationFeeHigher(new BigDecimal(100))
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

    assertFeeCalculation(response, "MED1", 210.90);
  }

  @ParameterizedTest
  @MethodSource("testDataOtherCivil")
  void getFeeCalculation_shouldReturnExpectedCalculation_otherCivil(String feeCode, String schemeCode, String schemeName,
                                                                    LocalDate validFrom, BigDecimal fixedFee,
                                                                    CalculationType calculationType, double expectedTotal) {

    when(feeSchemesRepository.findValidSchemeForDate(any(), any(), any()))
        .thenReturn(List.of(buildFeeSchemesEntity(schemeCode, schemeName, validFrom)));
    when(feeRepository.findByFeeCodeAndFeeSchemeCode(any(), any()))
        .thenReturn(Optional.of(buildFeeEntity(feeCode, fixedFee, calculationType, schemeCode)));

    FeeCalculationRequest request = FeeCalculationRequest.builder()
        .feeCode(feeCode)
        .startDate(LocalDate.of(2025, 3, 5))
        .vatIndicator(true)
        .netDisbursementAmount(62.93)
        .disbursementVatAmount(12.59)
        .build();

    FeeCalculationResponse response = feeService.getFeeCalculation(request);

    assertFeeCalculation(response, feeCode, expectedTotal);
  }

  @Test
  void getFeeCalculation_shouldReturnExpectedCalculation_immigrationAsylumFixedFee() {
    FeeSchemesEntity feeSchemesEntity = buildFeeSchemesEntity("I&A_FS2020",
        "Standard Fee - Immigration CLR (2c + advocacy substantive hearing fee)", LocalDate.parse("2025-04-01"));

    when(feeSchemesRepository.findValidSchemeForDate(any(), any(), any())).thenReturn(List.of(feeSchemesEntity));

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("IMCC")
        .feeSchemeCode(FeeSchemesEntity.builder().schemeCode("I&A_FS2023").build())
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

    assertFeeCalculation(response, "IMCC", 1007.70);
  }

  @Test
  void getFeeCalculation_shouldReturnExpectedCalculation_immigrationAsylumHourlyRate_legalHelp() {
    FeeSchemesEntity feeSchemesEntity = buildFeeSchemesEntity("I&A_FS2013",
        "Immigration and Asylum Scheme 2013", LocalDate.parse("2013-04-01"));

    when(feeSchemesRepository.findValidSchemeForDate(any(), any(), any())).thenReturn(List.of(feeSchemesEntity));

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("IAXL")
        .feeSchemeCode(FeeSchemesEntity.builder().schemeCode("I&A_FS2013").build())
        .profitCostLimit(new BigDecimal("800"))
        .disbursementLimit(new BigDecimal("400"))
        .calculationType(IMMIGRATION_ASYLUM_HOURLY_RATE)
        .build();
    when(feeRepository.findByFeeCodeAndFeeSchemeCode(any(), any())).thenReturn(Optional.of(feeEntity));

    FeeCalculationRequest request = FeeCalculationRequest.builder()
        .feeCode("IAXL")
        .startDate(LocalDate.of(2025, 6, 14))
        .netProfitCosts(166.25)
        .jrFormFilling(635.44)
        .netDisbursementAmount(89.56)
        .disbursementVatAmount(17.91)
        .vatIndicator(true)
        .build();

    FeeCalculationResponse response = feeService.getFeeCalculation(request);

    assertFeeCalculation(response, "IAXL", 1069.5);
  }

  @Test
  void getFeeCalculation_shouldReturnExpectedCalculation_policeStationId() {
    FeeSchemesEntity feeSchemesEntity = buildFeeSchemesEntity("POL_FS2022",
        "Police Station Work 2022", LocalDate.parse("2022-04-01"));

    PoliceStationFeesEntity policeStationFeesEntity = PoliceStationFeesEntity.builder().feeSchemeCode("POL_FS2022")
        .fixedFee(new BigDecimal("37.89")).psSchemeId("1004").build();

    when(feeSchemesRepository.findValidSchemeForDate(any(), any(), any())).thenReturn(List.of(feeSchemesEntity));

    when(policeStationFeesRepository.findPoliceStationFeeByPoliceStationIdAndFeeSchemeCode(any(), any()))
        .thenReturn(List.of(policeStationFeesEntity));

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("INVC")
        .calculationType(POLICE_STATION)
        .feeType("FIXED")
        .build();
    when(feeRepository.findByFeeCodeAndFeeSchemeCode(any(), any())).thenReturn(Optional.of(feeEntity));

    FeeCalculationRequest request = FeeCalculationRequest.builder()
        .feeCode("INVC")
        .uniqueFileNumber("120523/7382")
        .policeStationId("NE008")
        .policeStationSchemeId("1002")
        .vatIndicator(false)
        .build();

    FeeCalculationResponse response = feeService.getFeeCalculation(request);

    assertFeeCalculation(response, "INVC", 37.89);
  }

  @Test
  void getFeeCalculation_shouldReturnExpectedCalculation_whenPoliceStationIdIsNullAndPoliceStationSchemeIdProvided() {

    PoliceStationFeesEntity policeStationFeesEntity = PoliceStationFeesEntity.builder().feeSchemeCode("POL_FS2022")
        .fixedFee(new BigDecimal("37.89")).psSchemeId("1004").build();

    when(policeStationFeesRepository.findPoliceStationFeeByPsSchemeIdAndFeeSchemeCode(any(), any()))
        .thenReturn(List.of(policeStationFeesEntity));
    FeeSchemesEntity feeSchemesEntity = buildFeeSchemesEntity("POL_FS2022",
        "Police Station Work 2022", LocalDate.parse("2022-04-01"));

    when(feeSchemesRepository.findValidSchemeForDate(any(), any(), any())).thenReturn(List.of(feeSchemesEntity));

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("INVC")
        .profitCostLimit(new BigDecimal("100.00"))
        .calculationType(POLICE_STATION)
        .feeType("FIXED")
        .build();
    when(feeRepository.findByFeeCodeAndFeeSchemeCode(any(), any())).thenReturn(Optional.of(feeEntity));

    FeeCalculationRequest request = FeeCalculationRequest.builder()
        .feeCode("INVC")
        .uniqueFileNumber("120523/7382")
        .policeStationId(null)
        .policeStationSchemeId("1004")
        .vatIndicator(false)
        .build();

    FeeCalculationResponse response = feeService.getFeeCalculation(request);

    assertFeeCalculation(response, "INVC", 37.89);
  }

  @Test
  void getFeeCalculation_shouldReturnExpectedCalculation_whenOtherPoliceStationFeeCodeUsedInClaim() {

    FeeSchemesEntity feeSchemesEntity = buildFeeSchemesEntity("POL_FS2022",
        "Police Station Work 2022", LocalDate.parse("2022-04-01"));

    when(feeSchemesRepository.findValidSchemeForDate(any(), any(), any())).thenReturn(List.of(feeSchemesEntity));

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("INVM")
        .profitCostLimit(new BigDecimal("100.00"))
        .calculationType(POLICE_STATION)
        .feeSchemeCode(feeSchemesEntity)
        .feeType("HOURLY")
        .build();
    when(feeRepository.findByFeeCodeAndFeeSchemeCode(any(), any())).thenReturn(Optional.of(feeEntity));

    FeeCalculationRequest request = FeeCalculationRequest.builder()
        .feeCode("INVM")
        .netProfitCosts(59.8)
        .netDisbursementAmount(20.1)
        .travelAndWaitingCosts(10.4)
        .uniqueFileNumber("120523/7382")
        .policeStationId(null)
        .policeStationSchemeId("1004")
        .vatIndicator(Boolean.TRUE)
        .build();

    FeeCalculationResponse response = feeService.getFeeCalculation(request);

    assertFeeCalculation(response, "INVM", 128.46);

  }

  @Test
  void getFeeCalculation_shouldThrowException_whenPoliceFeeRecordNotFoundForPoliceStationSchemeId() {

    when(policeStationFeesRepository.findPoliceStationFeeByPsSchemeIdAndFeeSchemeCode(any(), any()))
        .thenReturn(List.of());

    FeeSchemesEntity feeSchemesEntity = buildFeeSchemesEntity("POL_FS2022",
        "Police Station Work 2022", LocalDate.parse("2022-04-01"));

    when(feeSchemesRepository.findValidSchemeForDate(any(), any(), any())).thenReturn(List.of(feeSchemesEntity));

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("INVC")
        .profitCostLimit(new BigDecimal("100.00"))
        .calculationType(POLICE_STATION)
        .build();
    when(feeRepository.findByFeeCodeAndFeeSchemeCode(any(), any())).thenReturn(Optional.of(feeEntity));

    FeeCalculationRequest request = FeeCalculationRequest.builder()
        .feeCode("INVC")
        .uniqueFileNumber("120523/7382")
        .policeStationId(null)
        .policeStationSchemeId("1004")
        .build();

    assertThatThrownBy(() -> feeService.getFeeCalculation(request))
        .hasMessage("Police Station Fee not found for Police Station Scheme Id 1004");
  }

  @Test
  void getFeeCalculation_shouldThrowException_whenPoliceFeeRecordNotFoundForPoliceStationId() {

    when(policeStationFeesRepository.findPoliceStationFeeByPoliceStationIdAndFeeSchemeCode(any(), any()))
        .thenReturn(List.of());

    FeeSchemesEntity feeSchemesEntity = buildFeeSchemesEntity("POL_FS2022",
        "Police Station Work 2022", LocalDate.parse("2022-04-01"));

    when(feeSchemesRepository.findValidSchemeForDate(any(), any(), any())).thenReturn(List.of(feeSchemesEntity));

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("INVC")
        .profitCostLimit(new BigDecimal("100.00"))
        .calculationType(POLICE_STATION)
        .build();
    when(feeRepository.findByFeeCodeAndFeeSchemeCode(any(), any())).thenReturn(Optional.of(feeEntity));

    FeeCalculationRequest request = FeeCalculationRequest.builder()
        .feeCode("INVC")
        .uniqueFileNumber("120523/7382")
        .policeStationId("MB2004")
        .policeStationSchemeId("1004")
        .build();

    assertThatThrownBy(() -> feeService.getFeeCalculation(request))
        .hasMessage("Police Station Fee not found for Police Station Id MB2004, with case start date 2023-05-12");
  }

  private void assertFeeCalculation(FeeCalculationResponse response, String feeCode, double total) {
    assertThat(response).isNotNull();
    assertThat(response.getFeeCode()).isEqualTo(feeCode);

    FeeCalculation calculation = response.getFeeCalculation();
    assertThat(calculation).isNotNull();
    assertThat(calculation.getTotalAmount()).isEqualTo(total);
  }

}