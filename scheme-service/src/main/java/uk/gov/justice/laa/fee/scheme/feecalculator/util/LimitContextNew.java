package uk.gov.justice.laa.fee.scheme.feecalculator.util;

import java.math.BigDecimal;
import uk.gov.justice.laa.fee.scheme.enums.LimitType;
import uk.gov.justice.laa.fee.scheme.enums.WarningType;

/**
 * Context for limit validation.
 */
// TODO rename/replace to limitContext once all warnings done.
public record LimitContextNew(LimitType limitType, BigDecimal limit, String authority, WarningType warning) {
}
