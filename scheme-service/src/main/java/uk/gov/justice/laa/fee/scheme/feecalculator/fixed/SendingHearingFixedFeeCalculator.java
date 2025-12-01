package uk.gov.justice.laa.fee.scheme.feecalculator.fixed;

import static uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil.buildFeeCalculationResponse;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil.calculateTotalAmount;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil.calculateVatAmount;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil.getFeeClaimStartDate;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.defaultToZeroIfNull;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toBigDecimal;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toDouble;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toDoubleOrNull;

import java.math.BigDecimal;
import java.time.LocalDate;
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
import uk.gov.justice.laa.fee.scheme.service.VatRatesService;

/**
 * Calculate the Sending Hearing fee for a given fee entity and fee calculation request.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SendingHearingFixedFeeCalculator implements FeeCalculator {

  private final VatRatesService vatRatesService;

  @Override
  public Set<CategoryType> getSupportedCategories() {
    return Set.of(CategoryType.SENDING_HEARING);
  }

  @Override
  public FeeCalculationResponse calculate(FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity) {

    log.info("Calculate Sending Hearing fixed fee");

    // Get fixed fee amount
    BigDecimal fixedFeeAmount = defaultToZeroIfNull(feeEntity.getFixedFee());

    // Calculate VAT if applicable
    LocalDate claimStartDate = getFeeClaimStartDate(CategoryType.SENDING_HEARING, feeCalculationRequest);
    Boolean vatIndicator = feeCalculationRequest.getVatIndicator();
    BigDecimal vatRate = vatRatesService.getVatRateForDate(claimStartDate, vatIndicator);
    BigDecimal calculatedVatAmount = calculateVatAmount(fixedFeeAmount, vatRate);

    // Get disbursements
    BigDecimal netDisbursementAmount = toBigDecimal(feeCalculationRequest.getNetDisbursementAmount());
    BigDecimal disbursementVatAmount = toBigDecimal(feeCalculationRequest.getDisbursementVatAmount());

    // Calculate total amount
    BigDecimal totalAmount = calculateTotalAmount(fixedFeeAmount,
        calculatedVatAmount, netDisbursementAmount, disbursementVatAmount);

    FeeCalculation feeCalculation = FeeCalculation.builder()
        .totalAmount(toDouble(totalAmount))
        .vatIndicator(vatIndicator)
        .vatRateApplied(toDoubleOrNull(vatRate))
        .calculatedVatAmount(toDouble(calculatedVatAmount))
        .disbursementAmount(feeCalculationRequest.getNetDisbursementAmount())
        .requestedNetDisbursementAmount(feeCalculationRequest.getNetDisbursementAmount())
        .disbursementVatAmount(feeCalculationRequest.getDisbursementVatAmount())
        .fixedFeeAmount(toDouble(fixedFeeAmount))
        .build();

    return buildFeeCalculationResponse(feeCalculationRequest, feeEntity, feeCalculation);
  }
}
