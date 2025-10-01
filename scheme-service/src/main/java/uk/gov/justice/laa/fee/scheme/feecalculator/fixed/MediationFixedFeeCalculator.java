package uk.gov.justice.laa.fee.scheme.feecalculator.fixed;

import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.MEDIATION;

import java.math.BigDecimal;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.exception.InvalidMediationSessionException;
import uk.gov.justice.laa.fee.scheme.feecalculator.FeeCalculator;
import uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;

/**
 * Calculate the mediation fee for a given fee entity and fee data.
 */
@Slf4j
@Component
public class MediationFixedFeeCalculator implements FeeCalculator {

  @Override
  public Set<CategoryType> getSupportedCategories() {
    return Set.of(MEDIATION);
  }

  /**
   * Determines whether the calculation should include mediation sessions based presence of numberOfMediationSessions.
   */
  @Override
  public FeeCalculationResponse calculate(FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity) {

    log.info("Calculate Mediation fixed fee");

    if (feeEntity.getFixedFee() == null) {
      log.info("Using numberOfMediationSessions to calculate Mediation fee");
      // Where fee code type is MED numberOfMediationSessions is required, numberOfMediationSessions will determine fixed fee amount.
      return getCalculationWithMediationSessions(feeEntity, feeCalculationRequest);
    } else {
      log.info("Using fixed fee to calculate Mediation fee");
      // Where fee code type is MAM numberOfMediationSessions is not required, and will be omitted from calculation
      return FeeCalculationUtil.calculate(feeEntity, feeCalculationRequest);
    }
  }

  /**
   * Gets fixed fee depending on number if mediation sessions.
   */
  private static FeeCalculationResponse getCalculationWithMediationSessions(FeeEntity feeEntity,
                                                                            FeeCalculationRequest feeData) {
    log.info("Check numberOfMediationSessions is valid");
    Integer numberOfMediationSessions = feeData.getNumberOfMediationSessions();
    if (numberOfMediationSessions == null || numberOfMediationSessions <= 0) {
      throw new InvalidMediationSessionException(feeEntity.getFeeCode());
    }

    BigDecimal baseFee = (numberOfMediationSessions == 1) ? feeEntity.getMediationFeeLower() : feeEntity.getMediationFeeHigher();

    return FeeCalculationUtil.calculate(baseFee, feeData, feeEntity);
  }

}