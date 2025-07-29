package uk.gov.justice.laa.fee.scheme.feecalculator.service;

import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.fee.scheme.feecalculator.model.CategoryOfLawResponseDto;
import uk.gov.justice.laa.fee.scheme.feecalculator.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.feecalculator.model.FeeCalculationRequestDto;
import uk.gov.justice.laa.fee.scheme.feecalculator.model.FeeCalculationResponseDto;

import java.math.BigDecimal;

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
        FeeCalculationRequestDto request = new FeeCalculationRequestDto();
        request.setFeeCode("FEE123");

        FeeCalculationResponseDto response = feeService.getFeeCalculation(request);

        assertNotNull(response);
        assertEquals("FEE123", response.getFeeCode());

        FeeCalculation calculation = response.getFeeCalculation();
        assertNotNull(calculation);
        assertEquals(new BigDecimal("1234.12"), calculation.getSubTotal());
        assertEquals(new BigDecimal("1506.56"), calculation.getFinalTotal());
    }
}