package uk.gov.justice.laa.fee.scheme.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.entity.FeeSchemesEntity;
import uk.gov.justice.laa.fee.scheme.feeCalculators.CalculateMediationFee;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.repository.FeeRepository;
import uk.gov.justice.laa.fee.scheme.repository.FeeSchemesRepository;

/**
 * Initial service for determining category law code, and for fee calculation.
 */
@RequiredArgsConstructor
@Service
public class FeeService {

  private final FeeRepository feeRepository;
  private final FeeSchemesRepository feeSchemesRepository;

  /**
   * Initial method for determining fee calculation, using fee data.
   */
  public FeeCalculationResponse getFeeCalculation(FeeCalculationRequest feeData) {

    //   FIND THE SCHEME ENTITY ID USING THE FEE CODE AND THE START DATE
    FeeSchemesEntity feeSchemesEntity = feeSchemesRepository
        .findValidSchemeForDate(feeData.getFeeCode(), feeData.getStartDate())
        .orElseThrow(() -> new EntityNotFoundException(
            "No valid scheme found for group " + feeData.getFeeCode() + " on date " + feeData.getStartDate())
        );
    String schemeId = feeSchemesEntity.getSchemeCode();


    //  FIND THE FEE ENTITY USING THE FEE CODE AND THE FEE SCHEME ID PREVIOUSLY DETERMINED
    FeeEntity feeEntity = feeRepository.findByFeeCodeAndFeeSchemeCode(feeData.getFeeCode(), schemeId)
        .orElseThrow(() -> new EntityNotFoundException(
            "Fee not found for code: " + feeData.getFeeCode()
        ));

    System.out.println("FeeEntity " + feeEntity);
    // I.E.
    // "feeCode": "INVA",
    //  "startDate": "2022-09-30",
    // WILL FIND ENTITY CORRESPONDING WITH SCHEME POL_FS2022
    //FeeEntity FeeEntity(feeId=8, feeCode=INVA, description=Advice and Assistance (not at the police station), feeSchemeCode=POL_FS2022, totalFee=null, profitCostLimit=314.81, disbursementLimit=null, escapeThresholdLimit=null, priorAuthorityApplicable=null, scheduleReference=null, hoInterviewBoltOn=null, oralCmrhBoltOn=null, telephoneCmrhBoltOn=null, substantiveHearingBoltOn=null, adjornHearingBoltOn=null, mediationSessionOne=null, mediationSessionTwo=null, region=null, calculationType=null)
    //
     // "feeCode": "INVA",
    //   "startDate": "2021-02-30",
    // WILL FIND ENTITY CORRESPONDING WITH SCHEME POL_FS2016
    //FeeEntity FeeEntity(feeId=1, feeCode=INVA, description=Advice and Assistance (not at the police station), feeSchemeCode=POL_FS2016, totalFee=null, profitCostLimit=273.75, disbursementLimit=null, escapeThresholdLimit=null, priorAuthorityApplicable=null, scheduleReference=null, hoInterviewBoltOn=null, oralCmrhBoltOn=null, telephoneCmrhBoltOn=null, substantiveHearingBoltOn=null, adjornHearingBoltOn=null, mediationSessionOne=null, mediationSessionTwo=null, region=null, calculationType=null)


    String calculationType = feeEntity.getCalculationType();
    getCalculation(calculationType, feeEntity);

    return null;
  }

  public FeeCalculationResponse getCalculation(String calculationType, FeeEntity feeEntity) {

    return switch (calculationType) {
      case "MEDIATION" -> CalculateMediationFee.getFee(feeEntity);
      case "another one" -> null;
      default -> null;
    };

  }

}

