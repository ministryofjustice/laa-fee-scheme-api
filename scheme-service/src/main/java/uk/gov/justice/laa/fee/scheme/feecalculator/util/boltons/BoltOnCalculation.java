package uk.gov.justice.laa.fee.scheme.feecalculator.util.boltons;

import java.math.BigDecimal;

/**
 * Record Class for use in BoltOnUtil.
 */
public record BoltOnCalculation(BoltOnType type, Integer requested, BigDecimal amount) {}

