package uk.gov.justice.laa.fee.scheme.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;

/**
 * Factory class to return category specific calculators.
 */
@Component
public class FeeCalculatorFactory {

  private final Map<CategoryType, FeeCalculator> calculators;

  /**
   *  Loading all Category Fee Calculators.
   *
   * @param calculators List
   */

  public FeeCalculatorFactory(List<FeeCalculator> calculators) {
    this.calculators = calculators.stream()
        .flatMap(feeCalculator -> feeCalculator.getSupportedCategories().stream()
            .map(categoryType -> Map.entry(categoryType, feeCalculator)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  public FeeCalculator getCalculator(CategoryType category) {
    return calculators.get(category);
  }
}

