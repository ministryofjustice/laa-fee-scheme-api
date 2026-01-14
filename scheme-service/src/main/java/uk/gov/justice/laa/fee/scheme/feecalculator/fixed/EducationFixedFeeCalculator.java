package uk.gov.justice.laa.fee.scheme.feecalculator.fixed;

import static uk.gov.justice.laa.fee.scheme.enums.WarningType.WARN_EDUCATION_ESCAPE_THRESHOLD;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil.buildValidationWarning;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.limit.LimitUtil.isEscapedCase;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toBigDecimal;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner;
import uk.gov.justice.laa.fee.scheme.service.VatRatesService;

/**
 * Calculate Education fee for a given fee entity and fee data.
 */
@Slf4j
@Component
public class EducationFixedFeeCalculator extends BaseFixedFeeCalculator {

  public EducationFixedFeeCalculator(VatRatesService vatRatesService) {
    super(vatRatesService);
  }

  @Override
  public Set<CategoryType> getSupportedCategories() {
    return Set.of();
  }

  @Override
  protected boolean canEscape() {
    return true;
  }

  @Override
  protected boolean handleEscapeCase(FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity,
                                     List<ValidationMessagesInner> messages, BigDecimal totalAmount) {

    BigDecimal netProfitCosts = toBigDecimal(feeCalculationRequest.getNetProfitCosts());
    boolean escaped = isEscapedCase(netProfitCosts, feeEntity);

    if (escaped) {
      messages.add(buildValidationWarning(WARN_EDUCATION_ESCAPE_THRESHOLD, "Fee total exceeds escape threshold limit"));
    }
    return escaped;
  }
}
