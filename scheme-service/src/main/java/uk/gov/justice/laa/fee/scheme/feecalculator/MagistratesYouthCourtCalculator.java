package uk.gov.justice.laa.fee.scheme.feecalculator;

import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.MAGISTRATES_COURT;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.YOUTH_COURT;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil.isCourtDesignated;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.feecalculator.fixed.DesignatedCourtFixedFeeCalculator;
import uk.gov.justice.laa.fee.scheme.feecalculator.fixed.UndesignatedCourtFixedFeeCalculator;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;

/**
 * Implementation class for Magistrates / Youth Court fee category (Fixed only).
 */
@RequiredArgsConstructor
@Component
public class MagistratesYouthCourtCalculator implements FeeCalculator {

  private final DesignatedCourtFixedFeeCalculator designatedCourtFixedFeeCalculator;

  private final UndesignatedCourtFixedFeeCalculator undesignatedCourtFixedFeeCalculator;

  @Override
  public Set<CategoryType> getSupportedCategories() {
    return Set.of(
        MAGISTRATES_COURT, YOUTH_COURT
    );
  }

  /**
   * Determines the calculation based on Court Designation Type.
   */
  @Override
  public FeeCalculationResponse calculate(FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity) {

    if (isCourtDesignated(feeEntity.getCourtDesignationType())) {
      return designatedCourtFixedFeeCalculator.calculate(feeCalculationRequest, feeEntity);
    } else {
      return undesignatedCourtFixedFeeCalculator.calculate(feeCalculationRequest, feeEntity);
    }
  }

}
