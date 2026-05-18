package uk.gov.justice.laa.fee.scheme.exception;

/**
 * Exception when no area of law found.
 */
public class AreaOfLawNotFoundException extends RuntimeException {
  public AreaOfLawNotFoundException(String areaOfLaw) {
    super(String.format("Area of law not found for: %s", areaOfLaw));
  }
}
