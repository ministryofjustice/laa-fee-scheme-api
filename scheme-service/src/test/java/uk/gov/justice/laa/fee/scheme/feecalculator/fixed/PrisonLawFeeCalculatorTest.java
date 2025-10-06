package uk.gov.justice.laa.fee.scheme.feecalculator.fixed;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.PRISON_LAW;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.entity.FeeSchemesEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.enums.FeeType;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;

@ExtendWith(MockitoExtension.class)
class PrisonLawFeeCalculatorTest {

  @Spy
  @InjectMocks
  private PrisonLawFeeCalculator prisonLawFeeCalculator;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testGetSupportedCategories() {
    assertTrue(prisonLawFeeCalculator.getSupportedCategories().contains(CategoryType.PRISON_LAW));
    assertEquals(1, prisonLawFeeCalculator.getSupportedCategories().size());
  }

  // ✅ Positive scenario
  @ParameterizedTest
  @MethodSource("testDataForPrisonLawClaims")
  void test_whenClaimsSubmittedForPrisonLaw_shouldReturnFee(
      String description,
      String feeCode,
      String uniqueFileNumber,
      boolean vatIndicator,
      double fixedFeeAmount,
      double fixedFeeVatAmount,
      double disbursementAmount,
      double disbursementVatAmount,
      double expectedTotal,
      double expectedFixedFee,
      double expectedCalculatedVat
  ) {

      FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode(feeCode)
        .startDate(LocalDate.of(2017, 7, 29))
        .vatIndicator(vatIndicator)
        .uniqueFileNumber(uniqueFileNumber)
        .netDisbursementAmount(disbursementAmount)
        .disbursementVatAmount(disbursementVatAmount)
        .build();

    FeeSchemesEntity feeSchemesEntity = FeeSchemesEntity.builder().schemeCode("PRISON_FS2016").build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode(feeCode)
        .feeSchemeCode(feeSchemesEntity)
        .fixedFee(BigDecimal.valueOf(fixedFeeAmount))
        .categoryType(PRISON_LAW)
        .feeType(FeeType.FIXED)
        .build();


    FeeCalculationResponse response = prisonLawFeeCalculator.calculate(feeCalculationRequest, feeEntity);

    FeeCalculation expectedCalculation = FeeCalculation.builder()
        .totalAmount(expectedTotal)
        .vatIndicator(vatIndicator)
        .vatRateApplied(20.0)
        .fixedFeeAmount(expectedFixedFee)
        .calculatedVatAmount(expectedCalculatedVat)
        .disbursementAmount(100.0)
        .disbursementVatAmount(20.0)
        .requestedNetDisbursementAmount(100.0)
        .build();

    FeeCalculationResponse expectedResponse = FeeCalculationResponse.builder()
        .feeCode(feeCode)
        .schemeId("PRISON_FS2016")
        .validationMessages(new ArrayList<>())
        .escapeCaseFlag(false)
        .feeCalculation(expectedCalculation)
        .build();

    assertThat(response)
        .usingRecursiveComparison()
        .isEqualTo(expectedResponse);
  }

  private static Arguments arguments(String testDescription,
                                     String feeCode,
                                     String uniqueFileNumber,
                                     boolean vatIndicator,
                                     double fixedFeeAmount,
                                     double fixedFeeVatAmount,
                                     double disbursementAmount,
                                     double disbursementVatAmount,
                                     double expectedTotal,
                                     double expectedFixedFee,
                                     double expectedCalculatedVat) {
    return Arguments.of(testDescription, feeCode, uniqueFileNumber, vatIndicator,
        fixedFeeAmount, fixedFeeVatAmount, disbursementAmount, disbursementVatAmount, expectedTotal, expectedFixedFee,
        expectedCalculatedVat);
  }

  public static Stream<Arguments> testDataForPrisonLawClaims() {
    return Stream.of(
        arguments("PRIA Prison Law Fee Code, VAT applied", "PRIA",
            "121221/799", true, 200.75, 40.15,
            100.00, 20.00, 360.9, 200.75, 40.15),

        arguments("PRIB1 Prison Law Fee Code, VAT not applied", "PRIB1",
            "121216/899", true, 203.93, 0.00,
            100.00, 20.00, 364.72, 203.93,40.79)
    );
  }
}
