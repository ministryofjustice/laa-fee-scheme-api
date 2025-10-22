package uk.gov.justice.laa.fee.scheme.enums;

import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WarningCode {

  WARN_IMM_ASYLM_ESCAPE_THRESHOLD("WARIA3", "Immigration & Asylum escape case threshold", CategoryType.IMMIGRATION_ASYLUM),
  WARN_MENTAL_HEALTH_ESCAPE_THRESHOLD("WARMH1", "Mental Health escape case threshold", CategoryType.MENTAL_HEALTH),
  WARN_FAMILY_ESCAPE_THRESHOLD("WARFAM1", "Family escape case threshold", CategoryType.FAMILY),
  WARN_DISCRIMINATION_ESCAPE_THRESHOLD("WAROTH1", "Other Civil - Discrimination escape case threshold", CategoryType.DISCRIMINATION),
  WARN_CLAIM_AGNST_PUB_AUTH_ESCAPE_THRESHOLD("WAROTH2", "Other Civil - Claims Against Public Authorities escape case threshold",
      CategoryType.CLAIMS_PUBLIC_AUTHORITIES),
  WARN_CLINICAL_NEGLIGENCE_ESCAPE_THRESHOLD("WAROTH3", "Other Civil - Clinical Negligence escape case threshold", CategoryType.CLINICAL_NEGLIGENCE),
  WARN_COMMUNITY_CARE_ESCAPE_THRESHOLD("WAROTH4", "Other Civil - Community Care escape case threshold", CategoryType.COMMUNITY_CARE),
  WARN_CIVIL_ESCAPE_THRESHOLD("WAROTH5", "Other Civil - Debt escape case threshold", CategoryType.DEBT),
  WARN_HOUSING_HLPAS_ESCAPE_THRESHOLD("WAROTH6", "Other Civil - Early Legal Advice (Housing - HLPAS) escape case threshold",CategoryType.HOUSING_HLPAS),
  WARN_EDUCATION_ESCAPE_THRESHOLD("WAROTH7", "Other Civil - Education escape case threshold", CategoryType.EDUCATION),
  WARN_HOUSING_ESCAPE_THRESHOLD("WAROTH8", "Other Civil - Housing escape case threshold", CategoryType.HOUSING),
  WARN_MISCELLANEOUS_ESCAPE_THRESHOLD("WAROTH9", "Other Civil - Miscellaneous escape case threshold", CategoryType.MISCELLANEOUS),
  WARN_PUBLIC_LAW_ESCAPE_THRESHOLD("WAROTH10", "Other Civil - Public Law (Non-Family) escape case threshold", CategoryType.PUBLIC_LAW),
  WARN_WELFARE_BENEFITS_ESCAPE_THRESHOLD("WAROTH11", "Other Civil - Welfare Benefits escape case threshold", CategoryType.WELFARE_BENEFITS);

  private final String code;

  private final String message;

  private final CategoryType categoryType;

  /**
   * Helper to get enum by code safely.
   */
  public static WarningCode getMessageFromCode(String code) {
    for (WarningCode e : values()) {
      if (e.code.equalsIgnoreCase(code)) {
        return e;
      }
    }
    throw new IllegalArgumentException("Unknown Escape Case Threshold code: " + code);
  }

  // 🔹 Helper to get all error codes for a given CategoryType
  public static List<WarningCode> getByCategory(CategoryType categoryType) {
    return Arrays.stream(values())
        .filter(e -> e.getCategoryType() == categoryType)
        .toList();
  }
}
