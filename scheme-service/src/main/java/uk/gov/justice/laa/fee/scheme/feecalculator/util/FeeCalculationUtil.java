package uk.gov.justice.laa.fee.scheme.feecalculator.util;

import static uk.gov.justice.laa.fee.scheme.feecalculator.util.VatUtil.getVatAmount;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.VatUtil.getVatRateForDate;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.defaultToZeroIfNull;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toBigDecimal;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toDouble;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.feecalculator.util.boltons.BoltOnUtil;
import uk.gov.justice.laa.fee.scheme.model.BoltOnFeeDetails;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;

/**
 * Utility class for fee calculation operations.
 */
@Slf4j
public final class FeeCalculationUtil {

  private FeeCalculationUtil() {
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
    // get the bolt fee details from util class
    BoltOnFeeDetails boltOnFeeDetails = BoltOnUtil.calculateBoltOnAmounts(feeCalculationRequest, feeEntity);
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


    log.info("Get fields from fee calculation request");
    Boolean vatApplicable = feeCalculationRequest.getVatIndicator();
    LocalDate startDate = feeCalculationRequest.getStartDate();

    BigDecimal boltOnVatAmount = BigDecimal.ZERO;
    BigDecimal boltOnValue = null;
    // Mental health has bolt on, rest do not
    boolean isMentalHealth = feeEntity.getCategoryType().equals(CategoryType.MENTAL_HEALTH);
    if (isMentalHealth) {
      log.info("Calculate bolt on amounts for fee calculation");
      boltOnValue = toBigDecimal(boltOnFeeDetails.getBoltOnTotalFeeAmount());
      boltOnVatAmount = getVatAmount(boltOnValue, startDate, vatApplicable);
    }

    BigDecimal fixedFeeVatAmount = getVatAmount(fixedFee, startDate, vatApplicable);
    BigDecimal calculatedVatAmount = boltOnVatAmount.add(fixedFeeVatAmount);
    BigDecimal netDisbursementAmount = toBigDecimal(feeCalculationRequest.getNetDisbursementAmount());
    BigDecimal disbursementVatAmount = toBigDecimal(feeCalculationRequest.getDisbursementVatAmount());

    log.info("Calculate total amount for fee calculation");
    BigDecimal finalTotal = fixedFee
        .add(fixedFeeVatAmount)
        .add(defaultToZeroIfNull(boltOnValue))
        .add(boltOnVatAmount)
        .add(netDisbursementAmount)
        .add(disbursementVatAmount);

    log.info("Build fee calculation response");
    return FeeCalculationResponse.builder()
        .feeCode(feeCalculationRequest.getFeeCode())
        .schemeId(feeEntity.getFeeSchemeCode().getSchemeCode())
        .claimId(feeCalculationRequest.getClaimId())
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
