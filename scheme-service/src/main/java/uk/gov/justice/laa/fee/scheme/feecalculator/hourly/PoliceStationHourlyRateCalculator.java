package uk.gov.justice.laa.fee.scheme.feecalculator.hourly;

import static uk.gov.justice.laa.fee.scheme.enums.WarningType.WARN_POLICE_OTHER_UPPER_LIMIT;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil.buildValidationWarning;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil.calculateVatAmount;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil.getFeeClaimStartDate;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toBigDecimal;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toDouble;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toDoubleOrNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.feecalculator.FeeCalculator;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner;
import uk.gov.justice.laa.fee.scheme.service.VatRatesService;

/**
 * Calculate the police station fee for a given fee entity and fee data.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PoliceStationHourlyRateCalculator implements FeeCalculator {

  private final VatRatesService vatRatesService;

  @Override
  public Set<CategoryType> getSupportedCategories() {
    return Set.of(); // Only used by PoliceStationFeeCalculator and not available via FeeCalculatorFactory
  }

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

    BigDecimal upperCostLimit = feeEntity.getUpperCostLimit();

    BigDecimal netProfitCosts = toBigDecimal(feeCalculationRequest.getNetProfitCosts());

    BigDecimal netDisbursementAmount = toBigDecimal(feeCalculationRequest.getNetDisbursementAmount());

    BigDecimal travelCosts = toBigDecimal(feeCalculationRequest.getNetTravelCosts());

    BigDecimal waitingCosts = toBigDecimal(feeCalculationRequest.getNetWaitingCosts());

    log.info("Calculate hourly rate and costs");
    BigDecimal feeTotal = netProfitCosts.add(netDisbursementAmount).add(travelCosts).add(waitingCosts);

    if (feeTotal.compareTo(upperCostLimit) > 0) {
      validationMessages.add(buildValidationWarning(WARN_POLICE_OTHER_UPPER_LIMIT,
          "Fee total exceeds upper cost limit"));
    }

    // Calculate VAT if applicable
    LocalDate claimStartDate = getFeeClaimStartDate(CategoryType.POLICE_STATION, feeCalculationRequest);
    Boolean vatIndicator = feeCalculationRequest.getVatIndicator();
    BigDecimal vatRate = vatRatesService.getVatRateForDate(claimStartDate, vatIndicator);
    BigDecimal calculatedVatAmount = calculateVatAmount(feeTotal, vatRate);

    BigDecimal disbursementVatAmount = toBigDecimal(feeCalculationRequest.getDisbursementVatAmount());

    // Calculate total amount
    BigDecimal totalAmount = feeTotal.add(calculatedVatAmount).add(disbursementVatAmount);

    log.info("Build fee calculation response");
    return new FeeCalculationResponse().toBuilder()
        .feeCode(feeCalculationRequest.getFeeCode())
        .schemeId(feeEntity.getFeeScheme().getSchemeCode())
        .claimId(feeCalculationRequest.getClaimId())
        .validationMessages(validationMessages)
        .feeCalculation(FeeCalculation.builder()
            .totalAmount(toDouble(totalAmount))
            .vatIndicator(feeCalculationRequest.getVatIndicator())
            .vatRateApplied(toDoubleOrNull(vatRate))
            .calculatedVatAmount(toDouble(calculatedVatAmount))
            .disbursementAmount(feeCalculationRequest.getNetDisbursementAmount())
            // disbursement not capped, so requested and calculated will be same
            .requestedNetDisbursementAmount(feeCalculationRequest.getNetDisbursementAmount())
            .disbursementVatAmount(feeCalculationRequest.getDisbursementVatAmount())
            .hourlyTotalAmount(toDouble(feeTotal))
            .netTravelCostsAmount(feeCalculationRequest.getNetTravelCosts())
            .netWaitingCostsAmount(feeCalculationRequest.getNetWaitingCosts())
            .netProfitCostsAmount(feeCalculationRequest.getNetProfitCosts())
            // net profit cost not capped, so requested and calculated will be same
            .requestedNetProfitCostsAmount(feeCalculationRequest.getNetProfitCosts())
            .build())
        .build();
  }
}