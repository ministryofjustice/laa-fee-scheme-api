package uk.gov.justice.laa.fee.scheme.service;

import static uk.gov.justice.laa.fee.scheme.enums.CaseType.CIVIL;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.enums.CaseType;
import uk.gov.justice.laa.fee.scheme.feecalculator.FeeCalculator;
import uk.gov.justice.laa.fee.scheme.feecalculator.FeeCalculatorFactory;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.service.validation.CivilFeeValidationService;
import uk.gov.justice.laa.fee.scheme.service.validation.CrimeFeeValidationService;

/**
 * Calculate fee for a given fee calculation request.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class FeeCalculationService {

  private final FeeCalculatorFactory calculatorFactory;

  private final FeeDataService feeDataService;

  private final FeeDetailsService feeDetailsService;

  private final CivilFeeValidationService civilFeeValidationService;
  private final CrimeFeeValidationService crimeFeeValidationService;

  /**
   * Calculate Fees.
   *
   * @param request FeeCalculationRequest
   * @return FeeCalculationResponse
   */
  public FeeCalculationResponse calculateFee(FeeCalculationRequest request) {

    log.info("Start calculating fee");
    CaseType caseType = feeDetailsService.getCaseType(request);

    List<FeeEntity> feeEntityList = feeDataService.getFeeEntities(request.getFeeCode());

    FeeEntity feeEntity;
    if (caseType == CIVIL) {
      feeEntity = civilFeeValidationService.getValidFeeEntity(feeEntityList, request);
    } else {
      feeEntity = crimeFeeValidationService.getValidFeeEntity(feeEntityList, request);
    }

    // Calculate fee
    FeeCalculator calculator = calculatorFactory.getCalculator(feeEntity.getCategoryType());
    FeeCalculationResponse response = calculator.calculate(request, feeEntity);

    log.info("Finished calculating fee");

    return response;
  }
}
