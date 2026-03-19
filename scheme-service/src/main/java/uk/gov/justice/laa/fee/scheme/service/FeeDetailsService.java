package uk.gov.justice.laa.fee.scheme.service;

import static uk.gov.justice.laa.fee.scheme.enums.ErrorType.ERR_ALL_FEE_CODE;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.fee.scheme.entity.FeeCategoryMappingEntity;
import uk.gov.justice.laa.fee.scheme.enums.CaseType;
import uk.gov.justice.laa.fee.scheme.exception.CategoryCodeNotFoundException;
import uk.gov.justice.laa.fee.scheme.exception.FeeContext;
import uk.gov.justice.laa.fee.scheme.exception.ValidationException;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeDetailsResponseV1;
import uk.gov.justice.laa.fee.scheme.model.FeeDetailsResponseV2;
import uk.gov.justice.laa.fee.scheme.repository.FeeCategoryMappingRepository;

/**
 * Service for retrieving category of law and Fee details based on fee code.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class FeeDetailsService {

  private final FeeCategoryMappingRepository feeCategoryMappingRepository;

  private static final Set<String> ASSOC_CIVIL_FEE_CODES = Set.of("ASMS", "ASPL", "ASAS");
  private static final List<String> ASSOC_CIVIL_CATEGORY_CODES = List.of("APPEALS", "INVEST", "PRISON");

  /**
   * Get category of law code, fee type, fee code description based on given fee code.
   *
   * @param feeCode the fee code
   * @return the fee details response (v1)
   * @throws CategoryCodeNotFoundException if the fee code is not found
   */
  public FeeDetailsResponseV1 getFeeDetailsV1(String feeCode) {

    log.info("Get category of law and fee details (v1)");

    FeeCategoryMappingEntity feeCategoryMapping = getFeeCategoryMapping(feeCode);

    return FeeDetailsResponseV1.builder()
        .categoryOfLawCode(feeCategoryMapping.getCategoryOfLawType().getCode())
        .feeCodeDescription(feeCategoryMapping.getFeeCode().getFeeDescription())
        .feeType(feeCategoryMapping.getFeeCode().getFeeType().toString())
        .build();
  }

  /**
   * Get category of law code, fee type, fee code description based on given fee code.
   *
   * @param feeCode the fee code
   * @return the fee details response (v2)
   * @throws CategoryCodeNotFoundException if the fee code is not found
   */
  public FeeDetailsResponseV2 getFeeDetailsV2(String feeCode) {

    log.info("Get category of law and fee details (v2)");

    FeeCategoryMappingEntity feeCategoryMapping = getFeeCategoryMapping(feeCode);

    List<String> categoryOfLawCodes = ASSOC_CIVIL_FEE_CODES.contains(feeCode) ? ASSOC_CIVIL_CATEGORY_CODES
        : Collections.singletonList(feeCategoryMapping.getCategoryOfLawType().getCode());

    return FeeDetailsResponseV2.builder()
        .categoryOfLawCodes(categoryOfLawCodes)
        .feeCodeDescription(feeCategoryMapping.getFeeCode().getFeeDescription())
        .feeType(feeCategoryMapping.getFeeCode().getFeeType().toString())
        .build();
  }

  /**
   * Get case type based on given fee code.
   *
   * @param feeCalculationRequest the feeCalculationRequest
   * @return the case type
   * @throws ValidationException if the fee code is not found
   */
  public CaseType getCaseType(FeeCalculationRequest feeCalculationRequest) {
    FeeCategoryMappingEntity feeCategoryMapping = getFeeCategoryMapping(feeCalculationRequest);

    return feeCategoryMapping.getCategoryOfLawType().getAreaOfLawType().getCaseType();
  }

  private FeeCategoryMappingEntity getFeeCategoryMapping(FeeCalculationRequest feeCalculationRequest) {
    return feeCategoryMappingRepository.findByFeeCodeFeeCode(feeCalculationRequest.getFeeCode())
        .orElseThrow(() -> new ValidationException(ERR_ALL_FEE_CODE, new FeeContext(feeCalculationRequest)));
  }

  private FeeCategoryMappingEntity getFeeCategoryMapping(String feeCode) {
    return feeCategoryMappingRepository.findByFeeCodeFeeCode(feeCode)
        .orElseThrow(() -> new CategoryCodeNotFoundException(feeCode));
  }
}
