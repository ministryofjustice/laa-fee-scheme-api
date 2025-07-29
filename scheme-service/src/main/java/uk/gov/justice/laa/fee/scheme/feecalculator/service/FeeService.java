package uk.gov.justice.laa.fee.scheme.feecalculator.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.fee.scheme.feecalculator.model.CategoryOfLawResponseDto;
import uk.gov.justice.laa.fee.scheme.feecalculator.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.feecalculator.model.FeeCalculationRequestDto;
import uk.gov.justice.laa.fee.scheme.feecalculator.model.FeeCalculationResponseDto;

import java.math.BigDecimal;

@AllArgsConstructor
@Service
public class FeeService {

    public CategoryOfLawResponseDto getCategoryCode(String feeCode) {
        // logic using feecode to be implemented

        CategoryOfLawResponseDto categoryOfLaw = CategoryOfLawResponseDto.builder()
                .feeCode(feeCode)
                .categoryLawCode("asylum 123")
                .build();
        return categoryOfLaw;
    }

    public FeeCalculationResponseDto getFeeCalculation(FeeCalculationRequestDto feeData) {
        // logic using feeData to be implemented

        FeeCalculationResponseDto feeCalculationResponseDto = FeeCalculationResponseDto.builder()
                .feeCode("FEE123")
                .feeCalculation(FeeCalculation.builder()
                        .subTotal(new BigDecimal("1234.12"))
                        .finalTotal(new BigDecimal("1506.56"))
                        .build())
                .build();

        return feeCalculationResponseDto;
    }
 }

