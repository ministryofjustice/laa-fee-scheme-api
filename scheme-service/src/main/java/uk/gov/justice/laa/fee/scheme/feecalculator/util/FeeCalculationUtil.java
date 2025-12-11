package uk.gov.justice.laa.fee.scheme.feecalculator.util;

import static java.util.Objects.nonNull;
import static uk.gov.justice.laa.fee.scheme.enums.ClaimStartDateType.CASE_CONCLUDED_DATE;
import static uk.gov.justice.laa.fee.scheme.enums.ClaimStartDateType.CASE_START_DATE;
import static uk.gov.justice.laa.fee.scheme.enums.ClaimStartDateType.REP_ORDER_DATE;
import static uk.gov.justice.laa.fee.scheme.enums.ClaimStartDateType.UFN;
import static uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner.TypeEnum.WARNING;
import static uk.gov.justice.laa.fee.scheme.service.CrimeValidationService.FEE_CODE_PROH_TYPE;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.enums.ClaimStartDateType;
import uk.gov.justice.laa.fee.scheme.enums.WarningType;
import uk.gov.justice.laa.fee.scheme.exception.CaseConcludedDateRequiredException;
import uk.gov.justice.laa.fee.scheme.exception.StartDateRequiredException;
import uk.gov.justice.laa.fee.scheme.model.BoltOnFeeDetails;
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
      case CASE_CONCLUDED_DATE -> Optional.ofNullable(feeCalculationRequest.getCaseConcludedDate())
          .orElseThrow(() -> new CaseConcludedDateRequiredException(feeCalculationRequest.getFeeCode()));
      default -> Optional.ofNullable(feeCalculationRequest.getStartDate())
          .orElseThrow(() -> new StartDateRequiredException(feeCalculationRequest.getFeeCode()));
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
   * PROH, PROH1, PROH2 will use representation order date if present, falls back to UFN if not.
   */
  private static ClaimStartDateType getFeeClaimStartDateAdvocacyAppealsReviews(FeeCalculationRequest feeCalculationRequest) {
    if (FEE_CODE_PROH_TYPE.contains(feeCalculationRequest.getFeeCode()) && nonNull(feeCalculationRequest.getRepresentationOrderDate())) {
      log.info("Determining fee start date for PROH, PROH1, PROH2, using Representation Order Date");
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



  /**
   * If bolts ons are null, return null for request.
   */
  public static BoltOnFeeDetails filterBoltOnFeeDetails(BoltOnFeeDetails boltOnFeeDetails) {
    if (boltOnFeeDetails == null || boltOnFeeDetails.getBoltOnTotalFeeAmount() == null) {
      return null;
    }
    return boltOnFeeDetails;
  }

  /**
   * Returns fee calculation response for the given parameters.
   *
   * @param feeCalculationRequest the fee calculation request
   * @param feeEntity             the fee entity
   * @param feeCalculation        the fee calculation
   * @return the fee calculation response
   */
  public static FeeCalculationResponse buildFeeCalculationResponse(FeeCalculationRequest feeCalculationRequest,
                                                                   FeeEntity feeEntity,
                                                                   FeeCalculation feeCalculation) {
    return buildFeeCalculationResponse(feeCalculationRequest, feeEntity, feeCalculation, List.of(), null);
  }

  /**
   * Returns fee calculation response for the given parameters.
   *
   * @param feeCalculationRequest the fee calculation request
   * @param feeEntity             the fee entity
   * @param feeCalculation        the fee calculation
   * @param validationMessages    the list of validation messages
   * @return the fee calculation response
   */
  public static FeeCalculationResponse buildFeeCalculationResponse(FeeCalculationRequest feeCalculationRequest,
                                                                   FeeEntity feeEntity,
                                                                   FeeCalculation feeCalculation,
                                                                   List<ValidationMessagesInner> validationMessages) {
    return buildFeeCalculationResponse(feeCalculationRequest, feeEntity, feeCalculation, validationMessages, null);
  }

  /**
   * Returns fee calculation response for the given parameters.
   *
   * @param feeCalculationRequest the fee calculation request
   * @param feeEntity             the fee entity
   * @param feeCalculation        the fee calculation
   * @param validationMessages    the list of validation messages
   * @param escapeCaseFlag        the escape case flag
   * @return the fee calculation response
   */
  public static FeeCalculationResponse buildFeeCalculationResponse(FeeCalculationRequest feeCalculationRequest,
                                                                   FeeEntity feeEntity,
                                                                   FeeCalculation feeCalculation,
                                                                   List<ValidationMessagesInner> validationMessages,
                                                                   Boolean escapeCaseFlag) {
    return buildFeeCalculationResponse(feeCalculationRequest, feeCalculation, validationMessages, escapeCaseFlag,
        feeEntity.getFeeScheme().getSchemeCode());
  }

  /**
   * Builds fee calculation response for the given parameters.
   *
   * @param feeCalculationRequest the fee calculation request
   * @param feeCalculation        the fee calculation
   * @param validationMessages    the list of validation messages
   * @param escapeCaseFlag        the escape case flag
   * @param schemeId              the fee scheme id
   * @return the fee calculation response
   */
  public static FeeCalculationResponse buildFeeCalculationResponse(FeeCalculationRequest feeCalculationRequest,
                                                                   FeeCalculation feeCalculation,
                                                                   List<ValidationMessagesInner> validationMessages,
                                                                   Boolean escapeCaseFlag,
                                                                   String schemeId) {
    log.info("Build fee calculation response");
    return FeeCalculationResponse.builder()
        .feeCode(feeCalculationRequest.getFeeCode())
        .schemeId(schemeId)
        .claimId(feeCalculationRequest.getClaimId())
        .escapeCaseFlag(escapeCaseFlag)
        .validationMessages(validationMessages)
        .feeCalculation(feeCalculation)
        .build();
  }
}