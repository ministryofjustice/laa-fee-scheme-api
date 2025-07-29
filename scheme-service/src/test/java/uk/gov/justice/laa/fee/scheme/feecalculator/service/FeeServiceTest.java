package uk.gov.justice.laa.fee.scheme.feecalculator.service;

import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.fee.scheme.feecalculator.model.CategoryOfLawResponseDto;
import uk.gov.justice.laa.fee.scheme.feecalculator.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.feecalculator.model.FeeCalculationRequestDto;
import uk.gov.justice.laa.fee.scheme.feecalculator.model.FeeCalculationResponseDto;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FeeServiceTest {

    private final FeeService feeService = new FeeService();

    @Test
    void getCategoryCode_shouldReturnExpectedCategoryOfLaw() {
        String feeCode = "FEE123";

        CategoryOfLawResponseDto response = feeService.getCategoryCode(feeCode);

        assertNotNull(response);
        assertEquals("FEE123", response.getFeeCode());
        assertEquals("asylum 123", response.getCategoryLawCode());
    }

    @Test
    void getFeeCalculation_shouldReturnExpectedCalculation() {
        FeeCalculationRequestDto request = getFeeCalculationRequestDto();

        FeeCalculationResponseDto response = feeService.getFeeCalculation(request);

        assertNotNull(response);
        assertEquals("FEE123", response.getFeeCode());

        FeeCalculation calculation = response.getFeeCalculation();
        assertNotNull(calculation);
        assertEquals(new BigDecimal("1234.12"), calculation.getSubTotal());
        assertEquals(new BigDecimal("1506.56"), calculation.getFinalTotal());
    }

    private static FeeCalculationRequestDto getFeeCalculationRequestDto() {
        FeeCalculationRequestDto requestDto = new FeeCalculationRequestDto();
        requestDto.setFeeCode("FEE123");
        requestDto.setStartDate(LocalDate.of(2025, 7, 29));
        requestDto.setNetProfitCosts(new BigDecimal("1000.50"));
        requestDto.setNetDisbursementAmount(new BigDecimal("200.75"));
        requestDto.setDisbursementVatAmount(new BigDecimal("40.15"));
        requestDto.setVatIndicator(true);
        requestDto.setDisbursementPriorAuthority("AUTH123");
        requestDto.setBoltOnAdjournedHearing(1);
        requestDto.setBoltOnDetentionTravelWaitingCosts(2);
        requestDto.setBoltOnJrFormFilling(0);
        requestDto.setBoltOnCmrhOral(1);
        requestDto.setBoltOnCrmhTelephone(3);
        return requestDto;
    }
}