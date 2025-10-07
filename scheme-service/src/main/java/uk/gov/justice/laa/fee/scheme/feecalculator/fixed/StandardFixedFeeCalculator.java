package uk.gov.justice.laa.fee.scheme.feecalculator.fixed;

import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.ASSOCIATED_CIVIL;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.FAMILY;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.MENTAL_HEALTH;

import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.feecalculator.FeeCalculator;
import uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;

/**
 * Calculate the fixed fee for a given fee entity and fee calculation request.
 */
@Slf4j
@Component
public class StandardFixedFeeCalculator implements FeeCalculator {

  @Override
  public Set<CategoryType> getSupportedCategories() {
    return Set.of(ASSOCIATED_CIVIL, FAMILY, MENTAL_HEALTH);
  }

  /**
   * Calculated fee based on the provided fee entity and fee calculation request.
   *
   * @param feeCalculationRequest the request containing fee calculation data
   * @return FeeCalculationResponse with calculated fee
   */
  @Override
  public FeeCalculationResponse calculate(FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity) {

    log.info("Calculate Standard fixed fee");

    return FeeCalculationUtil.calculate(feeCalculationRequest, feeEntity);
  }
}
