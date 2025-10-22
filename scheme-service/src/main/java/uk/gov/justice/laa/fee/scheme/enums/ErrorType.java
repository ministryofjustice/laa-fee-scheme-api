package uk.gov.justice.laa.fee.scheme.enums;

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
  ERR_CRIME_REP_ORDER_DATE("ERRCRM12", "Fee Code is not valid for the Case Start Date."),
  ERR_CRIME_UFN_MISSING("ERRCRM7", "Enter a UFN."),
  ERR_CRIME_UFN_DATE("ERRCRM1", "Fee Code is not valid for the Case Start Date."),

  ERR_MEDIATION_SESSIONS("ERRMED1", "Number of Mediation Sessions must be entered for this fee code");

  private final String code;
  private final String message;
}
