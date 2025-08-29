package uk.gov.justice.laa.fee.scheme.feecalculator;

import static uk.gov.justice.laa.fee.scheme.feecalculator.utility.NumberUtility.toBigDecimal;
import static uk.gov.justice.laa.fee.scheme.feecalculator.utility.NumberUtility.toDouble;

import java.math.BigDecimal;
import org.apache.commons.lang3.StringUtils;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.feecalculator.utility.VatUtility;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.model.Warning;

/**
 * Calculate the Immigration and Asylum hourly rate fee for a given fee entity and fee calculation request.
 */
public final class ImmigrationAsylumHourlyRateCalculator {

  private ImmigrationAsylumHourlyRateCalculator() {
  }

  private static final String WARNING_NET_PROFIT_COSTS = "123"; // @TODO: TBC
  private static final String WARNING_NET_DISBURSEMENTS = "456"; // @TODO: TBC


  /**
   * Calculated fee based on the provided fee entity and fee calculation request.
   *
   * @param feeEntity             the fee entity containing fee details
   * @param feeCalculationRequest the request containing fee calculation data
   * @return FeeCalculationResponse with calculated fee
   */
  public static FeeCalculationResponse getFee(FeeEntity feeEntity, FeeCalculationRequest feeCalculationRequest) {
    // LocalDate startDate = feeCalculationRequest.getStartDate();
    Warning warning = null;


    BigDecimal netProfitCosts = toBigDecimal(feeCalculationRequest.getNetProfitCosts());
    BigDecimal profitCostLimit = feeEntity.getProfitCostLimit();
    if (netProfitCosts.compareTo(profitCostLimit) > 0
        && StringUtils.isBlank(feeCalculationRequest.getImmigrationPriorityAuthority())) {
      netProfitCosts = profitCostLimit;
      warning = Warning.builder()
          .warningDescription(WARNING_NET_PROFIT_COSTS)
          .build();
    }

    BigDecimal netDisbursementAmount = toBigDecimal(feeCalculationRequest.getNetDisbursementAmount());
    BigDecimal disbursementLimit = feeEntity.getDisbursementLimit();
    if (netDisbursementAmount.compareTo(disbursementLimit) > 0
        && StringUtils.isBlank(feeCalculationRequest.getImmigrationPriorityAuthority())) {
      netProfitCosts = profitCostLimit;
      warning = Warning.builder()
          .warningDescription(WARNING_NET_DISBURSEMENTS)
          .build();
    }

    BigDecimal jrFormFilling = toBigDecimal(feeCalculationRequest.getJrFormFilling());
    BigDecimal feeTotal = netProfitCosts.add(jrFormFilling);

    // Apply VAT where applicable
    BigDecimal calculatedVatValue = VatUtility.getVatValue(feeTotal, feeCalculationRequest.getStartDate(),
        Boolean.TRUE.equals(feeCalculationRequest.getVatIndicator()));

    BigDecimal disbursementVatAmount = toBigDecimal(feeCalculationRequest.getDisbursementVatAmount());

    BigDecimal finalTotal = feeTotal
        .add(calculatedVatValue)
        .add(netDisbursementAmount)
        .add(disbursementVatAmount);

    return new FeeCalculationResponse().toBuilder()
        .feeCode(feeCalculationRequest.getFeeCode())
        .schemeId(feeEntity.getFeeSchemeCode().getSchemeCode())
        .warning(warning)
        .feeCalculation(FeeCalculation.builder()
            .totalAmount(toDouble(finalTotal))
            .vatIndicator(feeCalculationRequest.getVatIndicator())
            .vatRateApplied(toDouble(VatUtility.getVatRateForDate(feeCalculationRequest.getStartDate())))
            .calculatedVatAmount(toDouble(calculatedVatValue))
            .disbursementAmount(toDouble(netDisbursementAmount))
            .disbursementVatAmount(toDouble(disbursementVatAmount))
            .hourlyTotalAmount(toDouble(feeTotal))
            .netProfitCostsAmount(toDouble(netProfitCosts))
            .jrFormFillingAmount(toDouble(jrFormFilling))
            .build())
        .build();
  }
}
