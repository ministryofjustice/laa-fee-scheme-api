package uk.gov.justice.laa.fee.scheme.feecalculator.utility;


import static uk.gov.justice.laa.fee.scheme.feecalculator.utility.NumberUtility.toDouble;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.model.BoltOn;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;

/**
 * Class for calculating bolt ons amount details.
 */
public class BoltOnUtility {

  private BoltOnUtility() {
  }

  /**
   * Where bolt on exists, 
   * Assign each boltOnXXXXXCount to number of bolt on type request.
   * Multiply count by bolt on fee for boltOnXXXXXXFee
   * Add each boltOnXXXXXXFee for a total, boltOnTotalFeeAmount 
   */
  public static BoltOn calculateBoltOnAmounts(FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity) {
    BoltOn boltOnTotal = BoltOn.builder().boltOnTotalFeeAmount(toDouble(BigDecimal.ZERO)).build();

    if (feeCalculationRequest.getBoltOns() != null) {

      List<BoltOnCalculation> calculations = Arrays.asList(
          new BoltOnCalculation("AdjournedHearing", feeCalculationRequest.getBoltOns().getBoltOnAdjournedHearing(),
              feeEntity.getAdjornHearingBoltOn()),
          new BoltOnCalculation("HomeOfficeInterview", feeCalculationRequest.getBoltOns().getBoltOnHomeOfficeInterview(),
              feeEntity.getHoInterviewBoltOn()),
          new BoltOnCalculation("CmrhOral", feeCalculationRequest.getBoltOns().getBoltOnCmrhOral(),
              feeEntity.getOralCmrhBoltOn()),
          new BoltOnCalculation("CmrhTelephone", feeCalculationRequest.getBoltOns().getBoltOnCmrhTelephone(),
              feeEntity.getTelephoneCmrhBoltOn())
      );

      BigDecimal totalFee = calculations.stream()
          .filter(i -> i.requested() != null && i.amount() != null)
          .map(i -> {
            BigDecimal total = BigDecimal.valueOf(i.requested()).multiply(i.amount());

            switch (i.name()) {
              case "AdjournedHearing" -> {
                boltOnTotal.setBoltOnAdjournedHearingCount(i.requested());
                boltOnTotal.setBoltOnAdjournedHearingFee(toDouble(total));
              }
              case "HomeOfficeInterview" -> {
                boltOnTotal.setBoltOnHomeOfficeInterviewCount(i.requested());
                boltOnTotal.setBoltOnHomeOfficeInterviewFee(toDouble(total));
              }
              case "CmrhOral" -> {
                boltOnTotal.setBoltOnCmrhOralCount(i.requested());
                boltOnTotal.setBoltOnCmrhOralFee(toDouble(total));
              }
              case "CmrhTelephone" -> {
                boltOnTotal.setBoltOnCmrhTelephoneCount(i.requested());
                boltOnTotal.setBoltOnCmrhTelephoneFee(toDouble(total));
              }
            }
            return total;
          })
          .reduce(BigDecimal.ZERO, BigDecimal::add);

      boltOnTotal.setBoltOnTotalFeeAmount(toDouble(totalFee));
    }

    return boltOnTotal;
  }

}
