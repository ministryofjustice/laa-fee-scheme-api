package uk.gov.justice.laa.fee.scheme.feecalculator.util.limit;

import static uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner.TypeEnum.WARNING;

import java.math.BigDecimal;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.enums.WarningType;
import uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner;

/**
 * Utility class for fee related limits.
 */
@Slf4j
public class LimitUtil {

  private LimitUtil() {
  }

  /**
   * Determine if escaped case when the amount exceeds the escape threshold limit.
   *
   * @param amount    the amount to compare
   * @param feeEntity the fee entity with escape threshold limit
   * @return true if the amount exceeds the escape threshold limit, false otherwise
   */
  public static boolean isEscapedCase(BigDecimal amount, FeeEntity feeEntity) {
    return isOverLimit(amount, feeEntity.getEscapeThresholdLimit());
  }

  /**
   * Determine if escaped case when the amount exceeds the given escape threshold limit.
   *
   * @param amount               the amount to compare
   * @param escapeThresholdLimit the escape threshold limit
   * @return true if the amount exceeds the escape threshold limit, false otherwise
   */
  public static boolean isEscapedCase(BigDecimal amount, BigDecimal escapeThresholdLimit) {
    return isOverLimit(amount, escapeThresholdLimit);
  }

  /**
   * Determine if escaped case when the amount exceeds the given escape threshold limit.
   *
   * @param amount    the amount to compare
   * @param feeEntity the fee entity with upper cost limit
   * @return true if the amount exceeds the upper cost limit, false otherwise
   */
  public static boolean isOverUpperCostLimit(BigDecimal amount, FeeEntity feeEntity) {
    return isOverLimit(amount, feeEntity.getUpperCostLimit());
  }

  /**
   * Check if amount exceeds limit without authority and cap to limit if exceeded.
   *
   * @param amount             the amount to check
   * @param limitContext       the limit context containing limit details
   * @param validationMessages the list to add validation messages to
   * @return the capped amount if limit exceeded without authority, otherwise the original amount
   */
  public static BigDecimal checkLimitAndCapIfExceeded(BigDecimal amount, LimitContext limitContext,
                                                      List<ValidationMessagesInner> validationMessages) {
    log.info("Check {} is below limit for fee calculation", limitContext.limitType().getDisplayName());
    BigDecimal limit = limitContext.limit();

    if (isOverLimitWithoutAuthority(amount, limitContext)) {
      log.warn("{} limit exceeded without prior authority capping to limit: {}",
          limitContext.limitType().getDisplayName(), limitContext.limit());

      WarningType warning = limitContext.warning();
      if (warning != null) {
        validationMessages.add(ValidationMessagesInner.builder()
            .message(warning.getMessage())
            .code(warning.getCode())
            .type(WARNING)
            .build());
      }
      return limit;
    }
    return amount;
  }

  private static boolean isOverLimit(BigDecimal amount, BigDecimal limit) {
    return limit != null && amount.compareTo(limit) > 0;
  }

  private static boolean isOverLimitWithoutAuthority(BigDecimal amount, LimitContext limitContext) {
    return isOverLimit(amount, limitContext.limit()) && StringUtils.isBlank(limitContext.authority());
  }
}
