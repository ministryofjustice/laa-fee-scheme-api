package uk.gov.justice.laa.fee.scheme.service;

import static uk.gov.justice.laa.fee.scheme.enums.CaseType.CIVIL;
import static uk.gov.justice.laa.fee.scheme.enums.CaseType.CRIME;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.FAMILY;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.IMMIGRATION_ASYLUM;
import static uk.gov.justice.laa.fee.scheme.enums.ClaimStartDateType.REP_ORDER_DATE;
import static uk.gov.justice.laa.fee.scheme.enums.ErrorType.ERR_ALL_FEE_CODE;
import static uk.gov.justice.laa.fee.scheme.enums.ErrorType.ERR_CIVIL_START_DATE;
import static uk.gov.justice.laa.fee.scheme.enums.ErrorType.ERR_CIVIL_START_DATE_TOO_OLD;
import static uk.gov.justice.laa.fee.scheme.enums.ErrorType.ERR_CRIME_REP_ORDER_DATE;
import static uk.gov.justice.laa.fee.scheme.enums.ErrorType.ERR_CRIME_REP_ORDER_DATE_MISSING;
import static uk.gov.justice.laa.fee.scheme.enums.ErrorType.ERR_CRIME_UFN_DATE;
import static uk.gov.justice.laa.fee.scheme.enums.ErrorType.ERR_CRIME_UFN_MISSING;
import static uk.gov.justice.laa.fee.scheme.enums.ErrorType.ERR_FAMILY_LONDON_RATE;

import io.micrometer.common.util.StringUtils;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.enums.CaseType;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.enums.ClaimStartDateType;
import uk.gov.justice.laa.fee.scheme.enums.ErrorType;
import uk.gov.justice.laa.fee.scheme.enums.Region;
import uk.gov.justice.laa.fee.scheme.exception.FeeContext;
import uk.gov.justice.laa.fee.scheme.exception.ValidationException;
import uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;

