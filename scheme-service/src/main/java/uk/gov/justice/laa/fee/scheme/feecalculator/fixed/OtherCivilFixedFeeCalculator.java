package uk.gov.justice.laa.fee.scheme.feecalculator.fixed;

import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.CLAIMS_PUBLIC_AUTHORITIES;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.CLINICAL_NEGLIGENCE;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.COMMUNITY_CARE;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.DEBT;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.HOUSING;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.HOUSING_HLPAS;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.MISCELLANEOUS;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.PUBLIC_LAW;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.WELFARE_BENEFITS;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.limit.LimitUtil.isEscapedCase;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toBigDecimal;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.enums.WarningType;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.service.VatRatesService;

/**
 * Calculate the Other Civil fee for a given fee entity and fee data.
 */
@Slf4j
@Component
public class OtherCivilFixedFeeCalculator extends BaseFixedFeeCalculator {

  public OtherCivilFixedFeeCalculator(VatRatesService vatRatesService) {
    super(vatRatesService);
  }

  @Override
  public Set<CategoryType> getSupportedCategories() {
    return Set.of(CLAIMS_PUBLIC_AUTHORITIES, CLINICAL_NEGLIGENCE, COMMUNITY_CARE, DEBT,
        HOUSING, HOUSING_HLPAS, MISCELLANEOUS, PUBLIC_LAW, WELFARE_BENEFITS);
  }

  @Override
  protected boolean canEscape() {
    return true;
  }

  @Override
  protected boolean determineEscapeCase(FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity) {
    BigDecimal netProfitCosts = toBigDecimal(feeCalculationRequest.getNetProfitCosts());
    return isEscapedCase(netProfitCosts, feeEntity);
  }

  @Override
  protected WarningType getEscapeWarningCode(FeeEntity feeEntity) {
    List<WarningType> warningTypes = WarningType.getByCategory(feeEntity.getCategoryType());
    if (warningTypes.isEmpty()) {
      throw new IllegalStateException("No warning codes found for category: " + feeEntity.getCategoryType());
    }
    return  warningTypes.getFirst();
  }

  @Override
  protected String getEscapeWarningMessage() {
    return "Fee total exceeds escape threshold limit";
  }

}
