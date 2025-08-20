package uk.gov.justice.laa.fee.scheme.feecalculator.utility;

import java.math.BigDecimal;
import lombok.Data;

/**
 * Data model class for storing bolt on pairs, bolt on type and number of bolt on of that type.
 */
@Data
public class BoltOnPair {
  final Integer numberOfBoltOns;
  final BigDecimal boltOnAmount;
}
