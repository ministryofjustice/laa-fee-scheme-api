package uk.gov.justice.laa.fee.scheme.service.validation;

import static uk.gov.justice.laa.fee.scheme.enums.ClaimStartDateType.REP_ORDER_DATE;
import static uk.gov.justice.laa.fee.scheme.enums.ErrorType.ERR_CRIME_REP_ORDER_DATE;
import static uk.gov.justice.laa.fee.scheme.enums.ErrorType.ERR_CRIME_REP_ORDER_DATE_MISSING;
import static uk.gov.justice.laa.fee.scheme.enums.ErrorType.ERR_CRIME_UFN_DATE;
import static uk.gov.justice.laa.fee.scheme.enums.ErrorType.ERR_CRIME_UFN_MISSING;
import static uk.gov.justice.laa.fee.scheme.service.FeeCodeConstants.FEE_CODE_PROD;
import static uk.gov.justice.laa.fee.scheme.service.FeeCodeConstants.FEE_CODE_PROH_TYPE;

import io.micrometer.common.util.StringUtils;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.enums.ClaimStartDateType;
import uk.gov.justice.laa.fee.scheme.enums.ErrorType;
import uk.gov.justice.laa.fee.scheme.exception.FeeContext;
import uk.gov.justice.laa.fee.scheme.exception.ValidationException;
import uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;

/**
 * Service for performing crime validations.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CrimeFeeValidationService extends AbstractFeeValidationService {

  /**
   * Validates the fee code and claim start date and returns the valid Fee entity.
   *
   * @param feeEntityList         the fee entity list
   * @param feeCalculationRequest the fee calculation request
   * @return the valid Fee entity
   */
  @Override
  public FeeEntity getValidFeeEntity(List<FeeEntity> feeEntityList, FeeCalculationRequest feeCalculationRequest) {

    log.info("Getting valid fee entity");

    validateEmptyFeeList(feeEntityList, feeCalculationRequest);

    CategoryType categoryType = feeEntityList.getFirst().getCategoryType();
    validateCrimeFee(feeCalculationRequest, categoryType);

    return getFeeEntityForStartDate(feeEntityList, feeCalculationRequest);
  }

  private void validateCrimeFee(FeeCalculationRequest feeCalculationRequest, CategoryType categoryType) {
    ClaimStartDateType claimStartDateType = FeeCalculationUtil.getFeeClaimStartDateType(categoryType, feeCalculationRequest);
    String feeCode = feeCalculationRequest.getFeeCode();

    // Validate PROH fee codes
    validateProhTypeFeeCodes(feeCalculationRequest, feeCode);

    // Validate based on claim start date type
    if (claimStartDateType == REP_ORDER_DATE) {
      validateRepresentationOrderDate(feeCalculationRequest);
    } else {
      validateUniqueFileNumber(feeCalculationRequest, feeCode);
    }
  }

  private void validateProhTypeFeeCodes(FeeCalculationRequest feeCalculationRequest, String feeCode) {
    if (FEE_CODE_PROH_TYPE.contains(feeCode)
        && feeCalculationRequest.getRepresentationOrderDate() == null
        && StringUtils.isBlank(feeCalculationRequest.getUniqueFileNumber())) {
      throw new ValidationException(ERR_CRIME_UFN_MISSING, new FeeContext(feeCalculationRequest));
    }
  }

  private void validateRepresentationOrderDate(FeeCalculationRequest feeCalculationRequest) {
    if (feeCalculationRequest.getRepresentationOrderDate() == null) {
      throw new ValidationException(ERR_CRIME_REP_ORDER_DATE_MISSING, new FeeContext(feeCalculationRequest));
    }
  }

  private void validateUniqueFileNumber(FeeCalculationRequest feeCalculationRequest, String feeCode) {
    boolean isProhOrProd = FEE_CODE_PROD.equals(feeCode) || FEE_CODE_PROH_TYPE.contains(feeCode);
    
    if (StringUtils.isBlank(feeCalculationRequest.getUniqueFileNumber()) && !isProhOrProd) {
      throw new ValidationException(ERR_CRIME_UFN_MISSING, new FeeContext(feeCalculationRequest));
    }
  }

  @Override
  protected ErrorType getErrorType(FeeCalculationRequest feeCalculationRequest, CategoryType categoryType) {
    ClaimStartDateType claimStartDateType = FeeCalculationUtil.getFeeClaimStartDateType(categoryType, feeCalculationRequest);
    return  claimStartDateType == REP_ORDER_DATE ? ERR_CRIME_REP_ORDER_DATE : ERR_CRIME_UFN_DATE;
  }
}