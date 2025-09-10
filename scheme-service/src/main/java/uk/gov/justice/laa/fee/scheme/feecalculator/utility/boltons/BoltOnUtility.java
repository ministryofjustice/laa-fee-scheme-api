package uk.gov.justice.laa.fee.scheme.feecalculator.utility.boltons;


import static uk.gov.justice.laa.fee.scheme.feecalculator.utility.NumberUtility.toDouble;
import static uk.gov.justice.laa.fee.scheme.feecalculator.utility.boltons.BoltOnType.ADJOURNED_HEARING;
import static uk.gov.justice.laa.fee.scheme.feecalculator.utility.boltons.BoltOnType.CMRH_ORAL;
import static uk.gov.justice.laa.fee.scheme.feecalculator.utility.boltons.BoltOnType.CMRH_TELEPHONE;
import static uk.gov.justice.laa.fee.scheme.feecalculator.utility.boltons.BoltOnType.HOME_OFFICE_INTERVIEW;

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
          new BoltOnCalculation(ADJOURNED_HEARING, feeCalculationRequest.getBoltOns().getBoltOnAdjournedHearing(),
              feeEntity.getAdjornHearingBoltOn()),
          new BoltOnCalculation(HOME_OFFICE_INTERVIEW, feeCalculationRequest.getBoltOns().getBoltOnHomeOfficeInterview(),
              feeEntity.getHoInterviewBoltOn()),
          new BoltOnCalculation(CMRH_ORAL, feeCalculationRequest.getBoltOns().getBoltOnCmrhOral(),
              feeEntity.getOralCmrhBoltOn()),
          new BoltOnCalculation(CMRH_TELEPHONE, feeCalculationRequest.getBoltOns().getBoltOnCmrhTelephone(),
              feeEntity.getTelephoneCmrhBoltOn())
      );

      BigDecimal totalFee = calculations.stream()
          .filter(i -> i.requested() != null && i.amount() != null)
          .map(i -> {
            BigDecimal total = BigDecimal.valueOf(i.requested()).multiply(i.amount());

            switch (i.type()) {
              case ADJOURNED_HEARING -> {
                boltOnTotal.setBoltOnAdjournedHearingCount(i.requested());
                boltOnTotal.setBoltOnAdjournedHearingFee(toDouble(total));
              }
              case HOME_OFFICE_INTERVIEW -> {
                boltOnTotal.setBoltOnHomeOfficeInterviewCount(i.requested());
                boltOnTotal.setBoltOnHomeOfficeInterviewFee(toDouble(total));
              }
              case CMRH_ORAL -> {
                boltOnTotal.setBoltOnCmrhOralCount(i.requested());
                boltOnTotal.setBoltOnCmrhOralFee(toDouble(total));
              }
              case CMRH_TELEPHONE -> {
                boltOnTotal.setBoltOnCmrhTelephoneCount(i.requested());
                boltOnTotal.setBoltOnCmrhTelephoneFee(toDouble(total));
              }
              default -> { }
            }
            return total;
          })
          .reduce(BigDecimal.ZERO, BigDecimal::add);

      boltOnTotal.setBoltOnTotalFeeAmount(toDouble(totalFee));
    }

    return boltOnTotal;
  }

}
