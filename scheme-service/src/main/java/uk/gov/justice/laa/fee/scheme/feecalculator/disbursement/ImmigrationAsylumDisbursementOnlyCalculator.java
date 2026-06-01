package uk.gov.justice.laa.fee.scheme.feecalculator.disbursement;

import static java.util.Objects.nonNull;
import static uk.gov.justice.laa.fee.scheme.enums.WarningType.WARN_IMM_ASYLM_DISB_ONLY;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil.buildFeeCalculationResponse;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil.calculateTotalAmount;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil.getFeeClaimStartDate;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil.validateAndCapDisbursementVat;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.limit.LimitType.DISBURSEMENT;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.limit.LimitUtil.checkLimitAndCapIfExceeded;
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
import uk.gov.justice.laa.fee.scheme.feecalculator.FeeCalculator;
import uk.gov.justice.laa.fee.scheme.feecalculator.util.limit.LimitContext;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner;
import uk.gov.justice.laa.fee.scheme.service.VatRatesService;

/**
 * Calculate the Immigration and asylum disbursement only fee for a given fee entity and fee calculation request.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ImmigrationAsylumDisbursementOnlyCalculator implements FeeCalculator {

  private final VatRatesService vatRatesService;

  @Override
  public Set<CategoryType> getSupportedCategories() {
    return Set.of();
  }

  /**
   * Calculated fee for Immigration and asylum disbursement only fee based on the provided fee entity and fee calculation request.
   */
  @Override
  public FeeCalculationResponse calculate(FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity) {

    log.info("Calculate Immigration and Asylum disbursements only");

    BigDecimal requestedNetDisbursementAmount = toBigDecimal(feeCalculationRequest.getNetDisbursementAmount());
    BigDecimal requestedDisbursementVatAmount = toBigDecimal(feeCalculationRequest.getDisbursementVatAmount());
    String immigrationPriorAuthorityNumber = feeCalculationRequest.getImmigrationPriorAuthorityNumber();

    List<ValidationMessagesInner> validationMessages = new ArrayList<>();
    LimitContext disbursementLimitContext = new LimitContext(DISBURSEMENT, feeEntity.getDisbursementLimit(),
        immigrationPriorAuthorityNumber, WARN_IMM_ASYLM_DISB_ONLY);
    BigDecimal netDisbursementAmount = checkLimitAndCapIfExceeded(requestedNetDisbursementAmount,
        disbursementLimitContext, validationMessages);

    // Calculate VAT rate for disbursement VAT cap (apply against capped net disbursement amount)
    LocalDate claimStartDate = getFeeClaimStartDate(feeEntity.getCategoryType(), feeCalculationRequest);
    Boolean vatIndicator = feeCalculationRequest.getVatIndicator();
    BigDecimal vatRate = vatRatesService.getVatRateForDate(claimStartDate, vatIndicator);

    // Validate and cap disbursement VAT
    BigDecimal disbursementVatAmount = validateAndCapDisbursementVat(
        netDisbursementAmount, requestedDisbursementVatAmount, vatRate, validationMessages);

    BigDecimal totalAmount = calculateTotalAmount(netDisbursementAmount, disbursementVatAmount);

    FeeCalculation feeCalculation = FeeCalculation.builder()
        .totalAmount(toDouble(totalAmount))
        .disbursementAmount(nonNull(feeCalculationRequest.getNetDisbursementAmount()) ? toDouble(netDisbursementAmount) : null)
        .requestedNetDisbursementAmount(feeCalculationRequest.getNetDisbursementAmount())
        .disbursementVatAmount(toDoubleOrNull(disbursementVatAmount))
        .requestedDisbursementVatAmount(feeCalculationRequest.getDisbursementVatAmount())
        .build();

    return buildFeeCalculationResponse(feeCalculationRequest, feeEntity, feeCalculation, validationMessages);
  }
}
