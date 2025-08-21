package uk.gov.justice.laa.fee.scheme.feecalculator.utility;

import java.math.BigDecimal;

/**
 * Record class for storing bolt on pairs, bolt on type and number of bolt on of that type.
 */
public record BoltOnPair(Integer numberOfBoltOns, BigDecimal boltOnAmount) {
}
