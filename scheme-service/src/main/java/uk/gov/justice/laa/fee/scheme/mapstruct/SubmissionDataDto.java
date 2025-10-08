package uk.gov.justice.laa.fee.scheme.mapstruct;

import lombok.Data;

@Data
public class SubmissionDataDto {

  // example of legal help specific field
  private String legalHelpSpecific;
  // example of crime Lower specific field
  private String crimeLowerSpecific;

  // example fields outside of fee scheme api
  private String caseReference;
  private String clientForename;
  private String clientSurname;

  // Top level FeeCalculationResponse fields
  private String feeCode;
  private String schemeId;
  private String claimId;
  private Boolean caseEscaped;
  private String validationMessages;

  // Nested FeeCalculation fields
  private Double totalAmount;
  private Boolean vatOnClaim;
  private Double vatRateApplied;
  private Double vatAmount;
  private Double disbursementAmount;
  private Double disbursementVatAmount;
  private Double hourlyTotal;
  private Double fixedFee;
  private Double profitCosts;
  private Double costOfCounsel;
  private Double travelCosts;
  private Double waitingCosts;
  private Double detentionAndWaitingCosts;
  private Double jrFormFilling;
  private Double travelAndWaitingCosts;

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
