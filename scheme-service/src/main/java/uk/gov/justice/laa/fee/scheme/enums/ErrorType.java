package uk.gov.justice.laa.fee.scheme.enums;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * For error codes and messages.
 */

@Getter
@RequiredArgsConstructor
public enum ErrorType {
  ERR_ALL_FEE_CODE("ERRALL1", "Enter a valid Fee Code."),

  ERR_CIVIL_START_DATE("ERRCIV1", "Fee Code is not valid for the Case Start Date."),
  ERR_CIVIL_START_DATE_TOO_OLD("ERRCIV2", "Case Start Date is too far in the past."),

  ERR_CRIME_POLICE_SCHEME_ID("ERRCRM4", "Enter a valid Scheme ID."),
  ERR_CRIME_POLICE_STATION_ID("ERRCRM3", "Enter a valid Police station ID, Court ID, or Prison ID."),
  ERR_CRIME_PREORDER_COVER_UPPER_LIMIT("ERRCRM10", "Net Cost is more than the Upper Cost Limitation."),
  ERR_CRIME_REP_ORDER_DATE("ERRCRM12", "Fee Code is not valid for the Representation Order Date provided."),
  ERR_CRIME_REP_ORDER_DATE_MISSING("ERRCRM8", "Enter a representation order date."),
  ERR_CRIME_UFN_MISSING("ERRCRM7", "Enter a UFN.",
      Set.of("INVC", "INVA", "INVH", "INVK", "INVL", "INVM", "INVB1", "INVB2", "PROT", "PROU", "ASMS", "ASPL", "ASAS",
          "PRIA", "PRIB1", "PRIB2", "PRIC1", "PRIC2", "PRID1", "PRID2", "PRIE1", "PRIE2")),
  ERR_CRIME_UFN_DATE("ERRCRM1", "Fee Code is not valid for the Case Start Date."),

  ERR_FAMILY_LONDON_RATE("ERRFAM1", "London/non-London rate must be entered for the Fee Code used."),

  ERR_IMM_ASYLUM_BETWEEN_DATE("ERRIA1", "For the Fee Code used, Case Start Date must be between 8 "
      + "June 2020 and 31 Mar 2023.", Set.of("IACC", "IACD", "IMCC", "IMCD")),
  ERR_IMM_ASYLUM_BEFORE_DATE("ERRIA2", "For the Fee Code used, Case Start Date must be before  to "
      + "1st April 2023", Set.of("IACA", "IACB", "IMCA", "IMCB")),
  ERR_IMM_ASYLUM_AFTER_DATE("ERRIA3", "For the Fee Code used, Case Start Date must be on or after "
      + "1st April 2023", Set.of("IACE", "IACF", "IMCE", "IMCF")),

  ERR_MEDIATION_SESSIONS("ERRMED1", "Number of Mediation Sessions must be entered for this fee code");

  ErrorType(String code, String message) {
    this.code = code;
    this.message = message;
    this.feeCodes = Set.of();
  }

  private final String code;
  private final String message;
  private final Set<String> feeCodes;

  /**
   * Returns the error type associated with a given fee code, if any.
   */
  public static Optional<ErrorType> findByFeeCode(String feeCode) {
    return Arrays.stream(values())
        .filter(e -> e.feeCodes != null && e.feeCodes.contains(feeCode))
        .findFirst();
  }
}
