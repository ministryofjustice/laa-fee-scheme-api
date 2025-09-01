package uk.gov.justice.laa.fee.scheme.feecalculator;

import static uk.gov.justice.laa.fee.scheme.feecalculator.utility.NumberUtility.toBigDecimal;
import static uk.gov.justice.laa.fee.scheme.feecalculator.utility.NumberUtility.toDouble;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.feecalculator.utility.VatUtility;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;

/**
 * Calculate the Immigration and Asylum hourly rate fee for a given fee entity and fee calculation request.
 */
public final class ImmigrationAsylumHourlyRateCalculator {

  private ImmigrationAsylumHourlyRateCalculator() {
  }

  private static final String IAXL = "IAXL";
  private static final String IMXL = "IMXL";

  private static final String WARNING_NET_PROFIT_COSTS = "warning net profit costs"; // @TODO: TBC
  private static final String WARNING_NET_DISBURSEMENTS = "warning net disbursements"; // @TODO: TBC

  /**
   * Calculated fee based on the provided fee entity and fee calculation request.
   *
   * @param feeEntity             the fee entity containing fee details
   * @param feeCalculationRequest the request containing fee calculation data
   * @return FeeCalculationResponse with calculated fee
   */
  public static FeeCalculationResponse getFee(FeeEntity feeEntity, FeeCalculationRequest feeCalculationRequest) {
    String feeCode = feeEntity.getFeeCode();
    if (IAXL.equals(feeCode) || IMXL.equals(feeCode)) {
      List<String> warnings = new ArrayList<>();

      BigDecimal netProfitCosts = toBigDecimal(feeCalculationRequest.getNetProfitCosts());
      BigDecimal profitCostLimit = feeEntity.getProfitCostLimit();
      if (netProfitCosts.compareTo(profitCostLimit) > 0
          && StringUtils.isBlank(feeCalculationRequest.getImmigrationPriorityAuthority())) {
        netProfitCosts = profitCostLimit;
        warnings.add(WARNING_NET_PROFIT_COSTS);
      }

      BigDecimal netDisbursementAmount = toBigDecimal(feeCalculationRequest.getNetDisbursementAmount());
      BigDecimal disbursementLimit = feeEntity.getDisbursementLimit();
      if (netDisbursementAmount.compareTo(disbursementLimit) > 0
          && StringUtils.isBlank(feeCalculationRequest.getImmigrationPriorityAuthority())) {
        netDisbursementAmount = disbursementLimit;
        warnings.add(WARNING_NET_DISBURSEMENTS);
      }

      BigDecimal jrFormFilling = toBigDecimal(feeCalculationRequest.getJrFormFilling());
      BigDecimal feeTotal = netProfitCosts.add(jrFormFilling);

      // Apply VAT where applicable
      LocalDate startDate = feeCalculationRequest.getStartDate();
      Boolean vatApplicable = feeCalculationRequest.getVatIndicator();
      BigDecimal calculatedVatAmount = VatUtility.getVatAmount(feeTotal, startDate, vatApplicable);

      BigDecimal disbursementVatAmount = toBigDecimal(feeCalculationRequest.getDisbursementVatAmount());

      BigDecimal finalTotal = feeTotal
          .add(calculatedVatAmount)
          .add(netDisbursementAmount)
          .add(disbursementVatAmount);

      return new FeeCalculationResponse().toBuilder()
          .feeCode(feeCalculationRequest.getFeeCode())
          .schemeId(feeEntity.getFeeSchemeCode().getSchemeCode())
          .warnings(warnings)
          .feeCalculation(FeeCalculation.builder()
              .totalAmount(toDouble(finalTotal))
              .vatIndicator(feeCalculationRequest.getVatIndicator())
              .vatRateApplied(toDouble(VatUtility.getVatRateForDate(feeCalculationRequest.getStartDate())))
              .calculatedVatAmount(toDouble(calculatedVatAmount))
              .disbursementAmount(toDouble(netDisbursementAmount))
              .disbursementVatAmount(toDouble(disbursementVatAmount))
              .hourlyTotalAmount(toDouble(feeTotal))
              .netProfitCostsAmount(toDouble(netProfitCosts))
              .jrFormFillingAmount(toDouble(jrFormFilling))
              .build())
          .build();
    } else {
      //@TODO: to be removed once bus rules for all fee codes are implemented
      throw new IllegalArgumentException("Fee code not supported: " + feeCode);
    }
  }
}