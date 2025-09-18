package uk.gov.justice.laa.fee.scheme.service;

import static uk.gov.justice.laa.fee.scheme.util.LoggingUtil.getLogMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.feecalculator.FeeCalculator;
import uk.gov.justice.laa.fee.scheme.feecalculator.FeeCalculatorFactory;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;

/**
 * Calculate fee for a given fee calculation request.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class FeeCalculationService {

  private final FeeCalculatorFactory calculatorFactory;

  private final FeeDataService feeDataService;

  /**
   * Calculate Fees.
   *
   * @param request FeeCalculationRequest
   * @return FeeCalculationResponse
   */
  public FeeCalculationResponse calculateFee(FeeCalculationRequest request) {
    log.info(getLogMessage("Start calculating fee", request));

    FeeEntity feeEntity = feeDataService.getFeeEntity(request);

    FeeCalculator calculator = calculatorFactory.getCalculator(feeEntity.getCategoryType());

    FeeCalculationResponse response = calculator.calculate(request, feeEntity);

    log.info(getLogMessage("Finished calculating fee", request));

    return response;
  }
}
