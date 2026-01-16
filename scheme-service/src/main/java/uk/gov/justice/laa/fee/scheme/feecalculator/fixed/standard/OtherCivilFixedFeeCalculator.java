package uk.gov.justice.laa.fee.scheme.feecalculator.fixed.standard;

import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.CLAIMS_PUBLIC_AUTHORITIES;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.CLINICAL_NEGLIGENCE;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.COMMUNITY_CARE;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.DEBT;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.HOUSING;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.HOUSING_HLPAS;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.MISCELLANEOUS;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.PUBLIC_LAW;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.WELFARE_BENEFITS;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil.buildValidationWarning;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.limit.LimitUtil.isEscapedCase;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toBigDecimal;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.enums.WarningType;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner;
import uk.gov.justice.laa.fee.scheme.service.VatRatesService;

/**
 * Calculate the Other Civil fee for a given fee entity and fee data.
 */
@Component
public class OtherCivilFixedFeeCalculator extends StandardFixedFeeCalculator {

  public OtherCivilFixedFeeCalculator(VatRatesService vatRatesService) {
    super(vatRatesService, true);
  }

  @Override
  public Set<CategoryType> getSupportedCategories() {
    return Set.of(CLAIMS_PUBLIC_AUTHORITIES, CLINICAL_NEGLIGENCE, COMMUNITY_CARE, DEBT,
        HOUSING, HOUSING_HLPAS, MISCELLANEOUS, PUBLIC_LAW, WELFARE_BENEFITS);
  }

  @Override
  protected boolean handleEscapeCase(FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity,
                                     List<ValidationMessagesInner> messages, BigDecimal totalAmount) {

    BigDecimal netProfitCosts = toBigDecimal(feeCalculationRequest.getNetProfitCosts());
    boolean escaped = isEscapedCase(netProfitCosts, feeEntity);

    if (escaped) {
      List<WarningType> warningTypes = WarningType.getByCategory(feeEntity.getCategoryType());
      if (warningTypes.isEmpty()) {
        throw new IllegalStateException("No warning codes found for category: " + feeEntity.getCategoryType());
      }
      messages.add(buildValidationWarning(warningTypes.getFirst(), "Fee total exceeds escape threshold limit"));
    }
    return escaped;
  }

}
