package uk.gov.justice.laa.fee.scheme.feecalculator;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;

/**
 * Factory class to return category specific calculators.
 */
@Slf4j
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

  /**
   * Get fee calculator for given category.
   *
   * @param category the category
   * @return the fee calculator
   */
  public FeeCalculator getCalculator(CategoryType category) {
    log.info("Get fee calculator for category: {}", category);

    return calculators.get(category);
  }
}

