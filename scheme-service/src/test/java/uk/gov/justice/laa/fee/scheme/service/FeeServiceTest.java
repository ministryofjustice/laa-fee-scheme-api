package uk.gov.justice.laa.fee.scheme.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.fee.scheme.model.CategoryOfLawResponse;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;

class FeeServiceTest {

  private final FeeService feeService = new FeeService();

  private static FeeCalculationRequest getFeeCalculationRequestDto() {
    FeeCalculationRequest requestDto = new FeeCalculationRequest();
    requestDto.setFeeCode("FEE123");
    requestDto.setStartDate(LocalDate.of(2025, 7, 29));
    requestDto.setNetProfitCosts(Double.valueOf("1000.50"));
    requestDto.setNetDisbursementAmount(Double.valueOf("200.75"));
    requestDto.setDisbursementVatAmount(Double.valueOf("40.15"));
    requestDto.setVatIndicator(true);
    requestDto.setDisbursementPriorAuthority("AUTH123");
    requestDto.setBoltOnAdjournedHearing(1);
    requestDto.setBoltOnDetentionTravelWaitingCosts(2);
    requestDto.setBoltOnJrFormFilling(0);
    requestDto.setBoltOnCmrhOral(1);
    requestDto.setBoltOnCrmhTelephone(3);
    return requestDto;
  }

  @Test
  void getCategoryCode_shouldReturnExpectedCategoryOfLaw() {
    String feeCode = "ASY";

    CategoryOfLawResponse response = feeService.getCategoryCode(feeCode);

    assertNotNull(response);
    assertEquals("ASY", response.getCategoryOfLawCode());
  }

  @Test
  void getFeeCalculation_shouldReturnExpectedCalculation() {
    FeeCalculationRequest request = getFeeCalculationRequestDto();

    FeeCalculationResponse response = feeService.getFeeCalculation(request);

    assertNotNull(response);
    assertEquals("FEE123", response.getFeeCode());

    FeeCalculation calculation = response.getFeeCalculation();
    assertNotNull(calculation);
    assertEquals(new BigDecimal("1234.14"), calculation.getSubTotal());
    assertEquals(new BigDecimal("1500.56"), calculation.getFinalTotal());
  }
}