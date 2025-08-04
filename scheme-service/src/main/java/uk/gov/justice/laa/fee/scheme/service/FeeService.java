package uk.gov.justice.laa.fee.scheme.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;

/**
 * Initial service for determining category law code, and for fee calculation.
 */
@RequiredArgsConstructor
@Service
public class FeeService {

  /**
   * Initial method for determining fee calculation, using fee data.
   */
  public FeeCalculationResponse getFeeCalculation(FeeCalculationRequest feeData) {
    // Logic using the OpenAPI-generated request model `feeData`

    FeeCalculationResponse response = FeeCalculationResponse.builder()
        .feeCode("FEE123")
        .feeCalculation(FeeCalculation.builder()
            .subTotal(1234.14)
            .totalAmount(1500.56)
            .build())
        .build();

    return response;
  }
}

