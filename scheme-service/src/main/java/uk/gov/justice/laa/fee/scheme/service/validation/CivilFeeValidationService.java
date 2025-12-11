package uk.gov.justice.laa.fee.scheme.service.validation;

import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.FAMILY;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.IMMIGRATION_ASYLUM;
import static uk.gov.justice.laa.fee.scheme.enums.ErrorType.ERR_CIVIL_START_DATE;
import static uk.gov.justice.laa.fee.scheme.enums.ErrorType.ERR_CIVIL_START_DATE_TOO_OLD;
import static uk.gov.justice.laa.fee.scheme.enums.ErrorType.ERR_FAMILY_LONDON_RATE;
import static uk.gov.justice.laa.fee.scheme.enums.ErrorType.findByFeeCode;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.enums.ErrorType;
import uk.gov.justice.laa.fee.scheme.exception.FeeContext;
import uk.gov.justice.laa.fee.scheme.exception.ValidationException;
import uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;

/**
 * Service for performing civil validations.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CivilFeeValidationService extends AbstractFeeValidationService {

  private static final LocalDate CIVIL_START_DATE = LocalDate.of(2013, 4, 1);

  /**
   * Validates the fee code and claim start date and returns the valid Fee entity.
   *
   * @param feeEntityList         the fee entity list
   * @param feeCalculationRequest the fee calculation request
   * @return the valid Fee entity
   */
  public FeeEntity getValidFeeEntity(List<FeeEntity> feeEntityList, FeeCalculationRequest feeCalculationRequest) {

    log.info("Getting valid fee entity");

    validateEmptyFeeList(feeEntityList, feeCalculationRequest);
    CategoryType categoryType = feeEntityList.getFirst().getCategoryType();
    validateCivilStartDate(feeEntityList, feeCalculationRequest, categoryType);

    if (categoryType == FAMILY) {
      validateLondonRate(feeCalculationRequest);
    }

    LocalDate claimStartDate = FeeCalculationUtil.getFeeClaimStartDate(categoryType, feeCalculationRequest);
    return getFeeEntityForStartDate(feeEntityList, feeCalculationRequest, claimStartDate);
  }

  /**
   * Check that the start date is valid.
   *
   * @param feeEntityList         the fee entity list
   * @param feeCalculationRequest the fee calculation request
   * @param categoryType          the category type
   */
  private void validateCivilStartDate(List<FeeEntity> feeEntityList, FeeCalculationRequest feeCalculationRequest,
                                      CategoryType categoryType) {
    LocalDate claimStartDate = FeeCalculationUtil.getFeeClaimStartDate(categoryType, feeCalculationRequest);
    LocalDate earliestFeeSchemeDate = getEarliestFeeSchemeDate(feeEntityList);

    if (claimStartDate.isBefore(earliestFeeSchemeDate)) {
      if (categoryType == IMMIGRATION_ASYLUM) {
        ErrorType error = findByFeeCode(feeCalculationRequest.getFeeCode())
            .filter(e -> !claimStartDate.isBefore(CIVIL_START_DATE))
            .orElse(ERR_CIVIL_START_DATE_TOO_OLD);
        throw new ValidationException(error, new FeeContext(feeCalculationRequest));
      } else {
        // find by fee code or default to generic civil error
        throw new ValidationException(ERR_CIVIL_START_DATE_TOO_OLD, new FeeContext(feeCalculationRequest));
      }
    }
  }

  private void validateLondonRate(FeeCalculationRequest feeCalculationRequest) {
    if (feeCalculationRequest.getLondonRate() == null) {
      throw new ValidationException(ERR_FAMILY_LONDON_RATE, new FeeContext(feeCalculationRequest));
    }
  }

  private LocalDate getEarliestFeeSchemeDate(List<FeeEntity> feeEntityList) {
    return feeEntityList.stream()
        .map(feeEntity -> feeEntity.getFeeScheme().getValidFrom())
        .min(LocalDate::compareTo).orElse(null);
  }

  @Override
  protected ErrorType getErrorType(FeeCalculationRequest request, CategoryType categoryType) {
    if (categoryType == IMMIGRATION_ASYLUM) {
      return findByFeeCode(request.getFeeCode()).orElse(ERR_CIVIL_START_DATE);
    }
    return ERR_CIVIL_START_DATE;
  }

}
