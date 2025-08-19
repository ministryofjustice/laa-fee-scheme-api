package uk.gov.justice.laa.fee.scheme.feecalculator;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class BoltOnPair {
  final Integer numberOfBoltOns;
  final BigDecimal boltOnAmount;
}
