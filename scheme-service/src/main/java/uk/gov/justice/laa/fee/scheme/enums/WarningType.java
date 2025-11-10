package uk.gov.justice.laa.fee.scheme.enums;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * For warning codes and messages.
 */

@RequiredArgsConstructor
@Getter
public enum WarningType {

  WARN_ADVOCACY_APPEALS_REVIEWS_UPPER_LIMIT("WARCRM3", "Costs are included. The Net Costs exceeds the Upper Costs Limitation."),

  WARN_ASSOCIATED_CIVIL_ESCAPE_THRESHOLD("WARCRM4", "The claim exceeds the Escape Case Threshold. An Escape "
      + "Case Claim must be submitted for further costs to be paid."),


  WARN_CLAIM_AGNST_PUB_AUTH_ESCAPE_THRESHOLD("WAROTH2", "Other Civil - Claims Against Public Authorities"
                                                        + " escape case threshold", CategoryType.CLAIMS_PUBLIC_AUTHORITIES),

  WARN_CLINICAL_NEGLIGENCE_ESCAPE_THRESHOLD("WAROTH3", "Other Civil - Clinical Negligence escape case"
                                                       + " threshold", CategoryType.CLINICAL_NEGLIGENCE),

  WARN_COMMUNITY_CARE_ESCAPE_THRESHOLD("WAROTH4", "Other Civil - Community Care escape case threshold",
      CategoryType.COMMUNITY_CARE),

  WARN_DEBT_ESCAPE_THRESHOLD("WAROTH5", "Other Civil - Debt escape case threshold", CategoryType.DEBT),

  WARN_DISCRIMINATION_ESCAPE_THRESHOLD("WAROTH1", "Other Civil - Discrimination escape case threshold",
      CategoryType.DISCRIMINATION),

  WARN_EDUCATION_ESCAPE_THRESHOLD("WAROTH7", "Other Civil - Education escape case threshold", CategoryType.EDUCATION),

  WARN_FAMILY_ESCAPE_THRESHOLD("WARFAM1", "Family escape case threshold", CategoryType.FAMILY),

  WARN_HOUSING_ESCAPE_THRESHOLD("WAROTH8", "Other Civil - Housing escape case threshold", CategoryType.HOUSING),

  WARN_HOUSING_HLPAS_ESCAPE_THRESHOLD("WAROTH6", "Other Civil - Early Legal Advice (Housing - HLPAS) "
                                                 + "escape case threshold", CategoryType.HOUSING_HLPAS),

  WARN_IMM_ASYLM_DISB_600_CLR("WARIA1", "Costs have been capped at £600 without an Immigration Priority "
                                        + "Authority Number. Disbursement costs exceed the Disbursement Limit."),
  WARN_IMM_ASYLM_DISB_400_LEGAL_HELP("WARIA2", "Costs have been capped at £400 without an Immigration "
                                               + "Priority Authority Number. Disbursement costs exceed the Disbursement Limit."),
  WARN_IMM_ASYLM_ESCAPE_THRESHOLD("WARIA3", "The claim exceeds the Escape Case Threshold. An Escape Case Claim "
                                            + "must be submitted for further costs to be paid.", CategoryType.IMMIGRATION_ASYLUM),
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
  WARN_IMM_ASYLM_JR_FORM_FILLING("WARIA10", "Costs have been included. JR/ form filling costs should only be completed "
                                            + "for standard fee cases. Hourly rates costs should be reported in the Profit Costs."),

  WARN_IMM_ASYLM_DISB_ONLY("WARIA11", "Costs have been capped without an Immigration Priority Authority Number. "
                                      + "Disbursement costs exceed the Disbursement Limit."),

  WARN_MENTAL_HEALTH_ESCAPE_THRESHOLD("WARMH1", "Mental Health escape case threshold", CategoryType.MENTAL_HEALTH),

  WARN_MISCELLANEOUS_ESCAPE_THRESHOLD("WAROTH9", "Other Civil - Miscellaneous escape case threshold", CategoryType.MISCELLANEOUS),

  WARN_POLICE_OTHER_UPPER_LIMIT("WARCRM7", "Costs have been included. Net Costs exceed the Upper Cost Limitation."),

  WARN_POLICE_STATIONS_ESCAPE_THRESHOLD("WARCRM8", "The claim exceeds the Escape Case Threshold. "
                                                   + "An Escape Case Claim must be submitted for further costs to be paid."),

  WARN_PRISON_MAY_HAVE_ESCAPED("WARCRM5", "Costs are included. Profit and Waiting Costs exceed the Lower "
      + "Standard Fee Limit. An escape fee may be payable."),
  WARN_PRISON_HAS_ESCAPED("WARCRM6", "The claim exceeds the Escape Case Threshold. An Escape Case Claim "
      + "must be submitted for further costs to be paid."),

  WARN_PUBLIC_LAW_ESCAPE_THRESHOLD("WAROTH10", "Other Civil - Public Law (Non-Family) escape case threshold", CategoryType.PUBLIC_LAW),

  WARN_WELFARE_BENEFITS_ESCAPE_THRESHOLD("WAROTH11", "Other Civil - Welfare Benefits escape case threshold", CategoryType.WELFARE_BENEFITS);

  WarningType(String code, String message) {
    this(code, message, null, null);
  }

  WarningType(String code, String message, Set<String> feeCodes) {
    this(code, message, feeCodes, null);
  }

  WarningType(String code, String message, CategoryType categoryType) {
    this(code, message, null, categoryType);
  }

  private final String code;
  private final String message;
  private final Set<String> feeCodes;
  private final CategoryType categoryType;

  /**
   * Helper to get all error codes for a given CategoryType.
   */
  public static List<WarningType> getByCategory(CategoryType categoryType) {
    return Arrays.stream(values())
        .filter(e -> e.getCategoryType() == categoryType)
        .toList();
  }

  public boolean containsFeeCode(String code) {
    return feeCodes.contains(code);
  }

}
