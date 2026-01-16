package uk.gov.justice.laa.fee.scheme.feecalculator.fixed.standardfixedfee;

import static uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil.calculateTotalAmount;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil.calculateVatAmount;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil.getFeeClaimStartDate;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.defaultToZeroIfNull;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toBigDecimal;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toDouble;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toDoubleOrNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.feecalculator.FeeCalculator;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner;
import uk.gov.justice.laa.fee.scheme.service.VatRatesService;

/**
 * Standard Fixed Fee Calculator.
 * Fixed Fee plus Disbursements.
 */
@Slf4j
@RequiredArgsConstructor
public abstract class StandardFixedFeeCalculator implements FeeCalculator {

  private final VatRatesService vatRatesService;
  private final boolean canEscape;

  @Override
  public FeeCalculationResponse calculate(FeeCalculationRequest feeCalculationRequest,
                                          FeeEntity feeEntity) {

    log.info("Starting fee calculation for {}", feeEntity.getCategoryType());

    //Step 1: get Fixed Fee Amount
    BigDecimal fixedFeeAmount = defaultToZeroIfNull(feeEntity.getFixedFee());

    //Step 2: get Start Date
    LocalDate claimStartDate = getFeeClaimStartDate(feeEntity.getCategoryType(), feeCalculationRequest);

    //Step 3: get VAT Rate
    Boolean vatIndicator = feeCalculationRequest.getVatIndicator();
    BigDecimal vatRate = vatRatesService.getVatRateForDate(claimStartDate, vatIndicator);

    //Step 4: Calculate VAT Amount
    BigDecimal calculatedVatAmount = calculateVatAmount(fixedFeeAmount, vatRate);

    //Step 5: get Disbursements
    BigDecimal netDisbursementAmount = toBigDecimal(feeCalculationRequest.getNetDisbursementAmount());
    BigDecimal disbursementVatAmount = toBigDecimal(feeCalculationRequest.getDisbursementVatAmount());

    //Step 6: calculate Total Amount
    BigDecimal totalAmount = calculateTotalAmount(fixedFeeAmount, calculatedVatAmount, netDisbursementAmount,
        disbursementVatAmount);

    //Step 7: check if escaped, if eligible
    List<ValidationMessagesInner> validationMessages = new ArrayList<>();
    boolean isEscaped = false;
    if (canEscape) {
      isEscaped = handleEscapeCase(feeCalculationRequest, feeEntity, validationMessages, totalAmount);
    }

    //Step 8: build FeeCalculation
    FeeCalculation feeCalculation = FeeCalculation.builder()
        .totalAmount(toDouble(totalAmount))
        .vatIndicator(vatIndicator)
        .vatRateApplied(toDoubleOrNull(vatRate))
        .calculatedVatAmount(toDouble(calculatedVatAmount))
        .disbursementAmount(toDoubleOrNull(netDisbursementAmount))
        .requestedNetDisbursementAmount(toDoubleOrNull(netDisbursementAmount))
        .disbursementVatAmount(toDoubleOrNull(disbursementVatAmount))
        .fixedFeeAmount(toDouble(fixedFeeAmount))
        .build();

    //step 9: build response
    log.info("Build fee calculation response");
    return FeeCalculationResponse.builder()
        .feeCode(feeCalculationRequest.getFeeCode())
        .schemeId(feeEntity.getFeeScheme().getSchemeCode())
        .claimId(feeCalculationRequest.getClaimId())
        .escapeCaseFlag(canEscape ? isEscaped : null)
        .validationMessages(validationMessages)
        .feeCalculation(feeCalculation)
        .build();
  }

  protected boolean handleEscapeCase(FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity,
                                     List<ValidationMessagesInner> messages, BigDecimal totalAmount) {
    return false;
  }
}