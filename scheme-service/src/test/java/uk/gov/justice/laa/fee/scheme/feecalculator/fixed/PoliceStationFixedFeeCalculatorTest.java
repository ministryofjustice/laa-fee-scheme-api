package uk.gov.justice.laa.fee.scheme.feecalculator.fixed;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.POLICE_STATION;
import static uk.gov.justice.laa.fee.scheme.enums.ErrorCode.ERR_CRIME_POLICE_SCHEME_ID;
import static uk.gov.justice.laa.fee.scheme.enums.ErrorCode.ERR_CRIME_POLICE_STATION_ID;
import static uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner.TypeEnum.WARNING;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.enums.FeeType;
import uk.gov.justice.laa.fee.scheme.exception.ValidationException;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner;
import uk.gov.justice.laa.fee.scheme.repository.PoliceStationFeesRepository;

@ExtendWith(MockitoExtension.class)
class PoliceStationFixedFeeCalculatorTest {

  @InjectMocks
  PoliceStationFixedFeeCalculator policeStationFixedFeeCalculator;

  @Mock
  PoliceStationFeesRepository policeStationFeesRepository;

  @Test
  void test_whenPoliceStationClaimForInvoiceSubmitted_shouldReturnValidResponse() {

    FeeSchemesEntity feeSchemesEntity = FeeSchemesEntity.builder().schemeCode("POL_FS2022").build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("INVC")
        .feeScheme(feeSchemesEntity)
        .profitCostLimit(new BigDecimal("123.56"))
        .fixedFee(new BigDecimal("200.56"))
        .categoryType(POLICE_STATION)
        .feeType(FeeType.FIXED)
        .build();

    PoliceStationFeesEntity policeStationFeesEntity = PoliceStationFeesEntity.builder()
        .psSchemeId("1004")
        .feeSchemeCode("POL_FS2022")
        .fixedFee(new BigDecimal("200.56"))
        .build();

    FeeCalculationRequest feeData = FeeCalculationRequest.builder()
        .feeCode("INVC")
        .startDate(LocalDate.of(2017, 7, 29))
        .vatIndicator(true)
        .policeStationSchemeId("1001")
        .policeStationId("NE001")
        .netDisbursementAmount(50.50)
        .disbursementVatAmount(20.15)
        .uniqueFileNumber("121222/4523")
        .travelAndWaitingCosts(45.0)
        .netProfitCosts(676.0)
        .build();

    when(policeStationFeesRepository.findPoliceStationFeeByPoliceStationIdAndFeeSchemeCode("NE001",
        "POL_FS2022")).thenReturn(List.of(policeStationFeesEntity));


    FeeCalculationResponse response = policeStationFixedFeeCalculator.calculate(feeData, feeEntity);


    FeeCalculation expectedCalculation = FeeCalculation.builder()
        .totalAmount(311.32)
        .vatIndicator(Boolean.TRUE)
        .vatRateApplied(20.0)
        .calculatedVatAmount(40.11)
        .disbursementAmount(50.5)
        .requestedNetDisbursementAmount(50.5)
        .disbursementVatAmount(20.15)
        .fixedFeeAmount(200.56)
        .calculatedVatAmount(40.11)
        .build();

    FeeCalculationResponse expectedResponse = FeeCalculationResponse.builder()
        .feeCode("INVC")
        .schemeId("POL_FS2022")
        .validationMessages(null)
        .escapeCaseFlag(false)
        .feeCalculation(expectedCalculation)
        .build();

    assertThat(response)
        .usingRecursiveComparison()
        .isEqualTo(expectedResponse);

  }

