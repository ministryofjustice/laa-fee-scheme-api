package uk.gov.justice.laa.fee.scheme.feecalculator.fixed;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.POLICE_STATION;
import static uk.gov.justice.laa.fee.scheme.enums.ErrorType.ERR_CRIME_POLICE_SCHEME_ID;
import static uk.gov.justice.laa.fee.scheme.enums.ErrorType.ERR_CRIME_POLICE_STATION_ID;
import static uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner.TypeEnum.WARNING;

import java.math.BigDecimal;
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
    FeeEntity feeEntity = buildFixedFeeEntity("INVC", feeSchemesEntity, new BigDecimal("200.56"));

    PoliceStationFeesEntity policeStationFeesEntity = PoliceStationFeesEntity.builder()
        .psSchemeId("1004")
        .feeSchemeCode("POL_FS2022")
        .fixedFee(new BigDecimal("200.56"))
        .build();

    FeeCalculationRequest feeData = FeeCalculationRequest.builder()
        .feeCode("INVC")
        .vatIndicator(true)
        .policeStationSchemeId("1001")
        .policeStationId("NE001")
        .netDisbursementAmount(50.50)
        .disbursementVatAmount(20.15)
        .uniqueFileNumber("121222/452")
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
        .build();

    FeeCalculationResponse expectedResponse = FeeCalculationResponse.builder()
        .feeCode("INVC")
        .schemeId("POL_FS2022")
        .validationMessages(new ArrayList<>())
        .escapeCaseFlag(false)
        .feeCalculation(expectedCalculation)
        .build();

    assertThat(response).isEqualTo(expectedResponse);
  }

  @Test
  void test_whenPoliceStationClaimForInvoiceSubmitted_PoliceStationId_NotSupplied_shouldReturnValidResponse() {

    FeeSchemesEntity feeSchemesEntity = FeeSchemesEntity.builder().schemeCode("POL_FS2022").build();
    FeeEntity feeEntity = buildFixedFeeEntity("INVC", feeSchemesEntity, new BigDecimal("200.56"));

    PoliceStationFeesEntity policeStationFeesEntity = PoliceStationFeesEntity.builder()
        .psSchemeId("1004")
        .feeSchemeCode("POL_FS2022")
        .fixedFee(new BigDecimal("200.56"))
        .build();

    FeeCalculationRequest feeData = FeeCalculationRequest.builder()
        .feeCode("INVC")
        .claimId("claim_123")
        .vatIndicator(true)
        .policeStationSchemeId("1001")
        .policeStationId(null)
        .netDisbursementAmount(50.50)
        .disbursementVatAmount(20.15)
        .uniqueFileNumber("121222/452")
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
        .build();

    FeeCalculationResponse expectedResponse = FeeCalculationResponse.builder()
        .feeCode("INVC")
        .claimId("claim_123")
        .schemeId("POL_FS2022")
        .validationMessages(new ArrayList<>())
        .escapeCaseFlag(false)
        .feeCalculation(expectedCalculation)
        .build();

    assertThat(response).isEqualTo(expectedResponse);

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
      String feeSchemeCode,
      double expectedCalculatedVat,
      double expectedDisbursementAmount,
      double disbursementVatAmount,
      double expectedFixedFee
  ) {

    FeeCalculationRequest feeData = FeeCalculationRequest.builder()
        .feeCode(feeCode)
        .claimId("claim_123")
        .vatIndicator(vatIndicator)
        .policeStationSchemeId(policeStationSchemeId)
        .policeStationId(policeStationId)
        .netDisbursementAmount(50.50)
        .disbursementVatAmount(20.15)
        .uniqueFileNumber(uniqueFileNumber)
        .netProfitCosts(0.00)
        .build();

    FeeSchemesEntity feeSchemesEntity = FeeSchemesEntity.builder().schemeCode(feeSchemeCode).build();
    FeeEntity feeEntity = buildFixedFeeEntity(feeCode, feeSchemesEntity, new BigDecimal("200.56"));

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
        .vatRateApplied(vatIndicator ? 20.0 : null)
        .disbursementAmount(expectedDisbursementAmount)
        .requestedNetDisbursementAmount(50.5)
        .disbursementVatAmount(disbursementVatAmount)
        .fixedFeeAmount(expectedFixedFee)
        .calculatedVatAmount(expectedCalculatedVat)
        .build();

    FeeCalculationResponse expectedResponse = FeeCalculationResponse.builder()
        .feeCode(feeCode)
        .claimId("claim_123")
        .schemeId(feeSchemeCode)
        .validationMessages(new ArrayList<>())
        .escapeCaseFlag(false)
        .feeCalculation(expectedCalculation)
        .build();

    assertThat(response).isEqualTo(expectedResponse);
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
      String feeSchemeCode,
      double expectedCalculatedVat,
      double expectedDisbursementAmount,
      double disbursementVatAmount,
      double expectedFixedFee
  ) {

    FeeCalculationRequest feeData = FeeCalculationRequest.builder()
        .feeCode(feeCode)
        .vatIndicator(vatIndicator)
        .policeStationSchemeId(policeStationSchemeId)
        .policeStationId(policeStationId)
        .uniqueFileNumber(uniqueFileNumber)
        .netTravelCosts(0.00)
        .netWaitingCosts(0.00)
        .netProfitCosts(0.00)
        .build();

    FeeSchemesEntity feeSchemesEntity = FeeSchemesEntity.builder().schemeCode(feeSchemeCode).build();
    FeeEntity feeEntity = buildFixedFeeEntity(feeCode, feeSchemesEntity, fixedFee);

    FeeCalculationResponse response = policeStationFixedFeeCalculator.calculate(feeData, feeEntity);

    FeeCalculation expectedCalculation = FeeCalculation.builder()
        .totalAmount(expectedTotal)
        .vatIndicator(vatIndicator)
        .vatRateApplied(vatIndicator ? 20.0 : null)
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

    assertThat(response).isEqualTo(expectedResponse);
  }

  @Test
  void test_whenClaimsSubmittedForPoliceStationAreFlaggedAsEscape_shouldReturnFeeWithEscapeFlagEnabled() {

    FeeCalculationRequest feeData = FeeCalculationRequest.builder()
        .feeCode("INVC")
        .vatIndicator(true)
        .policeStationSchemeId("1001")
        .policeStationId("NE001")
        .uniqueFileNumber("121222/789")
        .netDisbursementAmount(300.0)
        .disbursementVatAmount(60.0)
        .build();

    FeeSchemesEntity feeSchemesEntity = FeeSchemesEntity.builder().schemeCode("POL_FS2022").build();
    FeeEntity feeEntity = buildFixedFeeEntity("INVC", feeSchemesEntity, new BigDecimal("999.99"));

    PoliceStationFeesEntity policeStationFeesEntity = PoliceStationFeesEntity.builder()
        .psSchemeId("1001")
        .feeSchemeCode("POL_FS2022")
        .fixedFee(new BigDecimal("200.00"))
        .escapeThreshold(new BigDecimal("400.00"))
        .build();

    when(policeStationFeesRepository.findPoliceStationFeeByPoliceStationIdAndFeeSchemeCode("NE001",
        "POL_FS2022")).thenReturn(List.of(policeStationFeesEntity));

    FeeCalculationResponse response = policeStationFixedFeeCalculator.calculate(feeData, feeEntity);

    FeeCalculation expectedCalculation = FeeCalculation.builder()
        .totalAmount(600.0)
        .vatIndicator(true)
        .vatRateApplied(20.0)
        .fixedFeeAmount(200.0)
        .calculatedVatAmount(40.0)
        .disbursementAmount(300.0)
        .disbursementVatAmount(60.0)
        .requestedNetDisbursementAmount(300.00)
        .build();

    ValidationMessagesInner validationMessage = ValidationMessagesInner.builder()
        .code("WARCRM8")
        .message("The claim exceeds the Escape Case Threshold. An Escape Case Claim must be submitted for further costs to be paid.")
        .type(WARNING)
        .build();

    FeeCalculationResponse expectedResponse = FeeCalculationResponse.builder()
        .feeCode("INVC")
        .schemeId("POL_FS2022")
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
    FeeEntity feeEntity = buildFixedFeeEntity("INVC", feeSchemesEntity, new BigDecimal("200.56"));

    FeeCalculationRequest feeData = FeeCalculationRequest.builder()
        .feeCode("INVC")
        .vatIndicator(true)
        .policeStationId("BLAH")
        .netDisbursementAmount(50.50)
        .disbursementVatAmount(20.15)
        .uniqueFileNumber("121222/452")
        .netTravelCosts(35.00)
        .netWaitingCosts(10.00)
        .netProfitCosts(676.0)
        .build();

    when(policeStationFeesRepository.findPoliceStationFeeByPoliceStationIdAndFeeSchemeCode("BLAH",
        "POL_FS2022")).thenReturn(List.of());

    assertThatThrownBy(() -> policeStationFixedFeeCalculator.calculate(feeData, feeEntity))
        .isInstanceOf(ValidationException.class)
        .hasFieldOrPropertyWithValue("error", ERR_CRIME_POLICE_STATION_ID)
        .hasMessage("ERRCRM3 - Enter a valid Police station ID, Court ID, or Prison ID.");
  }

  @Test
  void getPoliceStationFeesEntity_whenGivenInvalidPoliceSchemeId_shouldThrowException() {

    FeeSchemesEntity feeSchemesEntity = FeeSchemesEntity.builder().schemeCode("POL_FS2022").build();
    FeeEntity feeEntity = buildFixedFeeEntity("INVC", feeSchemesEntity, new BigDecimal("200.56"));

    FeeCalculationRequest feeData = FeeCalculationRequest.builder()
        .feeCode("INVC")
        .vatIndicator(true)
        .policeStationSchemeId("BLAH")
        .netDisbursementAmount(50.50)
        .disbursementVatAmount(20.15)
        .uniqueFileNumber("121222/452")
        .netTravelCosts(35.00)
        .netWaitingCosts(10.00)
        .netProfitCosts(676.0)
        .build();

    when(policeStationFeesRepository.findPoliceStationFeeByPsSchemeIdAndFeeSchemeCode("BLAH",
        "POL_FS2022")).thenReturn(List.of());

    assertThatThrownBy(() -> policeStationFixedFeeCalculator.calculate(feeData, feeEntity))
        .isInstanceOf(ValidationException.class)
        .hasFieldOrPropertyWithValue("error", ERR_CRIME_POLICE_SCHEME_ID)
        .hasMessage("ERRCRM4 - Enter a valid Scheme ID.");
  }

  @Test
  void getPoliceStationFeesEntity_whenPoliceStationAndPoliceSchemeIdIsMissing_shouldThrowException() {

    FeeSchemesEntity feeSchemesEntity = FeeSchemesEntity.builder().schemeCode("POL_FS2022").build();
    FeeEntity feeEntity = buildFixedFeeEntity("INVC", feeSchemesEntity, new BigDecimal("200.56"));

    FeeCalculationRequest feeData = FeeCalculationRequest.builder()
        .feeCode("INVC")
        .vatIndicator(true)
        .netDisbursementAmount(50.50)
        .disbursementVatAmount(20.15)
        .uniqueFileNumber("121222/452")
        .netTravelCosts(35.00)
        .netWaitingCosts(10.00)
        .netProfitCosts(676.0)
        .build();

    assertThatThrownBy(() -> policeStationFixedFeeCalculator.calculate(feeData, feeEntity))
        .isInstanceOf(ValidationException.class)
        .hasFieldOrPropertyWithValue("error", ERR_CRIME_POLICE_SCHEME_ID)
        .hasMessage("ERRCRM4 - Enter a valid Scheme ID.");

    verify(policeStationFeesRepository, never()).findPoliceStationFeeByPoliceStationIdAndFeeSchemeCode(any(), any());
  }

  @Test
  void calculate_whenGivenFeeCodeAndNetDisbursementAmount_shouldReturnWithoutWarning() {

    FeeSchemesEntity feeSchemesEntity = FeeSchemesEntity.builder().schemeCode("POL_FS2022").build();
    FeeEntity feeEntity = buildFixedFeeEntity("INVB1", feeSchemesEntity, new BigDecimal("200.56"));

    FeeCalculationRequest feeData = FeeCalculationRequest.builder()
        .feeCode("INVB1")
        .claimId("claim_123")
        .vatIndicator(true)
        .policeStationSchemeId("1001")
        .netDisbursementAmount(50.50)
        .disbursementVatAmount(20.15)
        .uniqueFileNumber("121222/452")
        .netTravelCosts(20.00)
        .netWaitingCosts(10.00)
        .netProfitCosts(50.00)
        .build();

    FeeCalculationResponse response = policeStationFixedFeeCalculator.calculate(feeData, feeEntity);

    FeeCalculation expectedCalculation = FeeCalculation.builder()
        .totalAmount(240.67)
        .vatIndicator(Boolean.TRUE)
        .vatRateApplied(20.0)
        .calculatedVatAmount(40.11)
        .fixedFeeAmount(200.56)
        .build();

    FeeCalculationResponse expectedResponse = FeeCalculationResponse.builder()
        .feeCode("INVB1")
        .schemeId("POL_FS2022")
        .claimId("claim_123")
        .escapeCaseFlag(false)
        .feeCalculation(expectedCalculation)
        .build();

    assertThat(response).isEqualTo(expectedResponse);

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
            new BigDecimal("14.4"), "POL_2016", 2.88, 14.4),

        arguments("INVC Police Fee Code, VAT not applied", "INVC", "NE013",
            "1004", "121223/6655", false, 85.05,
            new BigDecimal("14.4"), "POL_2023", 0, 14.4)
    );
  }

  public static Stream<Arguments> testPoliceStationTelephonicAdviceClaims() {
    return Stream.of(
        arguments("INVB1 Police Fee Code, VAT applied", "INVB1", "NE001",
            "1001", "121221/7899", true, 17.28,
            new BigDecimal("14.4"), "POL_2016", 2.88,14.4),
        arguments("INVB1 Police Fee Code, VAT applied", "INVB1", "NE001",
            "1001", "121221/7899", true, 17.28,
            new BigDecimal("14.4"), "POL_2016", 2.88,14.4),

        arguments("INVB1 Police Fee Code, VAT not applied", "INVB1", "NE013",
            "1004", "121223/6655", false, 14.4,
            new BigDecimal("14.4"), "POL_2023", 0, 14.4),

        arguments("INVB2 Police Fee Code, VAT applied", "INVB2", "NE001",
            "1001", "121221/7899", true, 17.28,
            new BigDecimal("14.4"),  "POL_2016", 2.88, 14.4),

        arguments("INVB2 Police Fee Code, VAT not applied", "INVB2", "NE013",
            "1004", "121223/6655", false, 14.4,
            new BigDecimal("14.4"), "POL_2023", 0, 14.4),

        arguments("INVB1 Police Fee Code, Police Station Id Not Provided ", "INVB1", null,
            "1004", "121223/6655", true, 20.74,
            new BigDecimal("17.28"),  "POL_2023", 3.46,17.28),

        arguments("INVB2 Police Fee Code, Police Station Scheme Id Not Provided", "INVB2", "NE001",
            null, "121221/7899", true, 17.28,
            new BigDecimal("14.4"), "POL_2016", 2.88,14.4)
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
                                     String feeSchemeCode,
                                     double expectedCalculatedVat,
                                     double fixedFeeAmount) {

    return Arguments.of(testDescription, feeCode, policeStationId, policeStationSchemeId, uniqueFileNumber, vatIndicator,
        expectedTotal, fixedFee, feeSchemeCode, expectedCalculatedVat, 50.5, 20.15, fixedFeeAmount);
  }

  private FeeEntity buildFixedFeeEntity(String feeCode, FeeSchemesEntity feeSchemesEntity, BigDecimal fixedFee) {
    return FeeEntity.builder()
        .feeCode(feeCode)
        .feeScheme(feeSchemesEntity)
        .fixedFee(fixedFee)
        .categoryType(POLICE_STATION)
        .feeType(FeeType.FIXED)
        .build();
  }

}
