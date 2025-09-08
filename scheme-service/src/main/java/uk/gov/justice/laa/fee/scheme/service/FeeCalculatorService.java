package uk.gov.justice.laa.fee.scheme.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.fee.scheme.feecalculator.type.CategoryType;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;

/**
 * Calculate fee for a given fee calculation request.
 */
@RequiredArgsConstructor
@Service
public class FeeCalculatorService {

  private final FeeCalculatorFactory calculatorFactory;

  /**
   * Calculate Fees.
   *
   * @param request FeeCalculationRequest
   * @return FeeCalculationResponse
   */
  public FeeCalculationResponse calculateFee(FeeCalculationRequest request) {
    CategoryType categoryType = CategoryType.valueOf(request.getAreaOfLaw());
    FeeCalculator calculator = calculatorFactory.getCalculator(categoryType);
    return calculator.calculate(request);
  }
}