/**
 * Service for performing validations.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ValidationService {

  private static final String FEE_CODE_PROD = "PROD";
  private static final String FEE_CODE_PROH = "PROH";

  private static final LocalDate CIVIL_START_DATE = LocalDate.of(2013, 4, 1);

  /**
   * Validates the fee code and claim start date and returns the valid Fee entity.
   *
   * @param feeEntityList         the fee entity list
   * @param feeCalculationRequest the fee calculation request
   * @param caseType              the fee case type civil/crime
   * @return the valid Fee entity
   */
  public FeeEntity getValidFeeEntity(List<FeeEntity> feeEntityList, FeeCalculationRequest feeCalculationRequest, CaseType caseType) {

    log.info("Getting valid fee entity");

    if (feeEntityList.isEmpty()) {
      throw new ValidationException(ERR_ALL_FEE_CODE, new FeeContext(feeCalculationRequest));
    }

    CategoryType categoryType = feeEntityList.getFirst().getCategoryType();
    if (caseType == CRIME) {
      validateCrimeFee(feeCalculationRequest, categoryType);
    }

    if (caseType == CIVIL) {
      validateCivilStartDate(feeEntityList, feeCalculationRequest, categoryType);
    }

    if (categoryType == FAMILY) {
      validateLondonRate(feeCalculationRequest);
    }

    LocalDate claimStartDate = FeeCalculationUtil.getFeeClaimStartDate(categoryType, feeCalculationRequest);
    return getFeeEntityForStartDate(feeEntityList, feeCalculationRequest, claimStartDate, caseType);
  }

  private void validateCrimeFee(FeeCalculationRequest feeCalculationRequest, CategoryType categoryType) {
    ClaimStartDateType claimStartDateType = FeeCalculationUtil.getFeeClaimStartDateType(categoryType, feeCalculationRequest);

    if (feeCalculationRequest.getFeeCode().equals(FEE_CODE_PROH)) {
      if (feeCalculationRequest.getRepresentationOrderDate() == null
          && StringUtils.isBlank(feeCalculationRequest.getUniqueFileNumber())) {
        throw new ValidationException(ERR_CRIME_UFN_MISSING, new FeeContext(feeCalculationRequest));
      }
    } else {
      if (!feeCalculationRequest.getFeeCode().equals(FEE_CODE_PROD)
          && !isFeeCodeValidForRepOrderDate(feeCalculationRequest)) {
        throw new ValidationException(ERR_CRIME_REP_ORDER_DATE, new FeeContext(feeCalculationRequest));
      }
    }

    if (claimStartDateType == REP_ORDER_DATE) {
      if (feeCalculationRequest.getRepresentationOrderDate() == null) {
        throw new ValidationException(ERR_CRIME_REP_ORDER_DATE_MISSING, new FeeContext(feeCalculationRequest));
      }
    } else if (StringUtils.isBlank(feeCalculationRequest.getUniqueFileNumber())
               && !(feeCalculationRequest.getFeeCode().equals(FEE_CODE_PROD)
                    || feeCalculationRequest.getFeeCode().equals(FEE_CODE_PROH))) {
      throw new ValidationException(ERR_CRIME_UFN_MISSING, new FeeContext(feeCalculationRequest));
    }
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
        ErrorType error = ErrorType.findByFeeCode(feeCalculationRequest.getFeeCode())
            .filter(e -> !claimStartDate.isBefore(CIVIL_START_DATE))
            .orElse(ErrorType.ERR_CIVIL_START_DATE_TOO_OLD);
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

  private boolean filterByRegion(FeeEntity fee, Boolean isLondonRate) {
    if (fee.getCategoryType() != FAMILY) {
      return true;
    }

    return isLondonRate != null && fee.getRegion() == (isLondonRate ? Region.LONDON : Region.NON_LONDON);
  }

  private boolean isValidFee(FeeEntity fee, LocalDate claimStartDate) {
    LocalDate validFrom = fee.getFeeScheme().getValidFrom();
    LocalDate validTo = fee.getFeeScheme().getValidTo();

    return !validFrom.isAfter(claimStartDate) && (validTo == null || !claimStartDate.isAfter(validTo));
  }

  private FeeEntity getFeeEntityForStartDate(List<FeeEntity> feeEntityList, FeeCalculationRequest feeCalculationRequest,
                                             LocalDate claimStartDate, CaseType caseType) {

    CategoryType categoryType = feeEntityList.getFirst().getCategoryType();
    return feeEntityList.stream()
        .filter(fee -> filterByRegion(fee, feeCalculationRequest.getLondonRate()))
        .filter(fee -> isValidFee(fee, claimStartDate)) // startDate <= inputDate
        .max(Comparator.comparing(fee -> fee.getFeeScheme().getValidFrom()))
        .orElseThrow(() -> {
          ErrorType error;
          if (caseType == CIVIL) {
            if (categoryType == IMMIGRATION_ASYLUM) {
              // find by fee code or default to generic civil error
              error = ErrorType.findByFeeCode(feeCalculationRequest.getFeeCode()).orElse(ErrorType.ERR_CIVIL_START_DATE);
            } else {
              error = ERR_CIVIL_START_DATE;
            }
          } else {
            ClaimStartDateType claimStartDateType = FeeCalculationUtil.getFeeClaimStartDateType(categoryType, feeCalculationRequest);
            error = (claimStartDateType == REP_ORDER_DATE) ? ERR_CRIME_REP_ORDER_DATE : ERR_CRIME_UFN_DATE;
          }
          return new ValidationException(error, new FeeContext(feeCalculationRequest));
        });
  }

  /**
   * Validate Fee Code against a set of Magistrates, Youth & Advocacy Fee codes.
   *
   * @param feeCalculationRequest FeeCalculationRequest
   * @return boolean
   */
  public boolean isFeeCodeValidForRepOrderDate(FeeCalculationRequest feeCalculationRequest) {
    final Pattern repOrderDtPattern = Pattern.compile(
        "^(PRO[EFKLV][1-4]|PROJ[1-8]|YOU[EF][1-3]|YOU[XKLY][1-4]|APP[AB]|PROW)$");

    if (feeCalculationRequest.getRepresentationOrderDate() != null) {
      return repOrderDtPattern.matcher(feeCalculationRequest.getFeeCode()).matches();
    }
    return true;
  }
}
