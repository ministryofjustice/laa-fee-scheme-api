package uk.gov.justice.laa.fee.scheme.service.validation;

import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.FAMILY;
import static uk.gov.justice.laa.fee.scheme.enums.ErrorType.ERR_ALL_FEE_CODE;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.enums.ErrorType;
import uk.gov.justice.laa.fee.scheme.enums.Region;
import uk.gov.justice.laa.fee.scheme.exception.FeeContext;
import uk.gov.justice.laa.fee.scheme.exception.ValidationException;
import uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;

/**
 * Abstract class for common validation logic.
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractFeeValidationService {

  /**
   * Validates that the provided list of FeeEntity objects is not empty.
   *
   * @param feeEntityList the list of fee entities to validate
   * @param request       the fee calculation request context
   * @throws ValidationException if the fee entity list is empty
   */
  protected void validateEmptyFeeList(List<FeeEntity> feeEntityList, FeeCalculationRequest request) {
    if (feeEntityList.isEmpty()) {
      throw new ValidationException(ERR_ALL_FEE_CODE, new FeeContext(request));
    }
  }

  /**
   * Determines whether a given fee is valid for the provided claim start date.
   * A fee is valid if the claim start date is on or after the fee scheme's validFrom date
   * and on or before the validTo date (if specified).
   *
   * @param fee            the fee entity to check
   * @param claimStartDate the date of the claim
   * @return true if the fee is valid for the claim start date, false otherwise
   */
  protected boolean isValidFee(FeeEntity fee, LocalDate claimStartDate) {
    LocalDate validFrom = fee.getFeeScheme().getValidFrom();
    LocalDate validTo = fee.getFeeScheme().getValidTo();
    return !validFrom.isAfter(claimStartDate) && (validTo == null || !claimStartDate.isAfter(validTo));
  }

  /**
   * Retrieves the FeeEntity for a given claim start date.
   * with the latest validFrom date that is still valid for the claim start date.
   *
   * @param feeEntityList         the list of fee entities to search
   * @param feeCalculationRequest the request context containing claim details
   * @return the valid FeeEntity
   * @throws ValidationException if no valid fee is found
   */
  protected FeeEntity getFeeEntityForStartDate(List<FeeEntity> feeEntityList,
                                               FeeCalculationRequest feeCalculationRequest) {
    CategoryType categoryType = feeEntityList.getFirst().getCategoryType();
    LocalDate claimStartDate = FeeCalculationUtil.getFeeClaimStartDate(categoryType, feeCalculationRequest);

    return feeEntityList.stream()
        .filter(fee -> isValidFee(fee, claimStartDate))
        .filter(fee -> filterByRegion(fee, feeCalculationRequest.getLondonRate()))
        .max(Comparator.comparing(fee -> fee.getFeeScheme().getValidFrom()))
        .orElseThrow(() -> new ValidationException(getErrorType(feeCalculationRequest, categoryType),
            new FeeContext(feeCalculationRequest)));
  }

  /**
   * Applies additional filtering based on the fee category and region.
   * filtering applies only to FAMILY category fees
   *
   * @param fee          the fee entity to check
   * @param isLondonRate whether the claim is for a London rate
   * @return true if the fee passes the filter, false otherwise
   */
  protected boolean filterByRegion(FeeEntity fee, Boolean isLondonRate) {
    if (fee.getCategoryType() != FAMILY) {
      return true;
    }
    return isLondonRate != null && fee.getRegion() == (isLondonRate ? Region.LONDON : Region.NON_LONDON);
  }

  /**
   * Get the appropriate error type for a fee calculation request.
   *
   * @param request      the fee calculation request
   * @param categoryType the category type of the fees being processed
   * @return the ErrorType to throw if validation fails
   */
  abstract ErrorType getErrorType(FeeCalculationRequest request, CategoryType categoryType);
}