  @Test
  void test_whenPoliceStationClaimForInvoiceSubmitted_PoliceStationId_NotSupplied_shouldReturnValidResponse() {

    FeeSchemesEntity feeSchemesEntity = FeeSchemesEntity.builder().schemeCode("POL_FS2022").build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("INVC")
        .feeScheme(feeSchemesEntity)
        .profitCostLimit(new BigDecimal("123.56"))
        .fixedFee(new BigDecimal("200.56"))
        .categoryType(POLICE_STATION)
        .feeType(FeeType.FIXED)
        .build();

    PoliceStationFeesEntity policeStationFeesEntity = PoliceStationFeesEntity.builder()
        .psSchemeId("1004")
        .feeSchemeCode("POL_FS2022")
        .fixedFee(new BigDecimal("200.56"))
        .build();

    FeeCalculationRequest feeData = FeeCalculationRequest.builder()
        .feeCode("INVC")
        .startDate(LocalDate.of(2017, 7, 29))
        .vatIndicator(true)
        .policeStationSchemeId("1001")
        .policeStationId(null)
        .netDisbursementAmount(50.50)
        .disbursementVatAmount(20.15)
        .uniqueFileNumber("121222/4523")
        .travelAndWaitingCosts(45.0)
        .netProfitCosts(676.0)
        .build();

    when(policeStationFeesRepository.findPoliceStationFeeByPsSchemeIdAndFeeSchemeCode(any(),
        any())).thenReturn(List.of(policeStationFeesEntity));

    FeeCalculationResponse response = policeStationFixedFeeCalculator.calculate(feeData, feeEntity);

    FeeCalculation expectedCalculation = FeeCalculation.builder()
        .totalAmount(311.32)
        .vatIndicator(Boolean.TRUE)
        .vatRateApplied(20.0)
        .calculatedVatAmount(40.11)
        .disbursementAmount(50.5)
        .requestedNetDisbursementAmount(50.5)
        .disbursementVatAmount(20.15)
        .fixedFeeAmount(200.56)
        .calculatedVatAmount(40.11)
        .build();

    FeeCalculationResponse expectedResponse = FeeCalculationResponse.builder()
        .feeCode("INVC")
        .schemeId("POL_FS2022")
        .validationMessages(null)
        .escapeCaseFlag(false)
        .feeCalculation(expectedCalculation)
        .build();

    assertThat(response)
        .usingRecursiveComparison()
        .isEqualTo(expectedResponse);

  }

  @ParameterizedTest
  @MethodSource("testPoliceStationAttendanceClaims")
  void test_whenClaimsSubmittedForPoliceStationAttendance_shouldReturnFee(
      String description,
      String feeCode,
      String policeStationId,
      String policeStationSchemeId,
      String uniqueFileNumber,
      boolean vatIndicator,
      double expectedTotal,
      BigDecimal fixedFee,
      BigDecimal profitCostLimit,
      String feeSchemeCode,
      double expectedCalculatedVat,
      double expectedDisbursementAmount,
      double disbursementVatAmount,
      double expectedFixedFee,
      double travelAndWaitingCostAmount,
      double netProfitCostsAmount
  ) {

    FeeCalculationRequest feeData = FeeCalculationRequest.builder()
        .feeCode(feeCode)
        .startDate(LocalDate.of(2021, 7, 29))
        .vatIndicator(vatIndicator)
        .policeStationSchemeId(policeStationSchemeId)
        .policeStationId(policeStationId)
        .netDisbursementAmount(50.50)
        .disbursementVatAmount(20.15)
        .uniqueFileNumber(uniqueFileNumber)
        .travelAndWaitingCosts(travelAndWaitingCostAmount)
        .netProfitCosts(netProfitCostsAmount)
        .build();

    FeeSchemesEntity feeSchemesEntity = FeeSchemesEntity.builder().schemeCode(feeSchemeCode).build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode(feeCode)
        .feeScheme(feeSchemesEntity)
        .profitCostLimit(profitCostLimit)
        .fixedFee(fixedFee)
        .categoryType(POLICE_STATION)
        .feeType(FeeType.FIXED)
        .build();

    PoliceStationFeesEntity policeStationFeesEntity = PoliceStationFeesEntity.builder()
        .psSchemeId(policeStationSchemeId)
        .feeSchemeCode(feeSchemeCode)
        .fixedFee(fixedFee)
        .build();

    when(policeStationFeesRepository.findPoliceStationFeeByPoliceStationIdAndFeeSchemeCode(policeStationId,
        feeSchemeCode)).thenReturn(List.of(policeStationFeesEntity));

    FeeCalculationResponse response = policeStationFixedFeeCalculator.calculate(feeData, feeEntity);

    FeeCalculation expectedCalculation = FeeCalculation.builder()
        .totalAmount(expectedTotal)
        .vatIndicator(vatIndicator)
        .vatRateApplied(20.0)
        .disbursementAmount(expectedDisbursementAmount)
        .requestedNetDisbursementAmount(50.5)
        .disbursementVatAmount(disbursementVatAmount)
        .fixedFeeAmount(expectedFixedFee)
        .calculatedVatAmount(expectedCalculatedVat)
        .build();

    FeeCalculationResponse expectedResponse = FeeCalculationResponse.builder()
        .feeCode(feeCode)
        .schemeId(feeSchemeCode)
        .validationMessages(null)
        .escapeCaseFlag(false)
        .feeCalculation(expectedCalculation)
        .build();

    assertThat(response)
        .usingRecursiveComparison()
        .isEqualTo(expectedResponse);
  }

