package uk.gov.justice.laa.fee.scheme.feecalculator.util;

import static java.util.Objects.nonNull;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.VatUtil.getVatAmount;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.VatUtil.getVatRateForDate;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.defaultToZeroIfNull;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toBigDecimal;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toDouble;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.enums.FeeType;
import uk.gov.justice.laa.fee.scheme.feecalculator.util.boltons.BoltOnUtil;
import uk.gov.justice.laa.fee.scheme.model.BoltOnFeeDetails;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.util.DateUtil;

/**
 * Utility class for fee calculation operations.
 */
@Slf4j
public final class FeeCalculationUtil {

  private FeeCalculationUtil() {
  }

  public static boolean isFixedFee(FeeType feeType) {
    return feeType == FeeType.FIXED;
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
   * Fixed fee + bolt ons (if exists) + netDisbursementAmount = subtotal.
   * If Applicable add VAT to subtotal.
   * subtotalWithVat + netDisbursementAmount + netDisbursementVatAmount = finalTotal.
   */
  private static FeeCalculationResponse calculateAndBuildResponse(BigDecimal fixedFee,
                                                                  FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity) {
    // Calculate bolt on amounts if bolt ons exist
    BoltOnFeeDetails boltOnFeeDetails = BoltOnUtil.calculateBoltOnAmounts(feeCalculationRequest, feeEntity);

    log.info("Get fields from fee calculation request");
    Boolean vatApplicable = feeCalculationRequest.getVatIndicator();
    LocalDate claimStartDate = getFeeClaimStartDate(feeEntity.getCategoryType(), feeCalculationRequest);

    BigDecimal boltOnVatAmount = BigDecimal.ZERO;
    BigDecimal boltOnValue = null;
    // Mental health has bolt on, rest do not
    boolean isMentalHealth = feeEntity.getCategoryType().equals(CategoryType.MENTAL_HEALTH);
    if (isMentalHealth) {
      log.info("Calculate bolt on amounts for fee calculation");
      boltOnValue = toBigDecimal(boltOnFeeDetails.getBoltOnTotalFeeAmount());
      boltOnVatAmount = getVatAmount(boltOnValue, claimStartDate, vatApplicable);
    }

    BigDecimal fixedFeeVatAmount = getVatAmount(fixedFee, claimStartDate, vatApplicable);
    BigDecimal calculatedVatAmount = boltOnVatAmount.add(fixedFeeVatAmount);
    BigDecimal netDisbursementAmount = toBigDecimal(feeCalculationRequest.getNetDisbursementAmount());
    BigDecimal disbursementVatAmount = toBigDecimal(feeCalculationRequest.getDisbursementVatAmount());

    log.info("Calculate total fee amount with any disbursements, bolt ons and VAT where applicable");
    BigDecimal finalTotal = fixedFee
        .add(fixedFeeVatAmount)
        .add(defaultToZeroIfNull(boltOnValue))
        .add(boltOnVatAmount)
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
            .calculatedVatAmount(toDouble(calculatedVatAmount))
            .disbursementAmount(toDouble(netDisbursementAmount))
            // disbursement not capped, so requested and calculated will be same
            .requestedNetDisbursementAmount(toDouble(netDisbursementAmount))
            .disbursementVatAmount(toDouble(disbursementVatAmount))
            .fixedFeeAmount(toDouble(fixedFee))
            // Mental health has bolt on, rest do not
            .boltOnFeeDetails(isMentalHealth ? boltOnFeeDetails : null)
            .build())
        .build();
  }

  /**
   * Return appropriate date based on Category Type of the claim request.
   *
   * @param categoryType CategoryType
   * @param feeCalculationRequest FeeCalculationRequest
   * @return LocalDate
   */
  public static LocalDate getFeeClaimStartDate(CategoryType categoryType, FeeCalculationRequest feeCalculationRequest) {
    return switch (categoryType) {
      case ASSOCIATED_CIVIL, POLICE_STATION, PRISON_LAW ->
          DateUtil.toLocalDate(Objects.requireNonNull(feeCalculationRequest.getUniqueFileNumber()));
      case MAGS_COURT_DESIGNATED, MAGS_COURT_UNDESIGNATED, YOUTH_COURT_DESIGNATED, YOUTH_COURT_UNDESIGNATED ->
          feeCalculationRequest.getRepresentationOrderDate();
      case ADVOCACY_APPEALS_REVIEWS ->  getFeeClaimStartDateAdvocacyAppealsReviews(feeCalculationRequest);
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
    } else  {
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
}
