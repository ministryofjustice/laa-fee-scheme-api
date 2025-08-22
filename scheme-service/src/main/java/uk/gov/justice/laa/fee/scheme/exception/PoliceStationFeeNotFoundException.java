package uk.gov.justice.laa.fee.scheme.exception;

import java.time.LocalDate;

/**
 * Exception for when no fee entity can be found for fee_code and start_date.
 */
public class PoliceStationFeeNotFoundException extends RuntimeException  {
  public PoliceStationFeeNotFoundException(String psSchemeId, LocalDate startDate) {
    super(String.format("Police Station Fee not found for Police Scheme Id %s, with case start date %s", psSchemeId, startDate));
  }
}