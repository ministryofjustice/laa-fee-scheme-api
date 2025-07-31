package uk.gov.justice.laa.fee.scheme.model;

import lombok.Builder;
import lombok.Data;

/**
 * model for category of law code response.
 */
@Builder
@Data
public class CategoryOfLawResponseDto {

  private String feeCode;
  private String categoryLawCode;
}