package uk.gov.justice.laa.fee.scheme.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.fee.scheme.model.BoltOnType;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;

class FeeServiceTest {

  private final FeeService feeService = new FeeService();

  private static FeeCalculationRequest getFeeCalculationRequestDto() {
    FeeCalculationRequest requestDto = new FeeCalculationRequest();
    requestDto.setFeeCode("FEE123");
    requestDto.setStartDate(LocalDate.of(2025, 7, 29));
    requestDto.setNetProfitCosts(1000.50);
    requestDto.setNetDisbursementAmount(200.75);
    requestDto.setDisbursementVatAmount(40.15);
    requestDto.setVatIndicator(true);
    requestDto.setDisbursementPriorAuthority("AUTH123");
    requestDto.boltOns(BoltOnType.builder()
        .boltOnAdjournedHearing(1)
        .boltOnDetentionTravelWaitingCosts(2)
        .boltOnJrFormFilling(0)
        .boltOnCmrhOral(1)
        .boltOnCrmhTelephone(3)
        .build());
    return requestDto;
  }

  @Test
  void getFeeCalculation_shouldReturnExpectedCalculation() {
    FeeCalculationRequest request = getFeeCalculationRequestDto();

    FeeCalculationResponse response = feeService.getFeeCalculation(request);

    assertNotNull(response);
    assertEquals("FEE123", response.getFeeCode());

    FeeCalculation calculation = response.getFeeCalculation();
    assertNotNull(calculation);
    assertEquals(1234.14, calculation.getSubTotal());
    assertEquals(1500.56, calculation.getTotalAmount());
  }
}