  @ParameterizedTest
  @MethodSource("testPoliceStationTelephonicAdviceClaims")
  void test_whenClaimsSubmittedForPoliceStationTelephonicAdvice_shouldReturnFee(
      String description,
      String feeCode,
      String policeStationId,
      String policeStationSchemeId,
      String uniqueFileNumber,
      boolean vatIndicator,
      double expectedTotal,
      BigDecimal fixedFee,
      BigDecimal profitCostLimit,
      String feeSchemeCode,
      double expectedCalculatedVat,
      double expectedDisbursementAmount,
      double disbursementVatAmount,
      double expectedFixedFee,
      double travelAndWaitingCostAmount,
      double netProfitCostsAmount
  ) {

    FeeCalculationRequest feeData = FeeCalculationRequest.builder()
        .feeCode(feeCode)
        .startDate(LocalDate.of(2017, 7, 29))
        .vatIndicator(vatIndicator)
        .policeStationSchemeId(policeStationSchemeId)
        .policeStationId(policeStationId)
        .uniqueFileNumber(uniqueFileNumber)
        .travelAndWaitingCosts(travelAndWaitingCostAmount)
        .netProfitCosts(netProfitCostsAmount)
        .build();

    FeeSchemesEntity feeSchemesEntity = FeeSchemesEntity.builder().schemeCode(feeSchemeCode).build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode(feeCode)
        .feeScheme(feeSchemesEntity)
        .profitCostLimit(profitCostLimit)
        .fixedFee(fixedFee)
        .categoryType(POLICE_STATION)
        .feeType(FeeType.FIXED)
        .build();

    FeeCalculationResponse response = policeStationFixedFeeCalculator.calculate(feeData, feeEntity);

    FeeCalculation expectedCalculation = FeeCalculation.builder()
        .totalAmount(expectedTotal)
        .vatIndicator(vatIndicator)
        .vatRateApplied(20.0)
        .fixedFeeAmount(expectedFixedFee)
        .calculatedVatAmount(expectedCalculatedVat)
        .build();

    FeeCalculationResponse expectedResponse = FeeCalculationResponse.builder()
        .feeCode(feeCode)
        .schemeId(feeSchemeCode)
        .validationMessages(new ArrayList<>())
        .escapeCaseFlag(false)
        .feeCalculation(expectedCalculation)
        .build();

    assertThat(response)
        .usingRecursiveComparison()
        .isEqualTo(expectedResponse);
  }


  @ParameterizedTest
  @MethodSource("testPoliceStationAttendanceClaimsForEscapeCases")
  void test_whenClaimsSubmittedForPoliceStationTelephonicAdviceAreFlaggedAsEscape_shouldReturnFeeWithEscapeFlagEnabled(
      String description,
      String feeCode,
      String policeStationId,
      String policeStationSchemeId,
      String uniqueFileNumber,
      boolean vatIndicator,
      double expectedTotal,
      BigDecimal fixedFee,
      BigDecimal profitCostLimit,
      String feeSchemeCode,
      double expectedCalculatedVat,
      double expectedDisbursementAmount,
      double disbursementVatAmount,
      double expectedFixedFee,
      double travelAndWaitingCostAmount,
      double netProfitCostsAmount
  ) {

    FeeCalculationRequest feeData = FeeCalculationRequest.builder()
        .feeCode(feeCode)
        .startDate(LocalDate.of(2022, 12, 29))
        .vatIndicator(vatIndicator)
        .policeStationSchemeId(policeStationSchemeId)
        .policeStationId(policeStationId)
        .uniqueFileNumber(uniqueFileNumber)
        .netProfitCosts(netProfitCostsAmount)
        .build();

    FeeSchemesEntity feeSchemesEntity = FeeSchemesEntity.builder().schemeCode(feeSchemeCode).build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode(feeCode)
        .feeScheme(feeSchemesEntity)
        .fixedFee(new BigDecimal("999.99"))
        .categoryType(POLICE_STATION)
        .feeType(FeeType.FIXED)
        .build();

    PoliceStationFeesEntity policeStationFeesEntity = PoliceStationFeesEntity.builder()
        .psSchemeId(policeStationSchemeId)
        .feeSchemeCode(feeSchemeCode)
        .fixedFee(new BigDecimal("999.99"))
        .escapeThreshold(new BigDecimal("123.99"))
        .build();

    when(policeStationFeesRepository.findPoliceStationFeeByPoliceStationIdAndFeeSchemeCode(policeStationId,
        feeSchemeCode)).thenReturn(List.of(policeStationFeesEntity));

    FeeCalculationResponse response = policeStationFixedFeeCalculator.calculate(feeData, feeEntity);

    FeeCalculation expectedCalculation = FeeCalculation.builder()
        .totalAmount(expectedTotal)
        .vatIndicator(vatIndicator)
        .vatRateApplied(20.0)
        .fixedFeeAmount(expectedFixedFee)
        .calculatedVatAmount(expectedCalculatedVat)
        .disbursementVatAmount(0.0)
        .disbursementAmount(0.0)
        .requestedNetDisbursementAmount(0.0)
        .build();

    ValidationMessagesInner validationMessage = ValidationMessagesInner.builder()
        .message("123")
        .type(WARNING)
        .build();

    FeeCalculationResponse expectedResponse = FeeCalculationResponse.builder()
        .feeCode(feeCode)
        .schemeId(feeSchemeCode)
        .validationMessages(List.of(validationMessage))
        .escapeCaseFlag(true)
        .feeCalculation(expectedCalculation)
        .build();


    assertThat(response)
        .usingRecursiveComparison()
        .isEqualTo(expectedResponse);
  }

