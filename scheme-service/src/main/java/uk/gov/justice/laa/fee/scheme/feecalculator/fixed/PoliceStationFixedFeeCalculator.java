package uk.gov.justice.laa.fee.scheme.feecalculator.fixed;

import static uk.gov.justice.laa.fee.scheme.enums.ErrorType.ERR_CRIME_POLICE_SCHEME_ID;
import static uk.gov.justice.laa.fee.scheme.enums.ErrorType.ERR_CRIME_POLICE_STATION_ID;
import static uk.gov.justice.laa.fee.scheme.enums.WarningType.WARN_POLICE_STATIONS_ESCAPE_THRESHOLD;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil.buildFeeCalculationResponse;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil.buildValidationWarning;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil.calculateTotalAmount;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil.calculateVatAmount;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil.getFeeClaimStartDate;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.limit.LimitUtil.isEscapedCase;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.defaultToZeroIfNull;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toBigDecimal;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toDouble;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toDoubleOrNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.entity.PoliceStationFeesEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.enums.ErrorType;
import uk.gov.justice.laa.fee.scheme.exception.FeeContext;
import uk.gov.justice.laa.fee.scheme.exception.ValidationException;
import uk.gov.justice.laa.fee.scheme.feecalculator.FeeCalculator;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner;
import uk.gov.justice.laa.fee.scheme.repository.PoliceStationFeesRepository;
import uk.gov.justice.laa.fee.scheme.service.VatRatesService;

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

  private static final String FEE_CODE_INVC = "INVC";

  private final PoliceStationFeesRepository policeStationFeesRepository;

  private final VatRatesService vatRatesService;

  /**
   * Determines the calculation based on police fee code.
   */
  @Override
  public FeeCalculationResponse calculate(FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity) {

    log.info("Calculate Police Station fixed fee");

    if (FEE_CODE_INVC.equals(feeCalculationRequest.getFeeCode())) {
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

    // Get fixed fee amount
    BigDecimal fixedFeeAmount = defaultToZeroIfNull(policeStationFeesEntity.getFixedFee());

    // Calculate VAT if applicable
    LocalDate claimStartDate = getFeeClaimStartDate(CategoryType.POLICE_STATION, feeCalculationRequest);
    Boolean vatIndicator = feeCalculationRequest.getVatIndicator();
    BigDecimal vatRate = vatRatesService.getVatRateForDate(claimStartDate, vatIndicator);
    BigDecimal calculatedVatAmount = calculateVatAmount(fixedFeeAmount, vatRate);

    // Get disbursements
    BigDecimal requestedNetDisbursementAmount = toBigDecimal(feeCalculationRequest.getNetDisbursementAmount());
    BigDecimal disbursementVatAmount = toBigDecimal(feeCalculationRequest.getDisbursementVatAmount());

    // Calculate total amount
    BigDecimal totalAmount = calculateTotalAmount(fixedFeeAmount, calculatedVatAmount,
        requestedNetDisbursementAmount, disbursementVatAmount);

    // Calculate Amount for Escape case

    BigDecimal netProfitCosts  = toBigDecimal(feeCalculationRequest.getNetProfitCosts());

    BigDecimal netTravelCosts = toBigDecimal(feeCalculationRequest.getNetTravelCosts());

    BigDecimal netWaitingCosts = toBigDecimal(feeCalculationRequest.getNetWaitingCosts());

    BigDecimal escapeTotalAmount = netProfitCosts.add(netTravelCosts).add(netWaitingCosts);

    // Escape case logic
    List<ValidationMessagesInner> validationMessages = new ArrayList<>();
    boolean isEscaped = isEscapedCase(escapeTotalAmount, policeStationFeesEntity.getEscapeThreshold());

    if (isEscaped) {
      validationMessages.add(buildValidationWarning(WARN_POLICE_STATIONS_ESCAPE_THRESHOLD,
          "Fee total exceeds escape threshold limit"));
    }

    FeeCalculation feeCalculation = FeeCalculation.builder()
        .totalAmount(toDouble(totalAmount))
        .vatIndicator(vatIndicator)
        .vatRateApplied(toDoubleOrNull(vatRate))
        .calculatedVatAmount(toDouble(calculatedVatAmount))
        .disbursementAmount(feeCalculationRequest.getNetDisbursementAmount())
        .requestedNetDisbursementAmount(feeCalculationRequest.getNetDisbursementAmount())
        .disbursementVatAmount(feeCalculationRequest.getDisbursementVatAmount())
        .fixedFeeAmount(toDouble(fixedFeeAmount))
        .build();

    return buildFeeCalculationResponse(feeCalculationRequest, feeCalculation, validationMessages,
            isEscaped, policeStationFeesEntity.getFeeSchemeCode());
  }

  private FeeCalculationResponse calculateFeesUsingFeeCode(FeeEntity feeEntity,
                                                           FeeCalculationRequest feeCalculationRequest) {

    log.info("Calculate fixed fee and costs using fee entity");

    // Get fixed fee amount
    BigDecimal fixedFeeAmount = feeEntity.getFixedFee();

    // Calculate VAT if applicable
    LocalDate claimStartDate = getFeeClaimStartDate(CategoryType.POLICE_STATION, feeCalculationRequest);
    Boolean vatIndicator = feeCalculationRequest.getVatIndicator();
    BigDecimal vatRate = vatRatesService.getVatRateForDate(claimStartDate, vatIndicator);
    BigDecimal calculatedVatAmount = calculateVatAmount(fixedFeeAmount, vatRate);

    BigDecimal totalAmount = calculateTotalAmount(fixedFeeAmount, calculatedVatAmount);

    FeeCalculation feeCalculation = FeeCalculation.builder()
        .totalAmount(toDouble(totalAmount))
        .vatIndicator(vatIndicator)
        .vatRateApplied(toDoubleOrNull(vatRate))
        .calculatedVatAmount(toDouble(calculatedVatAmount))
        .fixedFeeAmount(toDouble(fixedFeeAmount))
        .build();

    return buildFeeCalculationResponse(feeCalculationRequest, feeEntity, feeCalculation);
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
          feeCalculationRequest.getPoliceStationId(), feeEntity.getFeeScheme().getSchemeCode());

      policeStationFeesEntity = policeStationFeesRepository
          .findPoliceStationFeeByPoliceStationIdAndFeeSchemeCode(feeCalculationRequest.getPoliceStationId(),
              feeEntity.getFeeScheme().getSchemeCode())
          .stream()
          .findFirst()
          .orElseThrow(throwException(ERR_CRIME_POLICE_STATION_ID, feeCalculationRequest));

    } else if (StringUtils.isNotBlank(feeCalculationRequest.getPoliceStationSchemeId())) {

      log.info("Get police station fees entity using policeStationSchemeId: {} and schemeCode: {}",
          feeCalculationRequest.getPoliceStationSchemeId(), feeEntity.getFeeScheme().getSchemeCode());

      policeStationFeesEntity = policeStationFeesRepository
          .findPoliceStationFeeByPsSchemeIdAndFeeSchemeCode(feeCalculationRequest.getPoliceStationSchemeId(),
              feeEntity.getFeeScheme().getSchemeCode())
          .stream()
          .findFirst()
          .orElseThrow(throwException(ERR_CRIME_POLICE_SCHEME_ID, feeCalculationRequest));

    } else {
      throw new ValidationException(ERR_CRIME_POLICE_SCHEME_ID, new FeeContext(feeCalculationRequest));
    }

    log.info("Retrieved police station fees entity with policeStationFeesId: {}", policeStationFeesEntity.getPoliceStationFeesId());

    return policeStationFeesEntity;
  }

  private static Supplier<ValidationException> throwException(ErrorType error, FeeCalculationRequest feeCalculationRequest) {
    return () -> new ValidationException(error, new FeeContext(feeCalculationRequest));
  }

}