package uk.gov.justice.laa.fee.scheme.mapstruct;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SubmissionDataMapperInterface {
  SubmissionDataMapperInterface INSTANCE = Mappers.getMapper(SubmissionDataMapperInterface.class);

  // example of field specific to legal help
  @Mapping(source = "legalHelpSpecific", target = "legalHelpSpecific")
  // example of field specific to crime lower
  @Mapping(source = "crimeLowerSpecific", target = "crimeLowerSpecific")

  // example fields outside of fee scheme api
  @Mapping(source = "caseReference", target = "caseReference")
  @Mapping(source = "clientForename", target = "clientForename")
  @Mapping(source = "clientSurname", target = "clientSurname")

  // top level fee calculation response
  @Mapping(source = "feeCode", target = "feeCode")
  @Mapping(expression = "java(DataMappingUtil.mapSchemeId(submissionJsonData))", target = "schemeId")
  @Mapping(source = "claimId", target = "claimId")
  @Mapping(source = "escapeCaseFlag", target = "caseEscaped")
  @Mapping(expression = "java(DataMappingUtil.mapValidationMessages(submissionJsonData))", target = "validationMessages")

  // nested fee calculation response details
  @Mapping(source = "feeCalculation.totalAmount", target = "totalAmount")
  @Mapping(source = "feeCalculation.vatIndicator", target = "vatOnClaim")
  @Mapping(source = "feeCalculation.vatRateApplied", target = "vatRateApplied")
  @Mapping(source = "feeCalculation.calculatedVatAmount", target = "vatAmount")
  @Mapping(source = "feeCalculation.boltOnFeeDetails.boltOnTotalFeeAmount", target = "boltOnTotalFeeAmount")
  @Mapping(source = "feeCalculation.boltOnFeeDetails.boltOnHomeOfficeInterviewFee", target = "boltOnHomeOfficeInterviewFee")
  @Mapping(source = "feeCalculation.boltOnFeeDetails.boltOnAdjournedHearingFee", target = "boltOnAdjournedHearingFee")
  @Mapping(source = "feeCalculation.boltOnFeeDetails.boltOnCmrhTelephoneFee", target = "boltOnCmrhTelephoneFee")
  @Mapping(source = "feeCalculation.boltOnFeeDetails.boltOnCmrhOralFee", target = "boltOnCmrhOralFee")
  @Mapping(source = "feeCalculation.disbursementAmount", target = "disbursementAmount")
  @Mapping(source = "feeCalculation.disbursementVatAmount", target = "disbursementVatAmount")
  @Mapping(source = "feeCalculation.hourlyTotalAmount", target = "hourlyTotal")
  @Mapping(source = "feeCalculation.fixedFeeAmount", target = "fixedFee")
  @Mapping(source = "feeCalculation.netProfitCostsAmount", target = "profitCosts")
  @Mapping(source = "feeCalculation.netCostOfCounselAmount", target = "costOfCounsel")
  @Mapping(source = "feeCalculation.netTravelCostsAmount", target = "travelCosts")
  @Mapping(source = "feeCalculation.netWaitingCosts", target = "waitingCosts")
  @Mapping(source = "feeCalculation.detentionAndWaitingCostsAmount", target = "detentionAndWaitingCosts")
  @Mapping(source = "feeCalculation.jrFormFillingAmount", target = "jrFormFilling")
  @Mapping(source = "feeCalculation.travelAndWaitingCostAmount", target = "travelAndWaitingCosts")
  SubmissionDataDto toDto(SubmissionJsonData submissionJsonData);
}
