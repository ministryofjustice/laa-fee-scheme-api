package uk.gov.justice.laa.fee.scheme.feecalculator.util.boltons;

import static uk.gov.justice.laa.fee.scheme.feecalculator.util.boltons.BoltOnType.ADJOURNED_HEARING;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.boltons.BoltOnType.CMRH_ORAL;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.boltons.BoltOnType.CMRH_TELEPHONE;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.boltons.BoltOnType.HOME_OFFICE_INTERVIEW;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toDouble;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.model.BoltOnFeeDetails;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;

/**
 * Class for calculating bolt ons amount details.
 */
@Slf4j
public final class BoltOnUtil {

  private BoltOnUtil() {
  }

  /**
   * Where bolt on exists, 
   * Assign each boltOnXXXXXCount to number of bolt on type requested.
   * Multiply count by bolt on fee for boltOnXXXXXXFee
   * Add each boltOnXXXXXXFee for a total, boltOnTotalFeeAmount 
   */
  @SuppressWarnings("checkstyle:MissingSwitchDefault")
  public static BoltOnFeeDetails calculateBoltOnAmounts(FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity) {
    BoltOnFeeDetails boltOnFeeDetails = BoltOnFeeDetails.builder().build();

    if (feeCalculationRequest.getBoltOns() != null) {

      log.info("Calculate bolt on amounts for fee calculation");

      List<BoltOnCalculation> calculations = getBoltOnCalculations(feeCalculationRequest, feeEntity);

      BigDecimal totalFee = calculations.stream()
          .filter(i -> i.requested() != null && i.amount() != null)
          .map(i -> {
            BigDecimal total = BigDecimal.valueOf(i.requested()).multiply(i.amount());

            switch (i.type()) {
              case ADJOURNED_HEARING -> {
                boltOnFeeDetails.setBoltOnAdjournedHearingCount(i.requested());
                boltOnFeeDetails.setBoltOnAdjournedHearingFee(toDouble(total));
              }
              case HOME_OFFICE_INTERVIEW -> {
                boltOnFeeDetails.setBoltOnHomeOfficeInterviewCount(i.requested());
                boltOnFeeDetails.setBoltOnHomeOfficeInterviewFee(toDouble(total));
              }
              case CMRH_ORAL -> {
                boltOnFeeDetails.setBoltOnCmrhOralCount(i.requested());
                boltOnFeeDetails.setBoltOnCmrhOralFee(toDouble(total));
              }
              case CMRH_TELEPHONE -> {
                boltOnFeeDetails.setBoltOnCmrhTelephoneCount(i.requested());
                boltOnFeeDetails.setBoltOnCmrhTelephoneFee(toDouble(total));
              }
            }
            return total;
          })
          .reduce(BigDecimal.ZERO, BigDecimal::add);

      boltOnFeeDetails.setBoltOnTotalFeeAmount(toDouble(totalFee));
    }

    return boltOnFeeDetails;
  }

  private static List<BoltOnCalculation> getBoltOnCalculations(FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity) {
    return Arrays.asList(
        new BoltOnCalculation(ADJOURNED_HEARING, feeCalculationRequest.getBoltOns().getBoltOnAdjournedHearing(),
            feeEntity.getAdjornHearingBoltOn()),
        new BoltOnCalculation(HOME_OFFICE_INTERVIEW, feeCalculationRequest.getBoltOns().getBoltOnHomeOfficeInterview(),
            feeEntity.getHoInterviewBoltOn()),
        new BoltOnCalculation(CMRH_ORAL, feeCalculationRequest.getBoltOns().getBoltOnCmrhOral(),
            feeEntity.getOralCmrhBoltOn()),
        new BoltOnCalculation(CMRH_TELEPHONE, feeCalculationRequest.getBoltOns().getBoltOnCmrhTelephone(),
            feeEntity.getTelephoneCmrhBoltOn())
    );
  }

}
