package uk.gov.justice.laa.fee.scheme.enums;

import lombok.Getter;

/**
 * For Validation error codes and messages.
 */
@Getter
public enum WarningCode {

  WARIA1("WARIA1", "Costs have been capped at £600 without an Immigration Priority Authority Number. "
      + "Disbursement costs exceed the Disbursement Limit."),
  WARIA2("WARIA2", "Costs have been capped at £400 without an Immigration Priority Authority Number. "
      + "Disbursement costs exceed the Disbursement Limit."),
  WARIA4("WARIA4", "Costs have been capped. The amount entered exceeds the Total Cost Limit. "
      + "An Immigration Prior Authority number must be entered."),
  WARIA5("WARIA5", "Costs have been capped. The amount entered exceeds the Total Cost Limit. "
      + "An Immigration Prior Authority number must be entered."),
  WARIA6("WARIA6", "Costs have been capped. The amount entered exceeds the Total Cost Limit. "
      + "An Immigration Prior Authority number must be entered."),
  WARIA7("WARIA7", "Costs have been capped without an Immigration Priority Authority Number. "
      + "Disbursement costs exceed the Disbursement Limit."),
  WARIA8("WARIA8", "Costs have been capped. Costs for the Fee Code used cannot exceed £100."),
  WARIA9("WARIA9", "Costs not included. Detention Travel and Waiting costs on hourly rates cases "
      + "should be reported as Profit Costs."),
  WARIA10("WARIA10", "Costs have been included. JR/ form filling costs should only be completed "
      + "for standard fee cases. Hourly rates costs should be reported in the Profit Costs.");

  private final String code;

  private final String message;

  WarningCode(String code, String message) {
    this.code = code;
    this.message = message;
  }

  /**
   * Helper to get enum by code safely.
   */
  public static WarningCode fromCode(String code) {
    for (WarningCode e : values()) {
      if (e.code.equalsIgnoreCase(code)) {
        return e;
      }
    }
    throw new IllegalArgumentException("Unknown Escape Case Threshold code: " + code);
  }
}
