package uk.gov.justice.laa.fee.scheme.feecalculator.hourly;

import static java.util.Objects.nonNull;
import static uk.gov.justice.laa.fee.scheme.enums.WarningType.WARN_IMM_ASYLM_DETENTION_TRAVEL;
import static uk.gov.justice.laa.fee.scheme.enums.WarningType.WARN_IMM_ASYLM_DISB_LEGAL_HELP;
import static uk.gov.justice.laa.fee.scheme.enums.WarningType.WARN_IMM_ASYLM_JR_FORM_FILLING;
import static uk.gov.justice.laa.fee.scheme.enums.WarningType.WARN_IMM_ASYLM_PRIOR_AUTH_CLR;
import static uk.gov.justice.laa.fee.scheme.enums.WarningType.WARN_IMM_ASYLM_PRIOR_AUTH_INTERIM;
import static uk.gov.justice.laa.fee.scheme.enums.WarningType.WARN_IMM_ASYLM_PRIOR_AUTH_LEGAL_HELP;
import static uk.gov.justice.laa.fee.scheme.enums.WarningType.WARN_IMM_ASYLM_SUM_OVER_LIMIT_LEGAL_HELP;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil.buildFeeCalculationResponse;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil.buildValidationWarning;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil.calculateVatAmount;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil.filterBoltOnFeeDetails;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.limit.LimitType.DISBURSEMENT;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.limit.LimitType.PROFIT_COST;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.limit.LimitType.TOTAL;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.limit.LimitUtil.checkLimitAndCapIfExceeded;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.defaultToZeroIfNull;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toBigDecimal;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toDouble;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toDoubleOrNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.enums.WarningType;
import uk.gov.justice.laa.fee.scheme.feecalculator.FeeCalculator;
import uk.gov.justice.laa.fee.scheme.feecalculator.util.boltons.BoltOnUtil;
import uk.gov.justice.laa.fee.scheme.feecalculator.util.limit.LimitContext;
import uk.gov.justice.laa.fee.scheme.model.BoltOnFeeDetails;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner;
import uk.gov.justice.laa.fee.scheme.service.VatRatesService;

