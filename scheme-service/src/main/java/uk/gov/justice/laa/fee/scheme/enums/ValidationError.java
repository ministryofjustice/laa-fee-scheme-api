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

  ERRCIV1("Fee Code is not valid for Case Start Date."),
  ERRCIV2("Case Start Date is too far in the past."),

  ERRCRM1("Fee Code is not valid for Case Start Date."),

  ERRMED1("Number of Mediation Sessions must be entered for this fee code");

  private final String errorMessage;
}
