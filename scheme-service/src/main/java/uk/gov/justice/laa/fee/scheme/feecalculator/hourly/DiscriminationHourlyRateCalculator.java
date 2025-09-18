package uk.gov.justice.laa.fee.scheme.feecalculator.hourly;

import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.DISCRIMINATION;
import static uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner.TypeEnum.WARNING;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toBigDecimal;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toDouble;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.feecalculator.FeeCalculator;
import uk.gov.justice.laa.fee.scheme.feecalculator.util.VatUtil;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner;

/**
 * Calculate the discrimination fee for a given fee entity and fee calculation request.
 */
@Component
public class DiscriminationHourlyRateCalculator implements FeeCalculator {

  @Override
  public Set<CategoryType> getSupportedCategories() {
    return Set.of(DISCRIMINATION);
  }

  private static final String WARNING_CODE_DESCRIPTION = "123"; // clarify what description should be

  /**
   * Calculated fee based on the provided fee entity and fee calculation request.
   * the fee entity containing fee details
   *
   * @param feeCalculationRequest the request containing fee calculation data
   * @param feeEntity             the fee entity containing fee details
   * @return FeeCalculationResponse with calculated fee
   */
  @Override
  public FeeCalculationResponse calculate(FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity) {

    BigDecimal netProfitCosts = toBigDecimal(feeCalculationRequest.getNetProfitCosts());
    BigDecimal netCostOfCounsel = toBigDecimal(feeCalculationRequest.getNetCostOfCounsel());
    String claimId = feeCalculationRequest.getClaimId();
    List<ValidationMessagesInner> validationMessages = new ArrayList<>();

    BigDecimal feeTotal = netProfitCosts.add(netCostOfCounsel);

    BigDecimal escapeThresholdLimit = feeEntity.getEscapeThresholdLimit();

    // @TODO: escape case logic TBC
    boolean escaped = false;
    if (feeTotal.compareTo(escapeThresholdLimit) > 0) {
      validationMessages.add(ValidationMessagesInner.builder()
          .message(WARNING_CODE_DESCRIPTION)
          .type(WARNING)
          .build());
      feeTotal = escapeThresholdLimit;
      escaped = true;
    }

    // Apply VAT where applicable
    LocalDate startDate = feeCalculationRequest.getStartDate();
    Boolean vatApplicable = feeCalculationRequest.getVatIndicator();
    BigDecimal calculatedVatAmount = VatUtil.getVatAmount(feeTotal, startDate, vatApplicable);

    BigDecimal netDisbursementAmount = toBigDecimal(feeCalculationRequest.getNetDisbursementAmount());
    BigDecimal disbursementVatAmount = toBigDecimal(feeCalculationRequest.getDisbursementVatAmount());

    BigDecimal finalTotal = feeTotal
        .add(calculatedVatAmount)
        .add(netDisbursementAmount)
        .add(disbursementVatAmount);

    return new FeeCalculationResponse().toBuilder()
        .feeCode(feeCalculationRequest.getFeeCode())
        .schemeId(feeEntity.getFeeSchemeCode().getSchemeCode())
        .claimId(claimId)
        .validationMessages(validationMessages)
        .escapeCaseFlag(escaped)
        .feeCalculation(FeeCalculation.builder()
            .totalAmount(toDouble(finalTotal))
            .vatIndicator(vatApplicable)
            .vatRateApplied(toDouble(VatUtil.getVatRateForDate(startDate)))
            .calculatedVatAmount(toDouble(calculatedVatAmount))
            .disbursementAmount(toDouble(netDisbursementAmount))
            // disbursement not capped, so requested and calculated will be same
            .requestedNetDisbursementAmount(toDouble(netDisbursementAmount))
            .disbursementVatAmount(toDouble(disbursementVatAmount))
            .hourlyTotalAmount(toDouble(feeTotal))
            .netCostOfCounselAmount(toDouble(netCostOfCounsel))
            .netProfitCostsAmount(toDouble(netProfitCosts))
            // net profit cost not capped, so requested and calculated will be same
            .requestedNetProfitCostsAmount(toDouble(netProfitCosts))
            .build())
        .build();
  }
}
