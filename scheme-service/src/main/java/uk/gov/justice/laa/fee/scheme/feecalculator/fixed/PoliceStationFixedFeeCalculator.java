package uk.gov.justice.laa.fee.scheme.feecalculator.fixed;

import static uk.gov.justice.laa.fee.scheme.feecalculator.util.VatUtil.getVatAmount;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.VatUtil.getVatRateForDate;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toBigDecimal;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toDouble;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.entity.PoliceStationFeesEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.exception.PoliceStationFeeNotFoundException;
import uk.gov.justice.laa.fee.scheme.feecalculator.FeeCalculator;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.repository.PoliceStationFeesRepository;

/**
 * Calculate the police station fee for a given fee entity and fee data.
 */
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

    if (feeCalculationRequest.getFeeCode().equals(INVC)) {
      PoliceStationFeesEntity policeStationFeesEntity = getPoliceStationFeesEntity(feeCalculationRequest, feeEntity);
      if (policeStationFeesEntity != null) {
        return calculateFeesUsingPoliceStation(policeStationFeesEntity, feeCalculationRequest);
      } else {
        throw new PoliceStationFeeNotFoundException(feeCalculationRequest.getPoliceStationSchemeId());
      }
    } else {
      return calculateFeesUsingFeeCode(feeEntity, feeCalculationRequest);
    }
  }

  /**
   * Gets fixed fee from police station fees.
   */
  private FeeCalculationResponse calculateFeesUsingPoliceStation(PoliceStationFeesEntity policeStationFeesEntity,
                                                                            FeeCalculationRequest feeCalculationRequest) {
    BigDecimal fixedFee = policeStationFeesEntity.getFixedFee();

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
        .feeCalculation(mapFeeCalculation(finalTotal, vatApplicable, startDate, calculatedVatAmount,
            netDisbursementAmount, disbursementVatAmount, fixedFee))
        .build();
  }


  private FeeCalculationResponse calculateFeesUsingFeeCode(FeeEntity feeEntity,
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
   * @param feeEntity FeeEntity
   * @return PoliceStationFeesEntity
   */
  private PoliceStationFeesEntity getPoliceStationFeesEntity(FeeCalculationRequest feeCalculationRequest,
                                                             FeeEntity feeEntity) {

    PoliceStationFeesEntity policeStationFeesEntity = null;

    if (StringUtils.isNotBlank(feeCalculationRequest.getPoliceStationId())) {
      policeStationFeesEntity = policeStationFeesRepository
          .findPoliceStationFeeByPoliceStationIdAndFeeSchemeCode(feeCalculationRequest.getPoliceStationId(),
              feeEntity.getFeeSchemeCode().getSchemeCode())
          .stream()
          .findFirst()
          .orElseThrow(() -> new PoliceStationFeeNotFoundException(feeCalculationRequest.getPoliceStationId(),
              feeCalculationRequest.getStartDate()));
    } else if (StringUtils.isNotBlank(feeCalculationRequest.getPoliceStationSchemeId())) {
      policeStationFeesEntity = policeStationFeesRepository
          .findPoliceStationFeeByPsSchemeIdAndFeeSchemeCode(feeCalculationRequest.getPoliceStationSchemeId(),
              feeEntity.getFeeSchemeCode().getSchemeCode())
          .stream()
          .findFirst()
          .orElseThrow(() -> new PoliceStationFeeNotFoundException(feeCalculationRequest.getPoliceStationSchemeId()));
    }

    return policeStationFeesEntity;
  }

  /**
   * Map fee calculation to return in response.
   *
   * @param finalTotal BigDecimal
   * @param vatApplicable Boolean
   * @param startDate LocalDate
   * @param calculatedVatAmount BigDecimal
   * @param netDisbursementAmount BigDecimal
   * @param disbursementVatAmount BigDecimal
   * @param fixedFee BigDecimal
   * @return FeeCalculation
   */
  private static FeeCalculation mapFeeCalculation(BigDecimal finalTotal, Boolean vatApplicable, LocalDate startDate,
                                                      BigDecimal calculatedVatAmount, BigDecimal netDisbursementAmount,
                                                          BigDecimal disbursementVatAmount, BigDecimal fixedFee) {
    return FeeCalculation.builder()
        .totalAmount(toDouble(finalTotal))
        .vatIndicator(vatApplicable)
        .vatRateApplied(toDouble(getVatRateForDate(startDate)))
        .calculatedVatAmount(toDouble(calculatedVatAmount))
        .disbursementAmount(toDouble(netDisbursementAmount))
        .disbursementVatAmount(toDouble(disbursementVatAmount))
        .fixedFeeAmount(toDouble(fixedFee)).build();
  }

}