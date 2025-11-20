package uk.gov.justice.laa.fee.scheme.service;

import static uk.gov.justice.laa.fee.scheme.enums.ErrorType.ERR_ALL_FEE_CODE;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.fee.scheme.entity.FeeCategoryMappingEntity;
import uk.gov.justice.laa.fee.scheme.enums.CaseType;
import uk.gov.justice.laa.fee.scheme.exception.CategoryCodeNotFoundException;
import uk.gov.justice.laa.fee.scheme.exception.FeeContext;
import uk.gov.justice.laa.fee.scheme.exception.ValidationException;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeDetailsResponse;
import uk.gov.justice.laa.fee.scheme.repository.FeeCategoryMappingRepository;

/**
 * Service for retrieving category of law and Fee details based on fee code.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class FeeDetailsService {

  private final FeeCategoryMappingRepository feeCategoryMappingRepository;

  /**
   * Get a category of law code based on given fee code.
   * Get Fee code type and fee code description based on given fee code.
   *
   * @param feeCode the fee code
   * @return category of law response
   * @throws CategoryCodeNotFoundException category law not found
   */
  public FeeDetailsResponse getFeeDetails(String feeCode) {

    log.info("Get category of law and fee details");

    FeeCategoryMappingEntity feeCategoryMapping = getFeeCategoryMapping(feeCode);

    return FeeDetailsResponse.builder()
        .categoryOfLawCode(feeCategoryMapping.getCategoryOfLawType().getCode())
        .feeCodeDescription(feeCategoryMapping.getFeeDescription())
        .feeType(feeCategoryMapping.getFeeType().name())
        .build();
  }

  /**
   * Get case type based on given fee code.
   *
   * @param feeCalculationRequest the feeCalculationRequest
   * @return the case type
   * @throws CategoryCodeNotFoundException category law not found
   */
  public CaseType getCaseType(FeeCalculationRequest feeCalculationRequest) {
    FeeCategoryMappingEntity feeCategoryMapping = getFeeCategoryMapping(feeCalculationRequest);

    return feeCategoryMapping.getCategoryOfLawType().getAreaOfLawType().getCaseType();
  }

  private FeeCategoryMappingEntity getFeeCategoryMapping(FeeCalculationRequest feeCalculationRequest) {
    return feeCategoryMappingRepository.findFirstByFee_FeeCode(feeCalculationRequest.getFeeCode())
        .orElseThrow(() -> new ValidationException(ERR_ALL_FEE_CODE, new FeeContext(feeCalculationRequest)));
  }

  private FeeCategoryMappingEntity getFeeCategoryMapping(String feeCode) {
    return feeCategoryMappingRepository.findFirstByFee_FeeCode(feeCode)
        .orElseThrow(() -> new CategoryCodeNotFoundException(feeCode));
  }
}
