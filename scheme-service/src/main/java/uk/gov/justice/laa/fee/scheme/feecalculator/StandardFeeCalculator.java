package uk.gov.justice.laa.fee.scheme.feecalculator;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.feecalculator.utility.FeeCalculationUtility;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.service.DataService;
import uk.gov.justice.laa.fee.scheme.service.FeeCalculator;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.CLAIMS_PUBLIC_AUTHORITIES;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.CLINICAL_NEGLIGENCE;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.COMMUNITY_CARE;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.DEBT;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.HOUSING;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.HOUSING_HLPAS;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.MENTAL_HEALTH;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.MISCELLANEOUS;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.PUBLIC_LAW;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.WELFARE_BENEFITS;
/**
 * Calculate the fixed fee for a given fee entity and fee calculation request.
 */
@RequiredArgsConstructor
@Component
public class StandardFeeCalculator implements FeeCalculator {

  private final DataService dataService;

  @Override
  public Set<CategoryType> getSupportedCategories() {
    return Set.of(CLAIMS_PUBLIC_AUTHORITIES, CLINICAL_NEGLIGENCE, COMMUNITY_CARE, DEBT, HOUSING, HOUSING_HLPAS,
        MENTAL_HEALTH, MISCELLANEOUS, PUBLIC_LAW, WELFARE_BENEFITS
    );
  }
  /**
   * Calculated fee based on the provided fee entity and fee calculation request.
   *
   * @param feeCalculationRequest the request containing fee calculation data
   * @return FeeCalculationResponse with calculated fee
   */
  @Override
  public FeeCalculationResponse calculate(FeeCalculationRequest feeCalculationRequest) {

    FeeEntity feeEntity = dataService.getFeeEntity(feeCalculationRequest);

    return FeeCalculationUtility.calculate(feeEntity, feeCalculationRequest);
  }
}
