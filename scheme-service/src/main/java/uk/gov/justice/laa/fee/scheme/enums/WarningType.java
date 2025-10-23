package uk.gov.justice.laa.fee.scheme.enums;

import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * For warning codes and messages.
 */

@RequiredArgsConstructor
@Getter
public enum WarningType {
  WARN_CRIME_TRAVEL_COSTS("WARCRM1", "Cost not included. Travel costs cannot be claimed with Fee Code used.",
      Set.of("INVB1", "INVB2", "PROT", "PROU", "PROW", "PRIA", "PRIB1", "PRIB2", "PRIC1", "PRIC2",
          "PRID1", "PRID2", "PRIE1", "PRIE2")),

  WARN_CRIME_WAITING_COSTS("WARCRM2", "Cost not included. Waiting costs cannot be claimed with Fee Code used.",
      Set.of("INVB1", "INVB2", "PROT", "PROU", "PROW")),

  WARN_IMM_ASYLM_DISB_600_CLR("WARIA1", "Costs have been capped at £600 without an Immigration Priority "
                                  + "Authority Number. Disbursement costs exceed the Disbursement Limit."),
  WARN_IMM_ASYLM_DISB_400_LEGAL_HELP("WARIA2", "Costs have been capped at £400 without an Immigration "
                                         + "Priority Authority Number. Disbursement costs exceed the Disbursement Limit."),
  WARN_IMM_ASYLM_PRIOR_AUTH_CLR("WARIA4", "Costs have been capped. The amount entered exceeds the Total "
                                    + "Cost Limit. An Immigration Prior Authority number must be entered."),
  WARN_IMM_ASYLM_PRIOR_AUTH_INTERIM("WARIA5", "Costs have been capped. The amount entered exceeds the "
                                        + "Total Cost Limit. An Immigration Prior Authority number must be entered."),
  WARN_IMM_ASYLM_PRIOR_AUTH_LEGAL_HELP("WARIA6", "Costs have been capped. The amount entered exceeds the"
                                           + " Total Cost Limit. An Immigration Prior Authority number must be entered."),
  WARN_IMM_ASYLM_DISB_LEGAL_HELP("WARIA7", "Costs have been capped without an Immigration Priority Authority"
                                     + " Number. Disbursement costs exceed the Disbursement Limit."),
  WARN_IMM_ASYLM_SUM_OVER_LIMIT_LEGAL_HELP("WARIA8", "Costs have been capped. Costs for the Fee Code used "
                                               + "cannot exceed £100."),
  WARN_IMM_ASYLM_DETENTION_TRAVEL("WARIA9", "Costs not included. Detention Travel and Waiting costs on hourly"
                                      + " rates cases should be reported as Profit Costs."),
  WARN_IMM_ASYLM_JR_FORM_FILLING("WARIA10", "Costs have been included. JR/ form filling costs should only"
                                     + " be completed for standard fee cases. Hourly rates costs should be reported in the Profit Costs.");

  WarningType(String code, String message) {
    this.code = code;
    this.message = message;
    this.feeCodes = Set.of();
  }

  private final String code;
  private final String message;
  private final Set<String> feeCodes;

  /**
   * Helper to get enum by code safely.
   */
  public static WarningType getMessageFromCode(WarningType warning) {
    for (WarningType e : values()) {
      if (e.code.equalsIgnoreCase(warning.getCode())) {
        return e;
      }
    }
    throw new IllegalArgumentException("Unknown Escape Case Threshold code: " + warning.getCode());
  }

  public boolean containsFeeCode(String code) {
    return feeCodes.contains(code);
  }

}
