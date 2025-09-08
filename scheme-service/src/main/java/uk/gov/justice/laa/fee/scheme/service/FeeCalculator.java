package uk.gov.justice.laa.fee.scheme.service;

import uk.gov.justice.laa.fee.scheme.feecalculator.type.CategoryType;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;

/**
 *  Fee Calculator interface to have declarations.
 */
public interface FeeCalculator {

  CategoryType getCategory();

  FeeCalculationResponse calculate(FeeCalculationRequest request);
  
}