  @Test
  void getPoliceStationFeesEntity_whenGivenInvalidPoliceStationId_shouldThrowException() {

    FeeSchemesEntity feeSchemesEntity = FeeSchemesEntity.builder().schemeCode("POL_FS2022").build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("INVC")
        .feeScheme(feeSchemesEntity)
        .profitCostLimit(new BigDecimal("123.56"))
        .fixedFee(new BigDecimal("200.56"))
        .categoryType(POLICE_STATION)
        .feeType(FeeType.FIXED)
        .build();

    FeeCalculationRequest feeData = FeeCalculationRequest.builder()
        .feeCode("INVC")
        .startDate(LocalDate.of(2017, 7, 29))
        .vatIndicator(true)
        .policeStationId("BLAH")
        .netDisbursementAmount(50.50)
        .disbursementVatAmount(20.15)
        .uniqueFileNumber("121222/4523")
        .travelAndWaitingCosts(45.0)
        .netProfitCosts(676.0)
        .build();

    when(policeStationFeesRepository.findPoliceStationFeeByPoliceStationIdAndFeeSchemeCode("BLAH",
        "POL_FS2022")).thenReturn(List.of());

    assertThatThrownBy(() -> policeStationFixedFeeCalculator.calculate(feeData, feeEntity))
        .isInstanceOf(ValidationException.class)
        .hasFieldOrPropertyWithValue("error", ERR_CRIME_POLICE_STATION_ID)
        .hasMessageContaining("ERRCRM3 - Enter a valid Police station ID, Court ID, or Prison ID.");
  }

  @Test
  void getPoliceStationFeesEntity_whenGivenInvalidPoliceSchemeId_shouldThrowException() {

    FeeSchemesEntity feeSchemesEntity = FeeSchemesEntity.builder().schemeCode("POL_FS2022").build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("INVC")
        .feeScheme(feeSchemesEntity)
        .profitCostLimit(new BigDecimal("123.56"))
        .fixedFee(new BigDecimal("200.56"))
        .categoryType(POLICE_STATION)
        .feeType(FeeType.FIXED)
        .build();

    FeeCalculationRequest feeData = FeeCalculationRequest.builder()
        .feeCode("INVC")
        .startDate(LocalDate.of(2017, 7, 29))
        .vatIndicator(true)
        .policeStationSchemeId("BLAH")
        .netDisbursementAmount(50.50)
        .disbursementVatAmount(20.15)
        .uniqueFileNumber("121222/4523")
        .travelAndWaitingCosts(45.0)
        .netProfitCosts(676.0)
        .build();

    when(policeStationFeesRepository.findPoliceStationFeeByPsSchemeIdAndFeeSchemeCode("BLAH",
        "POL_FS2022")).thenReturn(List.of());

    assertThatThrownBy(() -> policeStationFixedFeeCalculator.calculate(feeData, feeEntity))
        .isInstanceOf(ValidationException.class)
        .hasFieldOrPropertyWithValue("error", ERR_CRIME_POLICE_SCHEME_ID)
        .hasMessageContaining("ERRCRM4 - Enter a valid Scheme ID.");
  }

