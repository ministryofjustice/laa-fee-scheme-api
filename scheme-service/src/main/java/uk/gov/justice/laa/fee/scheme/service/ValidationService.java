package uk.gov.justice.laa.fee.scheme.service;

import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.IMMIGRATION_ASYLUM;
import static uk.gov.justice.laa.fee.scheme.enums.ErrorType.ERR_ALL_FEE_CODE;
import static uk.gov.justice.laa.fee.scheme.enums.ErrorType.ERR_CIVIL_START_DATE;
import static uk.gov.justice.laa.fee.scheme.enums.ErrorType.ERR_CIVIL_START_DATE_TOO_OLD;
import static uk.gov.justice.laa.fee.scheme.enums.ErrorType.ERR_CRIME_REP_ORDER_DATE;
import static uk.gov.justice.laa.fee.scheme.enums.ErrorType.ERR_CRIME_UFN_DATE;
import static uk.gov.justice.laa.fee.scheme.enums.ErrorType.ERR_CRIME_UFN_MISSING;
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
import uk.gov.justice.laa.fee.scheme.enums.AreaOfLawType;
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

  private static final LocalDate CIVIL_START_DATE = LocalDate.of(2013, 4, 1);
  private final FeeDetailsService feeDetailsService;

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
   * @param feeCalculationRequest the fee calculation request
   * @param feeEntityList         the fee entity list
   * @return the valid Fee entity
   */
  public FeeEntity getValidFeeEntity(List<FeeEntity> feeEntityList, FeeCalculationRequest feeCalculationRequest) {

    log.info("Getting valid fee entity");

    if (feeEntityList.isEmpty()) {
      throw new ValidationException(ERR_ALL_FEE_CODE, new FeeContext(feeCalculationRequest));
    }

    if (isCrime(feeCalculationRequest.getFeeCode()) && StringUtils.isBlank(feeCalculationRequest.getUniqueFileNumber())) {
      throw new ValidationException(ERR_CRIME_UFN_MISSING, new FeeContext(feeCalculationRequest));
    }

    CategoryType categoryType = feeEntityList.getFirst().getCategoryType();

    LocalDate claimStartDate = FeeCalculationUtil.getFeeClaimStartDate(categoryType, feeCalculationRequest);

    checkValidStartDate(feeEntityList, feeCalculationRequest);

    return getFeeEntityForStartDate(feeEntityList, feeCalculationRequest, claimStartDate);
  }

  /**
   * Checks the fee calculation request and returns any validation warnings.
   *
   * @param feeCalculationRequest the fee calculation request
   * @return the valid Fee entity
   */
  public List<ValidationMessagesInner> checkForWarnings(FeeCalculationRequest feeCalculationRequest) {

    log.info("Checking for warnings");

    List<ValidationMessagesInner> validationMessages = new ArrayList<>();
    String feeCode = feeCalculationRequest.getFeeCode();

    if (isCrime(feeCode)) {
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
  private void checkValidStartDate(List<FeeEntity> feeEntityList, FeeCalculationRequest feeCalculationRequest) {
    CategoryType categoryType = feeEntityList.getFirst().getCategoryType();
    LocalDate claimStartDate = FeeCalculationUtil.getFeeClaimStartDate(categoryType, feeCalculationRequest);
    LocalDate earliestFeeSchemeDate = getEarliestFeeSchemeDate(feeEntityList);

    if (isCivil(feeCalculationRequest.getFeeCode()) && !categoryType.equals(IMMIGRATION_ASYLUM)
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

  private boolean isCivil(String feeCode) {
    AreaOfLawType areaOfLaw = feeDetailsService.getAreaOfLaw(feeCode);

    return areaOfLaw == AreaOfLawType.LEGAL_HELP || areaOfLaw == AreaOfLawType.MEDIATION;
  }

  private boolean isCrime(String feeCode) {
    AreaOfLawType areaOfLaw = feeDetailsService.getAreaOfLaw(feeCode);

    return areaOfLaw == AreaOfLawType.CRIME_LOWER;
  }

  private FeeEntity getFeeEntityForStartDate(List<FeeEntity> feeEntityList,
                                             FeeCalculationRequest feeCalculationRequest, LocalDate claimStartDate) {
    CategoryType categoryType = feeEntityList.getFirst().getCategoryType();
    return feeEntityList.stream()
        .filter(fee -> filterByRegion(fee, feeCalculationRequest.getLondonRate()))
        .filter(fee -> isValidFee(fee, claimStartDate)) // startDate <= inputDate
        .max(Comparator.comparing(fee -> fee.getFeeScheme().getValidFrom()))
        .orElseThrow(() -> {
          ErrorType error;
          if (isCivil(feeCalculationRequest.getFeeCode())) {
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
