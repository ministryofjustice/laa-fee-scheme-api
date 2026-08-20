package uk.gov.justice.laa.fee.scheme.exception;

import java.time.LocalDate;

/**
 * Exception when no VAT rate is found for the supplied date.
 */
public class VatRateNotFoundException extends RuntimeException {
  public VatRateNotFoundException(LocalDate date) {
    super(String.format("No VAT rate found for date: %s", date));
  }
}
