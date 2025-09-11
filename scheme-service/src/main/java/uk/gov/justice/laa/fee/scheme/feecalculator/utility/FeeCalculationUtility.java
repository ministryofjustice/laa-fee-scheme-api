package uk.gov.justice.laa.fee.scheme.feecalculator.utility;

import static uk.gov.justice.laa.fee.scheme.feecalculator.utility.NumberUtility.defaultToZeroIfNull;
import static uk.gov.justice.laa.fee.scheme.feecalculator.utility.NumberUtility.toBigDecimal;
import static uk.gov.justice.laa.fee.scheme.feecalculator.utility.NumberUtility.toDouble;
import static uk.gov.justice.laa.fee.scheme.feecalculator.utility.VatUtility.getVatAmount;
import static uk.gov.justice.laa.fee.scheme.feecalculator.utility.VatUtility.getVatRateForDate;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtility.defaultToZeroIfNull;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtility.toBigDecimal;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtility.toDouble;

import java.math.BigDecimal;
import java.time.LocalDate;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.feecalculator.type.CategoryType;
import uk.gov.justice.laa.fee.scheme.feecalculator.utility.boltons.BoltOnUtility;
import uk.gov.justice.laa.fee.scheme.model.BoltOnFeeDetails;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;

/**
 * Utility class for fee calculation operations.
 */
public final class FeeCalculationUtility {

  private FeeCalculationUtility() {
  }



  public static boolean isFixedFee(String feeType) {
    return feeType.equals("FIXED");
  }

  /**
   * Fixed fee + bolt ons (if exists) + netDisbursementAmount = subtotal.
   * If Applicable add VAT to subtotal.
   * subtotalWithVat + netDisbursementAmount + netDisbursementVatAmount = finalTotal.
   */
  public static FeeCalculationResponse calculate(FeeEntity feeEntity, FeeCalculationRequest feeCalculationRequest) {
    BigDecimal fixedFee = defaultToZeroIfNull(feeEntity.getFixedFee());
    // get the bolt fee details from utility class
    BoltOnFeeDetails boltOnFeeDetails = BoltOnUtility.calculateBoltOnAmounts(feeCalculationRequest, feeEntity);
    return calculateAndBuildResponse(fixedFee, boltOnFeeDetails, feeCalculationRequest, feeEntity);
  }

  /**
   * Given fixed fee + netDisbursementAmount = subtotal.
   * If Applicable add VAT to subtotal.
   * subtotalWithVat + netDisbursementAmount + netDisbursementVatAmount = finalTotal.
   */
  public static FeeCalculationResponse calculate(BigDecimal fixedFee, FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity) {
    return calculateAndBuildResponse(fixedFee, null, feeCalculationRequest, feeEntity);
  }

  private static FeeCalculationResponse calculateAndBuildResponse(BigDecimal fixedFee, BoltOnFeeDetails boltOnFeeDetails,
                                                                  FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity) {
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