/**
 * Calculate the Immigration and Asylum hourly rate fee for a given fee entity and fee calculation request.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ImmigrationAsylumHourlyRateCalculator implements FeeCalculator {

  private final VatRatesService vatRatesService;

  // Legal Help fee codes
  private static final String FEE_CODE_IAXL = "IAXL";
  private static final String FEE_CODE_IMXL = "IMXL";
  private static final String FEE_CODE_IA100 = "IA100";

  // CLR fee codes
  private static final String FEE_CODE_IAXC = "IAXC";
  private static final String FEE_CODE_IMXC = "IMXC";
  private static final String FEE_CODE_IRAR = "IRAR";

  // CLR Interim fee codes
  private static final String FEE_CODE_IACD = "IACD";
  private static final String FEE_CODE_IMCD = "IMCD";

  @Override
  public Set<CategoryType> getSupportedCategories() {
    return Set.of(); // Only used by ImmigrationAsylumFeeCalculator and not available via FeeCalculatorFactory
  }

  /**
   * Calculate fee based on the provided fee calculation request and fee entity.
   *
   * @param feeCalculationRequest the request containing fee calculation data
   * @param feeEntity             the fee entity containing fee details
   * @return FeeCalculationResponse with calculated fee
   */
  @Override
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
  private FeeCalculationResponse calculateFeeLegalHelp(FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity) {
    log.info("Calculate Immigration and Asylum (Legal Help) hourly rate fee");

    List<ValidationMessagesInner> validationMessages = new ArrayList<>();

    BigDecimal netProfitCosts = toBigDecimal(feeCalculationRequest.getNetProfitCosts());
    BigDecimal netDisbursementAmount = toBigDecimal(feeCalculationRequest.getNetDisbursementAmount());
    String immigrationPriorAuthorityNumber = feeCalculationRequest.getImmigrationPriorAuthorityNumber();

    String feeCode = feeEntity.getFeeCode();
    if (FEE_CODE_IAXL.equals(feeCode) || FEE_CODE_IMXL.equals(feeCode)) {
      // Check profit costs limit
      LimitContext profitCostsLimitContext = new LimitContext(PROFIT_COST, feeEntity.getProfitCostLimit(),
          immigrationPriorAuthorityNumber, WARN_IMM_ASYLM_PRIOR_AUTH_LEGAL_HELP);
      netProfitCosts = checkLimitAndCapIfExceeded(netProfitCosts, profitCostsLimitContext, validationMessages);

      // Check disbursement limit
      LimitContext disbursementLimitContext = new LimitContext(DISBURSEMENT, feeEntity.getDisbursementLimit(),
          immigrationPriorAuthorityNumber, WARN_IMM_ASYLM_DISB_LEGAL_HELP);
      netDisbursementAmount = checkLimitAndCapIfExceeded(netDisbursementAmount, disbursementLimitContext, validationMessages);
    }

    // total = net profit costs + net disbursements
    BigDecimal feeTotal = netProfitCosts.add(netDisbursementAmount);

    if (FEE_CODE_IA100.equals(feeCode)) {
      // Check total limit
      LimitContext totalLimitContext = new LimitContext(TOTAL, feeEntity.getTotalLimit(),
          immigrationPriorAuthorityNumber, WARN_IMM_ASYLM_SUM_OVER_LIMIT_LEGAL_HELP);
      feeTotal = checkLimitAndCapIfExceeded(feeTotal, totalLimitContext, validationMessages);
    }

    checkFieldsAreEmpty(feeCalculationRequest, validationMessages);


    // Calculate VAT if applicable
    LocalDate startDate = feeCalculationRequest.getStartDate();
    Boolean vatIndicator = feeCalculationRequest.getVatIndicator();
    BigDecimal vatRate = vatRatesService.getVatRateForDate(startDate, vatIndicator);
    // VAT is calculated on net profit costs only
    BigDecimal calculatedVatAmount = calculateVatAmount(netProfitCosts, vatRate);

    BigDecimal disbursementVatAmount = toBigDecimal(feeCalculationRequest.getDisbursementVatAmount());

    // Calculate total amount
    BigDecimal totalAmount = feeTotal.add(calculatedVatAmount).add(disbursementVatAmount);

    FeeCalculation feeCalculation = buildFeeCalculation(feeCalculationRequest, totalAmount, vatRate,
        calculatedVatAmount, netDisbursementAmount, feeTotal, netProfitCosts, false, null);

    return buildFeeCalculationResponse(feeCalculationRequest, feeEntity, feeCalculation, validationMessages);
  }

  /**
   * Calculate fee for CLR (IAXC, IMXC, IRAR fee codes).
   */
  private FeeCalculationResponse calculateFeeClr(FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity) {
    log.info("Calculate Immigration and Asylum (CLR) hourly rate fee");

    List<ValidationMessagesInner> validationMessages = new ArrayList<>();

    BigDecimal netProfitCosts = toBigDecimal(feeCalculationRequest.getNetProfitCosts());
    BigDecimal netDisbursementAmount = toBigDecimal(feeCalculationRequest.getNetDisbursementAmount());
    BigDecimal netCostOfCounsel = toBigDecimal(feeCalculationRequest.getNetCostOfCounsel());

    // total = net profit costs + net cost of counsel + net disbursements
    BigDecimal feeTotal = netProfitCosts.add(netCostOfCounsel).add(netDisbursementAmount);

    String feeCode = feeEntity.getFeeCode();
    if (FEE_CODE_IAXC.equals(feeCode) || FEE_CODE_IMXC.equals(feeCode)) {
      // Check total limit
      LimitContext totalLimitContext = new LimitContext(TOTAL, feeEntity.getTotalLimit(),
          feeCalculationRequest.getImmigrationPriorAuthorityNumber(), WARN_IMM_ASYLM_PRIOR_AUTH_CLR);
      feeTotal = checkLimitAndCapIfExceeded(feeTotal, totalLimitContext, validationMessages);
    }

    checkFieldsAreEmpty(feeCalculationRequest, validationMessages);

    // VAT is calculated on net profit costs and net cost of counsel only
    BigDecimal feeWithoutDisbursements = netProfitCosts.add(netCostOfCounsel);

    // Calculate VAT if applicable
    LocalDate startDate = feeCalculationRequest.getStartDate();
    Boolean vatIndicator = feeCalculationRequest.getVatIndicator();
    BigDecimal vatRate = vatRatesService.getVatRateForDate(startDate, vatIndicator);
    BigDecimal calculatedVatAmount = calculateVatAmount(feeWithoutDisbursements, vatRate);

    BigDecimal disbursementVatAmount = toBigDecimal(feeCalculationRequest.getDisbursementVatAmount());

    // Calculate total amount
    BigDecimal totalAmount = feeTotal.add(calculatedVatAmount).add(disbursementVatAmount);

    FeeCalculation feeCalculation = buildFeeCalculation(feeCalculationRequest, totalAmount, vatRate,
        calculatedVatAmount, netDisbursementAmount, feeTotal, netProfitCosts, true, null);

    return buildFeeCalculationResponse(feeCalculationRequest, feeEntity,  feeCalculation, validationMessages);
  }

  /**
   * Calculate fee for CLR Interim (IACD, IAMD fee codes).
   */
  private FeeCalculationResponse calculateFeeClrInterim(FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity) {
    log.info("Calculate Immigration and Asylum (CLR Interim) hourly rate fee");

    List<ValidationMessagesInner> validationMessages = new ArrayList<>();

    BigDecimal netProfitCosts = toBigDecimal(feeCalculationRequest.getNetProfitCosts());
    BigDecimal netDisbursementAmount = toBigDecimal(feeCalculationRequest.getNetDisbursementAmount());
    BigDecimal netCostOfCounsel = toBigDecimal(feeCalculationRequest.getNetCostOfCounsel());

    // total = net profit costs + net cost of counsel + net disbursements
    BigDecimal feeTotal = netProfitCosts.add(netCostOfCounsel).add(netDisbursementAmount);

    // Check total limit
    LimitContext totalLimitContext = new LimitContext(TOTAL, feeEntity.getTotalLimit(),
        feeCalculationRequest.getImmigrationPriorAuthorityNumber(), WARN_IMM_ASYLM_PRIOR_AUTH_INTERIM);
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

    // Calculate VAT if applicable
    LocalDate startDate = feeCalculationRequest.getStartDate();
    Boolean vatIndicator = feeCalculationRequest.getVatIndicator();
    BigDecimal vatRate = vatRatesService.getVatRateForDate(startDate, vatIndicator);
    BigDecimal calculatedVatAmount = calculateVatAmount(feeWithoutDisbursements, vatRate);

    BigDecimal disbursementVatAmount = toBigDecimal(feeCalculationRequest.getDisbursementVatAmount());

    // Calculate total amount
    BigDecimal totalAmount = feeTotalWithBoltsOn.add(calculatedVatAmount).add(disbursementVatAmount);

    FeeCalculation feeCalculation = buildFeeCalculation(feeCalculationRequest, totalAmount, vatRate,
        calculatedVatAmount, netDisbursementAmount, feeTotalWithBoltsOn, netProfitCosts, true, boltOnFeeDetails);

    return buildFeeCalculationResponse(feeCalculationRequest, feeEntity,  feeCalculation, validationMessages);
  }

  private boolean isLegalHelp(String feeCode) {
    return FEE_CODE_IAXL.equals(feeCode) || FEE_CODE_IMXL.equals(feeCode) || FEE_CODE_IA100.equals(feeCode);
  }

  private boolean isClr(String feeCode) {
    return FEE_CODE_IAXC.equals(feeCode) || FEE_CODE_IMXC.equals(feeCode) || FEE_CODE_IRAR.equals(feeCode);
  }

  private boolean isClrInterim(String feeCode) {
    return FEE_CODE_IACD.equals(feeCode) || FEE_CODE_IMCD.equals(feeCode);
  }

  private void checkFieldsAreEmpty(FeeCalculationRequest feeCalculationRequest,
                                          List<ValidationMessagesInner> validationMessages) {
    log.info("Check detention travel waiting and costs field is empty for fee calculation");
    checkFieldIsEmpty(feeCalculationRequest.getDetentionTravelAndWaitingCosts(), validationMessages,
        WARN_IMM_ASYLM_DETENTION_TRAVEL, "Detention travel and waiting costs not applicable for legal help");

    log.info("Check JR form filling field is empty for fee calculation");
    checkFieldIsEmpty(feeCalculationRequest.getJrFormFilling(), validationMessages,
        WARN_IMM_ASYLM_JR_FORM_FILLING, "JR form filling not applicable for legal help");
  }

  private void checkFieldIsEmpty(Double value, List<ValidationMessagesInner> validationMessages,
                                        WarningType warning, String logMessage) {
    if (value != null) {
      validationMessages.add(buildValidationWarning(warning, logMessage));
    }
  }

  private FeeCalculation buildFeeCalculation(FeeCalculationRequest feeCalculationRequest,
                                                    BigDecimal totalAmount,
                                                    BigDecimal vatRate,
                                                    BigDecimal calculatedVatAmount,
                                                    BigDecimal disbursementAmount,
                                                    BigDecimal hourlyTotalAmount,
                                                    BigDecimal netProfitCostsAmount,
                                                    boolean includeCostOfCounsel,
                                                    BoltOnFeeDetails boltOnFeeDetails) {
    return FeeCalculation.builder()
        .totalAmount(toDouble(totalAmount))
        .vatIndicator(feeCalculationRequest.getVatIndicator())
        .vatRateApplied(toDoubleOrNull(vatRate))
        .calculatedVatAmount(toDouble(calculatedVatAmount))
        .disbursementAmount(nonNull(feeCalculationRequest.getNetDisbursementAmount()) ? toDouble(disbursementAmount) : null)
        .requestedNetDisbursementAmount(feeCalculationRequest.getNetDisbursementAmount())
        .disbursementVatAmount(feeCalculationRequest.getDisbursementVatAmount())
        .hourlyTotalAmount(toDouble(hourlyTotalAmount))
        .netProfitCostsAmount(nonNull(feeCalculationRequest.getNetProfitCosts()) ? toDouble(netProfitCostsAmount) : null)
        .requestedNetProfitCostsAmount(feeCalculationRequest.getNetProfitCosts())
        .netCostOfCounselAmount(includeCostOfCounsel ? feeCalculationRequest.getNetCostOfCounsel() : null)
        .boltOnFeeDetails(filterBoltOnFeeDetails(boltOnFeeDetails))
        .build();
  }
}