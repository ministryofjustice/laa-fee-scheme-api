package uk.gov.justice.laa.fee.scheme.service;

import static uk.gov.justice.laa.fee.scheme.enums.ClaimStartDateType.REP_ORDER_DATE;
import static uk.gov.justice.laa.fee.scheme.enums.ErrorType.ERR_ALL_FEE_CODE;
import static uk.gov.justice.laa.fee.scheme.enums.ErrorType.ERR_CRIME_REP_ORDER_DATE;
import static uk.gov.justice.laa.fee.scheme.enums.ErrorType.ERR_CRIME_REP_ORDER_DATE_MISSING;
import static uk.gov.justice.laa.fee.scheme.enums.ErrorType.ERR_CRIME_UFN_DATE;
import static uk.gov.justice.laa.fee.scheme.enums.ErrorType.ERR_CRIME_UFN_MISSING;

import io.micrometer.common.util.StringUtils;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
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
 * Service for performing validations.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CrimeValidationService {

  private static final String FEE_CODE_PROD = "PROD";
  public static final List<String> FEE_CODE_PROH_TYPE = List.of("PROH", "PROH1", "PROH2");
  private static final Pattern REP_ORDER_DATE_PATTERN = Pattern.compile(
      "^(PRO[EFKLV][1-4]|PROJ[1-8]|YOU[EFXKLY][1-4]|APP[AB]|PROW)$");

  /**
   * Validates the fee code and claim start date and returns the valid Fee entity.
   *
   * @param feeEntityList         the fee entity list
   * @param feeCalculationRequest the fee calculation request
   * @return the valid Fee entity
   */
  public FeeEntity getValidFeeEntity(List<FeeEntity> feeEntityList, FeeCalculationRequest feeCalculationRequest) {

    log.info("Getting valid fee entity");

    if (feeEntityList.isEmpty()) {
      throw new ValidationException(ERR_ALL_FEE_CODE, new FeeContext(feeCalculationRequest));
    }

    CategoryType categoryType = feeEntityList.getFirst().getCategoryType();
    validateCrimeFee(feeCalculationRequest, categoryType);

    LocalDate claimStartDate = FeeCalculationUtil.getFeeClaimStartDate(categoryType, feeCalculationRequest);
    return getFeeEntityForStartDate(feeEntityList, feeCalculationRequest, claimStartDate);
  }

  private void validateCrimeFee(FeeCalculationRequest feeCalculationRequest, CategoryType categoryType) {
    ClaimStartDateType claimStartDateType = FeeCalculationUtil.getFeeClaimStartDateType(categoryType, feeCalculationRequest);

    if (FEE_CODE_PROH_TYPE.contains(feeCalculationRequest.getFeeCode())) {
      if (feeCalculationRequest.getRepresentationOrderDate() == null
          && StringUtils.isBlank(feeCalculationRequest.getUniqueFileNumber())) {
        throw new ValidationException(ERR_CRIME_UFN_MISSING, new FeeContext(feeCalculationRequest));
      }
    } else {
      if (!FEE_CODE_PROD.equals(feeCalculationRequest.getFeeCode())
          && !isFeeCodeValidForRepOrderDate(feeCalculationRequest)) {
        throw new ValidationException(ERR_CRIME_REP_ORDER_DATE, new FeeContext(feeCalculationRequest));
      }
    }

    if (claimStartDateType == REP_ORDER_DATE) {
      if (feeCalculationRequest.getRepresentationOrderDate() == null) {
        throw new ValidationException(ERR_CRIME_REP_ORDER_DATE_MISSING, new FeeContext(feeCalculationRequest));
      }
    } else if (StringUtils.isBlank(feeCalculationRequest.getUniqueFileNumber())
               && !(FEE_CODE_PROD.equals(feeCalculationRequest.getFeeCode())
                    || FEE_CODE_PROH_TYPE.contains(feeCalculationRequest.getFeeCode()))) {
      throw new ValidationException(ERR_CRIME_UFN_MISSING, new FeeContext(feeCalculationRequest));
    }
  }

  private boolean isValidFee(FeeEntity fee, LocalDate claimStartDate) {
    LocalDate validFrom = fee.getFeeScheme().getValidFrom();
    LocalDate validTo = fee.getFeeScheme().getValidTo();

    return !validFrom.isAfter(claimStartDate) && (validTo == null || !claimStartDate.isAfter(validTo));
  }

  private FeeEntity getFeeEntityForStartDate(List<FeeEntity> feeEntityList, FeeCalculationRequest feeCalculationRequest,
                                             LocalDate claimStartDate) {

    CategoryType categoryType = feeEntityList.getFirst().getCategoryType();
    return feeEntityList.stream()
        .filter(fee -> isValidFee(fee, claimStartDate)) // startDate <= inputDate
        .max(Comparator.comparing(fee -> fee.getFeeScheme().getValidFrom()))
        .orElseThrow(() -> {
          ErrorType error;
          ClaimStartDateType claimStartDateType = FeeCalculationUtil.getFeeClaimStartDateType(categoryType, feeCalculationRequest);
          error = (claimStartDateType == REP_ORDER_DATE) ? ERR_CRIME_REP_ORDER_DATE : ERR_CRIME_UFN_DATE;
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
    if (feeCalculationRequest.getRepresentationOrderDate() != null) {
      return REP_ORDER_DATE_PATTERN.matcher(feeCalculationRequest.getFeeCode()).matches();
    }
    return true;
  }
}
