package uk.gov.justice.laa.fee.scheme.mapstruct;

import lombok.Data;

@Data
public class SubmissionDataDto {

  // Top-level FeeCalculationResponse fields
  private String feeCode;
  private String schemeId;
  private String claimId;
  private Boolean escapeCaseFlag;

  // Validation messages (optional, could flatten or keep as JSON string)
  private String validationMessages;

  // Nested FeeCalculation fields
  private Double totalAmount;
  private Boolean vatIndicator;
  private Double vatRateApplied;
  private Double calculatedVatAmount;
  private Double disbursementAmount;
  private Double requestedNetDisbursementAmount;
  private Double disbursementVatAmount;
  private Double hourlyTotalAmount;
  private Double fixedFeeAmount;
  private Double netProfitCostsAmount;
  private Double requestedNetProfitCostsAmount;
  private Double netCostOfCounselAmount;
  private Double netTravelCostsAmount;
  private Double netWaitingCosts;
  private Double detentionAndWaitingCostsAmount;
  private Double jrFormFillingAmount;
  private Double travelAndWaitingCostAmount;

  // Nested BoltOnFeeDetails
  private Double boltOnTotalFeeAmount;
  private Integer boltOnAdjournedHearingCount;
  private Double boltOnAdjournedHearingFee;
  private Integer boltOnCmrhTelephoneCount;
  private Double boltOnCmrhTelephoneFee;
  private Integer boltOnCmrhOralCount;
  private Double boltOnCmrhOralFee;
  private Integer boltOnHomeOfficeInterviewCount;
  private Double boltOnHomeOfficeInterviewFee;

}
