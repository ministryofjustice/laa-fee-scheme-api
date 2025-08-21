package uk.gov.justice.laa.fee.scheme.feecalculator.utility;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;

/**
 * Class for calculating total value of bolt ons.
 */
public class BoltOnUtility {

  /**
   * Where bolt on exists, multiply bolt types value by number requested.
   */
  public static BigDecimal calculateBoltOnAmount(FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity) {

    if (feeCalculationRequest.getBoltOns() != null) {
      List<BoltOnPair> boltOns = Arrays.asList(
          new BoltOnPair(feeCalculationRequest.getBoltOns().getBoltOnAdjournedHearing(), feeEntity.getAdjornHearingBoltOn()),
          new BoltOnPair(feeCalculationRequest.getBoltOns().getBoltOnHomeOfficeInterview(), feeEntity.getHoInterviewBoltOn()),
          new BoltOnPair(feeCalculationRequest.getBoltOns().getBoltOnCmrhOral(), feeEntity.getOralCmrhBoltOn()),
          new BoltOnPair(feeCalculationRequest.getBoltOns().getBoltOnCrmhTelephone(), feeEntity.getTelephoneCmrhBoltOn())
      );

      return boltOns.stream()
          .filter(i -> i.boltOnAmount() != null && i.numberOfBoltOns() != null)
          .map(i -> BigDecimal.valueOf(i.numberOfBoltOns()).multiply(i.boltOnAmount()))
          .reduce(BigDecimal.ZERO, BigDecimal::add);
    } else {
      return BigDecimal.ZERO;
    }
  }
}
