package uk.gov.justice.laa.fee.scheme.feecalculator.hourly;

import static uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner.TypeEnum.WARNING;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toBigDecimal;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toDouble;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.feecalculator.FeeCalculator;
import uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil;
import uk.gov.justice.laa.fee.scheme.feecalculator.util.VatUtil;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner;

/**
 * Calculate the Immigration and Asylum hourly rate fee for a given fee entity and fee calculation request.
 */
@Slf4j
@Component
public final class ImmigrationAsylumHourlyRateCalculator implements FeeCalculator {

  @Override
  public Set<CategoryType> getSupportedCategories() {
    return Set.of(); // Only used by ImmigrationAsylumFeeCalculator and not available via FeeCalculatorFactory
  }

  private ImmigrationAsylumHourlyRateCalculator() {}

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
  public FeeCalculationResponse calculate(FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity) {

    log.info("Calculate Immigration and Asylum hourly rate fee");

    String claimId = feeCalculationRequest.getClaimId();
    String feeCode = feeEntity.getFeeCode();
    if (IAXL.equals(feeCode) || IMXL.equals(feeCode)) {
      List<ValidationMessagesInner> validationMessages = new ArrayList<>();

      BigDecimal netProfitCosts = toBigDecimal(feeCalculationRequest.getNetProfitCosts());

      BigDecimal requestedNetProfitCosts = toBigDecimal(feeCalculationRequest.getNetProfitCosts());

      BigDecimal profitCostLimit = feeEntity.getProfitCostLimit();
      if (netProfitCosts.compareTo(profitCostLimit) > 0
          && StringUtils.isBlank(feeCalculationRequest.getImmigrationPriorAuthorityNumber())) {
        netProfitCosts = profitCostLimit;
        validationMessages.add(ValidationMessagesInner.builder()
            .message(WARNING_NET_PROFIT_COSTS)
            .type(WARNING)
            .build());
      }

      BigDecimal netDisbursementAmount = toBigDecimal(feeCalculationRequest.getNetDisbursementAmount());

      BigDecimal requestedNetDisbursementAmount = toBigDecimal(feeCalculationRequest.getNetDisbursementAmount());

      BigDecimal disbursementLimit = feeEntity.getDisbursementLimit();
      if (netDisbursementAmount.compareTo(disbursementLimit) > 0
          && StringUtils.isBlank(feeCalculationRequest.getImmigrationPriorAuthorityNumber())) {
        netDisbursementAmount = disbursementLimit;
        validationMessages.add(ValidationMessagesInner.builder()
            .message(WARNING_NET_DISBURSEMENTS)
            .type(WARNING)
            .build());
      }

      BigDecimal jrFormFilling = toBigDecimal(feeCalculationRequest.getJrFormFilling());
      BigDecimal feeTotal = netProfitCosts.add(jrFormFilling);

      // Apply VAT where applicable
      LocalDate startDate = feeCalculationRequest.getStartDate();
      Boolean vatApplicable = feeCalculationRequest.getVatIndicator();
      BigDecimal calculatedVatAmount = VatUtil.getVatAmount(feeTotal, startDate, vatApplicable);

      BigDecimal disbursementVatAmount = toBigDecimal(feeCalculationRequest.getDisbursementVatAmount());

      BigDecimal totalAmount = FeeCalculationUtil.calculateTotalAmount(feeTotal, calculatedVatAmount,
              netDisbursementAmount, disbursementVatAmount);

      return new FeeCalculationResponse().toBuilder()
          .feeCode(feeCalculationRequest.getFeeCode())
          .schemeId(feeEntity.getFeeScheme().getSchemeCode())
          .claimId(claimId)
          .validationMessages(validationMessages)
          .feeCalculation(FeeCalculation.builder()
              .totalAmount(toDouble(totalAmount))
              .vatIndicator(feeCalculationRequest.getVatIndicator())
              .vatRateApplied(toDouble(VatUtil.getVatRateForDate(feeCalculationRequest.getStartDate())))
              .calculatedVatAmount(toDouble(calculatedVatAmount))
              .disbursementAmount(toDouble(netDisbursementAmount))
              .requestedNetDisbursementAmount(toDouble(requestedNetDisbursementAmount))
              .disbursementVatAmount(toDouble(disbursementVatAmount))
              .hourlyTotalAmount(toDouble(feeTotal))
              .netProfitCostsAmount(toDouble(netProfitCosts))
              .requestedNetProfitCostsAmount(toDouble(requestedNetProfitCosts))
              .jrFormFillingAmount(toDouble(jrFormFilling))
              .build())
          .build();
    } else {
      //@TODO: to be removed once bus rules for all fee codes are implemented
      throw new IllegalArgumentException("Fee code not supported: " + feeCode);
    }
  }

}