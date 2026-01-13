package uk.gov.justice.laa.fee.scheme.feecalculator.disbursement;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.IMMIGRATION_ASYLUM;
import static uk.gov.justice.laa.fee.scheme.enums.FeeType.DISB_ONLY;
import static uk.gov.justice.laa.fee.scheme.enums.WarningType.WARN_IMM_ASYLM_DISB_ONLY;
import static uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner.TypeEnum.WARNING;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.entity.FeeSchemesEntity;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner;

@ExtendWith(MockitoExtension.class)
class ImmigrationAsylumDisbursementOnlyCalculatorTest {

  @InjectMocks
  ImmigrationAsylumDisbursementOnlyCalculator immigrationAsylumDisbursementOnlyCalculator;

  private static Stream<Arguments> testData() {
    return Stream.of(
        arguments("ICASD, below disbursement limit",
            "ICASD", null, 1000.0, 200.0,
            1000.0, 1600.00, 1200.0, false),

        arguments("ICISD, above disbursement limit, with prior authority",
            "ICISD", "xyz", 1500.0, 200.0,
            1500.0, 1200.0, 1700.0, false),

        arguments("ICSSD, above disbursement limit, without prior authority",
            "ICSSD", null, 1111.0, 200.0,
            600.0, 600.0, 800.0, true),

        arguments("ICSSD, no disbursement claimed",
            "ICSSD", null, null, null,
            null, 600.0, 0.0, false)
    );
  }

  private static Arguments arguments(String scenario, String feeCode, String priorAuthority, Double requestedNetDisbursementAmount,
                                     Double disbursementVatAmount, Double netDisbursementAmount, double netDisbursementLimit,
                                     double expectedTotal, boolean hasWarning) {

    return Arguments.of(scenario, feeCode, priorAuthority, requestedNetDisbursementAmount, disbursementVatAmount,
        netDisbursementAmount, netDisbursementLimit, expectedTotal, hasWarning);
  }

  @ParameterizedTest
  @MethodSource("testData")
  void calculate_whenImmigrationAndAsylum_withDisbursement(
      String description,
      String feeCode,
      String immigrationPriorityAuthority,
      Double requestedNetDisbursementAmount,
      Double disbursementVatAmount,
      Double netDisbursementAmount,
      double disbursementLimit,
      double expectedTotal,
      boolean hasWarning
  ) {

    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode(feeCode)
        .claimId("claim_123")
        .startDate(LocalDate.of(2025, 7, 29))
        .netDisbursementAmount(requestedNetDisbursementAmount)
        .disbursementVatAmount(disbursementVatAmount)
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
    if (hasWarning) {
      ValidationMessagesInner validationMessage = ValidationMessagesInner.builder()
          .code(WARN_IMM_ASYLM_DISB_ONLY.getCode())
          .message(WARN_IMM_ASYLM_DISB_ONLY.getMessage())
          .type(WARNING)
          .build();
      validationMessages.add(validationMessage);
    }

    FeeCalculation expectedCalculation = FeeCalculation.builder()
        .totalAmount(expectedTotal)
        .disbursementAmount(netDisbursementAmount)
        .requestedNetDisbursementAmount(requestedNetDisbursementAmount)
        .disbursementVatAmount(disbursementVatAmount)
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

}