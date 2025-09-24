package uk.gov.justice.laa.fee.scheme.aop;

import java.time.LocalDate;
import java.util.ArrayList;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import uk.gov.justice.laa.fee.scheme.controller.FeeCalculationController;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.service.FeeCalculationService;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoggingAspectTest {

  @InjectMocks
  LoggingAspect loggingAspect;

  @Mock
  JoinPoint joinPoint;

  @Mock
  Signature signature;

  @Mock
  FeeCalculationService feeCalculationService;

  @Mock
  FeeCalculationController feeCalculationController;

  @Test
  void testLogBeforeAndAfterReturningWithService() {
    // Arrange
    FeeCalculationRequest request = FeeCalculationRequest.builder()
        .feeCode("FEE123")
        .startDate(LocalDate.of(2025, 7, 29))
        .netDisbursementAmount(70.75)
        .disbursementVatAmount(20.15)
        .vatIndicator(true)
        .numberOfMediationSessions(2)
        .build();

    FeeCalculation expectedCalculation = FeeCalculation.builder()
        .totalAmount(1587.50)
        .vatIndicator(true)
        .vatRateApplied(20.0)
        .disbursementAmount(800.0)
        .disbursementVatAmount(40.0)
        .fixedFeeAmount(456.00)
        .calculatedVatAmount(236.00)
        .build();

    FeeCalculationResponse response = FeeCalculationResponse.builder()
        .feeCode("INVC")
        .schemeId("IMM_ASYLM_FS2023")
        .claimId("claim_123")
        .validationMessages(new ArrayList<>())
        .escapeCaseFlag(false) // hardcoded till escape logic implemented
        .feeCalculation(expectedCalculation)
        .build();
    when(joinPoint.getSignature()).thenReturn(signature);
    when(joinPoint.getTarget()).thenReturn(new Object());
    when(feeCalculationService.calculateFee(request)).thenReturn(response);

    // Simulate Aspect @Before
    loggingAspect.logMethodEntry(joinPoint);

    // Call mocked service
    FeeCalculationResponse result = feeCalculationService.calculateFee(request);

    // Simulate Aspect @AfterReturning
    loggingAspect.logMethodExit(joinPoint, result);

    // Verify service was called
    verify(feeCalculationService, times(1)).calculateFee(request);

    // Assert returned response
    assert "INVC".equals(result.getFeeCode());

    // Verify signature access
    verify(joinPoint, times(2)).getSignature();
  }

  @Test
  void testLogBeforeAndAfterReturningWithController() {
    // Arrange
    FeeCalculationRequest request = FeeCalculationRequest.builder()
        .feeCode("FEE123")
        .startDate(LocalDate.of(2025, 7, 29))
        .netDisbursementAmount(70.75)
        .disbursementVatAmount(20.15)
        .vatIndicator(true)
        .numberOfMediationSessions(2)
        .build();

    FeeCalculation expectedCalculation = FeeCalculation.builder()
        .totalAmount(1587.50)
        .vatIndicator(true)
        .vatRateApplied(20.0)
        .disbursementAmount(800.0)
        .disbursementVatAmount(40.0)
        .fixedFeeAmount(456.00)
        .calculatedVatAmount(236.00)
        .build();

    FeeCalculationResponse feeCalculationResponse = FeeCalculationResponse.builder()
        .feeCode("INVC")
        .schemeId("IMM_ASYLM_FS2023")
        .claimId("claim_123")
        .validationMessages(new ArrayList<>())
        .escapeCaseFlag(false) // hardcoded till escape logic implemented
        .feeCalculation(expectedCalculation)
        .build();

    when(joinPoint.getSignature()).thenReturn(signature);
    when(joinPoint.getTarget()).thenReturn(new Object());
    when(feeCalculationController.getFeeCalculation(request)).thenReturn(ResponseEntity.ok(feeCalculationResponse));

    // Simulate Aspect @Before
    loggingAspect.logMethodEntry(joinPoint);

    // Call mocked service
    ResponseEntity<FeeCalculationResponse> result = feeCalculationController.getFeeCalculation(request);

    // Simulate Aspect @AfterReturning
    loggingAspect.logMethodExit(joinPoint, result);

    // Verify service was called
    verify(feeCalculationController, times(1)).getFeeCalculation(request);

    // Assert returned response
    assert result.getStatusCode().equals(HttpStatusCode.valueOf(200));

    // Verify signature access
    verify(joinPoint, times(2)).getSignature();
  }

  @Test
  void testServicePointcutMethod() {
    // Call the pointcut method to satisfy Jacoco
    loggingAspect.serviceLayer();

    assertDoesNotThrow(() -> loggingAspect.serviceLayer(),
        "Calling serviceLayer pointcut method doesn't throw any exception");

  }

  @Test
  void testControllerPointcutMethod() {
    // Call the pointcut method to satisfy Jacoco
    loggingAspect.controllerLayer();

    assertDoesNotThrow(() -> loggingAspect.controllerLayer(),
        "Calling controllerLayer pointcut method doesn't throw any exception");

  }

  @Test
  void testFixedFeeCalculatorPointcutMethod() {
    // Call the pointcut method to satisfy Jacoco
    loggingAspect.fixedFeeCalculationLayer();

    assertDoesNotThrow(() -> loggingAspect.fixedFeeCalculationLayer(),
        "Calling fixedFeeCalculationLayer pointcut method doesn't throw any exception");

  }

  @Test
  void testHourlyFeeCalculatorPointcutMethod() {
    // Call the pointcut method to satisfy Jacoco
    loggingAspect.hourlyFeeCalculationLayer();

    assertDoesNotThrow(() -> loggingAspect.hourlyFeeCalculationLayer(),
        "Calling hourlyFeeCalculationLayer pointcut method doesn't throw any exception");

  }
}
