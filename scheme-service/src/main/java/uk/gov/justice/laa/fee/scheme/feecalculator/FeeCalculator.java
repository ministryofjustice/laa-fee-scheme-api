package uk.gov.justice.laa.fee.scheme.feecalculator;

import java.util.Set;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;

/**
 *  Fee Calculator interface to have declarations.
 */
public interface FeeCalculator {

  Set<CategoryType> getSupportedCategories();

  FeeCalculationResponse calculate(FeeCalculationRequest request, FeeEntity feeEntity);
  
}
