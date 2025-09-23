package uk.gov.justice.laa.fee.scheme.feecalculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.IMMIGRATION_ASYLUM;
import static uk.gov.justice.laa.fee.scheme.enums.FeeType.FIXED;
import static uk.gov.justice.laa.fee.scheme.enums.FeeType.HOURLY;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.entity.FeeSchemesEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.feecalculator.fixed.ImmigrationAsylumFixedFeeCalculator;
import uk.gov.justice.laa.fee.scheme.feecalculator.fixed.PoliceStationFixedFeeCalculator;
import uk.gov.justice.laa.fee.scheme.feecalculator.hourly.ImmigrationAsylumHourlyRateCalculator;
import uk.gov.justice.laa.fee.scheme.feecalculator.hourly.PoliceStationHourlyRateCalculator;
import uk.gov.justice.laa.fee.scheme.model.BoltOnType;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.service.FeeDataService;

@ExtendWith(MockitoExtension.class)
class ImmigrationAsylumFeeCalculatorTest {

  @Mock
  ImmigrationAsylumFixedFeeCalculator immigrationAsylumFixedFeeCalculator;

  @Mock
  ImmigrationAsylumHourlyRateCalculator immigrationAsylumHourlyRateCalculator;

  @InjectMocks
  ImmigrationAsylumFeeCalculator immigrationAsylumFeeCalculator;

  @Test
  void getFee_whenImmigrationAsylumClaimFeeCodeFixed_shouldReturnFeeCalculationResponse() {
    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode("IACA")
        .claimId("claim_123")
        .startDate(LocalDate.of(2025, 7, 29))
        .netDisbursementAmount(120.0)
        .disbursementVatAmount(24.0)
        .vatIndicator(true)
        .immigrationPriorAuthorityNumber("er23")
        .boltOns(BoltOnType.builder()
            .boltOnCmrhOral(2)
            .boltOnCmrhTelephone(2)
            .build())
        .detentionAndWaitingCosts(234.98)
        .jrFormFilling(34.9)
        .build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("IACA")
        .feeSchemeCode(FeeSchemesEntity.builder().schemeCode("IMM_ASYLM_FS2023").build())
        .fixedFee(new BigDecimal("75.50"))
        .categoryType(IMMIGRATION_ASYLUM)
        .feeType(FIXED)
        .disbursementLimit(new BigDecimal(435))
        .oralCmrhBoltOn(BigDecimal.valueOf(166))
        .telephoneCmrhBoltOn(BigDecimal.valueOf(90))
        .build();

    FeeCalculationResponse expectedResponse = FeeCalculationResponse.builder()
        .feeCode("IACA")
        .schemeId("IMM_ASYLM_FS2023")
        .validationMessages(new ArrayList<>())
        .escapeCaseFlag(false)
        .feeCalculation(FeeCalculation.builder()
            .totalAmount(1354.66)
            .vatIndicator(Boolean.TRUE)
            .vatRateApplied(20.0)
            .calculatedVatAmount(201.78)
            .disbursementAmount(120.0)
            .disbursementVatAmount(24.0)
            .fixedFeeAmount(227.0)
            .build())
        .build();

    when(immigrationAsylumFixedFeeCalculator.calculate(feeCalculationRequest, feeEntity)).thenReturn(expectedResponse);

    FeeCalculationResponse result = immigrationAsylumFeeCalculator.calculate(feeCalculationRequest, feeEntity);

    assertThat(result).isNotNull();
    assertThat(result.getFeeCode()).isEqualTo("IACA");
    assertThat(result.getFeeCalculation()).isNotNull();
    assertThat(result.getFeeCalculation().getTotalAmount()).isEqualTo(1354.66);
  }

  @Test
  void getFee_whenImmigrationAsylumClaimFeeCodeHourly_shouldReturnFeeCalculationResponse() {
    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode("IAXL")
        .startDate(LocalDate.of(2025, 5, 11))
        .netProfitCosts(166.25)
        .jrFormFilling(67.89)
        .netDisbursementAmount(120.0)
        .disbursementVatAmount(24.0)
        .vatIndicator(true)
        .immigrationPriorAuthorityNumber("1334WA")
        .build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("IAXL")
        .feeSchemeCode(FeeSchemesEntity.builder().schemeCode("IMM_ASYLM_FS2023").build())
        .categoryType(IMMIGRATION_ASYLUM)
        .feeType(HOURLY)
        .profitCostLimit(new BigDecimal("800.00"))
        .disbursementLimit(new BigDecimal("400.00"))
        .build();


    FeeCalculationResponse expectedResponse = FeeCalculationResponse.builder()
        .feeCode("IAXL")
        .schemeId("IMM_ASYLM_FS2023")
        .validationMessages(new ArrayList<>())
        .escapeCaseFlag(false)
        .feeCalculation(FeeCalculation.builder()
            .totalAmount(424.97)
            .vatIndicator(Boolean.TRUE)
            .vatRateApplied(20.0)
            .calculatedVatAmount(46.83)
            .disbursementAmount(120.0)
            .requestedNetDisbursementAmount(120.0)
            .disbursementVatAmount(24.0)
            .hourlyTotalAmount(234.14)
            .jrFormFillingAmount(67.89)
            .netProfitCostsAmount(166.25)
            .requestedNetProfitCostsAmount(166.25)
            .build())
        .build();

    when(immigrationAsylumHourlyRateCalculator.calculate(feeCalculationRequest, feeEntity)).thenReturn(expectedResponse);

    FeeCalculationResponse result = immigrationAsylumFeeCalculator.calculate(feeCalculationRequest, feeEntity);

    assertThat(result).isNotNull();
    assertThat(result.getFeeCode()).isEqualTo("IAXL");
    assertThat(result.getFeeCalculation()).isNotNull();
    assertThat(result.getFeeCalculation().getTotalAmount()).isEqualTo(424.97);
  }

  @Test
  void getSupportedCategories_shouldReturnImmigrationAsylum() {
    Set<CategoryType> result = immigrationAsylumFeeCalculator.getSupportedCategories();

    assertThat(result).isEqualTo(Set.of(IMMIGRATION_ASYLUM));
  }
}