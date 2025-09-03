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

  // Legal Help fee codes
  private static final String IAXL = "IAXL";
  private static final String IMXL = "IMXL";

  // CLR fee codes
  private static final String IAXC = "IAXC";
  private static final String IMXC = "IMXC";
  private static final String IA100 = "IA100";
  private static final String IRAR = "IRAR";

  private static final String WARNING_NET_PROFIT_COSTS = "warning net profit costs"; // @TODO: TBC
  private static final String WARNING_NET_DISBURSEMENTS = "warning net disbursements"; // @TODO: TBC
  private static final String WARNING_TOTAL_LIMIT = "warning total limit"; // @TODO: TBC

  /**
   * Calculated fee based on the provided fee entity and fee calculation request.
   *
   * @param feeEntity             the fee entity containing fee details
   * @param feeCalculationRequest the request containing fee calculation data
   * @return FeeCalculationResponse with calculated fee
   */
  public static FeeCalculationResponse getFee(FeeEntity feeEntity, FeeCalculationRequest feeCalculationRequest) {
    String feeCode = feeEntity.getFeeCode();
    if (isLegalHelpFeeCode(feeCode)) {
      return calculateFeeLegalHelp(feeEntity, feeCalculationRequest);
    } else if (isClrFeeCode(feeCode)) {
      return calculateFeeClr(feeEntity, feeCalculationRequest);
    } else {
      //@TODO: to be removed once bus rules for all fee codes are implemented
      throw new IllegalArgumentException("Fee code not supported: " + feeCode);
    }
  }

  /**
   * Calculate fee for Legal Help (IAXL & IMXL fee codes).
   */
  private static FeeCalculationResponse calculateFeeLegalHelp(FeeEntity feeEntity, FeeCalculationRequest feeCalculationRequest) {
    List<String> warnings = new ArrayList<>();

    BigDecimal netProfitCosts = toBigDecimal(feeCalculationRequest.getNetProfitCosts());
    BigDecimal profitCostLimit = feeEntity.getProfitCostLimit();
    if (netProfitCosts.compareTo(profitCostLimit) > 0
        && StringUtils.isBlank(feeCalculationRequest.getImmigrationPriorAuthorityNumber())) {
      netProfitCosts = profitCostLimit;
      warnings.add(WARNING_NET_PROFIT_COSTS);
    }

    BigDecimal netDisbursementAmount = toBigDecimal(feeCalculationRequest.getNetDisbursementAmount());
    BigDecimal disbursementLimit = feeEntity.getDisbursementLimit();
    if (netDisbursementAmount.compareTo(disbursementLimit) > 0
        && StringUtils.isBlank(feeCalculationRequest.getImmigrationPriorAuthorityNumber())) {
      netDisbursementAmount = disbursementLimit;
      warnings.add(WARNING_NET_DISBURSEMENTS);
    }

    BigDecimal jrFormFilling = toBigDecimal(feeCalculationRequest.getJrFormFilling());
    BigDecimal feeTotal = netProfitCosts.add(jrFormFilling);

    return buildResponse(feeEntity, feeCalculationRequest, feeTotal,
        netDisbursementAmount, netProfitCosts, null, warnings);
  }

  /**
   * Calculate fee for CLR (IAXC, IMXC, IA100 & IRAR fee codes).
   */
  private static FeeCalculationResponse calculateFeeClr(FeeEntity feeEntity, FeeCalculationRequest feeCalculationRequest) {
    List<String> warnings = new ArrayList<>();

    BigDecimal netProfitCosts = toBigDecimal(feeCalculationRequest.getNetProfitCosts());
    BigDecimal netDisbursementAmount = toBigDecimal(feeCalculationRequest.getNetDisbursementAmount());
    BigDecimal netCostOfCounsel = toBigDecimal(feeCalculationRequest.getNetCostOfCounsel());
    BigDecimal jrFormFilling = toBigDecimal(feeCalculationRequest.getJrFormFilling());

    BigDecimal feeTotal = netProfitCosts.add(netCostOfCounsel).add(jrFormFilling);

    String feeCode = feeEntity.getFeeCode();
    if (IAXC.equals(feeCode) || IMXC.equals(feeCode) || IA100.equals(feeCode)) {
      BigDecimal feeTotalWithDisbursements = feeTotal.add(netDisbursementAmount);
      BigDecimal totalLimit = feeEntity.getTotalLimit();
      if (feeTotalWithDisbursements.compareTo(totalLimit) > 0) {
        if (IA100.equals(feeCode) || StringUtils.isBlank(feeCalculationRequest.getImmigrationPriorAuthorityNumber())) {
          feeTotal = totalLimit;
          warnings.add(WARNING_TOTAL_LIMIT);
        }
      }
    }

    return buildResponse(feeEntity, feeCalculationRequest, feeTotal,
        netDisbursementAmount, netProfitCosts, netCostOfCounsel, warnings);

  }

  private static boolean isLegalHelpFeeCode(String feeCode) {
    return IAXL.equals(feeCode) || IMXL.equals(feeCode);
  }

  private static boolean isClrFeeCode(String feeCode) {
    return IAXC.equals(feeCode) || IMXC.equals(feeCode) || IA100.equals(feeCode) || IRAR.equals(feeCode);
  }

  private static FeeCalculationResponse buildResponse(FeeEntity feeEntity, FeeCalculationRequest feeCalculationRequest,
                                                      BigDecimal feeTotal, BigDecimal netDisbursementAmount,
                                                      BigDecimal netProfitCosts, BigDecimal netCostOfCounsel,
                                                      List<String> warnings) {
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
        .claimId(feeCalculationRequest.getClaimId())
        .warnings(warnings)
        .feeCalculation(FeeCalculation.builder()
            .totalAmount(toDouble(finalTotal))
            .vatIndicator(feeCalculationRequest.getVatIndicator())
            .vatRateApplied(toDouble(VatUtility.getVatRateForDate(startDate)))
            .calculatedVatAmount(toDouble(calculatedVatAmount))
            .disbursementAmount(toDouble(netDisbursementAmount))
            .disbursementVatAmount(toDouble(disbursementVatAmount))
            .hourlyTotalAmount(toDouble(feeTotal))
            .netProfitCostsAmount(toDouble(netProfitCosts))
            .netCostOfCounselAmount(netCostOfCounsel != null ? feeCalculationRequest.getNetCostOfCounsel() : null)
            .jrFormFillingAmount(feeCalculationRequest.getJrFormFilling())
            .build())
        .build();

  }
}