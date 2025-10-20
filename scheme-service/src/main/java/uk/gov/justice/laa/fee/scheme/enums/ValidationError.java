package uk.gov.justice.laa.fee.scheme.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * For Validation error codes and messages.
 */

@Getter
@RequiredArgsConstructor
public enum ValidationError {
  ERRALL1("Enter a valid Fee Code."),

  ERRCIV1("Fee Code is not valid for the Case Start Date."),
  ERRCIV2("Case Start Date is too far in the past."),

  ERRCRM1("Fee Code is not valid for the Case Start Date."),
  ERRCRM2("Representation Order Date is too far in the past. "
          + "For cases opened before April 2016, use the paper process."),
  ERRCRM5("UFN Date is too far in the past. Check Case Start Date. "
          + "For cases opened before April 2016, use the paper process."),
  ERRCRM6("Fee Code is not valid for the Case Start Date.");

  private final String errorMessage;
}
