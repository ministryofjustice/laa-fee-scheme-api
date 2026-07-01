package uk.gov.justice.laa.fee.scheme.feecalculator.fixed.standard;

import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toBigDecimal;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner;
import uk.gov.justice.laa.fee.scheme.service.VatRatesService;

/**
 * Calculate the Sending Hearing fee for a given fee entity and fee calculation request.
 */
@Slf4j
@Component
public class SendingHearingFixedFeeCalculator extends StandardFixedFeeCalculator {

  public SendingHearingFixedFeeCalculator(VatRatesService vatRatesService) {
    super(vatRatesService, false);
  }

  @Override
  public Set<CategoryType> getSupportedCategories() {
    return Set.of(CategoryType.SENDING_HEARING);
  }

  @Override
  protected BigDecimal capDisbursementVat(FeeCalculationRequest feeCalculationRequest,
                                          VatRatesService vatRatesService,
                                          List<ValidationMessagesInner> validationMessages) {

    BigDecimal netDisbursementAmount = toBigDecimal(feeCalculationRequest.getNetDisbursementAmount());
    BigDecimal disbursementVatAmount = toBigDecimal(feeCalculationRequest.getDisbursementVatAmount());

    // Calculate disbursed vat amount
    BigDecimal disbursementVatRate = vatRatesService.getVatRateForRequest(feeCalculationRequest, Boolean.TRUE);
    disbursementVatAmount =
        FeeCalculationUtil.capDisbursementVat(
            netDisbursementAmount, disbursementVatAmount, disbursementVatRate, validationMessages);

    return disbursementVatAmount;
  }

}
