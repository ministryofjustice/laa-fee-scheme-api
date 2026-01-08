package uk.gov.justice.laa.fee.scheme.feecalculator.fixed;

import static uk.gov.justice.laa.fee.scheme.enums.WarningType.WARN_EDUCATION_ESCAPE_THRESHOLD;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.limit.LimitUtil.isEscapedCase;

import java.math.BigDecimal;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.enums.WarningType;
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
  protected boolean determineEscapeCase(BigDecimal netProfitCosts, FeeEntity feeEntity) {
    return isEscapedCase(netProfitCosts, feeEntity);
  }

  @Override
  protected WarningType getEscapeWarningCode() {
    return WARN_EDUCATION_ESCAPE_THRESHOLD;
  }

  @Override
  protected String getEscapeWarningMessage() {
    return "Fee total exceeds escape threshold limit";
  }
}
