package uk.gov.justice.laa.fee.scheme.feecalculator.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class FeeCalculation {

    // determine what else to include here i.e. calculated fields etc

    private BigDecimal subTotal;
    private BigDecimal finalTotal;
}
