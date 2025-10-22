package uk.gov.justice.laa.fee.scheme.service;

import static uk.gov.justice.laa.fee.scheme.enums.ErrorCode.ERR_ALL_FEE_CODE;
import static uk.gov.justice.laa.fee.scheme.enums.ErrorCode.ERR_CIVIL_START_DATE;
import static uk.gov.justice.laa.fee.scheme.enums.ErrorCode.ERR_CIVIL_START_DATE_TOO_OLD;
import static uk.gov.justice.laa.fee.scheme.enums.ErrorCode.ERR_CRIME_REP_ORDER_DATE;
import static uk.gov.justice.laa.fee.scheme.enums.ErrorCode.ERR_CRIME_UFN_DATE;
import static uk.gov.justice.laa.fee.scheme.enums.ErrorCode.ERR_CRIME_UFN_MISSING;
import static uk.gov.justice.laa.fee.scheme.enums.WarningCode.WARCRM1;
import static uk.gov.justice.laa.fee.scheme.enums.WarningCode.WARCRM2;
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
import uk.gov.justice.laa.fee.scheme.enums.ErrorCode;
import uk.gov.justice.laa.fee.scheme.enums.Region;
import uk.gov.justice.laa.fee.scheme.enums.WarningCode;
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

  private final FeeDetailsService feeDetailsService;

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
    List<ValidationMessagesInner> validationMessages = new ArrayList<>();

    if (isCrime(feeCalculationRequest.getFeeCode())) {
      if (feeCalculationRequest.getNetTravelCosts() != null) {
        log.warn("{} - Net travel costs cannot be claimed", WARCRM1.getCode());
        validationMessages.add(buildValidationMessage(WARCRM1));
      }

      if (feeCalculationRequest.getNetWaitingCosts() != null) {
        log.warn("{} - Net waiting costs cannot be claimed", WARCRM2.getCode());
        validationMessages.add(buildValidationMessage(WARCRM2));
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
    if (isCivil(feeCalculationRequest.getFeeCode())) {
      CategoryType categoryType = feeEntityList.getFirst().getCategoryType();
      LocalDate claimStartDate = FeeCalculationUtil.getFeeClaimStartDate(categoryType, feeCalculationRequest);
      LocalDate earliestFeeSchemeDate = getEarliestFeeSchemeDate(feeEntityList);
      if (claimStartDate.isBefore(earliestFeeSchemeDate)) {
        throw new ValidationException(ERR_CIVIL_START_DATE_TOO_OLD, new FeeContext(feeCalculationRequest));
      }
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
    return feeEntityList.stream()
        .filter(fee -> filterByRegion(fee, feeCalculationRequest.getLondonRate()))
        .filter(fee -> isValidFee(fee, claimStartDate)) // startDate <= inputDate
        .max(Comparator.comparing(fee -> fee.getFeeScheme().getValidFrom()))
        .orElseThrow(() -> {
          ErrorCode errorCode;
          if (isCivil(feeCalculationRequest.getFeeCode())) {
            errorCode = ERR_CIVIL_START_DATE;
          } else {
            CategoryType categoryType = feeEntityList.getFirst().getCategoryType();
            ClaimStartDateType claimStartDateType = FeeCalculationUtil.getFeeClaimStartDateType(categoryType, feeCalculationRequest);
            errorCode = (claimStartDateType == ClaimStartDateType.REP_ORDER_DATE) ? ERR_CRIME_REP_ORDER_DATE : ERR_CRIME_UFN_DATE;
          }
          return new ValidationException(errorCode, new FeeContext(feeCalculationRequest));
        });
  }

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

  private static ValidationMessagesInner buildValidationMessage(WarningCode warningCode) {
    return ValidationMessagesInner.builder()
        .type(WARNING)
        .code(warningCode.getCode())
        .message(warningCode.getMessage())
        .build();
  }
}
