package uk.gov.justice.laa.fee.scheme.feecalculator.fixed.standard;

import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.INQUEST;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil.getCaseConcludedDate;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toBigDecimal;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner;
import uk.gov.justice.laa.fee.scheme.service.VatRatesService;

/**
 * Calculate Inquest fee for a given fee entity and fee data.
 */
@Component
public class InquestFixedFeeCalculator extends StandardFixedFeeCalculator {

  public InquestFixedFeeCalculator(VatRatesService vatRatesService) {
    super(vatRatesService, true);
  }

  @Override
  public Set<CategoryType> getSupportedCategories() {
    return Set.of(INQUEST);
  }


  @Override
  protected BigDecimal capDisbursementVat(FeeCalculationRequest feeCalculationRequest,
                                          VatRatesService vatRatesService,
                                          List<ValidationMessagesInner> validationMessages) {

    BigDecimal netDisbursementAmount = toBigDecimal(feeCalculationRequest.getNetDisbursementAmount());
    BigDecimal disbursementVatAmount = toBigDecimal(feeCalculationRequest.getDisbursementVatAmount());

    // Calculate disbursed vat amount
    BigDecimal disbursementVatRate = vatRatesService.getVatRateForDate(
            getCaseConcludedDate(feeCalculationRequest), true);
    disbursementVatAmount =
            FeeCalculationUtil.capDisbursementVat(
                    netDisbursementAmount, disbursementVatAmount, disbursementVatRate, validationMessages);

    return disbursementVatAmount;
  }
}
