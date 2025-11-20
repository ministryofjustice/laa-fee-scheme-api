package uk.gov.justice.laa.fee.scheme.feecalculator.util;

import static java.util.Objects.nonNull;
import static uk.gov.justice.laa.fee.scheme.enums.ClaimStartDateType.CASE_CONCLUDED_DATE;
import static uk.gov.justice.laa.fee.scheme.enums.ClaimStartDateType.CASE_START_DATE;
import static uk.gov.justice.laa.fee.scheme.enums.ClaimStartDateType.REP_ORDER_DATE;
import static uk.gov.justice.laa.fee.scheme.enums.ClaimStartDateType.UFN;
import static uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner.TypeEnum.WARNING;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.enums.ClaimStartDateType;
import uk.gov.justice.laa.fee.scheme.enums.WarningType;
import uk.gov.justice.laa.fee.scheme.model.BoltOnFeeDetails;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner;
import uk.gov.justice.laa.fee.scheme.util.DateUtil;

/**
 * Utility class for fee calculation operations.
 */
@Slf4j
public final class FeeCalculationUtil {

  private FeeCalculationUtil() {
  }

  /**
   * Determine if escaped case when the amount exceeds the escape threshold limit.
   *
   * @param amount               the amount to compare
   * @param escapeThresholdLimit the escape threshold limit
   * @return true if the amount exceeds the escape threshold limit, false otherwise
   */
  public static boolean isEscapedCase(BigDecimal amount, BigDecimal escapeThresholdLimit) {
    return escapeThresholdLimit != null && amount.compareTo(escapeThresholdLimit) > 0;
  }

  /**
   * Check if amount exceeds limit without authority and cap to limit if exceeded.
   *
   * @param amount          the amount to check
   * @param limitContext    the limit context containing limit details
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

  /**
   * Return the appropriate date based on Category Type of the claim request.
   *
   * @param categoryType          CategoryType
   * @param feeCalculationRequest FeeCalculationRequest
   * @return LocalDate
   */
  public static ClaimStartDateType getFeeClaimStartDateType(CategoryType categoryType, FeeCalculationRequest feeCalculationRequest) {
    return switch (categoryType) {
      case ASSOCIATED_CIVIL, POLICE_STATION, PRISON_LAW, PRE_ORDER_COVER, EARLY_COVER, REFUSED_MEANS_TEST -> UFN;
      case MAGISTRATES_COURT, YOUTH_COURT, SENDING_HEARING -> REP_ORDER_DATE;
      case ADVOCACY_APPEALS_REVIEWS -> getFeeClaimStartDateAdvocacyAppealsReviews(feeCalculationRequest);
      case ADVICE_ASSISTANCE_ADVOCACY -> CASE_CONCLUDED_DATE;
      default -> CASE_START_DATE;
    };
  }

  /**
   * Return the appropriate date based on Category Type of the claim request.
   *
   * @param categoryType          CategoryType
   * @param feeCalculationRequest FeeCalculationRequest
   * @return LocalDate
   */
  public static LocalDate getFeeClaimStartDate(CategoryType categoryType, FeeCalculationRequest feeCalculationRequest) {
    ClaimStartDateType claimStartDateType = getFeeClaimStartDateType(categoryType, feeCalculationRequest);

    return switch (claimStartDateType) {
      case REP_ORDER_DATE -> feeCalculationRequest.getRepresentationOrderDate();
      case UFN -> DateUtil.toLocalDate(Objects.requireNonNull(feeCalculationRequest.getUniqueFileNumber()));
      case CASE_CONCLUDED_DATE -> feeCalculationRequest.getCaseConcludedDate();
      default -> feeCalculationRequest.getStartDate();
    };
  }

  /**
   * Builds a validation warning message based on the provided warning type and log message.
   *
   * @param warning    the warning type containing the code and message for validation
   * @param logMessage the log message associated with the warning
   * @return the ValidationMessagesInner object containing warning details
   */
  public static ValidationMessagesInner buildValidationWarning(WarningType warning, String logMessage) {
    log.warn("{} - {}", warning.getCode(), logMessage);
    return ValidationMessagesInner.builder()
        .code(warning.getCode())
        .message(warning.getMessage())
        .type(WARNING)
        .build();
  }

  /**
   * Calculate start date to use for Advocacy Assistance in the Crown Court or Appeals & Reviews,
   * PROH will use representation order date if present, falls back to UFN if not.
   */
  private static ClaimStartDateType getFeeClaimStartDateAdvocacyAppealsReviews(FeeCalculationRequest feeCalculationRequest) {
    if (feeCalculationRequest.getFeeCode().equals("PROH") && nonNull(feeCalculationRequest.getRepresentationOrderDate())) {
      log.info("Determining fee start date for PROH, using Representation Order Date");
      return REP_ORDER_DATE;
    } else {
      log.info("Determining fee start date, using Unique File Number");
      return UFN;
    }
  }

  /**
   * Calculate the VAT amount for a given value using the VAT rate.
   */
  public static BigDecimal calculateVatAmount(BigDecimal value, BigDecimal vatRate) {
    log.info("Calculate VAT amount");

    return value.multiply(vatRate)
        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
        .setScale(2, RoundingMode.HALF_UP);
  }

  /**
   * Calculate the total amount when only fees and VAT are applicable.
   */
  public static BigDecimal calculateTotalAmount(BigDecimal feeTotal, BigDecimal calculatedVatAmount) {
    log.info("Calculate total fee amount with VAT where applicable");

    return feeTotal
        .add(calculatedVatAmount);
  }

  /**
   * Calculate the total amount when fees, disbursements and VAT are applicable.
   */
  public static BigDecimal calculateTotalAmount(BigDecimal feeTotal, BigDecimal calculatedVatAmount,
                                                BigDecimal netDisbursementAmount, BigDecimal disbursementVatAmount) {
    log.info("Calculate total fee amount with any disbursements and VAT where applicable");

    return feeTotal
        .add(calculatedVatAmount)
        .add(netDisbursementAmount)
        .add(disbursementVatAmount);
  }

  private static boolean isOverLimitWithoutAuthority(BigDecimal amount, LimitContext limitContext) {
    return limitContext.limit() != null
        && amount.compareTo(limitContext.limit()) > 0
        && StringUtils.isBlank(limitContext.authority());
  }

  /**
   * If bolts ons are null, return null for request.
   */
  public static BoltOnFeeDetails filterBoltOnFeeDetails(BoltOnFeeDetails boltOnFeeDetails) {
    if (boltOnFeeDetails == null || boltOnFeeDetails.getBoltOnTotalFeeAmount() == null) {
      return null;
    }
    return boltOnFeeDetails;
  }

}
