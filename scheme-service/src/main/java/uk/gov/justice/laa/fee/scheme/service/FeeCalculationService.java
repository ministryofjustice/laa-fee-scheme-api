package uk.gov.justice.laa.fee.scheme.service;

import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.feecalculator.FeeCalculator;
import uk.gov.justice.laa.fee.scheme.feecalculator.FeeCalculatorFactory;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner;

/**
 * Calculate fee for a given fee calculation request.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class FeeCalculationService {

  private final FeeCalculatorFactory calculatorFactory;

  private final FeeDataService feeDataService;

  private final ValidationService validationService;

  /**
   * Calculate Fees.
   *
   * @param request FeeCalculationRequest
   * @return FeeCalculationResponse
   */
  public FeeCalculationResponse calculateFee(FeeCalculationRequest request) {

    log.info("Start calculating fee");

    List<FeeEntity> feeEntityList = feeDataService.getFeeEntities(request.getFeeCode());

    FeeEntity feeEntity = validationService.getValidFeeEntity(feeEntityList, request);

    // Calculate fee
    FeeCalculator calculator = calculatorFactory.getCalculator(feeEntity.getCategoryType());
    FeeCalculationResponse response = calculator.calculate(request, feeEntity);

    List<ValidationMessagesInner> warnings = validationService.checkForWarnings(request);

    // Add any warnings to response
    List<ValidationMessagesInner> validationMessages = response.getValidationMessages();
    if (validationMessages != null) {
      response.setValidationMessages(Stream.concat(validationMessages.stream(), warnings.stream()).toList());
    } else {
      response.setValidationMessages(warnings);
    }

    log.info("Finished calculating fee");

    return response;
  }
}
