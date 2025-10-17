package uk.gov.justice.laa.fee.scheme.service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.enums.Region;
import uk.gov.justice.laa.fee.scheme.exception.FeeNotFoundException;
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

  /**
   * Returns FeeEntity after making calls to database.
   *
   * @param feeCalculationRequest FeeCalculationRequest
   * @return FeeEntity
   */
  public FeeEntity getFeeEntity(FeeCalculationRequest feeCalculationRequest) {

    log.info("Get filtered fee entity");

    List<FeeEntity> feeEntityList = feeRepository.findByFeeCode(feeCalculationRequest.getFeeCode());

    if (!feeEntityList.isEmpty()) {

      CategoryType categoryType = feeEntityList.getFirst().getCategoryType();

      LocalDate claimStartDate = FeeCalculationUtil.getFeeClaimStartDate(categoryType, feeCalculationRequest);

      // filter out valid fee entity for a given input parameters
      Optional<FeeEntity> feeEntityOptional =  feeEntityList.stream()
          .filter(fee -> filterByRegion(fee, feeCalculationRequest.getLondonRate()))
          .filter(fee -> isValidFee(fee, claimStartDate)) // startDate <= inputDate
          .max(Comparator.comparing(fee -> fee.getFeeScheme().getValidFrom()));

      FeeEntity feeEntity = feeEntityOptional
          .orElseThrow(() -> new FeeNotFoundException(feeCalculationRequest.getFeeCode(), claimStartDate));

      log.info("Retrieved fee entity with feeId: {} and schemeCode: {}", feeEntity.getFeeId(),
          feeEntity.getFeeScheme().getSchemeCode());

      return feeEntity;

    } else {
      throw new FeeNotFoundException(feeCalculationRequest.getFeeCode(), feeCalculationRequest.getStartDate());
    }
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
}
