package uk.gov.justice.laa.fee.scheme.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.fee.scheme.model.*;

import java.math.BigDecimal;

@AllArgsConstructor
@Service
public class FeeService {

    public CategoryOfLaw getCategoryCode(String feeCode) {
        // logic using feecode to be implemented

        CategoryOfLaw categoryOfLaw = null;
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

