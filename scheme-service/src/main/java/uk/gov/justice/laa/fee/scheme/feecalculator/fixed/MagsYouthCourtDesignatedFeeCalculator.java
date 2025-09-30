package uk.gov.justice.laa.fee.scheme.feecalculator.fixed;

import static uk.gov.justice.laa.fee.scheme.feecalculator.util.VatUtil.getVatAmount;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.VatUtil.getVatRateForDate;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toBigDecimal;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toDouble;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;

/**
 * Calculate the designated magistrates or youth court fee for a given fee entity and fee calculation request.
 */
@Slf4j
@Component
public class MagsYouthCourtDesignatedFeeCalculator {

  /**
   * Calculated fee for designated magistrates or youth court fee based on the provided fee entity and fee calculation request.
   */
  public FeeCalculationResponse calculate(FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity) {
    log.info("Calculate magistrates and youth court designated fixed fee");
    log.info("Get fields from fee calculation request");
    // get the requested disbursement amount from feeCalculationRequest
    BigDecimal requestedNetDisbursementAmount = toBigDecimal(feeCalculationRequest.getNetDisbursementAmount());

    // get the requested disbursement VAT amount from feeCalculationRequest
    BigDecimal requestedDisbursementVatAmount = toBigDecimal(feeCalculationRequest.getDisbursementVatAmount());

    // @TODO: change to representation order date
    log.info("Calculate fixed fee and costs");
    BigDecimal fixedFeeAmount = feeEntity.getFixedFee();
    LocalDate startDate = feeCalculationRequest.getStartDate();
    Boolean vatApplicable = feeCalculationRequest.getVatIndicator();
    BigDecimal calculatedVatAmount = getVatAmount(fixedFeeAmount, startDate, vatApplicable);

    log.info("Calculate total amount for fee calculation");
    BigDecimal finalTotal = fixedFeeAmount
        .add(calculatedVatAmount)
        .add(requestedNetDisbursementAmount)
        .add(requestedDisbursementVatAmount);

    log.info("Build fee calculation response");
    FeeCalculation feeCalculation = FeeCalculation.builder()
        .totalAmount(toDouble(finalTotal))
        .vatIndicator(vatApplicable)
        .vatRateApplied(toDouble(getVatRateForDate(startDate)))
        .calculatedVatAmount(toDouble(calculatedVatAmount))
        .disbursementAmount(toDouble(requestedNetDisbursementAmount))
        .requestedNetDisbursementAmount(toDouble(requestedNetDisbursementAmount))
        .disbursementVatAmount(toDouble(requestedDisbursementVatAmount))
        .fixedFeeAmount(toDouble(fixedFeeAmount))
        .build();
    String claimId = feeCalculationRequest.getClaimId();

    return new FeeCalculationResponse().toBuilder()
        .feeCode(feeCalculationRequest.getFeeCode())
        .schemeId(feeEntity.getFeeSchemeCode().getSchemeCode())
        .claimId(claimId)
        .feeCalculation(feeCalculation)
        .build();
  }
}
