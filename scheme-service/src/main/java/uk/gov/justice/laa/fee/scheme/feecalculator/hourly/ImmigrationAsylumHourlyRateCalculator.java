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

  private ImmigrationAsylumHourlyRateCalculator() {
  }

  // Legal Help fee codes
  private static final String IAXL = "IAXL";
  private static final String IMXL = "IMXL";
  private static final String IA100 = "IA100";

  // CLR fee codes
  private static final String IAXC = "IAXC";
  private static final String IMXC = "IMXC";
  private static final String IRAR = "IRAR";

  private static final String WARNING_NET_PROFIT_COSTS = "warning net profit costs"; // @TODO: TBC
  private static final String WARNING_NET_DISBURSEMENTS = "warning net disbursements"; // @TODO: TBC
  private static final String WARNING_TOTAL_LIMIT = "warning total limit"; // @TODO: TBC
  private static final String WARNING_TRAVEL_WAITING_COSTS = "warning travel and waiting costs"; // @TODO: TBC
  private static final String WARNING_JR_FORM_FILLING = "warning jr form filling"; // @TODO: TBC

  /**
   * Calculated fee based on the provided fee entity and fee calculation request.
   *
   * @param feeEntity             the fee entity containing fee details
   * @param feeCalculationRequest the request containing fee calculation data
   * @return FeeCalculationResponse with calculated fee
   */
  public FeeCalculationResponse calculate(FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity) {

    log.info("Calculate Immigration and Asylum hourly rate fee");

    String feeCode = feeEntity.getFeeCode();

    if (isLegalHelp(feeCode)) {
      return calculateFeeLegalHelp(feeEntity, feeCalculationRequest);
    } else if (isClrFeeCode(feeCode)) {
      return calculateFeeClr(feeEntity, feeCalculationRequest);
    } else {
      //@TODO: to be removed once bus rules for all fee codes are implemented
      throw new IllegalArgumentException("Fee code not supported: " + feeCode);
    }
  }

  /**
   * Calculate fee for Legal Help (IAXL, IMXL, IA100 fee codes).
   */
  private static FeeCalculationResponse calculateFeeLegalHelp(FeeEntity feeEntity, FeeCalculationRequest feeCalculationRequest) {
    String feeCode = feeEntity.getFeeCode();
    BigDecimal netProfitCosts = toBigDecimal(feeCalculationRequest.getNetProfitCosts());
    BigDecimal netDisbursementAmount = toBigDecimal(feeCalculationRequest.getNetDisbursementAmount());

    String immigrationPriorAuthorityNumber = feeCalculationRequest.getImmigrationPriorAuthorityNumber();

    List<ValidationMessagesInner> validationMessages = new ArrayList<>();

    if (IAXL.equals(feeCode) || IMXL.equals(feeCode)) {
      log.info("Check net profit cost are below limit for fee calculation");
      BigDecimal profitCostLimit = feeEntity.getProfitCostLimit();
      if (isOverLimitWithoutAuthority(netProfitCosts, profitCostLimit, immigrationPriorAuthorityNumber)) {
        log.warn("Profit costs limit exceeded without prior authority capping to limit");
        validationMessages.add(ValidationMessagesInner.builder()
            .message(WARNING_NET_PROFIT_COSTS)
            .type(WARNING)
            .build());
        netProfitCosts = profitCostLimit;
      }

      log.info("Check disbursement is below limit for fee calculation");
      BigDecimal disbursementLimit = feeEntity.getDisbursementLimit();
      if (isOverLimitWithoutAuthority(netDisbursementAmount, disbursementLimit, immigrationPriorAuthorityNumber)) {
        log.warn("Disbursement limit exceeded without prior authority capping to limit");
        validationMessages.add(ValidationMessagesInner.builder()
            .message(WARNING_NET_DISBURSEMENTS)
            .type(WARNING)
            .build());
        netDisbursementAmount = disbursementLimit;
      }
    }

    BigDecimal feeTotal = netProfitCosts.add(netDisbursementAmount);

    if (IA100.equals(feeCode)) {
      log.info("Check total limit is below limit for fee calculation");
      BigDecimal totalLimit = feeEntity.getTotalLimit();
      if (isOverLimitWithoutAuthority(feeTotal, totalLimit, immigrationPriorAuthorityNumber)) {
        log.warn("Total limit exceeded without prior authority capping to limit");
        validationMessages.add(ValidationMessagesInner.builder()
            .message(WARNING_TOTAL_LIMIT)
            .type(WARNING)
            .build());
        feeTotal = totalLimit;
      }
    }

    log.info("Check travel waiting and costs field is empty for fee calculation");
    checkFieldIsEmpty(feeCalculationRequest.getTravelAndWaitingCosts(), validationMessages, WARNING_TRAVEL_WAITING_COSTS,
        "Travel and waiting costs not applicable for legal help");

    log.info("Check JR form filling field is empty for fee calculation");
    checkFieldIsEmpty(feeCalculationRequest.getJrFormFilling(), validationMessages, WARNING_JR_FORM_FILLING,
        "JR form filling not applicable for legal help");

    // Apply VAT where applicable
    LocalDate startDate = feeCalculationRequest.getStartDate();
    Boolean vatApplicable = feeCalculationRequest.getVatIndicator();

    // VAT is calculated on net profit costs only
    BigDecimal netProfitCostsVatAmount = VatUtil.getVatAmount(netProfitCosts, startDate, vatApplicable);

    BigDecimal disbursementVatAmount = toBigDecimal(feeCalculationRequest.getDisbursementVatAmount());

    BigDecimal totalAmount = feeTotal.add(netProfitCostsVatAmount).add(disbursementVatAmount);

    return new FeeCalculationResponse().toBuilder()
        .feeCode(feeCalculationRequest.getFeeCode())
        .schemeId(feeEntity.getFeeScheme().getSchemeCode())
        .claimId(feeCalculationRequest.getClaimId())
        .validationMessages(validationMessages)
        .feeCalculation(FeeCalculation.builder()
            .totalAmount(toDouble(totalAmount))
            .vatIndicator(feeCalculationRequest.getVatIndicator())
            .vatRateApplied(toDouble(VatUtil.getVatRateForDate(feeCalculationRequest.getStartDate())))
            .calculatedVatAmount(toDouble(netProfitCostsVatAmount))
            .disbursementAmount(toDouble(netDisbursementAmount))
            .requestedNetDisbursementAmount(feeCalculationRequest.getNetDisbursementAmount())
            .disbursementVatAmount(toDouble(disbursementVatAmount))
            .hourlyTotalAmount(toDouble(feeTotal))
            .netProfitCostsAmount(toDouble(netProfitCosts))
            .requestedNetProfitCostsAmount(feeCalculationRequest.getNetProfitCosts())
            .build())
        .build();
  }

  /**
   * Calculate fee for CLR (IAXC, IMXC, IRAR fee codes).
   */
  private static FeeCalculationResponse calculateFeeClr(FeeEntity feeEntity, FeeCalculationRequest feeCalculationRequest) {
    String feeCode = feeEntity.getFeeCode();

    List<ValidationMessagesInner> validationMessages = new ArrayList<>();

    BigDecimal netProfitCosts = toBigDecimal(feeCalculationRequest.getNetProfitCosts());
    BigDecimal netDisbursementAmount = toBigDecimal(feeCalculationRequest.getNetDisbursementAmount());
    BigDecimal netCostOfCounsel = toBigDecimal(feeCalculationRequest.getNetCostOfCounsel());

    BigDecimal feeWithoutDisbursements = netProfitCosts.add(netCostOfCounsel);

    // Apply VAT where applicable
    LocalDate startDate = feeCalculationRequest.getStartDate();
    Boolean vatApplicable = feeCalculationRequest.getVatIndicator();

    // VAT is calculated on net profit costs and net cost of counsel only
    BigDecimal calculatedVatAmount = VatUtil.getVatAmount(feeWithoutDisbursements, startDate, vatApplicable);
    BigDecimal disbursementVatAmount = toBigDecimal(feeCalculationRequest.getDisbursementVatAmount());

    BigDecimal feeTotal = feeWithoutDisbursements.add(netDisbursementAmount);

    if (IAXC.equals(feeCode) || IMXC.equals(feeCode)) {
      log.info("Check total is below limit for fee calculation");
      BigDecimal totalLimit = feeEntity.getTotalLimit();
      String immigrationPriorAuthority = feeCalculationRequest.getImmigrationPriorAuthorityNumber();
      feeTotal = checkAndCapLimit(feeTotal, totalLimit, immigrationPriorAuthority, validationMessages, WARNING_TOTAL_LIMIT);
    }

    BigDecimal totalAmount = feeTotal.add(calculatedVatAmount).add(disbursementVatAmount);

    return new FeeCalculationResponse().toBuilder()
        .feeCode(feeCalculationRequest.getFeeCode())
        .schemeId(feeEntity.getFeeScheme().getSchemeCode())
        .claimId(feeCalculationRequest.getClaimId())
        .validationMessages(validationMessages)
        .feeCalculation(FeeCalculation.builder()
            .totalAmount(toDouble(totalAmount))
            .vatIndicator(feeCalculationRequest.getVatIndicator())
            .vatRateApplied(toDouble(VatUtil.getVatRateForDate(feeCalculationRequest.getStartDate())))
            .calculatedVatAmount(toDouble(calculatedVatAmount))
            .disbursementAmount(toDouble(netDisbursementAmount))
            .requestedNetDisbursementAmount(feeCalculationRequest.getNetDisbursementAmount())
            .disbursementVatAmount(toDouble(disbursementVatAmount))
            .hourlyTotalAmount(toDouble(feeTotal))
            .netProfitCostsAmount(toDouble(netProfitCosts))
            .requestedNetProfitCostsAmount(feeCalculationRequest.getNetProfitCosts())
            .build())
        .build();
  }

  private static boolean isLegalHelp(String feeCode) {
    return IAXL.equals(feeCode) || IMXL.equals(feeCode) || IA100.equals(feeCode);
  }

  private static boolean isClrFeeCode(String feeCode) {
    return IAXC.equals(feeCode) || IMXC.equals(feeCode) || IRAR.equals(feeCode);
  }

  private static boolean isOverLimitWithoutAuthority(BigDecimal amount, BigDecimal limit, String priorAuthorityNumber) {
    return limit != null && amount.compareTo(limit) > 0 && StringUtils.isBlank(priorAuthorityNumber);
  }

  /**
   * Checks if the amount exceeds the limit without prior authority and caps it if necessary, adding a warning.
   */
  private static BigDecimal checkAndCapLimit(BigDecimal amount, BigDecimal limit, String priorAuthorityNumber,
                                             List<ValidationMessagesInner> validationMessages, String warningMessage) {
    if (limit != null && amount.compareTo(limit) > 0 && StringUtils.isBlank(priorAuthorityNumber)) {
      log.warn("Limit exceeded without prior authority capping to limit: {}", limit);
      validationMessages.add(ValidationMessagesInner.builder()
          .message(warningMessage)
          .type(WARNING)
          .build());
      return limit;
    }
    return amount;
  }

  private static void checkFieldIsEmpty(Double value, List<ValidationMessagesInner> validationMessages,
                                        String warningMessage, String logMessage) {
    if (value != null) {
      log.warn(logMessage);
      validationMessages.add(ValidationMessagesInner.builder()
          .message(warningMessage)
          .type(WARNING)
          .build());
    }
  }
}