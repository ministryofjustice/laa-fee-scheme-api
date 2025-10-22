package uk.gov.justice.laa.fee.scheme.enums;

import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * For Validation error codes and messages.
 */
@Getter
@RequiredArgsConstructor
public enum WarningCode {

  WARN_IMM_ASYLM_DISB_600_CLR("WARIA1", "Costs have been capped at £600 without an Immigration Priority "
      + "Authority Number. Disbursement costs exceed the Disbursement Limit.", CategoryType.IMMIGRATION_ASYLUM),

  WARN_IMM_ASYLM_DISB_400_LEGAL_HELP("WARIA2", "Costs have been capped at £400 without an Immigration "
      + "Priority Authority Number. Disbursement costs exceed the Disbursement Limit.", CategoryType.IMMIGRATION_ASYLUM),

  WARN_IMM_ASYLM_PRIOR_AUTH_CLR("WARIA4", "Costs have been capped. The amount entered exceeds the Total "
      + "Cost Limit. An Immigration Prior Authority number must be entered.", CategoryType.IMMIGRATION_ASYLUM),

  WARN_IMM_ASYLM_PRIOR_AUTH_INTERIM("WARIA5", "Costs have been capped. The amount entered exceeds the "
      + "Total Cost Limit. An Immigration Prior Authority number must be entered.", CategoryType.IMMIGRATION_ASYLUM),

  WARN_IMM_ASYLM_PRIOR_AUTH_LEGAL_HELP("WARIA6", "Costs have been capped. The amount entered exceeds the"
      + " Total Cost Limit. An Immigration Prior Authority number must be entered.", CategoryType.IMMIGRATION_ASYLUM),

  WARN_IMM_ASYLM_DISB_LEGAL_HELP("WARIA7", "Costs have been capped without an Immigration Priority Authority"
      + " Number. Disbursement costs exceed the Disbursement Limit.", CategoryType.IMMIGRATION_ASYLUM),

  WARN_IMM_ASYLM_SUM_OVER_LIMIT_LEGAL_HELP("WARIA8", "Costs have been capped. Costs for the Fee Code used "
      + "cannot exceed £100.", CategoryType.IMMIGRATION_ASYLUM),

  WARN_IMM_ASYLM_DETENTION_TRAVEL("WARIA9", "Costs not included. Detention Travel and Waiting costs on hourly"
      + " rates cases should be reported as Profit Costs.", CategoryType.IMMIGRATION_ASYLUM),

  WARN_IMM_ASYLM_JR_FORM_FILLING("WARIA10", "Costs have been included. JR/ form filling costs should only"
      + " be completed for standard fee cases. Hourly rates costs should be reported in the Profit Costs.",
      CategoryType.IMMIGRATION_ASYLUM);

  private final String code;

  private final String message;

  private final CategoryType categoryType;

  /**
   * Helper to get enum by code safely.
   */
  public static WarningCode getMessageFromCode(WarningCode warning) {
    for (WarningCode e : values()) {
      if (e.code.equalsIgnoreCase(warning.getCode())) {
        return e;
      }
    }
    throw new IllegalArgumentException("Unknown Escape Case Threshold code: " + warning.getCode());
  }

  /**
   * Helper to get all error codes for a given CategoryType.
   */
  public static List<WarningCode> getByCategory(CategoryType categoryType) {
    return Arrays.stream(values())
        .filter(e -> e.getCategoryType() == categoryType)
        .toList();
  }
}
