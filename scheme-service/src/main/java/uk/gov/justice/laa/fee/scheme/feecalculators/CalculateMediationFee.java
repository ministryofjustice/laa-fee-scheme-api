package uk.gov.justice.laa.fee.scheme.feecalculators;

import java.math.BigDecimal;
import java.math.RoundingMode;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;

/**
 * Calculate the mediation fee for a given fee entity and fee data.
 * */
public final class CalculateMediationFee {

  /**
   * Determines whether the calculation should include mediation sessions based presence of numberOfMediationSessions.
   */
  public static FeeCalculationResponse getFee(FeeEntity feeEntity, FeeCalculationRequest feeData) {
    String feeCode = feeData.getFeeCode();
    Integer numberOfMediationSessions = feeData.getNumberOfMediationSessions();

    if (numberOfMediationSessions == null) {
      return getCalculationWithoutMediationSessions(feeCode, feeEntity, feeData);
    } else {
      return getCalculationWithMediationSessions(feeCode, feeEntity, feeData);
    }
  }

  /**
   * Gets fixed fee depending on number if mediation sessions.
   */
  private static FeeCalculationResponse getCalculationWithMediationSessions(String feeCode, FeeEntity feeEntity,
                                                                            FeeCalculationRequest feeData) {
    BigDecimal baseFee = BigDecimal.ZERO;

    if (feeData.getNumberOfMediationSessions() == 1) {
      baseFee = BigDecimal.valueOf(feeEntity.getMediationSessionOne().doubleValue());
    } else if (feeData.getNumberOfMediationSessions() > 1) {
      baseFee = BigDecimal.valueOf(feeEntity.getMediationSessionTwo().doubleValue());
    }
    return buildFeeResponse(feeCode, baseFee, feeData);
  }

  /**
   * Gets fixed fee from static total_fee.
   */
  private static FeeCalculationResponse getCalculationWithoutMediationSessions(String feeCode, FeeEntity feeEntity,
                                                                               FeeCalculationRequest feeData) {
    BigDecimal baseFee = feeEntity.getTotalFee();

    return buildFeeResponse(feeCode, baseFee, feeData);
  }

  /**
   * Fixed fee + netDisbursementAmount = subtotal.
   * If Applicable add VAT to fixed fee,
   * fixedFeeWithVat + netDisbursementAmount + netDisbursementVatAmount = finalTotal.
   */
  private static FeeCalculationResponse buildFeeResponse(String feeCode, BigDecimal fixedFee, FeeCalculationRequest feeData) {

    BigDecimal fixedFeeWithVat = Boolean.TRUE.equals(feeData.getVatIndicator())
        ? VatUtility.addVat(fixedFee, feeData.getStartDate())
        : fixedFee;

    BigDecimal netDisbursementAmount = feeData.getNetDisbursementAmount() != null
        ? BigDecimal.valueOf(feeData.getNetDisbursementAmount())
        : BigDecimal.ZERO;

    BigDecimal netDisbursementVatAmount = feeData.getDisbursementVatAmount() != null
        ? BigDecimal.valueOf(feeData.getDisbursementVatAmount())
        : BigDecimal.ZERO;

    BigDecimal subTotal = fixedFee.add(netDisbursementAmount);
    BigDecimal finalTotal = fixedFeeWithVat.add(netDisbursementAmount).add(netDisbursementVatAmount);

    return new FeeCalculationResponse()
        .feeCode(feeCode)
        .feeCalculation(FeeCalculation.builder()
            .subTotal(subTotal.setScale(2, RoundingMode.HALF_UP).doubleValue())
            .totalAmount(finalTotal.setScale(2, RoundingMode.HALF_UP).doubleValue())
            .build());
  }
}