package uk.gov.justice.laa.fee.scheme.feecalculator.utility;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;

public class BoltOnUtility {

  public static BigDecimal calculateBoltOnAmount(FeeCalculationRequest feeData, FeeEntity feeEntity) {

    if (feeData.getBoltOns() != null) {
      List<BoltOnPair> boltOns = Arrays.asList(
          // add rest of boltons, currently has immigration and asylum
          new BoltOnPair(feeData.getBoltOns().getBoltOnAdjournedHearing(), feeEntity.getAdjornHearingBoltOn()),
          new BoltOnPair(feeData.getBoltOns().getBoltOnHomeOfficeInterview(), feeEntity.getHoInterviewBoltOn()),
          new BoltOnPair(feeData.getBoltOns().getBoltOnCmrhOral(), feeEntity.getOralCmrhBoltOn()),
          new BoltOnPair(feeData.getBoltOns().getBoltOnCrmhTelephone(), feeEntity.getTelephoneCmrhBoltOn())
      );

      return boltOns.stream()
          .filter(i -> i.boltOnAmount != null && i.numberOfBoltOns != null)
          .map(i -> BigDecimal.valueOf(i.numberOfBoltOns).multiply(i.boltOnAmount))
          .reduce(BigDecimal.ZERO, BigDecimal::add);
    } else return BigDecimal.ZERO;
  }
}
