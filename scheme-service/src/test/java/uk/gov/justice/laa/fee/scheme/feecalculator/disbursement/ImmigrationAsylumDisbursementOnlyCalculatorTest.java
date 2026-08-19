package uk.gov.justice.laa.fee.scheme.feecalculator.disbursement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.IMMIGRATION_ASYLUM;
import static uk.gov.justice.laa.fee.scheme.enums.FeeType.DISB_ONLY;
import static uk.gov.justice.laa.fee.scheme.enums.WarningType.WARN_DISBURSEMENT_VAT_CAPPED;
import static uk.gov.justice.laa.fee.scheme.enums.WarningType.WARN_IMM_ASYLM_DISB_ONLY;
import static uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner.TypeEnum.WARNING;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
import uk.gov.justice.laa.fee.scheme.exception.CaseConcludedDateRequiredException;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner;
import uk.gov.justice.laa.fee.scheme.service.VatRatesService;

@ExtendWith(MockitoExtension.class)
class ImmigrationAsylumDisbursementOnlyCalculatorTest {

  @Mock
  VatRatesService vatRatesService;

  @InjectMocks
  ImmigrationAsylumDisbursementOnlyCalculator immigrationAsylumDisbursementOnlyCalculator;

  public static Stream<Arguments> testData() {
    return Stream.of(
        arguments("ICASD", null, 1000.0, 200.0, 1000.0, 1600.0, 200.0, 1200.0, false, false),
        arguments("ICISD", "xyz", 1500.0, 200.0, 1500.0, 1200.0, 200.0, 1700.0, false, false),
        arguments("ICSSD", null, 1111.0, 200.0, 600.0, 600.0, 120.0, 720.0, true, true),
        arguments("ICSSD", null, null, null, null, 600.0, null, 0.0, false, false),
        arguments("ICASD", null, 500.0, 200.0, 500.0, 1600.0, 100.0, 600.0, false, true)
    );
  }

  private static Arguments arguments(String feeCode, String priorAuthority,
                                     Double requestedNetDisbursementAmount, Double requestedDisbursementVatAmount,
                                     Double expectedNetDisbursementAmount, double disbursementLimit,
                                     Double expectedDisbursementVatAmount, double expectedTotal,
                                     boolean hasNetWarning, boolean hasVatWarning) {
    return Arguments.of(feeCode, priorAuthority, requestedNetDisbursementAmount, requestedDisbursementVatAmount,
        expectedNetDisbursementAmount, disbursementLimit, expectedDisbursementVatAmount, expectedTotal,
        hasNetWarning, hasVatWarning);
  }

  @ParameterizedTest
  @MethodSource("testData")
  void calculate_whenImmigrationAndAsylum_withDisbursement(
      String feeCode,
      String immigrationPriorityAuthority,
      Double requestedNetDisbursementAmount,
      Double requestedDisbursementVatAmount,
      Double expectedNetDisbursementAmount,
      double disbursementLimit,
      Double expectedDisbursementVatAmount,
      double expectedTotal,
      boolean hasNetWarning,
      boolean hasVatWarning
  ) {

    // lenient: the "no disbursement VAT claimed" row never looks up the rate
    lenient().when(vatRatesService.getVatRateForDate(any(LocalDate.class))).thenReturn(BigDecimal.valueOf(20));

    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode(feeCode)
        .claimId("claim_123")
        .startDate(LocalDate.of(2025, 7, 29))
        .caseConcludedDate(LocalDate.of(2025, 7, 29))
        .netDisbursementAmount(requestedNetDisbursementAmount)
        .disbursementVatAmount(requestedDisbursementVatAmount)
        .immigrationPriorAuthorityNumber(immigrationPriorityAuthority)
        .build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode(feeCode)
        .feeScheme(FeeSchemesEntity.builder().schemeCode("IMM_ASYLM_FS2023").build())
        .categoryType(IMMIGRATION_ASYLUM)
        .feeType(DISB_ONLY)
        .disbursementLimit(BigDecimal.valueOf(disbursementLimit))
        .build();

    FeeCalculationResponse response = immigrationAsylumDisbursementOnlyCalculator.calculate(feeCalculationRequest, feeEntity);

    List<ValidationMessagesInner> validationMessages = new ArrayList<>();
    if (hasNetWarning) {
      validationMessages.add(buildWarning(WARN_IMM_ASYLM_DISB_ONLY.getCode(), WARN_IMM_ASYLM_DISB_ONLY.getMessage()));
    }
    if (hasVatWarning) {
      validationMessages.add(buildWarning(WARN_DISBURSEMENT_VAT_CAPPED.getCode(), WARN_DISBURSEMENT_VAT_CAPPED.getMessage()));
    }

    FeeCalculation expectedCalculation = FeeCalculation.builder()
        .totalAmount(expectedTotal)
        .disbursementAmount(expectedNetDisbursementAmount)
        .requestedNetDisbursementAmount(requestedNetDisbursementAmount)
        .disbursementVatAmount(expectedDisbursementVatAmount)
        .requestedDisbursementVatAmount(requestedDisbursementVatAmount)
        .build();

    FeeCalculationResponse expectedResponse = FeeCalculationResponse.builder()
        .feeCode(feeCode)
        .schemeId("IMM_ASYLM_FS2023")
        .claimId("claim_123")
        .validationMessages(validationMessages)
        .feeCalculation(expectedCalculation)
        .build();

    assertThat(response)
        .usingRecursiveComparison()
        .isEqualTo(expectedResponse);
  }

  @Test
  void calculate_whenDisbursementVatClaimedWithoutCaseConcludedDate_throws() {
    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode("ICASD")
        .claimId("claim_123")
        .startDate(LocalDate.of(2025, 7, 29))
        .netDisbursementAmount(100.0)
        .disbursementVatAmount(20.0)
        .build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("ICASD")
        .feeScheme(FeeSchemesEntity.builder().schemeCode("IMM_ASYLM_FS2023").build())
        .categoryType(IMMIGRATION_ASYLUM)
        .feeType(DISB_ONLY)
        .disbursementLimit(BigDecimal.valueOf(1600.0))
        .build();

    assertThatThrownBy(() -> immigrationAsylumDisbursementOnlyCalculator.calculate(feeCalculationRequest, feeEntity))
        .isInstanceOf(CaseConcludedDateRequiredException.class);
  }

  private static ValidationMessagesInner buildWarning(String code, String message) {
    return ValidationMessagesInner.builder()
        .code(code)
        .message(message)
        .type(WARNING)
        .build();
  }
}
