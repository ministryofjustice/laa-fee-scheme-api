package uk.gov.justice.laa.fee.scheme.feecalculator.fixed;

import static uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil.buildFeeCalculationResponse;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil.buildValidationWarning;
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
import uk.gov.justice.laa.fee.scheme.enums.WarningType;
import uk.gov.justice.laa.fee.scheme.feecalculator.FeeCalculator;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner;
import uk.gov.justice.laa.fee.scheme.service.VatRatesService;

/**
 * Base.
 */
@Slf4j
@RequiredArgsConstructor
public abstract class BaseFixedFeeCalculator implements FeeCalculator {

  protected final VatRatesService vatRatesService;

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

    //Step 7: check if escaped
    BigDecimal netProfitCosts = toBigDecimal(feeCalculationRequest.getNetProfitCosts());

    //Step 8: build Validation Messages
    boolean canEscape = canEscape();

    List<ValidationMessagesInner> validationMessages = new ArrayList<>();
    if (canEscape) {
      boolean isEscaped = determineEscapeCase(netProfitCosts, feeEntity);
      if (isEscaped) {
        validationMessages.add(buildValidationWarning(getEscapeWarningCode(), getEscapeWarningMessage()));
      }
    }


    //Step 9: build FeeCalculation
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

    return buildFeeCalculationResponse(feeCalculationRequest, feeEntity, feeCalculation, validationMessages, isEscaped);
  }

  protected boolean canEscape() {
    return false;
  }

  protected boolean determineEscapeCase(BigDecimal netProfitCosts, FeeEntity feeEntity) {
    return false;
  }

  protected WarningType getEscapeWarningCode() {
    return null;
  }

  protected String getEscapeWarningMessage() {
    return null;
  }
}