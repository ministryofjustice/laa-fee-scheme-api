package uk.gov.justice.laa.fee.scheme.api.feecalculation;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class FeeCalculationHourlyRateIntegrationTest extends BaseFeeCalculationIntegrationTest {

  @Test
  void shouldGetFeeCalculation_adviceAssistanceAdvocacyHourlyRate() throws Exception {
    String request = """ 
        {
          "feeCode": "PROD",
          "claimId": "claim_123",
          "caseConcludedDate": "2024-12-06",
          "netProfitCosts": 500,
          "netDisbursementAmount": 100,
          "disbursementVatAmount": 20,
          "vatIndicator": true,
          "netTravelCosts": 50,
          "netWaitingCosts": 50
        }
        """;

    postAndExpect(request, """
        {
          "feeCode": "PROD",
          "schemeId": "AAA_FS2016",
          "claimId": "claim_123",
          "feeCalculation": {
              "totalAmount": 840.0,
              "vatIndicator": true,
              "vatRateApplied": 20.0,
              "calculatedVatAmount": 120.0,
              "disbursementAmount": 100.0,
              "requestedNetDisbursementAmount": 100.0,
              "disbursementVatAmount": 20.0,
              "hourlyTotalAmount": 600.0,
              "netProfitCostsAmount": 500.0,
              "requestedNetProfitCostsAmount": 500.0,
              "netTravelCostsAmount": 50.0,
              "netWaitingCostsAmount": 50.0
          }
        }
        """);
  }

  @ParameterizedTest
  @CsvSource({
      "PROH, 211225/456, AAR_FS2022, 810.0, 120.0, 600.0, 500.0, 500.0",
      "APPA, 211225/456, AAR_FS2022, 261.6, 28.6, 143.0, 43.0, 43.0",
      "APPB, 211225/456, AAR_FS2022, 354.0, 44.0, 220.0, 120.0, 120.0",
      "PROH1, 221225/456, AAR_FS2025, 810.0, 120.0, 600.0, 500.0, 500.0",
      "PROH2, 221225/456, AAR_FS2025, 810.0, 120.0, 600.0, 500.0, 500.0",
      "APPA, 221225/456, AAR_FS2025, 261.6, 28.6, 143.0, 43.0, 43.0",
      "APPB, 221225/456, AAR_FS2025, 354.0, 44.0, 220.0, 120.0, 120.0"
  })
  void shouldGetFeeCalculation_advocacyAppealsReviewHourlyRate(String feeCode,
                                                     String uniqueFileNumber,
                                                     String schemeId,
                                                     String expectedTotal,
                                                     String expectedVatAmount,
                                                     String expectedHourlyTotalAmount,
                                                     String netProfitCostsAmount,
                                                     String requestedNetProfitCostsAmount
  ) throws Exception {
    String request = """ 
        {
          "feeCode": "%s",
          "uniqueFileNumber": "%s",
          "netProfitCosts": %s,
          "netDisbursementAmount": 80,
          "disbursementVatAmount": 10,
          "vatIndicator": true,
          "netTravelCosts": 50,
          "netWaitingCosts": 50
        }
        """.formatted(feeCode, uniqueFileNumber, netProfitCostsAmount);

    postAndExpect(request, """
        {
          "feeCode": "%s",
          "schemeId": "%s",
          "feeCalculation": {
            "totalAmount": %s,
            "vatIndicator": true,
            "vatRateApplied": 20.00,
            "calculatedVatAmount": %s,
            "disbursementAmount": 80.0,
            "requestedNetDisbursementAmount": 80.0,
            "disbursementVatAmount": 10.0,
            "hourlyTotalAmount": %s,
            "netProfitCostsAmount": %s,
            "requestedNetProfitCostsAmount": %s,
            "netTravelCostsAmount": 50,
            "netWaitingCostsAmount": 50
          }
        }
        """.formatted(feeCode, schemeId, expectedTotal, expectedVatAmount, expectedHourlyTotalAmount, netProfitCostsAmount, requestedNetProfitCostsAmount));
  }

  @ParameterizedTest
  @CsvSource({
      "PROH, 2025-12-21, 111111/456, AAR_FS2022, 810.0, 120.0, 600.0, 500.0, 500.0",
      "PROH1, 2025-12-22, 111111/456, AAR_FS2025, 810.0, 120.0, 600.0, 500.0, 500.0",
      "PROH2, 2025-12-22, 111111/456, AAR_FS2025, 810.0, 120.0, 600.0, 500.0, 500.0"
  })
  void shouldGetFeeCalculation_advocacyAppealsReviewHourlyRate_PROH_repOrderDate(String feeCode,
                                                               String repOrderDate,
                                                               String uniqueFileNumber,
                                                               String schemeId,
                                                               String expectedTotal,
                                                               String expectedVatAmount,
                                                               String expectedHourlyTotalAmount,
                                                               String netProfitCostsAmount,
                                                               String requestedNetProfitCostsAmount
  ) throws Exception {
    LocalDate representationOrderDate = LocalDate.parse(repOrderDate);

    String request = """ 
        {
          "feeCode": "%s",
          "representationOrderDate": "%s",
          "uniqueFileNumber": "%s",
          "netProfitCosts": %s,
          "netDisbursementAmount": 80,
          "disbursementVatAmount": 10,
          "vatIndicator": true,
          "netTravelCosts": 50,
          "netWaitingCosts": 50
        }
        """.formatted(feeCode, representationOrderDate, uniqueFileNumber, netProfitCostsAmount);

    postAndExpect(request, """
        {
          "feeCode": "%s",
          "schemeId": "%s",
          "feeCalculation": {
            "totalAmount": %s,
            "vatIndicator": true,
            "vatRateApplied": 20.00,
            "calculatedVatAmount": %s,
            "disbursementAmount": 80.0,
            "requestedNetDisbursementAmount": 80.0,
            "disbursementVatAmount": 10.0,
            "hourlyTotalAmount": %s,
            "netProfitCostsAmount": %s,
            "requestedNetProfitCostsAmount": %s,
            "netTravelCostsAmount": 50,
            "netWaitingCostsAmount": 50
          }
        }
        """.formatted(feeCode, schemeId, expectedTotal, expectedVatAmount, expectedHourlyTotalAmount, netProfitCostsAmount, requestedNetProfitCostsAmount));
  }

  @Test
  void shouldGetFeeCalculation_discriminationHourlyRate() throws Exception {
    String request = """ 
        {
          "feeCode": "DISC",
          "claimId": "claim_123",
          "startDate": "2019-09-30",
          "netProfitCosts": 239.06,
          "netCostOfCounsel": 79.19,
          "netDisbursementAmount": 100.21,
          "disbursementVatAmount": 20.12,
          "vatIndicator": true
        }
        """;

    postAndExpect(request, """
        {
          "feeCode": "DISC",
          "schemeId": "DISC_FS2013",
          "claimId": "claim_123",
          "escapeCaseFlag": false,
          "feeCalculation": {
            "totalAmount": 502.23,
            "vatIndicator": true,
            "vatRateApplied": 20.00,
            "calculatedVatAmount": 63.65,
            "disbursementAmount": 100.21,
            "requestedNetDisbursementAmount": 100.21,
            "disbursementVatAmount": 20.12,
            "hourlyTotalAmount": 318.25,
            "netCostOfCounselAmount": 79.19,
            "netProfitCostsAmount": 239.06,
            "requestedNetProfitCostsAmount": 239.06
          }
        }
        """);
  }

  @ParameterizedTest
  @CsvSource({
      "IAXC, IMM_ASYLM_FS2025",
      "IMXC, IMM_ASYLM_FS2025",
      "IRAR, IMM_ASYLM_FS2023"
  })
  void shouldGetFeeCalculation_immigrationAndAsylumHourlyRate_clr(String feeCode, String feeScheme) throws Exception {
    String request = """ 
        {
          "feeCode": "%s",
          "claimId": "claim_123",
          "startDate": "2025-12-22",
          "netProfitCosts": 116.89,
          "netCostOfCounsel": 356.90,
          "netDisbursementAmount": 125.70,
          "disbursementVatAmount": 25.14,
          "vatIndicator": true
        }
        """.formatted(feeCode);

    postAndExpect(request, """
        {
          "feeCode": "%s",
          "schemeId": "%s",
          "claimId": "claim_123",
          "feeCalculation": {
            "totalAmount": 719.39,
            "vatIndicator": true,
            "vatRateApplied": 20.00,
            "calculatedVatAmount": 94.76,
            "disbursementAmount": 125.70,
            "requestedNetDisbursementAmount": 125.70,
            "disbursementVatAmount": 25.14,
            "hourlyTotalAmount": 599.49,
            "netProfitCostsAmount": 116.89,
            "requestedNetProfitCostsAmount": 116.89,
            "netCostOfCounselAmount": 356.90
          }
        }
        """.formatted(feeCode, feeScheme));
  }

  @ParameterizedTest
  @CsvSource(value = {
      // feeCode, total, vat, hourlyTotal, boltOnTotal, boltOnSubHearing
      "IACD, 1781.39, 271.76, 1484.49, 885.0, 302.0",
      "IMCD, 1703.39, 258.76, 1419.49, 820.0, 237.0",
  })
  void shouldGetFeeCalculation_immigrationAndAsylumHourlyRate_clrInterim(String feeCode, double total, double vat,
                                                                         double hourlyTotal, double boltOnTotal,
                                                                         double boltOnSubHearing) throws Exception {
    String request = """ 
        {
          "feeCode": "%s",
          "claimId": "claim_123",
          "startDate": "2021-02-11",
          "netProfitCosts": 116.89,
          "netCostOfCounsel": 356.90,
          "netDisbursementAmount": 125.70,
          "disbursementVatAmount": 25.14,
          "boltOns": {
              "boltOnAdjournedHearing": 1,
              "boltOnCmrhOral": 2,
              "boltOnCmrhTelephone": 1,
              "boltOnSubstantiveHearing": true
          },
          "vatIndicator": true
        }
        """.formatted(feeCode);

    postAndExpect(request, """
        {
          "feeCode": "%s",
          "schemeId": "IMM_ASYLM_FS2020",
          "claimId": "claim_123",
          "feeCalculation": {
            "totalAmount": %s,
            "vatIndicator": true,
            "vatRateApplied": 20.00,
            "calculatedVatAmount": %s,
            "disbursementAmount": 125.70,
            "requestedNetDisbursementAmount": 125.70,
            "disbursementVatAmount": 25.14,
            "hourlyTotalAmount": %s,
            "netProfitCostsAmount": 116.89,
            "requestedNetProfitCostsAmount": 116.89,
            "netCostOfCounselAmount": 356.90,
            "boltOnFeeDetails": {
                  "boltOnTotalFeeAmount": %s,
                  "boltOnAdjournedHearingCount": 1,
                  "boltOnAdjournedHearingFee": 161.0,
                  "boltOnCmrhTelephoneCount": 1,
                  "boltOnCmrhTelephoneFee": 90.0,
                  "boltOnCmrhOralCount": 2,
                  "boltOnCmrhOralFee": 332.0,
                  "boltOnSubstantiveHearingFee": %s
             }
          }
        }
        """.formatted(feeCode, total, vat, hourlyTotal, boltOnTotal, boltOnSubHearing));

  }

  @ParameterizedTest
  @CsvSource({
      // feeCode, netProfitCosts, total, vat, hourlyTotal
      "IAXL, 116.89, 260.27, 23.38, 216.89",
      "IMXL, 116.89, 260.27, 23.38, 216.89",
      "IA100, 45, 174.0, 9.0, 145.0",
  })
  void shouldGetFeeCalculation_immigrationAndAsylumHourlyRate_legalHelp(String feeCode, double netProfitCosts, double total,
                                                                        double vat, double hourlyTotal) throws Exception {
    String request = """ 
        {
          "feeCode": "%s",
          "claimId": "claim_123",
          "startDate": "2025-12-22",
          "netProfitCosts": %s,
          "netDisbursementAmount": 100,
          "disbursementVatAmount": 20,
          "vatIndicator": true
        }
        """.formatted(feeCode, netProfitCosts);

    postAndExpect(request, """
        {
          "feeCode": "%s",
          "schemeId": "IMM_ASYLM_FS2025",
          "claimId": "claim_123",
          "feeCalculation": {
            "totalAmount": %s,
            "vatIndicator": true,
            "vatRateApplied": 20.00,
            "calculatedVatAmount": %s,
            "disbursementAmount": 100.0,
            "requestedNetDisbursementAmount": 100.0,
            "disbursementVatAmount": 20.0,
            "hourlyTotalAmount": %s,
            "netProfitCostsAmount": %s,
            "requestedNetProfitCostsAmount": %s
          }
        }
        """.formatted(feeCode, total, vat, hourlyTotal, netProfitCosts, netProfitCosts));
  }

  @Test
  void shouldGetFeeCalculation_policeStationHourlyRate() throws Exception {
    String request = """ 
        {
          "feeCode": "INVH",
          "claimId": "claim_123",
          "uniqueFileNumber": "041122/665",
          "policeStationId": "NE024",
          "policeStationSchemeId": "1007",
          "netProfitCosts": 34.56,
          "netDisbursementAmount": 50.5,
          "disbursementVatAmount": 20.15,
          "netTravelCosts": 20.0,
          "netWaitingCosts": 10.0,
          "vatIndicator": true
        }
        """;

    postAndExpect(request, """
        {
          "feeCode": "INVH",
          "schemeId": "POL_FS2022",
          "claimId": "claim_123",
          "feeCalculation": {
            "totalAmount": 158.22,
            "vatIndicator": true,
            "vatRateApplied": 20.0,
            "calculatedVatAmount": 23.01,
            "disbursementAmount": 50.5,
            "requestedNetDisbursementAmount": 50.5,
            "disbursementVatAmount": 20.15,
            "hourlyTotalAmount": 115.06,
            "netProfitCostsAmount": 34.56,
            "requestedNetProfitCostsAmount": 34.56,
            "netTravelCostsAmount": 20.0,
            "netWaitingCostsAmount": 10.0
          }
        }
        """);
  }

  @ParameterizedTest
  @CsvSource({
      "PROP1, 211225/123, POC_FS2022",
      "PROP1, 221225/123, POC_FS2025",
      "PROP2, 211225/123, POC_FS2022",
      "PROP2, 221225/123, POC_FS2025",
  })
  void shouldGetFeeCalculation_preOrderCoverHourlyRate(String feeCode, String ufn, String feeScheme) throws Exception {
    String request = """ 
        {
          "feeCode": "%s",
          "claimId": "claim_123",
          "uniqueFileNumber": "%s",
          "netProfitCosts": 10.56,
          "netDisbursementAmount": 10.5,
          "disbursementVatAmount": 2.1,
          "netTravelCosts": 11.35,
          "netWaitingCosts": 12.22,
          "vatIndicator": true
        }
        """.formatted(feeCode, ufn);

    postAndExpect(request, """
        {
          "feeCode": "%s",
          "schemeId": "%s",
          "claimId": "claim_123",
          "feeCalculation": {
              "totalAmount": 53.56,
              "vatIndicator": true,
              "vatRateApplied": 20.0,
              "calculatedVatAmount": 6.83,
              "disbursementAmount": 10.5,
              "requestedNetDisbursementAmount": 10.5,
              "disbursementVatAmount": 2.1,
              "hourlyTotalAmount": 34.13,
              "netProfitCostsAmount": 10.56,
              "requestedNetProfitCostsAmount": 10.56,
              "netTravelCostsAmount": 11.35,
              "netWaitingCostsAmount": 12.22
          }
        }
        """.formatted(feeCode, feeScheme));
  }
}
