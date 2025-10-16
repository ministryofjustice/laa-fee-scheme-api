package uk.gov.justice.laa.fee.scheme.feecalculator.hourly;

import static uk.gov.justice.laa.fee.scheme.enums.LimitType.DISBURSEMENT;
import static uk.gov.justice.laa.fee.scheme.enums.LimitType.PROFIT_COST;
import static uk.gov.justice.laa.fee.scheme.enums.LimitType.TOTAL;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil.checkLimitAndCapIfExceeded;
import static uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner.TypeEnum.WARNING;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.defaultToZeroIfNull;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toBigDecimal;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toDouble;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.feecalculator.FeeCalculator;
import uk.gov.justice.laa.fee.scheme.feecalculator.util.LimitContext;
import uk.gov.justice.laa.fee.scheme.feecalculator.util.VatUtil;
import uk.gov.justice.laa.fee.scheme.feecalculator.util.boltons.BoltOnUtil;
import uk.gov.justice.laa.fee.scheme.model.BoltOnFeeDetails;
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

  // CLR Interim fee codes
  private static final String IACD = "IACD";
  private static final String IMCD = "IMCD";

  private static final String WARIA4_TOTAL_LIMIT = "warning total limit"; // @TODO: TBC
  private static final String WARIA5_TOTAL_LIMIT = "warning total limit"; // @TODO: TBC
  private static final String WARIA6_NET_PROFIT_COSTS = "warning net profit costs"; // @TODO: TBC
  private static final String WARIA7_NET_DISBURSEMENTS = "warning net disbursements"; // @TODO: TBC
  private static final String WARIA8_TOTAL_LIMIT = "warning total limit"; // @TODO: TBC
  private static final String WARIA9_DETENTION_TRAVEL_WAITING_COSTS = "warning detention travel and waiting costs"; // @TODO: TBC
  private static final String WARIA10_JR_FORM_FILLING = "warning jr form filling"; // @TODO: TBC

  /**
   * Calculate fee based on the provided fee calculation request and fee entity.
   *
   * @param feeCalculationRequest the request containing fee calculation data
   * @param feeEntity             the fee entity containing fee details
   * @return FeeCalculationResponse with calculated fee
   */
  public FeeCalculationResponse calculate(FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity) {
    String feeCode = feeEntity.getFeeCode();

    if (isLegalHelp(feeCode)) {
      return calculateFeeLegalHelp(feeCalculationRequest, feeEntity);
    } else if (isClr(feeCode)) {
      return calculateFeeClr(feeCalculationRequest, feeEntity);
    } else if (isClrInterim(feeCode)) {
      return calculateFeeClrInterim(feeCalculationRequest, feeEntity);
    } else {
      throw new IllegalArgumentException("Fee code not supported: " + feeCode);
    }
  }

  /**
   * Calculate fee for Legal Help (IAXL, IMXL, IA100 fee codes).
   */
  private static FeeCalculationResponse calculateFeeLegalHelp(FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity) {
    log.info("Calculate Immigration and Asylum (Legal Help) hourly rate fee");

    List<ValidationMessagesInner> validationMessages = new ArrayList<>();

    BigDecimal netProfitCosts = toBigDecimal(feeCalculationRequest.getNetProfitCosts());
    BigDecimal netDisbursementAmount = toBigDecimal(feeCalculationRequest.getNetDisbursementAmount());
    String immigrationPriorAuthorityNumber = feeCalculationRequest.getImmigrationPriorAuthorityNumber();

    String feeCode = feeEntity.getFeeCode();
    if (IAXL.equals(feeCode) || IMXL.equals(feeCode)) {
      // Check profit costs limit
      LimitContext profitCostsLimitContext = new LimitContext(PROFIT_COST, feeEntity.getProfitCostLimit(),
          immigrationPriorAuthorityNumber, WARIA6_NET_PROFIT_COSTS);
      netProfitCosts = checkLimitAndCapIfExceeded(netProfitCosts, profitCostsLimitContext, validationMessages);

      // Check disbursement limit
      LimitContext disbursementLimitContext = new LimitContext(DISBURSEMENT, feeEntity.getDisbursementLimit(),
          immigrationPriorAuthorityNumber, WARIA7_NET_DISBURSEMENTS);
      netDisbursementAmount = checkLimitAndCapIfExceeded(netDisbursementAmount, disbursementLimitContext, validationMessages);
    }

    // total = net profit costs + net disbursements
    BigDecimal feeTotal = netProfitCosts.add(netDisbursementAmount);

    if (IA100.equals(feeCode)) {
      // Check total limit
      LimitContext totalLimitContext = new LimitContext(TOTAL, feeEntity.getTotalLimit(),
          immigrationPriorAuthorityNumber, WARIA8_TOTAL_LIMIT);
      feeTotal = checkLimitAndCapIfExceeded(feeTotal, totalLimitContext, validationMessages);
    }

    checkFieldsAreEmpty(feeCalculationRequest, validationMessages);

    // VAT is calculated on net profit costs only
    BigDecimal calculatedVatAmount = VatUtil.getVatAmount(netProfitCosts,
        feeCalculationRequest.getStartDate(), feeCalculationRequest.getVatIndicator());

    BigDecimal disbursementVatAmount = toBigDecimal(feeCalculationRequest.getDisbursementVatAmount());

    BigDecimal totalAmount = feeTotal.add(calculatedVatAmount).add(disbursementVatAmount);

    FeeCalculation feeCalculation = buildFeeCalculation(feeCalculationRequest, totalAmount, calculatedVatAmount,
        netDisbursementAmount, disbursementVatAmount, feeTotal, netProfitCosts, false, null);

    return buildFeeCalculationResponse(feeCalculationRequest, feeEntity, validationMessages, feeCalculation);
  }

  /**
   * Calculate fee for CLR (IAXC, IMXC, IRAR fee codes).
   */
  private static FeeCalculationResponse calculateFeeClr(FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity) {
    log.info("Calculate Immigration and Asylum (CLR) hourly rate fee");

    List<ValidationMessagesInner> validationMessages = new ArrayList<>();

    BigDecimal netProfitCosts = toBigDecimal(feeCalculationRequest.getNetProfitCosts());
    BigDecimal netDisbursementAmount = toBigDecimal(feeCalculationRequest.getNetDisbursementAmount());
    BigDecimal netCostOfCounsel = toBigDecimal(feeCalculationRequest.getNetCostOfCounsel());

    // total = net profit costs + net cost of counsel + net disbursements
    BigDecimal feeTotal = netProfitCosts.add(netCostOfCounsel).add(netDisbursementAmount);

    String feeCode = feeEntity.getFeeCode();
    if (IAXC.equals(feeCode) || IMXC.equals(feeCode)) {
      // Check total limit
      LimitContext totalLimitContext = new LimitContext(TOTAL, feeEntity.getTotalLimit(),
          feeCalculationRequest.getImmigrationPriorAuthorityNumber(), WARIA4_TOTAL_LIMIT);
      feeTotal = checkLimitAndCapIfExceeded(feeTotal, totalLimitContext, validationMessages);
    }

    checkFieldsAreEmpty(feeCalculationRequest, validationMessages);

    // VAT is calculated on net profit costs and net cost of counsel only
    BigDecimal feeWithoutDisbursements = netProfitCosts.add(netCostOfCounsel);
    BigDecimal calculatedVatAmount = VatUtil.getVatAmount(feeWithoutDisbursements,
        feeCalculationRequest.getStartDate(), feeCalculationRequest.getVatIndicator());

    BigDecimal disbursementVatAmount = toBigDecimal(feeCalculationRequest.getDisbursementVatAmount());

    BigDecimal totalAmount = feeTotal.add(calculatedVatAmount).add(disbursementVatAmount);

    FeeCalculation feeCalculation = buildFeeCalculation(feeCalculationRequest, totalAmount, calculatedVatAmount,
        netDisbursementAmount, disbursementVatAmount, feeTotal, netProfitCosts, true, null);

    return buildFeeCalculationResponse(feeCalculationRequest, feeEntity, validationMessages, feeCalculation);
  }

  /**
   * Calculate fee for CLR Interim (IACD, IAMD fee codes).
   */
  private static FeeCalculationResponse calculateFeeClrInterim(FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity) {
    log.info("Calculate Immigration and Asylum (CLR Interim) hourly rate fee");

    List<ValidationMessagesInner> validationMessages = new ArrayList<>();

    BigDecimal netProfitCosts = toBigDecimal(feeCalculationRequest.getNetProfitCosts());
    BigDecimal netDisbursementAmount = toBigDecimal(feeCalculationRequest.getNetDisbursementAmount());
    BigDecimal netCostOfCounsel = toBigDecimal(feeCalculationRequest.getNetCostOfCounsel());

    // total = net profit costs + net cost of counsel + net disbursements
    BigDecimal feeTotal = netProfitCosts.add(netCostOfCounsel).add(netDisbursementAmount);

    // Check total limit
    LimitContext totalLimitContext = new LimitContext(TOTAL, feeEntity.getTotalLimit(),
        feeCalculationRequest.getImmigrationPriorAuthorityNumber(), WARIA5_TOTAL_LIMIT);
    feeTotal = checkLimitAndCapIfExceeded(feeTotal, totalLimitContext, validationMessages);

    checkFieldsAreEmpty(feeCalculationRequest, validationMessages);

    // Add any bolts on to fee total
    BigDecimal boltsOnTotal = BigDecimal.ZERO;
    BoltOnFeeDetails boltOnFeeDetails = BoltOnUtil.calculateBoltOnAmounts(feeCalculationRequest, feeEntity);
    if (feeCalculationRequest.getBoltOns() != null) {
      if (Boolean.TRUE.equals(feeCalculationRequest.getBoltOns().getBoltOnSubstantiveHearing())) {
        BigDecimal substantiveHearingBoltOn = defaultToZeroIfNull(feeEntity.getSubstantiveHearingBoltOn());
        boltsOnTotal = toBigDecimal(boltOnFeeDetails.getBoltOnTotalFeeAmount()).add(substantiveHearingBoltOn);
        boltOnFeeDetails.setBoltOnTotalFeeAmount(toDouble(boltsOnTotal));
        boltOnFeeDetails.setBoltOnSubstantiveHearingFee(toDouble(substantiveHearingBoltOn));
      } else {
        boltsOnTotal = toBigDecimal(boltOnFeeDetails.getBoltOnTotalFeeAmount());
      }
    }

    BigDecimal feeTotalWithBoltsOn = feeTotal.add(boltsOnTotal);

    // VAT is calculated on net profit costs, net cost of counsel & bolt ons only
    BigDecimal feeWithoutDisbursements = netProfitCosts.add(netCostOfCounsel).add(boltsOnTotal);
    BigDecimal calculatedVatAmount = VatUtil.getVatAmount(feeWithoutDisbursements,
        feeCalculationRequest.getStartDate(), feeCalculationRequest.getVatIndicator());

    BigDecimal disbursementVatAmount = toBigDecimal(feeCalculationRequest.getDisbursementVatAmount());

    BigDecimal totalAmount = feeTotalWithBoltsOn.add(calculatedVatAmount).add(disbursementVatAmount);

    FeeCalculation feeCalculation = buildFeeCalculation(feeCalculationRequest, totalAmount, calculatedVatAmount,
        netDisbursementAmount, disbursementVatAmount, feeTotalWithBoltsOn, netProfitCosts, true, boltOnFeeDetails);

    return buildFeeCalculationResponse(feeCalculationRequest, feeEntity, validationMessages, feeCalculation);
  }

  private static boolean isLegalHelp(String feeCode) {
    return IAXL.equals(feeCode) || IMXL.equals(feeCode) || IA100.equals(feeCode);
  }

  private static boolean isClr(String feeCode) {
    return IAXC.equals(feeCode) || IMXC.equals(feeCode) || IRAR.equals(feeCode);
  }

  private static boolean isClrInterim(String feeCode) {
    return IACD.equals(feeCode) || IMCD.equals(feeCode);
  }

  private static void checkFieldsAreEmpty(FeeCalculationRequest feeCalculationRequest,
                                          List<ValidationMessagesInner> validationMessages) {
    log.info("Check detention travel waiting and costs field is empty for fee calculation");
    checkFieldIsEmpty(feeCalculationRequest.getDetentionTravelAndWaitingCosts(), validationMessages,
        WARIA9_DETENTION_TRAVEL_WAITING_COSTS, "Detention travel and waiting costs not applicable for legal help");

    log.info("Check JR form filling field is empty for fee calculation");
    checkFieldIsEmpty(feeCalculationRequest.getJrFormFilling(), validationMessages,
        WARIA10_JR_FORM_FILLING, "JR form filling not applicable for legal help");
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

  private static FeeCalculationResponse buildFeeCalculationResponse(FeeCalculationRequest feeCalculationRequest,
                                                                    FeeEntity feeEntity,
                                                                    List<ValidationMessagesInner> validationMessages,
                                                                    FeeCalculation feeCalculation) {
    return new FeeCalculationResponse().toBuilder()
        .feeCode(feeCalculationRequest.getFeeCode())
        .schemeId(feeEntity.getFeeScheme().getSchemeCode())
        .claimId(feeCalculationRequest.getClaimId())
        .validationMessages(validationMessages)
        .feeCalculation(feeCalculation)
        .build();
  }

  private static FeeCalculation buildFeeCalculation(FeeCalculationRequest feeCalculationRequest,
                                                    BigDecimal totalAmount,
                                                    BigDecimal calculatedVatAmount,
                                                    BigDecimal disbursementAmount,
                                                    BigDecimal disbursementVatAmount,
                                                    BigDecimal hourlyTotalAmount,
                                                    BigDecimal netProfitCostsAmount,
                                                    boolean includeCostOfCounsel,
                                                    BoltOnFeeDetails boltOnFeeDetails
  ) {
    return FeeCalculation.builder()
        .totalAmount(toDouble(totalAmount))
        .vatIndicator(feeCalculationRequest.getVatIndicator())
        .vatRateApplied(toDouble(VatUtil.getVatRateForDate(feeCalculationRequest.getStartDate())))
        .calculatedVatAmount(toDouble(calculatedVatAmount))
        .disbursementAmount(toDouble(disbursementAmount))
        .requestedNetDisbursementAmount(feeCalculationRequest.getNetDisbursementAmount())
        .disbursementVatAmount(toDouble(disbursementVatAmount))
        .hourlyTotalAmount(toDouble(hourlyTotalAmount))
        .netProfitCostsAmount(toDouble(netProfitCostsAmount))
        .requestedNetProfitCostsAmount(feeCalculationRequest.getNetProfitCosts())
        .netCostOfCounselAmount(includeCostOfCounsel ? feeCalculationRequest.getNetCostOfCounsel() : null)
        .boltOnFeeDetails(boltOnFeeDetails)
        .build();
  }
}