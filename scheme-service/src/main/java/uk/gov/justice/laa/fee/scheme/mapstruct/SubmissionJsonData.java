package uk.gov.justice.laa.fee.scheme.mapstruct;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionJsonData {

  private String feeCode;
  private String schemeId;
  private String claimId;
  private Boolean escapeCaseFlag;
  private List<ValidationMessage> validationMessages;
  private FeeCalculation feeCalculation;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class FeeCalculation {
    private Double totalAmount;
    private Boolean vatIndicator;
    private Double vatRateApplied;
    private Double calculatedVatAmount;
    private Double disbursementAmount;
    private Double requestedNetDisbursementAmount;
    private Double disbursementVatAmount;
    private Double fixedFeeAmount;
    private Double detentionAndWaitingCostsAmount;
    private Double jrFormFillingAmount;
    private Double netWaitingCosts;
    private BoltOnFeeDetails boltOnFeeDetails;
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
