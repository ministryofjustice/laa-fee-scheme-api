package uk.gov.justice.laa.fee.scheme.feecalculator.hourly;

import static uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner.TypeEnum.WARNING;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toBigDecimal;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toDouble;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.feecalculator.util.VatUtil;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner;

/**
 * Calculate the police station fee for a given fee entity and fee data.
 */
@Component
@RequiredArgsConstructor
public class PoliceStationHourlyRateCalculator {

  private static final String WARNING_NET_PROFIT_COSTS = "warning net profit costs";

  /**
   * Calculated fee based on the provided fee entity and fee calculation request.
   *
   * @param feeEntity             the fee entity containing fee details
   * @param feeCalculationRequest the request containing fee calculation data
   * @return FeeCalculationResponse with calculated fee
   */
  public FeeCalculationResponse getFee(FeeEntity feeEntity, FeeCalculationRequest feeCalculationRequest) {
    List<ValidationMessagesInner> validationMessages = new ArrayList<>();

    BigDecimal profitCostLimit = feeEntity.getProfitCostLimit();

    BigDecimal netProfitCosts = toBigDecimal(feeCalculationRequest.getNetProfitCosts());

    BigDecimal netDisbursementAmount = toBigDecimal(feeCalculationRequest.getNetDisbursementAmount());

    BigDecimal travelAndWaitingExpenses = toBigDecimal(feeCalculationRequest.getTravelAndWaitingCosts());

    BigDecimal feeTotal = netProfitCosts.add(netDisbursementAmount).add(travelAndWaitingExpenses);

    if (feeTotal.compareTo(profitCostLimit) > 0) {
      validationMessages.add(ValidationMessagesInner.builder()
          .message(WARNING_NET_PROFIT_COSTS)
          .type(WARNING)
          .build());
    }
    // Apply VAT where applicable
    LocalDate startDate = feeCalculationRequest.getStartDate();

    Boolean vatApplicable = feeCalculationRequest.getVatIndicator();

    BigDecimal calculatedVatAmount = VatUtil.getVatAmount(feeTotal, startDate, vatApplicable);

    BigDecimal disbursementVatAmount = toBigDecimal(feeCalculationRequest.getDisbursementVatAmount());

    BigDecimal finalTotal = feeTotal
        .add(calculatedVatAmount)
        .add(netDisbursementAmount)
        .add(disbursementVatAmount);

    return new FeeCalculationResponse().toBuilder()
        .feeCode(feeCalculationRequest.getFeeCode())
        .schemeId(feeEntity.getFeeSchemeCode().getSchemeCode())
        .validationMessages(validationMessages)
        .feeCalculation(FeeCalculation.builder()
            .totalAmount(toDouble(finalTotal))
            .vatIndicator(feeCalculationRequest.getVatIndicator())
            .vatRateApplied(toDouble(VatUtil.getVatRateForDate(feeCalculationRequest.getStartDate())))
            .calculatedVatAmount(toDouble(calculatedVatAmount))
            .disbursementAmount(toDouble(netDisbursementAmount))
            // disbursement not capped, so requested and calculated will be same
            .requestedNetDisbursementAmount(toDouble(netDisbursementAmount))
            .disbursementVatAmount(toDouble(disbursementVatAmount))
            .hourlyTotalAmount(toDouble(feeTotal))
            .travelAndWaitingCostAmount(toDouble(travelAndWaitingExpenses))
            .netProfitCostsAmount(toDouble(netProfitCosts))
            // net profit cost not capped, so requested and calculated will be same
            .requestedNetProfitCostsAmount(toDouble(netProfitCosts))
            .build())
        .build();
  }
}