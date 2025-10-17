package uk.gov.justice.laa.fee.scheme.service;

import static uk.gov.justice.laa.fee.scheme.enums.ValidationError.ERRALL1;
import static uk.gov.justice.laa.fee.scheme.enums.ValidationError.ERRCIV1;
import static uk.gov.justice.laa.fee.scheme.enums.ValidationError.ERRCIV2;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.enums.AreaOfLawType;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.enums.Region;
import uk.gov.justice.laa.fee.scheme.exception.FeeContext;
import uk.gov.justice.laa.fee.scheme.exception.ValidationException;
import uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.repository.FeeRepository;

/**
 * Service for retrieving Database Table entities.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class FeeDataService {

  private final FeeRepository feeRepository;
  private final FeeDetailsService feeDetailsService;

  /**
   * Returns FeeEntity after making calls to database.
   *
   * @param feeCalculationRequest FeeCalculationRequest
   * @return FeeEntity
   */
  public FeeEntity getFeeEntity(FeeCalculationRequest feeCalculationRequest) {

    log.info("Get filtered fee entity");

    String feeCode = feeCalculationRequest.getFeeCode();

    List<FeeEntity> feeEntityList = feeRepository.findByFeeCode(feeCode);

    if (feeEntityList.isEmpty()) {
      throw new ValidationException(ERRALL1, new FeeContext(feeCalculationRequest));
    }

    LocalDate minValidFromDate = feeEntityList.stream()
        .map(feeEntity -> feeEntity.getFeeScheme().getValidFrom())
        .min(LocalDate::compareTo).orElse(null);

    CategoryType categoryType = feeEntityList.getFirst().getCategoryType();

    LocalDate claimStartDate = FeeCalculationUtil.getFeeClaimStartDate(categoryType, feeCalculationRequest);

    if (isCivil(feeCode) && claimStartDate.isBefore(minValidFromDate)) {
      throw new ValidationException(ERRCIV2, new FeeContext(feeCalculationRequest));
    }

    // filter out valid fee entity for a given input parameters
    FeeEntity feeEntity = getValidFeeEntity(feeEntityList, feeCalculationRequest, claimStartDate);

    log.info("Retrieved fee entity with feeId: {} and schemeCode: {}", feeEntity.getFeeId(),
        feeEntity.getFeeScheme().getSchemeCode());

    return feeEntity;
  }

  private static boolean isValidFee(FeeEntity fee, LocalDate claimStartDate) {
    LocalDate validFrom = fee.getFeeScheme().getValidFrom();
    LocalDate validTo = fee.getFeeScheme().getValidTo();

    return !validFrom.isAfter(claimStartDate) && (validTo == null || !claimStartDate.isAfter(validTo));
  }

  private static boolean filterByRegion(FeeEntity fee, Boolean isLondonRate) {
    if (fee.getCategoryType() != CategoryType.FAMILY) {
      return true;
    }

    return isLondonRate != null && fee.getRegion() == (isLondonRate ? Region.LONDON : Region.NON_LONDON);
  }

  private boolean isCivil(String feeCode) {
    AreaOfLawType areaOfLaw = feeDetailsService.getAreaOfLaw(feeCode);

    return areaOfLaw == AreaOfLawType.LEGAL_HELP || areaOfLaw == AreaOfLawType.MEDIATION;
  }

  private FeeEntity getValidFeeEntity(List<FeeEntity> feeEntityList,
                                                FeeCalculationRequest feeCalculationRequest, LocalDate claimStartDate) {
    return feeEntityList.stream()
        .filter(fee -> filterByRegion(fee, feeCalculationRequest.getLondonRate()))
        .filter(fee -> isValidFee(fee, claimStartDate)) // startDate <= inputDate
        .max(Comparator.comparing(fee -> fee.getFeeScheme().getValidFrom()))
        .orElseThrow(() -> new ValidationException(ERRCIV1, new FeeContext(feeCalculationRequest)));
  }
}
