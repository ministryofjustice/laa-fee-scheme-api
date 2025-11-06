package uk.gov.justice.laa.fee.scheme.service;

import static uk.gov.justice.laa.fee.scheme.enums.CaseType.CIVIL;
import static uk.gov.justice.laa.fee.scheme.enums.CaseType.CRIME;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.IMMIGRATION_ASYLUM;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.MAGISTRATES_COURT;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.SENDING_HEARING;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.YOUTH_COURT;
import static uk.gov.justice.laa.fee.scheme.enums.ErrorType.ERR_ALL_FEE_CODE;
import static uk.gov.justice.laa.fee.scheme.enums.ErrorType.ERR_CIVIL_START_DATE;
import static uk.gov.justice.laa.fee.scheme.enums.ErrorType.ERR_CIVIL_START_DATE_TOO_OLD;
import static uk.gov.justice.laa.fee.scheme.enums.ErrorType.ERR_CRIME_REP_ORDER_DATE;
import static uk.gov.justice.laa.fee.scheme.enums.ErrorType.ERR_CRIME_REP_ORDER_DATE_MISSING;
import static uk.gov.justice.laa.fee.scheme.enums.ErrorType.ERR_CRIME_UFN_DATE;
import static uk.gov.justice.laa.fee.scheme.enums.ErrorType.ERR_CRIME_UFN_MISSING;
import static uk.gov.justice.laa.fee.scheme.enums.ErrorType.ERR_FAMILY_LONDON_RATE;
import static uk.gov.justice.laa.fee.scheme.enums.WarningType.WARN_CRIME_TRAVEL_COSTS;
import static uk.gov.justice.laa.fee.scheme.enums.WarningType.WARN_CRIME_WAITING_COSTS;
import static uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner.TypeEnum.WARNING;

import io.micrometer.common.util.StringUtils;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.enums.CaseType;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.enums.ClaimStartDateType;
import uk.gov.justice.laa.fee.scheme.enums.ErrorType;
import uk.gov.justice.laa.fee.scheme.enums.Region;
import uk.gov.justice.laa.fee.scheme.enums.WarningType;
import uk.gov.justice.laa.fee.scheme.exception.FeeContext;
import uk.gov.justice.laa.fee.scheme.exception.ValidationException;
import uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner;

