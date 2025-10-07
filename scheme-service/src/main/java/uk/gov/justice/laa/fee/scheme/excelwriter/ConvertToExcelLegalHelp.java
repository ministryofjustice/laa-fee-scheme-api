package uk.gov.justice.laa.fee.scheme.excelwriter;

import org.springframework.stereotype.Component;

@Component
public class ConvertToExcelLegalHelp extends AbstractExcelWriter {

  private static final String[] COLUMN_ORDER = {
      "caseReference",
      "clientForename",
      "clientSurname",
      "legalHelpSpecific",
      "feeCode",
      "schemeId",
      "claimId",
      "caseEscaped",
      "validationMessages",
      "totalAmount",
      "vatOnClaim",
      "vatRateApplied",
      "vatAmount",
      "boltOnTotalFeeAmount",
      "boltOnHomeOfficeInterviewFee",
      "boltOnAdjournedHearingFee",
      "boltOnCmrhTelephoneFee",
      "boltOnCmrhOralFee",
      "disbursementAmount",
      "disbursementVatAmount",
      "hourlyTotal",
      "fixedFee",
      "profitCosts",
      "costOfCounsel",
      "travelCosts",
      "waitingCosts",
      "detentionAndWaitingCosts",
      "jrFormFilling",
      "travelAndWaitingCosts"
  };

  @Override
  protected String[] getColumnOrder() {
    return COLUMN_ORDER;
  }

  @Override
  protected String getFileName() {
    return "submission_data_legal_help.xlsx";
  }

}


