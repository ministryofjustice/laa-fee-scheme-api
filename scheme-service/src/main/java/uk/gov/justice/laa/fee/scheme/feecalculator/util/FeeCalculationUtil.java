package uk.gov.justice.laa.fee.scheme.feecalculator.util;

import static java.util.Objects.nonNull;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.VatUtil.getVatAmount;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.VatUtil.getVatRateForDate;
import static uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner.TypeEnum.WARNING;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.defaultToZeroIfNull;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toBigDecimal;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toDouble;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.enums.FeeType;
import uk.gov.justice.laa.fee.scheme.enums.WarningCode;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
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
   * Check if the fee type is FIXED.
   *
   * @param feeType the fee type to check
   * @return true if the fee type is FIXED, false otherwise
   */
  public static boolean isFixedFee(FeeType feeType) {
    return feeType == FeeType.FIXED;
  }

  /**
   * Determine if escaped case when amount exceeds the escape threshold limit.
   *
   * @param amount               the amount to compare
   * @param escapeThresholdLimit the escape threshold limit
   * @return true if amount exceeds the escape threshold limit, false otherwise
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

      validationMessages.add(ValidationMessagesInner.builder()
          .message(limitContext.warningMessage())
          .type(WARNING)
          .build());

      return limit;
    }
    return amount;
  }

  // TODO replace checkLimitAndCapIfExceeded
  /**
   * Check if amount exceeds limit without authority and cap to limit if exceeded.
   *
   * @param amount          the amount to check
   * @param limitContext    the limit context containing limit details
   * @param validationMessages the list to add validation messages to
   * @return the capped amount if limit exceeded without authority, otherwise the original amount
   */
  public static BigDecimal checkLimitAndCapIfExceeded(BigDecimal amount, LimitContextNew limitContext,
                                                      List<ValidationMessagesInner> validationMessages) {
    log.info("Check {} is below limit for fee calculation", limitContext.limitType().getDisplayName());
    BigDecimal limit = limitContext.limit();

    if (isOverLimitWithoutAuthority(amount, limitContext)) {
      log.warn("{} limit exceeded without prior authority capping to limit: {}",
          limitContext.limitType().getDisplayName(), limitContext.limit());

      WarningCode warning = limitContext.warning();
      validationMessages.add(ValidationMessagesInner.builder()
          .message(warning.getMessage())
          .code(warning.getCode())
          .type(WARNING)
          .build());

      return limit;
    }
    return amount;
  }

  /**
   * Calculate fee using Fixed Fee from FeeEntity.
   */
  public static FeeCalculationResponse calculate(FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity) {
    BigDecimal fixedFee = defaultToZeroIfNull(feeEntity.getFixedFee());
    return calculateAndBuildResponse(fixedFee, feeCalculationRequest, feeEntity);
  }

  /**
   * Calculate fee using given Fixed Fee value.
   */
  public static FeeCalculationResponse calculate(BigDecimal fixedFee, FeeCalculationRequest feeCalculationRequest,
                                                 FeeEntity feeEntity) {
    return calculateAndBuildResponse(fixedFee, feeCalculationRequest, feeEntity);
  }

  /**
   * Fixed fee + netDisbursementAmount = subtotal.
   * If Applicable add VAT to subtotal.
   * subtotalWithVat + netDisbursementAmount + netDisbursementVatAmount = finalTotal.
   */
  private static FeeCalculationResponse calculateAndBuildResponse(BigDecimal fixedFee,
                                                                  FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity) {

    log.info("Get fields from fee calculation request");
    Boolean vatApplicable = feeCalculationRequest.getVatIndicator();
    LocalDate claimStartDate = getFeeClaimStartDate(feeEntity.getCategoryType(), feeCalculationRequest);

    BigDecimal fixedFeeVatAmount = getVatAmount(fixedFee, claimStartDate, vatApplicable);
    BigDecimal netDisbursementAmount = toBigDecimal(feeCalculationRequest.getNetDisbursementAmount());
    BigDecimal disbursementVatAmount = toBigDecimal(feeCalculationRequest.getDisbursementVatAmount());

    log.info("Calculate total fee amount with any disbursements, bolt ons and VAT where applicable");
    BigDecimal finalTotal = fixedFee
        .add(fixedFeeVatAmount)
        .add(netDisbursementAmount)
        .add(disbursementVatAmount);

    log.info("Build fee calculation response");
    return FeeCalculationResponse.builder()
        .feeCode(feeCalculationRequest.getFeeCode())
        .schemeId(feeEntity.getFeeScheme().getSchemeCode())
        .claimId(feeCalculationRequest.getClaimId())
        .escapeCaseFlag(false) // temp hard coded, till escape logic implemented
        .feeCalculation(FeeCalculation.builder()
            .totalAmount(toDouble(finalTotal))
            .vatIndicator(vatApplicable)
            .vatRateApplied(toDouble(getVatRateForDate(claimStartDate)))
            .calculatedVatAmount(toDouble(fixedFeeVatAmount))
            .disbursementAmount(toDouble(netDisbursementAmount))
            // disbursement not capped, so requested and calculated will be same
            .requestedNetDisbursementAmount(toDouble(netDisbursementAmount))
            .disbursementVatAmount(toDouble(disbursementVatAmount))
            .fixedFeeAmount(toDouble(fixedFee))
            .build())
        .build();
  }

  /**
   * Return appropriate date based on Category Type of the claim request.
   *
   * @param categoryType          CategoryType
   * @param feeCalculationRequest FeeCalculationRequest
   * @return LocalDate
   */
  public static LocalDate getFeeClaimStartDate(CategoryType categoryType, FeeCalculationRequest feeCalculationRequest) {
    return switch (categoryType) {
      case ASSOCIATED_CIVIL, POLICE_STATION, PRISON_LAW ->
          DateUtil.toLocalDate(Objects.requireNonNull(feeCalculationRequest.getUniqueFileNumber()));
      case MAGS_COURT_DESIGNATED, MAGS_COURT_UNDESIGNATED, YOUTH_COURT_DESIGNATED, YOUTH_COURT_UNDESIGNATED ->
          feeCalculationRequest.getRepresentationOrderDate();
      case ADVOCACY_APPEALS_REVIEWS -> getFeeClaimStartDateAdvocacyAppealsReviews(feeCalculationRequest);
      default -> feeCalculationRequest.getStartDate();
    };
  }

  /**
   * Calculate start date to use for Advocacy Assistance in the Crown Court or Appeals & Reviews,
   * PROH will use representation order date if present, falls back to UFN if not.
   */
  public static LocalDate getFeeClaimStartDateAdvocacyAppealsReviews(FeeCalculationRequest feeCalculationRequest) {
    if (feeCalculationRequest.getFeeCode().equals("PROH") && nonNull(feeCalculationRequest.getRepresentationOrderDate())) {
      log.info("Determining fee start date for PROH, using Representation Order Date");
      return feeCalculationRequest.getRepresentationOrderDate();
    } else {
      log.info("Determining fee start date, using Unique File Number");
      return DateUtil.toLocalDate(Objects.requireNonNull(feeCalculationRequest.getUniqueFileNumber()));
    }
  }

  /**
   * Calculate total amount when only fees and VAT are applicable.
   */
  public static BigDecimal calculateTotalAmount(BigDecimal feeTotal, BigDecimal calculatedVatAmount) {
    log.info("Calculate total fee amount with VAT where applicable");

    return feeTotal
        .add(calculatedVatAmount);
  }

  /**
   * Calculate total amount when fees, disbursements and VAT are applicable.
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

  private static boolean isOverLimitWithoutAuthority(BigDecimal amount, LimitContextNew limitContext) {
    return limitContext.limit() != null
        && amount.compareTo(limitContext.limit()) > 0
        && StringUtils.isBlank(limitContext.authority());
  }
}
