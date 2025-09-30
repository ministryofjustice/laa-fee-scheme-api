package uk.gov.justice.laa.fee.scheme.feecalculator;

import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.MAGS_COURT_DESIGNATED;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.MAGS_COURT_UNDESIGNATED;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.YOUTH_COURT_DESIGNATED;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.YOUTH_COURT_UNDESIGNATED;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.feecalculator.fixed.MagsYouthCourtDesignatedFeeCalculator;
import uk.gov.justice.laa.fee.scheme.feecalculator.fixed.MagsYouthCourtUndesignatedFeeCalculator;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;

/**
 * Determine which type of calculation to perform, designated or undesignated.
 */
@RequiredArgsConstructor
@Component
public class MagsYouthCourtFeeCalculator implements  FeeCalculator {


  private final MagsYouthCourtDesignatedFeeCalculator magsYouthCourtDesignatedFeeCalculator;
  private final MagsYouthCourtUndesignatedFeeCalculator magsYouthCourtUndesignatedFeeCalculator;

  @Override
  public Set<CategoryType> getSupportedCategories() {
    return Set.of(
        MAGS_COURT_DESIGNATED,
        MAGS_COURT_UNDESIGNATED,
        YOUTH_COURT_DESIGNATED,
        YOUTH_COURT_UNDESIGNATED
    );
  }

  /**
   * Determines the calculation based on category type.
   */
  @Override
  public FeeCalculationResponse calculate(FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity) {

    return switch (feeEntity.getCategoryType()) {
      case MAGS_COURT_DESIGNATED, YOUTH_COURT_DESIGNATED ->
          magsYouthCourtDesignatedFeeCalculator.calculate(feeCalculationRequest, feeEntity);
      case MAGS_COURT_UNDESIGNATED, YOUTH_COURT_UNDESIGNATED ->
          magsYouthCourtUndesignatedFeeCalculator.calculate(feeCalculationRequest, feeEntity);
      default -> throw new IllegalStateException("Unexpected category: " + feeEntity.getCategoryType());
    };
  }
}
