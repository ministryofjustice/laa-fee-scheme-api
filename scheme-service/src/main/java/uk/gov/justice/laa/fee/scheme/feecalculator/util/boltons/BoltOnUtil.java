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
public class BoltOnUtil {

  private BoltOnUtil() {
  }

  /**
   * Where bolt on exists, 
   * Assign each boltOnXXXXXCount to number of bolt on type requested.
   * Multiply count by bolt on fee for boltOnXXXXXXFee
   * Add each boltOnXXXXXXFee for a total, boltOnTotalFeeAmount 
   */
  public static BoltOnFeeDetails calculateBoltOnAmounts(FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity) {
    BoltOnFeeDetails boltOnTotal = BoltOnFeeDetails.builder().boltOnTotalFeeAmount(toDouble(BigDecimal.ZERO)).build();

    if (feeCalculationRequest.getBoltOns() != null) {

      log.info("Calculate bolt on amounts for fee calculation");

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
