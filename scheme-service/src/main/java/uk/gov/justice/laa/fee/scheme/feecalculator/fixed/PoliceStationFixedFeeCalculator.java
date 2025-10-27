package uk.gov.justice.laa.fee.scheme.feecalculator.fixed;

import static uk.gov.justice.laa.fee.scheme.enums.ErrorType.ERR_CRIME_POLICE_SCHEME_ID;
import static uk.gov.justice.laa.fee.scheme.enums.ErrorType.ERR_CRIME_POLICE_STATION_ID;
import static uk.gov.justice.laa.fee.scheme.enums.WarningType.WARN_POLICE_STATIONS_ESCAPE_THRESHOLD;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil.isEscapedCase;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.VatUtil.getVatAmount;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.VatUtil.getVatRateForDate;
import static uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner.TypeEnum.WARNING;
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
import uk.gov.justice.laa.fee.scheme.exception.PoliceStationFeeNotFoundException;
import uk.gov.justice.laa.fee.scheme.exception.ValidationException;
import uk.gov.justice.laa.fee.scheme.feecalculator.FeeCalculator;
import uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner;
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
    BigDecimal requestedNetDisbursementAmount = toBigDecimal(feeCalculationRequest.getNetDisbursementAmount());
    BigDecimal disbursementVatAmount = toBigDecimal(feeCalculationRequest.getDisbursementVatAmount());

    // Apply VAT where applicable
    Boolean vatApplicable = feeCalculationRequest.getVatIndicator();
    LocalDate claimStartDate = FeeCalculationUtil
        .getFeeClaimStartDate(CategoryType.POLICE_STATION, feeCalculationRequest);
    BigDecimal calculatedVatAmount = getVatAmount(fixedFee, claimStartDate, vatApplicable);

    BigDecimal totalAmount = FeeCalculationUtil.calculateTotalAmount(fixedFee, calculatedVatAmount,
        requestedNetDisbursementAmount, disbursementVatAmount);

    List<ValidationMessagesInner> validationMessages = new ArrayList<>();
    boolean isEscaped = isEscapedCase(totalAmount, policeStationFeesEntity.getEscapeThreshold());

    if (isEscaped) {
      log.warn("Fee total exceeds escape threshold limit");
      validationMessages.add(ValidationMessagesInner.builder()
          .code(WARN_POLICE_STATIONS_ESCAPE_THRESHOLD.getCode())
          .message(WARN_POLICE_STATIONS_ESCAPE_THRESHOLD.getMessage())
          .type(WARNING)
          .build());
    }

    log.info("Build fee calculation response");
    return FeeCalculationResponse.builder()
        .feeCode(feeCalculationRequest.getFeeCode())
        .schemeId(policeStationFeesEntity.getFeeSchemeCode())
        .claimId(feeCalculationRequest.getClaimId())
        .escapeCaseFlag(isEscaped)
        .validationMessages(validationMessages)
        .feeCalculation(FeeCalculation.builder()
            .totalAmount(toDouble(totalAmount))
            .vatIndicator(vatApplicable)
            .vatRateApplied(toDoubleOrNull(getVatRateForDate(claimStartDate, vatApplicable)))
            .calculatedVatAmount(toDouble(calculatedVatAmount))
            .disbursementAmount(feeCalculationRequest.getNetDisbursementAmount())
            .requestedNetDisbursementAmount(feeCalculationRequest.getNetDisbursementAmount())
            .disbursementVatAmount(feeCalculationRequest.getDisbursementVatAmount())
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
    Boolean vatApplicable = feeCalculationRequest.getVatIndicator();
    BigDecimal fixedFeeVatAmount = getVatAmount(fixedFee, claimStartDate, vatApplicable);
    BigDecimal calculatedVatAmount = getVatAmount(fixedFee, claimStartDate, vatApplicable);

    BigDecimal totalAmount = FeeCalculationUtil.calculateTotalAmount(fixedFee, calculatedVatAmount);

    log.info("Build fee calculation response");
    return FeeCalculationResponse.builder()
        .feeCode(feeCalculationRequest.getFeeCode())
        .schemeId(feeEntity.getFeeScheme().getSchemeCode())
        .claimId(feeCalculationRequest.getClaimId())
        .escapeCaseFlag(false) // temp hard coded, till escape logic implemented
        .feeCalculation(FeeCalculation.builder()
            .totalAmount(toDouble(totalAmount))
            .vatIndicator(vatApplicable)
            .vatRateApplied(toDoubleOrNull(getVatRateForDate(claimStartDate, vatApplicable)))
            .calculatedVatAmount(toDouble(fixedFeeVatAmount))
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
      throw new PoliceStationFeeNotFoundException(feeCalculationRequest.getPoliceStationSchemeId());
    }

    log.info("Retrieved police station fees entity with policeStationFeesId: {}", policeStationFeesEntity.getPoliceStationFeesId());

    return policeStationFeesEntity;
  }

  private static Supplier<ValidationException> throwException(ErrorType error, FeeCalculationRequest feeCalculationRequest) {
    return () -> new ValidationException(error, new FeeContext(feeCalculationRequest));
  }

}