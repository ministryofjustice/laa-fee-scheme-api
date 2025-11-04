package uk.gov.justice.laa.fee.scheme.feecalculator.util;

import java.math.BigDecimal;
import uk.gov.justice.laa.fee.scheme.enums.LimitType;
import uk.gov.justice.laa.fee.scheme.enums.WarningType;

/**
 * Context for limit validation.
 */
public record LimitContext(LimitType limitType, BigDecimal limit, String authority, WarningType warning) {
}
