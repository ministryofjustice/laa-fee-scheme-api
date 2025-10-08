package uk.gov.justice.laa.fee.scheme.mapstruct;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionJsonData {

  //assuming will be legal help, crime lower, mediation
  private String areaOfLaw;

  // example of legal help specific field
  private String legalHelpSpecific;
  // example of crime Lower specific field
  private String crimeLowerSpecific;
  private String caseReference;
  private String clientForename;
  private String clientSurname;

  private String feeCode;
  private String schemeId;
  private String claimId;
  private Boolean escapeCaseFlag;
  private List<SubmissionJsonData.ValidationMessage> validationMessages;
  private SubmissionJsonData.FeeCalculation feeCalculation;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class FeeCalculation {
    private Double totalAmount;
    private Boolean vatIndicator;
    private Double vatRateApplied;
    private Double calculatedVatAmount;
    private SubmissionJsonData.BoltOnFeeDetails boltOnFeeDetails;
    private Double disbursementAmount;
    private Double disbursementVatAmount;
    private Double hourlyTotalAmount;
    private Double fixedFeeAmount;
    private Double netProfitCostsAmount;
    private Double netCostOfCounselAmount;
    private Double netTravelCostsAmount;
    private Double netWaitingCosts;
    private Double detentionAndWaitingCostsAmount;
    private Double jrFormFillingAmount;
    private Double travelAndWaitingCostAmount;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class BoltOnFeeDetails {
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

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ValidationMessage {
    private String type;
    private String message;
  }
}
