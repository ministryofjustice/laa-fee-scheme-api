package uk.gov.justice.laa.fee.scheme.feecalculator.disbursement;

import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toBigDecimal;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toDouble;

import java.math.BigDecimal;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.feecalculator.FeeCalculator;
import uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;

/**
 * Calculate the Mental Health fee for a given fee entity and fee calculation request.
 */
@Slf4j
@Component
public class MentalHealthDisbursementOnlyCalculator implements FeeCalculator {

  // @TODO: TBC during error and validation work, and likely moved to common util
  public static final String WARNING_MESSAGE_WARMH1 = "The claim exceeds the Escape Case Threshold. An Escape Case Claim "
      + "must be submitted for further costs to be paid.";

  @Override
  public Set<CategoryType> getSupportedCategories() {
    return Set.of();
  }

  /**
   * Calculated fee based on the provided fee entity and fee calculation request.
   *
   * @param feeCalculationRequest the request containing fee calculation data
   * @return FeeCalculationResponse with calculated fee
   */
  @Override
  public FeeCalculationResponse calculate(FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity) {

    log.info("Get fields from fee calculation request");

    BigDecimal requestNetDisbursementAmount = toBigDecimal(feeCalculationRequest.getNetDisbursementAmount());
    BigDecimal requestedDisbursementVatAmount = toBigDecimal(feeCalculationRequest.getDisbursementVatAmount());

    log.info("Calculate total fee amount with any disbursements, bolt ons and VAT where applicable");
    BigDecimal totalAmount = FeeCalculationUtil.calculateTotalAmount(
        requestNetDisbursementAmount,
        requestedDisbursementVatAmount
    );

    log.info("Build fee calculation response");
    return FeeCalculationResponse.builder()
        .feeCode(feeCalculationRequest.getFeeCode())
        .schemeId(feeEntity.getFeeScheme().getSchemeCode())
        .escapeCaseFlag(Boolean.FALSE)
        .claimId(feeCalculationRequest.getClaimId())
        .feeCalculation(FeeCalculation.builder()
            .totalAmount(toDouble(totalAmount))
            .vatIndicator(Boolean.TRUE)
            .disbursementAmount(toDouble(requestNetDisbursementAmount))
            .requestedNetDisbursementAmount(toDouble(requestNetDisbursementAmount))
            .disbursementVatAmount(toDouble(requestedDisbursementVatAmount))
            .build())
        .build();
  }

}
