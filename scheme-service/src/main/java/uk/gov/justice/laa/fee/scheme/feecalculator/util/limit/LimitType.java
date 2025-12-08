package uk.gov.justice.laa.fee.scheme.feecalculator.util.limit;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enum for the types of limit.
 */
@Getter
@RequiredArgsConstructor
public enum LimitType {
  PROFIT_COST("Profit Costs"),
  DISBURSEMENT("Disbursements"),
  TOTAL("Total");

  private final String displayName;
}
