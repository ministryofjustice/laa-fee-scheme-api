package uk.gov.justice.laa.fee.scheme.feecalculator.utility.boltons;

import java.math.BigDecimal;

/**
 * Record Class for use in BoltOnUtility.
 */
public record BoltOnCalculation(BoltOnType type, Integer requested, BigDecimal amount) {}

