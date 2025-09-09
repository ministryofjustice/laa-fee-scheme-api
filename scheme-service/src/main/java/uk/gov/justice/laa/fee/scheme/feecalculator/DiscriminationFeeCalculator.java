package uk.gov.justice.laa.fee.scheme.feecalculator;

import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.DISCRIMINATION;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtility.toBigDecimal;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtility.toDouble;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.feecalculator.utility.VatUtility;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.service.DataService;
import uk.gov.justice.laa.fee.scheme.service.FeeCalculator;

/**
 * Calculate the discrimination fee for a given fee entity and fee calculation request.
 */
@RequiredArgsConstructor
@Component
public class DiscriminationFeeCalculator implements FeeCalculator {

  private final DataService dataService;

  @Override
  public Set<CategoryType> getSupportedCategories() {
    return Set.of(DISCRIMINATION);
  }

  private static final String WARNING_CODE_DESCRIPTION = "123"; // clarify what description should be

  /**
   * Calculated fee based on the provided fee entity and fee calculation request.
   *          the fee entity containing fee details
   * @param feeCalculationRequest the request containing fee calculation data
   * @return FeeCalculationResponse with calculated fee
   */
  @Override
  public FeeCalculationResponse calculate(FeeCalculationRequest feeCalculationRequest) {

    FeeEntity feeEntity = dataService.getFeeEntity(feeCalculationRequest);

    BigDecimal netProfitCosts = toBigDecimal(feeCalculationRequest.getNetProfitCosts());
    BigDecimal netCostOfCounsel = toBigDecimal(feeCalculationRequest.getNetCostOfCounsel());
    BigDecimal travelAndWaitingCosts = toBigDecimal(feeCalculationRequest.getTravelAndWaitingCosts());
    String claimId = feeCalculationRequest.getClaimId();
    List<String> warningList = new ArrayList<>();

    BigDecimal feeTotal = netProfitCosts.add(netCostOfCounsel).add(travelAndWaitingCosts);

    BigDecimal escapeThresholdLimit = feeEntity.getEscapeThresholdLimit();

    // @TODO: escape case logic TBC
    boolean escaped = false;
    if (feeTotal.compareTo(escapeThresholdLimit) > 0) {
      warningList.add(WARNING_CODE_DESCRIPTION);
      feeTotal = escapeThresholdLimit;
      escaped = true;
    }

    // Apply VAT where applicable
    LocalDate startDate = feeCalculationRequest.getStartDate();
    Boolean vatApplicable = feeCalculationRequest.getVatIndicator();
    BigDecimal calculatedVatAmount = VatUtility.getVatAmount(feeTotal, startDate, vatApplicable);

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
        .warnings(warningList)
        .escapeCaseFlag(escaped)
        .feeCalculation(FeeCalculation.builder()
            .totalAmount(toDouble(finalTotal))
            .vatIndicator(vatApplicable)
            .vatRateApplied(toDouble(VatUtility.getVatRateForDate(startDate)))
            .calculatedVatAmount(toDouble(calculatedVatAmount))
            .disbursementAmount(toDouble(netDisbursementAmount))
            .disbursementVatAmount(toDouble(disbursementVatAmount))
            .hourlyTotalAmount(toDouble(feeTotal))
            .netCostOfCounselAmount(toDouble(netCostOfCounsel))
            .netProfitCostsAmount(toDouble(netProfitCosts))
            .travelAndWaitingCostAmount(toDouble(travelAndWaitingCosts))
            .build())
        .build();
  }
}
