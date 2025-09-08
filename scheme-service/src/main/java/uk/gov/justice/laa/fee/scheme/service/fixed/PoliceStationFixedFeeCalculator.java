package uk.gov.justice.laa.fee.scheme.service.fixed;

import static uk.gov.justice.laa.fee.scheme.feecalculator.type.CategoryType.POLICE_STATION;
import static uk.gov.justice.laa.fee.scheme.feecalculator.utility.NumberUtility.toBigDecimal;
import static uk.gov.justice.laa.fee.scheme.feecalculator.utility.NumberUtility.toDouble;
import static uk.gov.justice.laa.fee.scheme.feecalculator.utility.VatUtility.getVatAmount;
import static uk.gov.justice.laa.fee.scheme.feecalculator.utility.VatUtility.getVatRateForDate;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.entity.FeeSchemesEntity;
import uk.gov.justice.laa.fee.scheme.entity.PoliceStationFeesEntity;
import uk.gov.justice.laa.fee.scheme.exception.FeeNotFoundException;
import uk.gov.justice.laa.fee.scheme.exception.PoliceStationFeeNotFoundException;
import uk.gov.justice.laa.fee.scheme.feecalculator.type.CategoryType;
import uk.gov.justice.laa.fee.scheme.feecalculator.utility.DateUtility;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.repository.FeeRepository;
import uk.gov.justice.laa.fee.scheme.repository.FeeSchemesRepository;
import uk.gov.justice.laa.fee.scheme.repository.PoliceStationFeesRepository;
import uk.gov.justice.laa.fee.scheme.service.FeeCalculator;

/**
 * Implementation class for police station fixed fee category.
 */
@RequiredArgsConstructor
@Component
public class PoliceStationFixedFeeCalculator implements FeeCalculator {

  private static final String INVC = "INVC";

  private final FeeRepository feeRepository;

  private final FeeSchemesRepository feeSchemesRepository;

  private final PoliceStationFeesRepository policeStationFeesRepository;

  @Override
  public CategoryType getCategory() {
    return POLICE_STATION;
  }

  /**
   * Determines the calculation based on police fee code.
   */
  @Override
  public FeeCalculationResponse calculate(FeeCalculationRequest feeCalculationRequest) {


    if (StringUtils.isNotBlank(feeCalculationRequest.getUniqueFileNumber())) {
      LocalDate caseStartDate = DateUtility.toLocalDate(feeCalculationRequest.getUniqueFileNumber());
      feeCalculationRequest.setStartDate(caseStartDate);
    }

    FeeSchemesEntity feeSchemesEntity = feeSchemesRepository
        .findValidSchemeForDate(feeCalculationRequest.getFeeCode(), feeCalculationRequest.getStartDate(), PageRequest.of(0, 1))
        .stream()
        .findFirst()
        .orElseThrow(() -> new FeeNotFoundException(feeCalculationRequest.getFeeCode(), feeCalculationRequest.getStartDate()));

    FeeEntity feeEntity = feeRepository.findByFeeCodeAndFeeSchemeCode(feeCalculationRequest.getFeeCode(), feeSchemesEntity)
        .orElseThrow(() -> new FeeNotFoundException(feeCalculationRequest.getFeeCode(), feeCalculationRequest.getStartDate()));

    PoliceStationFeesEntity policeStationFeesEntity = getPoliceStationFeesEntity(feeCalculationRequest, feeSchemesEntity);

    if (feeCalculationRequest.getFeeCode().equals(INVC)) {
      return calculateFeesUsingPoliceStation(policeStationFeesEntity, feeCalculationRequest);
    } else {
      return calculateFeesUsingFeeCode(feeEntity, feeCalculationRequest);
    }
  }

  /**
   * Gets fixed fee from police station fees.
   */
  private static FeeCalculationResponse calculateFeesUsingPoliceStation(PoliceStationFeesEntity policeStationFeesEntity,
                                                                        FeeCalculationRequest feeData) {
    BigDecimal baseFee = policeStationFeesEntity.getFixedFee();

    return calculateAndBuildResponsePoliceStation(baseFee, feeData, policeStationFeesEntity);
  }



