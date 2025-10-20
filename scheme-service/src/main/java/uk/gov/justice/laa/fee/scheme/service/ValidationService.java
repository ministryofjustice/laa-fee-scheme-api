package uk.gov.justice.laa.fee.scheme.service;

import static uk.gov.justice.laa.fee.scheme.enums.ValidationError.ERRALL1;
import static uk.gov.justice.laa.fee.scheme.enums.ValidationError.ERRCIV1;
import static uk.gov.justice.laa.fee.scheme.enums.ValidationError.ERRCIV2;
import static uk.gov.justice.laa.fee.scheme.enums.ValidationError.ERRCRM1;
import static uk.gov.justice.laa.fee.scheme.enums.ValidationError.ERRCRM2;
import static uk.gov.justice.laa.fee.scheme.enums.ValidationError.ERRCRM6;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.enums.AreaOfLawType;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.enums.ClaimStartDateType;
import uk.gov.justice.laa.fee.scheme.enums.Region;
import uk.gov.justice.laa.fee.scheme.enums.ValidationError;
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

  private final FeeDetailsService feeDetailsService;

  /**
   * Validates the fee code and claim start date and returns the valid Fee entity.
   *
   * @param feeCalculationRequest the fee calculation request
   * @param feeEntityList the fee entity list
   * @return the valid Fee entity
   */
  public FeeEntity getValidFeeEntity(List<FeeEntity> feeEntityList, FeeCalculationRequest feeCalculationRequest) {

    log.info("Getting valid fee entity");

    if (feeEntityList.isEmpty()) {
      throw new ValidationException(ERRALL1, new FeeContext(feeCalculationRequest));
    }

    CategoryType categoryType = feeEntityList.getFirst().getCategoryType();

    LocalDate claimStartDate = FeeCalculationUtil.getFeeClaimStartDate(categoryType, feeCalculationRequest);

    checkValidStartDate(feeEntityList, feeCalculationRequest);

    return getFeeEntityForStartDate(feeEntityList, feeCalculationRequest, claimStartDate);

  }

  /**
   * Check that claim start date is not too far in the past.
   *
   * @param feeEntityList         the fee entity list
   * @param feeCalculationRequest the fee calculation request
   */
  private void checkValidStartDate(List<FeeEntity> feeEntityList, FeeCalculationRequest feeCalculationRequest) {

    String feeCode = feeCalculationRequest.getFeeCode();
    CategoryType categoryType = feeEntityList.getFirst().getCategoryType();
    LocalDate claimStartDate = FeeCalculationUtil.getFeeClaimStartDate(categoryType, feeCalculationRequest);

    LocalDate earliestFeeSchemeDate = getEarliestFeeSchemeDate(feeEntityList);
    if (claimStartDate.isBefore(earliestFeeSchemeDate)) {
      ValidationError validationError;

      if (isCivil(feeCode)) {
        validationError = ERRCIV2;
      } else { // otherwise isCrime
        ClaimStartDateType claimStartDateType = FeeCalculationUtil.getFeeClaimStartDateType(categoryType, feeCalculationRequest);
        validationError = (claimStartDateType == ClaimStartDateType.REP_ORDER_DATE) ? ERRCRM2 : ERRCRM1;
      }
      throw new ValidationException(validationError, new FeeContext(feeCalculationRequest));
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

  private FeeEntity getFeeEntityForStartDate(List<FeeEntity> feeEntityList,
                                      FeeCalculationRequest feeCalculationRequest, LocalDate claimStartDate) {
    return feeEntityList.stream()
        .filter(fee -> filterByRegion(fee, feeCalculationRequest.getLondonRate()))
        .filter(fee -> isValidFee(fee, claimStartDate)) // startDate <= inputDate
        .max(Comparator.comparing(fee -> fee.getFeeScheme().getValidFrom()))
        .orElseThrow(() -> {
          ValidationError error = isCivil(feeCalculationRequest.getFeeCode()) ? ERRCIV1 : ERRCRM6;
          return new ValidationException(error, new FeeContext(feeCalculationRequest));
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
}