/**
 * Service for performing validations.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ValidationService {

  private static final String PROD_FEE_CODE = "PROD";

  private static final LocalDate CIVIL_START_DATE = LocalDate.of(2013, 4, 1);
  private static final List<CategoryType> CRIME_USING_REP_ORDER_DATE = List.of(MAGISTRATES_COURT, YOUTH_COURT, SENDING_HEARING);

  private static boolean filterByRegion(FeeEntity fee, Boolean isLondonRate) {
    if (fee.getCategoryType() != CategoryType.FAMILY) {
      return true;
    }

    return isLondonRate != null && fee.getRegion() == (isLondonRate ? Region.LONDON : Region.NON_LONDON);
  }

  private static boolean isValidFee(FeeEntity fee, LocalDate claimStartDate) {
    LocalDate validFrom = fee.getFeeScheme().getValidFrom();
    LocalDate validTo = fee.getFeeScheme().getValidTo();

    return !validFrom.isAfter(claimStartDate) && (validTo == null || !claimStartDate.isAfter(validTo));
  }

  private static ValidationMessagesInner buildValidationMessage(WarningType warning) {
    return ValidationMessagesInner.builder()
        .type(WARNING)
        .code(warning.getCode())
        .message(warning.getMessage())
        .build();
  }

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

    if (caseType.equals(CRIME)) {
      if (CRIME_USING_REP_ORDER_DATE.contains(categoryType)) {
        if (feeCalculationRequest.getRepresentationOrderDate() == null) {
          throw new ValidationException(ERR_CRIME_REP_ORDER_DATE_MISSING, new FeeContext(feeCalculationRequest));
        }
      } else if (StringUtils.isBlank(feeCalculationRequest.getUniqueFileNumber())
          && !feeCalculationRequest.getFeeCode().equals(PROD_FEE_CODE)) {
        throw new ValidationException(ERR_CRIME_UFN_MISSING, new FeeContext(feeCalculationRequest));
      }
    }

    if (categoryType.equals(CategoryType.FAMILY) && feeCalculationRequest.getLondonRate() == null) {
      throw new ValidationException(ERR_FAMILY_LONDON_RATE, new FeeContext(feeCalculationRequest));
    }

    LocalDate claimStartDate = FeeCalculationUtil.getFeeClaimStartDate(categoryType, feeCalculationRequest);

    checkValidStartDate(feeEntityList, feeCalculationRequest, caseType);

    return getFeeEntityForStartDate(feeEntityList, feeCalculationRequest, claimStartDate, caseType);
  }

  /**
   * Checks the fee calculation request and returns any validation warnings.
   *
   * @param feeCalculationRequest the fee calculation request
   * @param caseType              the fee case type civil/crime
   * @return the valid Fee entity
   */
  public List<ValidationMessagesInner> checkForWarnings(FeeCalculationRequest feeCalculationRequest, CaseType caseType) {

    log.info("Checking for warnings");

    List<ValidationMessagesInner> validationMessages = new ArrayList<>();
    String feeCode = feeCalculationRequest.getFeeCode();

    if (caseType.equals(CRIME)) {
      if (WARN_CRIME_TRAVEL_COSTS.containsFeeCode(feeCode) && feeCalculationRequest.getNetTravelCosts() != null) {
        log.warn("{} - Net travel costs cannot be claimed", WARN_CRIME_TRAVEL_COSTS.getCode());
        validationMessages.add(buildValidationMessage(WARN_CRIME_TRAVEL_COSTS));
      }

      if (WARN_CRIME_WAITING_COSTS.containsFeeCode(feeCode) && feeCalculationRequest.getNetWaitingCosts() != null) {
        log.warn("{} - Net waiting costs cannot be claimed", WARN_CRIME_WAITING_COSTS.getCode());
        validationMessages.add(buildValidationMessage(WARN_CRIME_WAITING_COSTS));
      }
    }
    return validationMessages;
  }

  /**
   * Check that claim start date is not too far in the past.
   *
   * @param feeEntityList         the fee entity list
   * @param feeCalculationRequest the fee calculation request
   */
  private void checkValidStartDate(List<FeeEntity> feeEntityList, FeeCalculationRequest feeCalculationRequest, CaseType caseType) {
    CategoryType categoryType = feeEntityList.getFirst().getCategoryType();
    LocalDate claimStartDate = FeeCalculationUtil.getFeeClaimStartDate(categoryType, feeCalculationRequest);
    LocalDate earliestFeeSchemeDate = getEarliestFeeSchemeDate(feeEntityList);

    if ((caseType.equals(CIVIL)) && !categoryType.equals(IMMIGRATION_ASYLUM)
        && claimStartDate.isBefore(earliestFeeSchemeDate)) {
      throw new ValidationException(ERR_CIVIL_START_DATE_TOO_OLD, new FeeContext(feeCalculationRequest));
    }

    if (categoryType.equals(IMMIGRATION_ASYLUM) && claimStartDate.isBefore(earliestFeeSchemeDate)) {
      // find by fee code or default to generic civil error
      ErrorType error = ErrorType.findByFeeCode(feeCalculationRequest.getFeeCode())
          .filter(e -> !claimStartDate.isBefore(CIVIL_START_DATE))
          .orElse(ErrorType.ERR_CIVIL_START_DATE_TOO_OLD);
      throw new ValidationException(error, new FeeContext(feeCalculationRequest));
    }
  }

  private LocalDate getEarliestFeeSchemeDate(List<FeeEntity> feeEntityList) {
    return feeEntityList.stream()
        .map(feeEntity -> feeEntity.getFeeScheme().getValidFrom())
        .min(LocalDate::compareTo).orElse(null);
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
          if (caseType.equals(CIVIL)) {
            if (categoryType.equals(IMMIGRATION_ASYLUM)) {
              // find by fee code or default to generic civil error
              error = ErrorType.findByFeeCode(feeCalculationRequest.getFeeCode()).orElse(ErrorType.ERR_CIVIL_START_DATE);
            } else {
              error = ERR_CIVIL_START_DATE;
            }
          } else {
            ClaimStartDateType claimStartDateType = FeeCalculationUtil.getFeeClaimStartDateType(categoryType, feeCalculationRequest);
            error = (claimStartDateType == ClaimStartDateType.REP_ORDER_DATE) ? ERR_CRIME_REP_ORDER_DATE : ERR_CRIME_UFN_DATE;
          }
          return new ValidationException(error, new FeeContext(feeCalculationRequest));
        });
  }
}
