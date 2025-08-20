package uk.gov.justice.laa.fee.scheme.exception;

/**
 * Exception for where numberOfMediationSessions is invalid.
 */
public class InvalidMediationSessionException extends IllegalArgumentException  {
  public InvalidMediationSessionException(String feeCode) {
    super(String.format("Invalid mediation session for Fee code %s: numberOfMediationSessions required", feeCode));
  }
}