  private static FeeCalculationResponse calculateAndBuildResponsePoliceStation(BigDecimal fixedFee,
                                                                               FeeCalculationRequest feeCalculationRequest,
                                                                               PoliceStationFeesEntity policeStationFeesEntity) {
    BigDecimal netDisbursementAmount = toBigDecimal(feeCalculationRequest.getNetDisbursementAmount());
    BigDecimal disbursementVatAmount = toBigDecimal(feeCalculationRequest.getDisbursementVatAmount());

    // Apply VAT where applicable
    Boolean vatApplicable = feeCalculationRequest.getVatIndicator();
    LocalDate startDate = feeCalculationRequest.getStartDate();
    BigDecimal calculatedVatAmount = getVatAmount(fixedFee, startDate, vatApplicable);

    BigDecimal finalTotal = fixedFee
        .add(calculatedVatAmount)
        .add(netDisbursementAmount)
        .add(disbursementVatAmount);

    return FeeCalculationResponse.builder()
        .feeCode(feeCalculationRequest.getFeeCode())
        .schemeId(policeStationFeesEntity.getFeeSchemeCode())
        .escapeCaseFlag(false) // temp hard coded, till escape logic implemented
        .feeCalculation(FeeCalculation.builder()
            .totalAmount(toDouble(finalTotal))
            .vatIndicator(vatApplicable)
            .vatRateApplied(toDouble(getVatRateForDate(startDate)))
            .calculatedVatAmount(toDouble(calculatedVatAmount))
            .disbursementAmount(toDouble(netDisbursementAmount))
            .disbursementVatAmount(toDouble(disbursementVatAmount))
            .fixedFeeAmount(toDouble(fixedFee)).build())
        .build();
  }

  private static FeeCalculationResponse calculateFeesUsingFeeCode(FeeEntity feeEntity,
                                                                  FeeCalculationRequest feeCalculationRequest) {

    BigDecimal fixedFee = feeEntity.getFixedFee();
    LocalDate startDate = feeCalculationRequest.getStartDate();
    Boolean vatApplicable = feeCalculationRequest.getVatIndicator();
    BigDecimal fixedFeeVatAmount = getVatAmount(fixedFee, startDate, vatApplicable);

    BigDecimal finalTotal = fixedFee.add(fixedFeeVatAmount);

    return FeeCalculationResponse.builder()
        .feeCode(feeCalculationRequest.getFeeCode())
        .schemeId(feeEntity.getFeeSchemeCode().getSchemeCode())
        .escapeCaseFlag(false) // temp hard coded, till escape logic implemented
        .feeCalculation(FeeCalculation.builder()
            .totalAmount(toDouble(finalTotal))
            .vatIndicator(vatApplicable)
            .vatRateApplied(toDouble(getVatRateForDate(startDate)))
            .calculatedVatAmount(toDouble(fixedFeeVatAmount))
            .fixedFeeAmount(toDouble(fixedFee))
            .build()).build();
  }


  /**
   * Retrieving Police Station Fee using Police Station ID/ PS Scheme ID and Fee Scheme Code.
   *
   * @param feeCalculationRequest FeeCalculationRequest
   * @param feeSchemesEntity FeeSchemesEntity
   * @return PoliceStationFeesEntity
   */
  private PoliceStationFeesEntity getPoliceStationFeesEntity(FeeCalculationRequest feeCalculationRequest,
                                                             FeeSchemesEntity feeSchemesEntity) {
    PoliceStationFeesEntity policeStationFeesEntity = null;
    if (feeCalculationRequest.getFeeCode().equals(INVC)) {
      if (StringUtils.isNotBlank(feeCalculationRequest.getPoliceStationId())) {
        policeStationFeesEntity = policeStationFeesRepository
            .findPoliceStationFeeByPoliceStationIdAndFeeSchemeCode(feeCalculationRequest.getPoliceStationId(),
                feeSchemesEntity.getSchemeCode())
            .stream()
            .findFirst()
            .orElseThrow(() -> new PoliceStationFeeNotFoundException(feeCalculationRequest.getPoliceStationId(),
                feeCalculationRequest.getStartDate()));
      } else if (StringUtils.isNotBlank(feeCalculationRequest.getPoliceStationSchemeId())) {
        policeStationFeesEntity = policeStationFeesRepository
            .findPoliceStationFeeByPsSchemeIdAndFeeSchemeCode(feeCalculationRequest.getPoliceStationSchemeId(),
                feeSchemesEntity.getSchemeCode())
            .stream()
            .findFirst()
            .orElseThrow(() -> new PoliceStationFeeNotFoundException(feeCalculationRequest.getPoliceStationSchemeId()));
      }
    }
    return policeStationFeesEntity;
  }
}