  @Test
  void getSupportedCategories_shouldReturnEmptySet() {
    Set<CategoryType> result = policeStationFixedFeeCalculator.getSupportedCategories();

    assertThat(result).isEmpty();
  }

  public static Stream<Arguments> testPoliceStationAttendanceClaims() {
    return Stream.of(
        arguments("INVC Police Fee Code, VAT applied", "INVC", "NE001",
            "1001", "121221/7899", true, 87.93,
            new BigDecimal("14.4"), null, "POL_2016", 2.88,
            50.5, 20.15, 14.4, 0.0, 0.0),

        arguments("INVC Police Fee Code, VAT not applied", "INVC", "NE013",
            "1004", "121223/6655", false, 85.05,
            new BigDecimal("14.4"), null, "POL_2023", 0,
            50.5, 20.15, 14.4, 0.0, 0.0)
    );
  }


  public static Stream<Arguments> testPoliceStationAttendanceClaimsForEscapeCases() {
    return Stream.of(
        arguments("INVC Police Fee Code, VAT applied", "INVC", "NE001",
            "1001", "121222/7899", true, 1199.99,
            new BigDecimal("14.4"), null, "POL_FS2022", 200.00,
            50.5, 20.15, 999.99, 0.0, 0.0)
    );
  }

  public static Stream<Arguments> testPoliceStationTelephonicAdviceClaims() {
    return Stream.of(
        arguments("INVB1 Police Fee Code, VAT applied", "INVB1", "NE001",
            "1001", "121221/7899", true, 17.28,
            new BigDecimal("14.4"), null, "POL_2016", 2.88,
            50.5, 20.15, 14.4, 0.0, 0.0),

        arguments("INVB1 Police Fee Code, VAT not applied", "INVB1", "NE013",
            "1004", "121223/6655", false, 14.4,
            new BigDecimal("14.4"), null, "POL_2023", 0,
            50.5, 20.15, 14.4, 0.0, 0.0),

        arguments("INVB2 Police Fee Code, VAT applied", "INVB2", "NE001",
            "1001", "121221/7899", true, 17.28,
            new BigDecimal("14.4"), null, "POL_2016", 2.88,
            50.5, 20.15, 14.4, 0.0, 0.0),

        arguments("INVB2 Police Fee Code, VAT not applied", "INVB2", "NE013",
            "1004", "121223/6655", false, 14.4,
            new BigDecimal("14.4"), null, "POL_2023", 0,
            50.5, 20.15, 14.4, 0.0, 0.0),

        arguments("INVB1 Police Fee Code, Police Station Id Not Provided ", "INVB1", null,
            "1004", "121223/6655", true, 20.74,
            new BigDecimal("17.28"), null, "POL_2023", 3.46,
            50.5, 20.15, 17.28, 0.0, 0.0),

        arguments("INVB2 Police Fee Code, Police Station Scheme Id Not Provided", "INVB2", "NE001",
            null, "121221/7899", true, 17.28,
            new BigDecimal("14.4"), null, "POL_2016", 2.88,
            50.5, 20.15, 14.4, 0.0, 0.0)
    );
  }


  private static Arguments arguments(String testDescription,
                                     String feeCode,
                                     String policeStationId,
                                     String policeStationSchemeId,
                                     String uniqueFileNumber,
                                     boolean vatIndicator,
                                     double expectedTotal,
                                     BigDecimal fixedFee,
                                     BigDecimal profitCostLimit,
                                     String feeSchemeCode,
                                     double expectedCalculatedVat,
                                     double disbursementAmount,
                                     double disbursementVatAmount,
                                     double fixedFeeAmount,
                                     double travelAndWaitingCostAmount,
                                     double netProfitCostsAmount) {
    return Arguments.of(testDescription, feeCode, policeStationId, policeStationSchemeId, uniqueFileNumber, vatIndicator,
        expectedTotal, fixedFee, profitCostLimit, feeSchemeCode, expectedCalculatedVat, disbursementAmount,
        disbursementVatAmount, fixedFeeAmount, travelAndWaitingCostAmount, netProfitCostsAmount);
  }

}
