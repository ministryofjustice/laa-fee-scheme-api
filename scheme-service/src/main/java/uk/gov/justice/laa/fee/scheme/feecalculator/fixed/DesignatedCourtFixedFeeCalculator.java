package uk.gov.justice.laa.fee.scheme.feecalculator.fixed;

import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.service.VatRatesService;

/**
 * Calculate the designated magistrates or youth court fee for a given fee entity and fee calculation request.
 */
@Slf4j
@Component
public class DesignatedCourtFixedFeeCalculator extends BaseFixedFeeCalculator {

  public DesignatedCourtFixedFeeCalculator(VatRatesService vatRatesService) {
    super(vatRatesService);
  }

  @Override
  public Set<CategoryType> getSupportedCategories() {
    return Set.of();
  }

}
