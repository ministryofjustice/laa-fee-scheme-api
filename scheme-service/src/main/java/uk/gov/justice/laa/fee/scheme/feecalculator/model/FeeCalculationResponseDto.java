package uk.gov.justice.laa.fee.scheme.feecalculator.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class FeeCalculationResponseDto {

    private String feeCode;
    private FeeCalculation feeCalculation;
}