package uk.gov.justice.laa.fee.scheme.feecalculator.util;

import static uk.gov.justice.laa.fee.scheme.feecalculator.util.VatUtil.getVatAmount;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.VatUtil.getVatRateForDate;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.defaultToZeroIfNull;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toBigDecimal;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toDouble;

import java.math.BigDecimal;
import java.time.LocalDate;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.enums.FeeType;
import uk.gov.justice.laa.fee.scheme.feecalculator.util.boltons.BoltOnUtil;
import uk.gov.justice.laa.fee.scheme.model.BoltOnFeeDetails;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;

/**
 * Utility class for fee calculation operations.
 */
public final class FeeCalculationUtil {

  private FeeCalculationUtil() {
  }

  public static boolean isFixedFee(FeeType feeType) {
    return feeType == FeeType.FIXED;
  }

  /**
   * Fixed fee (from DB) + bolt ons (if exists) + netDisbursementAmount = subtotal.
   * If Applicable add VAT to subtotal.
   * subtotalWithVat + netDisbursementAmount + netDisbursementVatAmount = finalTotal.
   */
  public static FeeCalculationResponse calculateFixedFee(FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity) {
    BigDecimal fixedFee = defaultToZeroIfNull(feeEntity.getFixedFee());
    return calculateAndBuildResponse(fixedFee, feeCalculationRequest, feeEntity);
  }

  /**
   * Given fixed fee + bolt ons (if exists) + netDisbursementAmount = subtotal.
   * If Applicable add VAT to subtotal.
   * subtotalWithVat + netDisbursementAmount + netDisbursementVatAmount = finalTotal.
   */
  public static FeeCalculationResponse calculateFixedFee(BigDecimal fixedFee, FeeCalculationRequest feeCalculationRequest,
                                                         FeeEntity feeEntity) {
    return calculateAndBuildResponse(fixedFee, feeCalculationRequest, feeEntity);
  }

  private static FeeCalculationResponse calculateAndBuildResponse(BigDecimal fixedFee,
                                                                  FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity) {
    // Calculate bolt on amounts if bolt ons exist
    BoltOnFeeDetails boltOnFeeDetails = BoltOnUtil.calculateBoltOnAmounts(feeCalculationRequest, feeEntity);

    BigDecimal netDisbursementAmount = toBigDecimal(feeCalculationRequest.getNetDisbursementAmount());
    BigDecimal disbursementVatAmount = toBigDecimal(feeCalculationRequest.getDisbursementVatAmount());
    String claimId = feeCalculationRequest.getClaimId();

    LocalDate startDate = feeCalculationRequest.getStartDate();
    Boolean vatApplicable = feeCalculationRequest.getVatIndicator();
    BigDecimal boltOnVatAmount = BigDecimal.ZERO;
    // Mental health has bolt on, rest do not
    BigDecimal boltOnValue = null;
    boolean isMentalHealth = feeEntity.getCategoryType().equals(CategoryType.MENTAL_HEALTH);
    if (isMentalHealth) {
      boltOnValue = toBigDecimal(boltOnFeeDetails.getBoltOnTotalFeeAmount());
      boltOnVatAmount = getVatAmount(boltOnValue, feeCalculationRequest.getStartDate(), vatApplicable);
    }
    BigDecimal fixedFeeVatAmount = getVatAmount(fixedFee, startDate, vatApplicable);

    BigDecimal calculatedVatAmount = boltOnVatAmount.add(fixedFeeVatAmount);

    BigDecimal finalTotal = fixedFee
        .add(fixedFeeVatAmount)
        .add(defaultToZeroIfNull(boltOnValue))
        .add(boltOnVatAmount)
        .add(netDisbursementAmount)
        .add(disbursementVatAmount);

    return FeeCalculationResponse.builder()
        .feeCode(feeCalculationRequest.getFeeCode())
        .schemeId(feeEntity.getFeeSchemeCode().getSchemeCode())
        .claimId(claimId)
        .escapeCaseFlag(false) // temp hard coded, till escape logic implemented
        .feeCalculation(FeeCalculation.builder()
            .totalAmount(toDouble(finalTotal))
            .vatIndicator(vatApplicable)
            .vatRateApplied(toDouble(getVatRateForDate(startDate)))
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

}
