package uk.gov.justice.laa.fee.scheme.service.model;

import java.math.BigDecimal;

/**
 * Record class for a VAT result.
 */
public record VatResult(BigDecimal vatAmount, BigDecimal vatRateApplied) {
}
