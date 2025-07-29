package uk.gov.justice.laa.fee.scheme.feecalculator.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CategoryOfLawResponseDto {

    private String feeCode;
    private String categoryLawCode;
}