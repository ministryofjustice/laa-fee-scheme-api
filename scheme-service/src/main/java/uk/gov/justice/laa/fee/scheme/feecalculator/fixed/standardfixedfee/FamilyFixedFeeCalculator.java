package uk.gov.justice.laa.fee.scheme.feecalculator.fixed.standardfixedfee;

import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.FAMILY;
import static uk.gov.justice.laa.fee.scheme.enums.WarningType.WARN_FAMILY_ESCAPE_THRESHOLD;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil.buildValidationWarning;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.limit.LimitUtil.isEscapedCase;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toBigDecimal;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner;
import uk.gov.justice.laa.fee.scheme.service.VatRatesService;

/**
 * Calculate the fixed fee for a given fee entity and fee calculation request.
 */
@Component
public class FamilyFixedFeeCalculator extends StandardFixedFeeCalculator {

  public FamilyFixedFeeCalculator(VatRatesService vatRatesService) {
    super(vatRatesService, true);
  }

  @Override
  public Set<CategoryType> getSupportedCategories() {
    return Set.of(FAMILY);
  }

  @Override
  protected boolean handleEscapeCase(FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity,
                                     List<ValidationMessagesInner> validationMessages, BigDecimal totalAmount) {

    boolean escaped = false;
    BigDecimal escapeThresholdLimit = feeEntity.getEscapeThresholdLimit();

    if (escapeThresholdLimit != null) {
      BigDecimal netProfitCosts = toBigDecimal(feeCalculationRequest.getNetProfitCosts());
      escaped = isEscapedCase(totalAmount.add(netProfitCosts), feeEntity);

      if (escaped) {
        validationMessages.add(buildValidationWarning(WARN_FAMILY_ESCAPE_THRESHOLD,
            "Fee total exceeds escape threshold limit"));
      }
    }

    return escaped;
  }
}
