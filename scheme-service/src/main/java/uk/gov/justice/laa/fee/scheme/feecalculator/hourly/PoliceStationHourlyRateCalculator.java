package uk.gov.justice.laa.fee.scheme.feecalculator.hourly;

import static uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner.TypeEnum.WARNING;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toBigDecimal;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toDouble;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toDoubleOrNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.feecalculator.FeeCalculator;
import uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil;
import uk.gov.justice.laa.fee.scheme.feecalculator.util.VatUtil;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner;

/**
 * Calculate the police station fee for a given fee entity and fee data.
 */
@Slf4j
@Component
public class PoliceStationHourlyRateCalculator implements FeeCalculator {

  @Override
  public Set<CategoryType> getSupportedCategories() {
    return Set.of(); // Only used by PoliceStationFeeCalculator and not available via FeeCalculatorFactory
  }

  private static final String WARNING_NET_PROFIT_COSTS = "warning net profit costs";

  /**
   * Calculated fee based on the provided fee entity and fee calculation request.
   *
   * @param feeCalculationRequest the request containing fee calculation data
   * @param feeEntity             the fee entity containing fee details
   * @return FeeCalculationResponse with calculated fee
   */
  public FeeCalculationResponse calculate(FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity) {

    log.info("Calculate Police Station hourly rate fee");

    List<ValidationMessagesInner> validationMessages = new ArrayList<>();

    BigDecimal profitCostLimit = feeEntity.getProfitCostLimit();

    BigDecimal netProfitCosts = toBigDecimal(feeCalculationRequest.getNetProfitCosts());

    BigDecimal netDisbursementAmount = toBigDecimal(feeCalculationRequest.getNetDisbursementAmount());

    BigDecimal travelAndWaitingExpenses = toBigDecimal(feeCalculationRequest.getTravelAndWaitingCosts());

    log.info("Calculate hourly rate and costs");
    BigDecimal feeTotal = netProfitCosts.add(netDisbursementAmount).add(travelAndWaitingExpenses);

    if (feeTotal.compareTo(profitCostLimit) > 0) {
      log.warn("Fee total exceeds profit cost limit");
      validationMessages.add(ValidationMessagesInner.builder()
          .message(WARNING_NET_PROFIT_COSTS)
          .type(WARNING)
          .build());
    }

    // Apply VAT where applicable
    LocalDate claimStartDate = FeeCalculationUtil
        .getFeeClaimStartDate(CategoryType.POLICE_STATION, feeCalculationRequest);

    Boolean vatApplicable = feeCalculationRequest.getVatIndicator();

    BigDecimal calculatedVatAmount = VatUtil.getVatAmount(feeTotal, claimStartDate, vatApplicable);

    BigDecimal disbursementVatAmount = toBigDecimal(feeCalculationRequest.getDisbursementVatAmount());

    BigDecimal totalAmount = FeeCalculationUtil.calculateTotalAmount(feeTotal, calculatedVatAmount,
            netDisbursementAmount, disbursementVatAmount);

    log.info("Build fee calculation response");
    return new FeeCalculationResponse().toBuilder()
        .feeCode(feeCalculationRequest.getFeeCode())
        .schemeId(feeEntity.getFeeScheme().getSchemeCode())
        .validationMessages(validationMessages)
        .feeCalculation(FeeCalculation.builder()
            .totalAmount(toDouble(totalAmount))
            .vatIndicator(feeCalculationRequest.getVatIndicator())
            .vatRateApplied(toDoubleOrNull(VatUtil.getVatRateForDate(feeCalculationRequest.getStartDate(), vatApplicable)))
            .calculatedVatAmount(toDouble(calculatedVatAmount))
            .disbursementAmount(feeCalculationRequest.getNetDisbursementAmount())
            // disbursement not capped, so requested and calculated will be same
            .requestedNetDisbursementAmount(feeCalculationRequest.getNetDisbursementAmount())
            .disbursementVatAmount(feeCalculationRequest.getDisbursementVatAmount())
            .hourlyTotalAmount(toDouble(feeTotal))
            .travelAndWaitingCostAmount(feeCalculationRequest.getTravelAndWaitingCosts())
            .netProfitCostsAmount(feeCalculationRequest.getNetProfitCosts())
            // net profit cost not capped, so requested and calculated will be same
            .requestedNetProfitCostsAmount(feeCalculationRequest.getNetProfitCosts())
            .build())
        .build();
  }
}