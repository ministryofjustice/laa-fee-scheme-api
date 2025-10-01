package uk.gov.justice.laa.fee.scheme.feecalculator.fixed;

import static uk.gov.justice.laa.fee.scheme.feecalculator.util.VatUtil.getVatAmount;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.VatUtil.getVatRateForDate;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toBigDecimal;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toDouble;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.entity.PoliceStationFeesEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.exception.PoliceStationFeeNotFoundException;
import uk.gov.justice.laa.fee.scheme.feecalculator.FeeCalculator;
import uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.repository.PoliceStationFeesRepository;

/**
 * Calculate the police station fee for a given fee entity and fee data.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PoliceStationFixedFeeCalculator implements FeeCalculator {

  @Override
  public Set<CategoryType> getSupportedCategories() {
    return Set.of(); // Only used by PoliceStationFeeCalculator and not available via FeeCalculatorFactory
  }

  private static final String INVC = "INVC";

  private final PoliceStationFeesRepository policeStationFeesRepository;

  /**
   * Determines the calculation based on police fee code.
   */
  public FeeCalculationResponse calculate(FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity) {

    log.info("Calculate Police Station fixed fee");

    if (feeCalculationRequest.getFeeCode().equals(INVC)) {
      PoliceStationFeesEntity policeStationFeesEntity = getPoliceStationFeesEntity(feeCalculationRequest, feeEntity);
      return calculateFeesUsingPoliceStation(policeStationFeesEntity, feeCalculationRequest);
    } else {
      return calculateFeesUsingFeeCode(feeEntity, feeCalculationRequest);
    }
  }

  /**
   * Gets fixed fee from police station fees.
   */
  private FeeCalculationResponse calculateFeesUsingPoliceStation(PoliceStationFeesEntity policeStationFeesEntity,
                                                                 FeeCalculationRequest feeCalculationRequest) {

    log.info("Calculate fixed fee and costs using police station fees entity");

    BigDecimal fixedFee = policeStationFeesEntity.getFixedFee();
    BigDecimal netDisbursementAmount = toBigDecimal(feeCalculationRequest.getNetDisbursementAmount());
    BigDecimal disbursementVatAmount = toBigDecimal(feeCalculationRequest.getDisbursementVatAmount());

    // Apply VAT where applicable
    LocalDate startDate = feeCalculationRequest.getStartDate();
    Boolean vatApplicable = feeCalculationRequest.getVatIndicator();
    LocalDate claimStartDate = FeeCalculationUtil
        .getFeeClaimStartDate(CategoryType.POLICE_STATION, feeCalculationRequest);
    BigDecimal calculatedVatAmount = getVatAmount(fixedFee, claimStartDate, vatApplicable);

    BigDecimal totalAmount = FeeCalculationUtil.calculateTotalAmount(fixedFee, calculatedVatAmount,
        netDisbursementAmount, disbursementVatAmount);

    log.info("Build fee calculation response");
    return FeeCalculationResponse.builder()
        .feeCode(feeCalculationRequest.getFeeCode())
        .schemeId(policeStationFeesEntity.getFeeSchemeCode())
        .escapeCaseFlag(false) // temp hard coded, till escape logic implemented
        .feeCalculation(FeeCalculation.builder()
            .totalAmount(toDouble(totalAmount))
            .vatIndicator(vatApplicable)
            .vatRateApplied(toDouble(getVatRateForDate(startDate)))
            .calculatedVatAmount(toDouble(calculatedVatAmount))
            .disbursementAmount(toDouble(netDisbursementAmount))
            .disbursementVatAmount(toDouble(disbursementVatAmount))
            .fixedFeeAmount(toDouble(fixedFee)).build())
        .build();
  }


  private FeeCalculationResponse calculateFeesUsingFeeCode(FeeEntity feeEntity,
                                                           FeeCalculationRequest feeCalculationRequest) {

    log.info("Calculate fixed fee and costs using fee entity");

    BigDecimal fixedFee = feeEntity.getFixedFee();
    LocalDate claimStartDate = FeeCalculationUtil
        .getFeeClaimStartDate(CategoryType.POLICE_STATION, feeCalculationRequest);

    // Apply VAT where applicable
    LocalDate startDate = feeCalculationRequest.getStartDate();
    Boolean vatApplicable = feeCalculationRequest.getVatIndicator();
    BigDecimal fixedFeeVatAmount = getVatAmount(fixedFee, claimStartDate, vatApplicable);
    BigDecimal calculatedVatAmount = getVatAmount(fixedFee, startDate, vatApplicable);

    BigDecimal totalAmount = FeeCalculationUtil.calculateTotalAmount(fixedFee, calculatedVatAmount);

    log.info("Build fee calculation response");
    return FeeCalculationResponse.builder()
        .feeCode(feeCalculationRequest.getFeeCode())
        .schemeId(feeEntity.getFeeSchemeCode().getSchemeCode())
        .escapeCaseFlag(false) // temp hard coded, till escape logic implemented
        .feeCalculation(FeeCalculation.builder()
            .totalAmount(toDouble(totalAmount))
            .vatIndicator(vatApplicable)
            .vatRateApplied(toDouble(getVatRateForDate(claimStartDate)))
            .calculatedVatAmount(toDouble(fixedFeeVatAmount))
            .vatRateApplied(toDouble(getVatRateForDate(startDate)))
            .calculatedVatAmount(toDouble(calculatedVatAmount))
            .fixedFeeAmount(toDouble(fixedFee))
            .build()).build();
  }

  /**
   * Retrieving Police Station Fee using Police Station ID/ PS Scheme ID and Fee Scheme Code.
   *
   * @param feeCalculationRequest FeeCalculationRequest
   * @param feeEntity             FeeEntity
   * @return PoliceStationFeesEntity
   */
  private PoliceStationFeesEntity getPoliceStationFeesEntity(FeeCalculationRequest feeCalculationRequest,
                                                             FeeEntity feeEntity) {

    PoliceStationFeesEntity policeStationFeesEntity;

    if (StringUtils.isNotBlank(feeCalculationRequest.getPoliceStationId())) {

      log.info("Get police station fees entity using policeStationId {} and schemeCode: {}",
          feeCalculationRequest.getPoliceStationId(), feeEntity.getFeeSchemeCode().getSchemeCode());

      policeStationFeesEntity = policeStationFeesRepository
          .findPoliceStationFeeByPoliceStationIdAndFeeSchemeCode(feeCalculationRequest.getPoliceStationId(),
              feeEntity.getFeeSchemeCode().getSchemeCode())
          .stream()
          .findFirst()
          .orElseThrow(() -> new PoliceStationFeeNotFoundException(feeCalculationRequest.getPoliceStationId(),
              feeCalculationRequest.getStartDate()));
    } else if (StringUtils.isNotBlank(feeCalculationRequest.getPoliceStationSchemeId())) {

      log.info("Get police station fees entity using policeStationSchemeId: {} and schemeCode: {}",
          feeCalculationRequest.getPoliceStationSchemeId(), feeEntity.getFeeSchemeCode().getSchemeCode());

      policeStationFeesEntity = policeStationFeesRepository
          .findPoliceStationFeeByPsSchemeIdAndFeeSchemeCode(feeCalculationRequest.getPoliceStationSchemeId(),
              feeEntity.getFeeSchemeCode().getSchemeCode())
          .stream()
          .findFirst()
          .orElseThrow(() -> new PoliceStationFeeNotFoundException(feeCalculationRequest.getPoliceStationSchemeId()));
    } else {
      throw new PoliceStationFeeNotFoundException(feeCalculationRequest.getPoliceStationSchemeId());
    }

    log.info("Retrieved police station fees entity with policeStationFeesId: {}", policeStationFeesEntity.getPoliceStationFeesId());

    return policeStationFeesEntity;
  }

}