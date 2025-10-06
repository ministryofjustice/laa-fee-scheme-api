package uk.gov.justice.laa.fee.scheme.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.CLAIMS_PUBLIC_AUTHORITIES;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.CLINICAL_NEGLIGENCE;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.COMMUNITY_CARE;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.DEBT;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.HOUSING;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.HOUSING_HLPAS;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.IMMIGRATION_ASYLUM;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.MISCELLANEOUS;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.PUBLIC_LAW;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.entity.FeeSchemesEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.feecalculator.FeeCalculator;
import uk.gov.justice.laa.fee.scheme.feecalculator.FeeCalculatorFactory;
import uk.gov.justice.laa.fee.scheme.feecalculator.ImmigrationAsylumFeeCalculator;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;

class FeeCalculationServiceTest {

  @Mock
  FeeCalculatorFactory feeCalculatorFactory;

  @Mock
  FeeDataService feeDataService;

  @InjectMocks
  private FeeCalculationService feeCalculationService;

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
            new BigDecimal("112.00"), PUBLIC_LAW, 209.92),
        Arguments.of("PUB", // welfare benefits
            "WB_FS2025", "Welfare benefits claims", LocalDate.parse("2025-04-01"),
            new BigDecimal("208.00"), PUBLIC_LAW, 325.12)
    );
  }


  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void calculateFee_DelegatesToCalculator() {

    // Picked Immigration Asylum Fee Calculator for stubbing
    FeeCalculator immigrationCalculator = mock(ImmigrationAsylumFeeCalculator.class);
    // Arrange
    CategoryType category = CategoryType.valueOf(IMMIGRATION_ASYLUM.name());
    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode("FEE123")
        .startDate(LocalDate.of(2025, 7, 29))
        .netDisbursementAmount(70.75)
        .disbursementVatAmount(20.15)
        .vatIndicator(true)
        .numberOfMediationSessions(2)
        .build();

    FeeEntity feeEntity =  FeeEntity.builder()
          .feeCode("INVC")
          .feeScheme(FeeSchemesEntity.builder().schemeCode("IMM_ASYLM_FS2023").build())
          .categoryType(IMMIGRATION_ASYLUM)
          .escapeThresholdLimit(new BigDecimal("700.00"))
          .build();

    when(feeCalculatorFactory.getCalculator(category)).thenReturn(immigrationCalculator);

    when(feeDataService.getFeeEntity(any())).thenReturn(feeEntity);


    FeeCalculation expectedCalculation = FeeCalculation.builder()
        .totalAmount(1587.50)
        .vatIndicator(true)
        .vatRateApplied(20.0)
        .disbursementAmount(800.0)
        .disbursementVatAmount(40.0)
        .fixedFeeAmount(456.00)
        .calculatedVatAmount(236.00)
        .build();

    FeeCalculationResponse expectedResponse = FeeCalculationResponse.builder()
        .feeCode("INVC")
        .schemeId("IMM_ASYLM_FS2023")
        .claimId("claim_123")
        .validationMessages(new ArrayList<>())
        .escapeCaseFlag(false) // hardcoded till escape logic implemented
        .feeCalculation(expectedCalculation)
        .build();
    when(immigrationCalculator.calculate(feeCalculationRequest, feeEntity)).thenReturn(expectedResponse);

    FeeCalculationResponse response = feeCalculationService.calculateFee(feeCalculationRequest);

    assertFeeCalculation(response, "INVC", 1587.5);

    verify(feeCalculatorFactory, times(1)).getCalculator(category);

    verify(immigrationCalculator).calculate(feeCalculationRequest, feeEntity);
  }



  private void assertFeeCalculation(FeeCalculationResponse response, String feeCode, double total) {
    assertThat(response).isNotNull();
    assertThat(response.getFeeCode()).isEqualTo(feeCode);

    FeeCalculation calculation = response.getFeeCalculation();
    assertThat(calculation).isNotNull();
    assertThat(calculation.getTotalAmount()).isEqualTo(total);
  }

}