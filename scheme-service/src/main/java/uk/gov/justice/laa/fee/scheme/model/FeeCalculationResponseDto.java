package uk.gov.justice.laa.fee.scheme.model;

import lombok.Builder;
import lombok.Data;

/**
 * model for fee calculation response.
 */
@Builder
@Data
public class FeeCalculationResponseDto {

  private String feeCode;
  private FeeCalculationDto feeCalculation;
}