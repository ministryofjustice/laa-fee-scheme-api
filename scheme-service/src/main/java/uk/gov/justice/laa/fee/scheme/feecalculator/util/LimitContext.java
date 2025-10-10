package uk.gov.justice.laa.fee.scheme.feecalculator.util;

import java.math.BigDecimal;
import uk.gov.justice.laa.fee.scheme.enums.LimitType;

/**
 * Context for limit validation.
 */
public record LimitContext(LimitType limitType, BigDecimal limit, String authority, String warningMessage) {
}
