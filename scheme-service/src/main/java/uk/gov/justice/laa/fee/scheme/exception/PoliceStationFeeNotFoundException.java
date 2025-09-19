package uk.gov.justice.laa.fee.scheme.exception;

import java.time.LocalDate;

/**
 * Exception for when no fee entity can be found for police scheme id or police station scheme id.
 */
public class PoliceStationFeeNotFoundException extends RuntimeException  {
  public PoliceStationFeeNotFoundException(String policeStationId, LocalDate startDate) {
    super(String.format("Police Station Fee not found for policeStationId: %s and startDate: %s",
        policeStationId, startDate));
  }

  public PoliceStationFeeNotFoundException(String policeStationSchemeId) {
    super(String.format("Police Station Fee not found for policeStationSchemeId: %s", policeStationSchemeId));
  }

  public PoliceStationFeeNotFoundException(String feeCode, String policeStationSchemeId) {
    super(String.format("Calculation Logic for Police Station Other Fee not implemented for feeCode: %s and "
        + "policeStationSchemeId: %s", feeCode, policeStationSchemeId));
  }
}