package uk.gov.justice.laa.fee.scheme.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * For error codes and messages.
 */

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
  ERRALL1("ERRALL1", "Enter a valid Fee Code."),

  ERRCIV1("ERRCIV1", "Fee Code is not valid for the Case Start Date."),
  ERRCIV2("ERRCIV2", "Case Start Date is too far in the past."),

  ERRCRM1("ERRCRM1", "Fee Code is not valid for the Case Start Date."),
  ERRCRM2("ERRCRM2", "Representation Order Date is too far in the past. "
                     + "For cases opened before April 2016, use the paper process."),
  ERRCRM3("ERRCRM3", "Enter a valid Police station ID, Court ID, or Prison ID."),
  ERRCRM4("ERRCRM4", "Enter a valid Scheme ID."),
  ERRCRM5("ERRCRM5", "UFN Date is too far in the past. Check Case Start Date. "
                     + "For cases opened before April 2016, use the paper process."),
  ERRCRM6("ERRCRM6", "Fee Code is not valid for the Case Start Date.");

  private final String code;
  private final String message;
}
