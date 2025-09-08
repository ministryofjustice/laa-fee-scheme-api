package uk.gov.justice.laa.fee.scheme.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.fee.scheme.feecalculator.type.CategoryType;

/**
 * Factory class to return category specific calculators.
 */
@Component
public class FeeCalculatorFactory {

  private final Map<CategoryType, FeeCalculator> calculators;

  public FeeCalculatorFactory(List<FeeCalculator> calculators) {
    this.calculators = calculators.stream()
        .collect(Collectors.toMap(FeeCalculator::getCategory, c -> c));
  }

  public FeeCalculator getCalculator(CategoryType category) {
    return calculators.get(category);
  }
}

