package uk.gov.justice.laa.fee.scheme.model;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

/**
 * model for feeCalculation object.
 */
@Builder
@Data
public class FeeCalculationDto {

  // determine what else to include here i.e. calculated fields etc

  private BigDecimal subTotal;
  private BigDecimal finalTotal;
}
