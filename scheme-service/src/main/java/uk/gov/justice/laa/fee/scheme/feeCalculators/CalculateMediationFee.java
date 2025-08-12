package uk.gov.justice.laa.fee.scheme.feeCalculators;

import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;

public class CalculateMediationFee {

  public static FeeCalculationResponse getFee(FeeEntity feeEntity) {
    return new  FeeCalculationResponse();
  }
}
