package uk.gov.justice.laa.fee.scheme.exception;

import java.time.LocalDate;

/**
 * Exception for when no fee entity can be found for police scheme id or police station scheme id.
 */
public class PoliceStationFeeNotFoundException extends RuntimeException  {
  public PoliceStationFeeNotFoundException(String policeStationId, LocalDate startDate) {
    super(String.format("Police Station Fee not found for Police Station Id %s, with case start date %s",
        policeStationId, startDate));
  }

  public PoliceStationFeeNotFoundException(String psSchemeId) {
    super(String.format("Police Station Fee not found for Police Station Scheme Id %s", psSchemeId));
  }

  public PoliceStationFeeNotFoundException(String feeCode, String policeStationSchemeId) {
    super(String.format("Calculation Logic for Police Station Other Fee not implemented, Fee Code %s, "
        + "Police Station Scheme Id %s", feeCode, policeStationSchemeId));
  }
}