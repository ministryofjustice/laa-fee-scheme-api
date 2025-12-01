package uk.gov.justice.laa.fee.scheme.validation;

import static org.springframework.test.json.JsonCompareMode.STRICT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.justice.laa.fee.scheme.postgrestestcontainer.PostgresContainerTestBase;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class FeeCalculationValidationIntegrationTest extends PostgresContainerTestBase {

  private static final String AUTH_TOKEN = "int-test-token";
  private static final String URI = "/api/v1/fee-calculation";

  @Autowired
  private MockMvc mockMvc;

  @Test
  void shouldReturnValidationError_whenFeeCodeIsInvalid() throws Exception {
    String request = """ 
        {
          "feeCode": "BLAH",
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
          "feeCode": "BLAH",
          "claimId": "claim_123",
          "validationMessages": [
            {
              "type":"ERROR",
              "code":"ERRALL1",
              "message":"Enter a valid Fee Code."
            }
          ]
        }
        """);
  }

  @Test
  void shouldReturnValidationError_whenCivilFeeCodeAndStartDateIsTooFarInThePast() throws Exception {
    String request = """ 
        {
          "feeCode": "DISC",
          "claimId": "claim_123",
          "startDate": "2012-09-30",
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
          "claimId": "claim_123",
          "validationMessages": [
            {
              "type":"ERROR",
              "code":"ERRCIV2",
              "message":"Case Start Date is too far in the past."
            }
          ]
        }
        """);
  }

  @Test
  void shouldReturnValidationError_whenCrimeFeeCodeAndStartDateIsInvalid() throws Exception {
    String request = """ 
        {
          "feeCode": "INVC",
          "claimId": "claim_123",
          "uniqueFileNumber": "121212/242",
          "policeStationId": "NE001",
          "policeStationSchemeId": "1001",
          "vatIndicator": false
        }
        """;

    postAndExpect(request, """
        {
          "feeCode": "INVC",
          "claimId": "claim_123",
          "validationMessages": [
            {
              "type":"ERROR",
              "code":"ERRCRM1",
              "message":"Fee Code is not valid for the Case Start Date."
            }
          ]
        }
        """);
  }

  @Test
  void shouldReturnValidationError_whenCrimeFeeCodeAndPoliceStationIdIsInvalid() throws Exception {
    String request = """ 
        {
          "feeCode": "INVC",
          "claimId": "claim_123",
          "uniqueFileNumber": "121219/242",
          "policeStationId": "BLAH",
          "policeStationSchemeId": "1001",
          "vatIndicator": false
        }
        """;

    postAndExpect(request, """
        {
          "feeCode": "INVC",
          "claimId": "claim_123",
          "validationMessages": [
            {
              "type":"ERROR",
              "code":"ERRCRM3",
              "message":"Enter a valid Police station ID, Court ID, or Prison ID."
            }
          ]
        }
        """);
  }

  @Test
  void shouldReturnValidationError_whenCrimeFeeCodeAndPoliceSchemeIdIsInvalid() throws Exception {
    String request = """ 
        {
          "feeCode": "INVC",
          "claimId": "claim_123",
          "uniqueFileNumber": "121221/242",
          "policeStationSchemeId": "BLAH",
          "vatIndicator": false
        }
        """;

    postAndExpect(request, """
        {
          "feeCode": "INVC",
          "claimId": "claim_123",
          "validationMessages": [
            {
              "type":"ERROR",
              "code":"ERRCRM4",
              "message":"Enter a valid Scheme ID."
            }
          ]
        }
        """);
  }

  @Test
  void shouldReturnValidationError_whenCrimeFeeCodeAndUfnIsMissing() throws Exception {
    String request = """ 
        {
          "feeCode": "INVK",
          "claimId": "claim_123",
          "policeStationId": "NE001",
          "policeStationSchemeId": "1001",
          "vatIndicator": false
        }
        """;

    postAndExpect(request, """
        {
          "feeCode": "INVK",
          "claimId": "claim_123",
          "validationMessages": [
            {
              "type":"ERROR",
              "code":"ERRCRM7",
              "message":"Enter a UFN."
            }
          ]
        }
        """);
  }

  @Test
  void shouldReturnValidationError_whenCrimeFeeCodeAndRepOrderDateIsInvalid() throws Exception {
    String request = """ 
        {
          "feeCode": "PROJ5",
          "claimId": "claim_123",
          "uniqueFileNumber": "010215/242",
          "representationOrderDate": "2015-02-01",
          "netDisbursementAmount": 123.38,
          "disbursementVatAmount": 24.67,
          "vatIndicator": true
        }
        """;

    postAndExpect(request, """
        {
          "feeCode": "PROJ5",
          "claimId": "claim_123",
          "validationMessages": [
            {
              "type":"ERROR",
              "code":"ERRCRM12",
              "message":"Fee Code is not valid for the Representation Order Date provided."
            }
          ]
        }
        """);
  }

  @Test
  void shouldReturnValidationWarning_family() throws Exception {
    String request = """ 
        {
          "feeCode": "FPB010",
          "startDate": "2023-04-01",
          "claimId": "claim_123",
          "netProfitCosts": 200.20,
          "netDisbursementAmount": 55.35,
          "disbursementVatAmount": 11.07,
          "londonRate": false,
          "vatIndicator": true
        }
        """;

    postAndExpect(request, """
        {
          "feeCode": "FPB010",
          "claimId": "claim_123",
          "schemeId": "FAM_NON_LON_FS2011",
          "validationMessages": [
            {
              "type": "WARNING",
              "code": "WARFAM1",
              "message": "The claim exceeds the Escape Case Threshold. An Escape Case Claim must be submitted for further costs to be paid."
            }
          ],
          "escapeCaseFlag": true,
          "feeCalculation": {
            "totalAmount": 224.82,
            "vatIndicator": true,
            "vatRateApplied": 20.0,
            "calculatedVatAmount": 26.4,
            "disbursementAmount": 55.35,
            "requestedNetDisbursementAmount": 55.35,
            "disbursementVatAmount": 11.07,
            "fixedFeeAmount": 132.0
          }
        }
        """);
  }

  @Test
  void should_GetErrorCodeAndMessage_WhenLondonRateIsNotSupplied_InFamilyClaimRequest() throws Exception {
    String request = """ 
        {
          "feeCode": "FPB010",
          "startDate": "2022-02-01",
          "netDisbursementAmount": 123.38,
          "disbursementVatAmount": 24.67,
          "vatIndicator": true
        }
        """;

    postAndExpect(request, """
        {
          "feeCode": "FPB010",
          "validationMessages": [
            {
              "type": "ERROR",
              "code": "ERRFAM1",
              "message": "London/non-London rate must be entered for the Fee Code used."
            }
          ]
        }
        """);
  }

  @ParameterizedTest
  @CsvSource({
      "IMCF, WARIA1, Costs have been capped at £600 without an Immigration Priority Authority Number. Disbursement costs exceed the Disbursement Limit., "
      + "false, 2173.72, 250.6, 650.0, 600.0, 1092.0, 0",
      "IALB, WARIA2, Costs have been capped at £400 without an Immigration Priority Authority Number. Disbursement costs exceed the Disbursement Limit., "
      + "false, 1158.92, 114.8, 450.0, 400.0, 413.0, 0",
      "IACE, WARIA3, The claim exceeds the Escape Case Threshold. An Escape Case Claim must be submitted for further costs to be paid., "
      + "true, 1116.12, 166.0, 50.0, 50.0, 669.0, 1500"
  })
  void shouldReturnValidationWarning_immigrationAndAsylumFixedFee(
      String feeCode,
      String warningType,
      String warningMessage,
      boolean escapeFlag,
      double totalAmount,
      double calculatedVatAmount,
      double requestedDisbursementAmount,
      double disbursementAmount,
      double fixedFeeAmount,
      double netProfitCosts
  ) throws Exception {
    String request = """ 
        {
          "feeCode": "%s",
          "claimId": "claim_123",
          "startDate": "2024-09-30",
          "netDisbursementAmount": %s,
          "disbursementVatAmount": 70.12,
          "vatIndicator": true,
          "detentionTravelAndWaitingCosts": 111.00,
          "jrFormFilling": 50.00,
          "netProfitCosts": "%s"
        }
        """.formatted(feeCode, requestedDisbursementAmount, netProfitCosts);

    postAndExpect(request, """
        {
          "feeCode": "%s",
          "schemeId": "IMM_ASYLM_FS2023",
          "claimId": "claim_123",
          "validationMessages": [
            {
              "type": "WARNING",
              "code": "%s",
              "message": "%s"
            }
          ],
          "escapeCaseFlag": %s,
          "feeCalculation": {
            "totalAmount": %s,
            "vatIndicator": true,
            "vatRateApplied": 20.0,
            "calculatedVatAmount": %s,
            "requestedNetDisbursementAmount": %s,
            "disbursementAmount": %s,
            "disbursementVatAmount": 70.12,
            "fixedFeeAmount": %s,
            "detentionTravelAndWaitingCostsAmount": 111.0,
            "jrFormFillingAmount": 50.0
          }
        }
        """.formatted(feeCode, warningType, warningMessage, escapeFlag, totalAmount, calculatedVatAmount, requestedDisbursementAmount, disbursementAmount,
        fixedFeeAmount));
  }

  @Test
  void shouldReturnValidationWarning_immigrationAndAsylumHourlyRate_legalHelpIA100() throws Exception {
    String request = """ 
        {
          "feeCode": "IA100",
          "claimId": "claim_123",
          "startDate": "2015-02-11",
          "netProfitCosts": 1160.89,
          "netDisbursementAmount": 825.70,
          "disbursementVatAmount": 25.14,
          "vatIndicator": true
        }
        """;

    postAndExpect(request, """
        {
          "feeCode": "IA100",
          "schemeId": "IMM_ASYLM_FS2013",
          "claimId": "claim_123",
          "validationMessages": [
            {
              "type": "WARNING",
              "code": "WARIA8",
              "message": "Costs have been capped. Costs for the Fee Code used cannot exceed £100."
            }
          ],
          "feeCalculation": {
            "totalAmount": 357.32,
            "vatIndicator": true,
            "vatRateApplied": 20.0,
            "calculatedVatAmount": 232.18,
            "disbursementAmount": 825.7,
            "requestedNetDisbursementAmount": 825.7,
            "disbursementVatAmount": 25.14,
            "hourlyTotalAmount": 100.0,
            "netProfitCostsAmount": 1160.89,
            "requestedNetProfitCostsAmount": 1160.89
          }
        }
        """);
  }

  @Test
  void shouldReturnValidationWarning_immigrationAndAsylumHourlyRate_legalHelp() throws Exception {
    String request = """ 
        {
          "feeCode": "IMXL",
          "claimId": "claim_123",
          "startDate": "2015-02-11",
          "netProfitCosts": 1160.89,
          "netDisbursementAmount": 825.70,
          "disbursementVatAmount": 25.14,
          "vatIndicator": true
        }
        """;

    postAndExpect(request, """
        {
          "feeCode": "IMXL",
          "schemeId": "IMM_ASYLM_FS2013",
          "claimId": "claim_123",
          "validationMessages": [
            {
              "type": "WARNING",
              "code": "WARIA6",
              "message": "Costs have been capped. The amount entered exceeds the Total Cost Limit. An Immigration Prior Authority number must be entered."
            },
            {
              "type": "WARNING",
              "code": "WARIA7",
              "message": "Costs have been capped without an Immigration Priority Authority Number. Disbursement costs exceed the Disbursement Limit."
            }
          ],
          "feeCalculation": {
            "totalAmount": 1025.14,
            "vatIndicator": true,
            "vatRateApplied": 20.0,
            "calculatedVatAmount": 100.0,
            "disbursementAmount": 400.0,
            "requestedNetDisbursementAmount": 825.7,
            "disbursementVatAmount": 25.14,
            "hourlyTotalAmount": 900.0,
            "netProfitCostsAmount": 500.0,
            "requestedNetProfitCostsAmount": 1160.89
          }
        }
        """);
  }

  @Test
  void shouldReturnValidationWarning_immigrationAndAsylumHourlyRate_clr() throws Exception {
    String request = """ 
        {
          "feeCode": "IAXC",
          "claimId": "claim_123",
          "startDate": "2015-02-11",
          "netProfitCosts": 1160.89,
          "netDisbursementAmount": 825.70,
          "disbursementVatAmount": 25.14,
          "vatIndicator": true
        }
        """;

    postAndExpect(request, """
        {
          "feeCode": "IAXC",
          "schemeId": "IMM_ASYLM_FS2013",
          "claimId": "claim_123",
          "validationMessages": [
            {
              "type": "WARNING",
              "code": "WARIA4",
              "message": "Costs have been capped. The amount entered exceeds the Total Cost Limit. An Immigration Prior Authority number must be entered."
            }
          ],
          "feeCalculation": {
            "totalAmount": 1857.32,
            "vatIndicator": true,
            "vatRateApplied": 20.0,
            "calculatedVatAmount": 232.18,
            "disbursementAmount": 825.7,
            "requestedNetDisbursementAmount": 825.7,
            "disbursementVatAmount": 25.14,
            "hourlyTotalAmount": 1600.0,
            "netProfitCostsAmount": 1160.89,
            "requestedNetProfitCostsAmount": 1160.89
          }
        }
        """);
  }

  @Test
  void shouldReturnValidationWarning_immigrationAndAsylumHourlyRate_clrInterim() throws Exception {
    String request = """ 
        {
          "feeCode": "IACD",
          "claimId": "claim_123",
          "startDate": "2021-02-11",
          "netProfitCosts": 1116.89,
           "netCostOfCounsel": 706.90,
          "netDisbursementAmount": 125.70,
          "disbursementVatAmount": 25.14,
          "boltOns": {
            "boltOnAdjournedHearing": 1,
            "boltOnCmrhOral": 2,
            "boltOnCmrhTelephone": 1,
            "boltOnSubstantiveHearing": true
          },
          "vatIndicator": true,
          "detentionTravelAndWaitingCosts": 111.00,
          "jrFormFilling": 50.00
        }
        """;

    postAndExpect(request, """
        {
          "feeCode": "IACD",
          "schemeId": "IMM_ASYLM_FS2020",
          "claimId": "claim_123",
          "validationMessages": [
            {
              "type": "WARNING",
              "code": "WARIA5",
              "message": "Costs have been capped. The amount entered exceeds the Total Cost Limit. An Immigration Prior Authority number must be entered."
            },
            {
              "type": "WARNING",
              "code": "WARIA9",
              "message": "Costs not included. Detention Travel and Waiting costs on hourly rates cases should be reported as Profit Costs."
            },
            {
              "type": "WARNING",
              "code": "WARIA10",
              "message": "Costs have been included. JR/ form filling costs should only be completed for standard fee cases. Hourly rates costs should be reported in the Profit Costs."
            }
          ],
          "feeCalculation": {
            "totalAmount": 3051.9,
            "vatIndicator": true,
            "vatRateApplied": 20.0,
            "calculatedVatAmount": 541.76,
            "disbursementAmount": 125.7,
            "requestedNetDisbursementAmount": 125.7,
            "disbursementVatAmount": 25.14,
            "hourlyTotalAmount": 2485.0,
            "netProfitCostsAmount": 1116.89,
            "requestedNetProfitCostsAmount": 1116.89,
            "netCostOfCounselAmount": 706.9,
            "boltOnFeeDetails": {
              "boltOnTotalFeeAmount": 885.0,
              "boltOnAdjournedHearingCount": 1,
              "boltOnAdjournedHearingFee": 161.0,
              "boltOnCmrhTelephoneCount": 1,
              "boltOnCmrhTelephoneFee": 90.0,
              "boltOnCmrhOralCount": 2,
              "boltOnCmrhOralFee": 332.0,
              "boltOnSubstantiveHearingFee": 302.0
            }
          }
        }
        """);
  }

  @Test
  void shouldReturnValidationWarning_immigrationAndAsylumDisbursementOnly() throws Exception {
    String request = """ 
        {
          "feeCode": "ICASD",
          "claimId": "claim_123",
          "startDate": "2021-09-30",
          "netDisbursementAmount": 2000,
          "disbursementVatAmount": 400
        }
        """;

    postAndExpect(request, """
        {
          "feeCode": "ICASD",
          "schemeId": "IMM_ASYLM_DISBURSEMENT_FS2013",
          "claimId": "claim_123",
          "validationMessages": [
            {
              "type": "WARNING",
              "code": "WARIA11",
              "message": "Costs have been capped without an Immigration Priority Authority Number. Disbursement costs exceed the Disbursement Limit."
            }
          ],
          "feeCalculation": {
            "totalAmount": 2000.0,
            "disbursementAmount": 1600.0,
            "requestedNetDisbursementAmount": 2000.0,
            "disbursementVatAmount": 400.0
          }
        }
        """);
  }

  @Test
  void shouldReturnValidationWarningForEscapeCase_policeStations() throws Exception {
    String request = """ 
        {
          "feeCode": "INVC",
          "claimId": "claim_123",
          "uniqueFileNumber": "12122019/242",
          "policeStationId": "NE001",
          "policeStationSchemeId": "1001",
          "netDisbursementAmount": 600,
          "disbursementVatAmount": 120,
          "vatIndicator": true
        }
        """;

    postAndExpect(request, """
        {
          "feeCode": "INVC",
          "schemeId": "POL_FS2016",
          "claimId": "claim_123",
          "validationMessages": [
              {
                  "type": "WARNING",
                  "code": "WARCRM8",
                  "message": "The claim exceeds the Escape Case Threshold. An Escape Case Claim must be submitted for further costs to be paid."
              }
          ],
          "escapeCaseFlag": true,
          "feeCalculation": {
              "totalAmount": 877.68,
              "vatIndicator": true,
              "vatRateApplied": 20.0,
              "calculatedVatAmount": 26.28,
              "disbursementAmount": 600.0,
              "requestedNetDisbursementAmount": 600.0,
              "disbursementVatAmount": 120.0,
              "fixedFeeAmount": 131.4
          }
        }
        """);
  }

  @Test
  void shouldReturnValidationWarning_policeOther() throws Exception {
    String request = """ 
        {
          "feeCode": "INVA",
          "claimId": "claim_123",
          "uniqueFileNumber": "12122019/242",
          "netProfitCosts": 50,
          "netTravelCosts": 20,
          "netWaitingCosts": 10,
          "netDisbursementAmount": 600,
          "disbursementVatAmount": 120,
          "vatIndicator": true
        }
        """;

    postAndExpect(request, """
        {
          "feeCode": "INVA",
          "schemeId": "POL_FS2016",
          "claimId": "claim_123",
          "validationMessages": [
              {
                  "type": "WARNING",
                  "code": "WARCRM7",
                  "message": "Costs have been included. Net Costs exceed the Upper Cost Limitation."
              }
          ],
          "feeCalculation": {
              "totalAmount": 936.0,
              "vatIndicator": true,
              "vatRateApplied": 20.0,
              "calculatedVatAmount": 136.0,
              "disbursementAmount": 600.0,
              "requestedNetDisbursementAmount": 600.0,
              "disbursementVatAmount": 120.0,
              "hourlyTotalAmount": 680.0,
              "netProfitCostsAmount": 50.0,
              "requestedNetProfitCostsAmount": 50.0,
              "netTravelCostsAmount": 20.0,
              "netWaitingCostsAmount": 10.0
          }
        }
        """);
  }

  @Test
  void shouldReturnValidationWarning_associatedCivil() throws Exception {
    String request = """ 
        {
          "feeCode": "ASMS",
          "claimId": "claim_123",
          "uniqueFileNumber": "020416/001",
          "netProfitCosts": 200.0,
          "netTravelCosts": 57.0,
          "netWaitingCosts": 70.0,
          "netDisbursementAmount": 55.35,
          "disbursementVatAmount": 11.07,
          "vatIndicator": true
        }
        """;

    postAndExpect(request, """
        {
          "feeCode": "ASMS",
          "schemeId": "ASSOC_FS2016",
          "claimId": "claim_123",
          "validationMessages": [
            {
              "type": "WARNING",
              "code": "WARCRM4",
              "message": "The claim exceeds the Escape Case Threshold. An Escape Case Claim must be submitted for further costs to be paid."
            }
          ],
          "escapeCaseFlag": true,
          "feeCalculation": {
            "totalAmount": 161.22,
            "vatIndicator": true,
            "vatRateApplied": 20.0,
            "calculatedVatAmount": 15.8,
            "disbursementAmount": 55.35,
            "requestedNetDisbursementAmount": 55.35,
            "disbursementVatAmount": 11.07,
            "fixedFeeAmount": 79.0
          }
        }
        """);
  }

  @Test
  void shouldReturnValidationWarning_advocacyAppealsReviews() throws Exception {
    String request = """ 
        {
          "feeCode": "PROH",
          "claimId": "claim_123",
          "uniqueFileNumber": "020416/001",
          "netProfitCosts": 1200.0,
          "netTravelCosts": 57.0,
          "netWaitingCosts": 70.0,
          "netDisbursementAmount": 55.35,
          "disbursementVatAmount": 11.07,
          "vatIndicator": true
        }
        """;

    postAndExpect(request, """
        {
          "feeCode": "PROH",
          "schemeId": "AAR_FS2016",
          "claimId": "claim_123",
          "validationMessages": [
            {
              "type": "WARNING",
              "code": "WARCRM3",
              "message": "Costs are included. The Net Costs exceeds the Upper Costs Limitation."
            }
          ],
          "feeCalculation": {
            "totalAmount": 1658.82,
            "vatIndicator": true,
            "vatRateApplied": 20.0,
            "calculatedVatAmount": 265.4,
            "disbursementAmount": 55.35,
            "requestedNetDisbursementAmount": 55.35,
            "disbursementVatAmount": 11.07,
            "hourlyTotalAmount": 1327.0,
            "netProfitCostsAmount": 1200.0,
            "requestedNetProfitCostsAmount": 1200.0,
            "netTravelCostsAmount": 57.0,
            "netWaitingCostsAmount": 70.0
          }
        }
        """);
  }

  @ParameterizedTest
  @CsvSource({
      "PRIA, WARCRM6, The claim exceeds the Escape Case Threshold. An Escape Case Claim must be submitted for further costs to be paid., "
      + "true, 360.9, 40.15, 200.75",
      "PRIB1, WARCRM5, Costs are included. Profit and Waiting Costs exceed the Lower Standard Fee Limit. An escape fee may be payable., "
      + "false, 364.72, 40.79, 203.93",
  })
  void shouldReturnValidationWarning_prisonLaw(
      String feeCode,
      String warningType,
      String warningMessage,
      boolean escapeFlag,
      double totalAmount,
      double calculatedVatAmount,
      double fixedFeeAmount
  ) throws Exception {
    String request = """ 
        {
          "feeCode": "%s",
          "claimId": "claim_123",
          "uniqueFileNumber": "110425/123",
          "netProfitCosts": 500,
          "netDisbursementAmount": 100,
          "disbursementVatAmount": 20,
          "vatIndicator": true,
          "netWaitingCosts": 150
        }
        """.formatted(feeCode);

    postAndExpect(request, """
        {
          "feeCode": "%s",
          "schemeId": "PRISON_FS2016",
          "claimId": "claim_123",
          "validationMessages": [
            {
              "type": "WARNING",
              "code": "%s",
              "message": "%s"
            }
          ],
          "escapeCaseFlag": %s,
          "feeCalculation": {
            "totalAmount": %s,
            "vatIndicator": true,
            "vatRateApplied": 20.0,
            "calculatedVatAmount": %s,
            "requestedNetDisbursementAmount": 100.0,
            "disbursementAmount": 100.0,
            "disbursementVatAmount": 20.0,
            "fixedFeeAmount": %s
          }
        }
        """.formatted(feeCode, warningType, warningMessage, escapeFlag, totalAmount, calculatedVatAmount, fixedFeeAmount));
  }

  @ParameterizedTest
  @ValueSource(strings = {"PROP1", "PROP2"})
  void shouldReturnValidationWarning_preOrderCover(String feeCode) throws Exception {
    String request = """ 
        {
          "feeCode": "%s",
          "claimId": "claim_123",
          "uniqueFileNumber": "221225/123",
          "netProfitCosts": 10.0,
          "netTravelCosts": 57.0,
          "netWaitingCosts": 70.0,
          "netDisbursementAmount": 55.35,
          "disbursementVatAmount": 11.07,
          "vatIndicator": true
        }
        """.formatted(feeCode);

    postAndExpect(request, """
        {
          "feeCode": "%s",
          "claimId": "claim_123",
          "validationMessages": [
            {
              "type": "ERROR",
              "code": "ERRCRM10",
              "message": "Net Cost is more than the Upper Cost Limitation."
            }
          ]
        }
        """.formatted(feeCode));
  }

  @Test
  void shouldReturnValidationWarning_mentalHealth() throws Exception {
    String request = """ 
        {
          "feeCode": "MHL03",
          "claimId": "claim_123",
          "startDate": "2025-02-01",
          "netDisbursementAmount": 123.38,
          "disbursementVatAmount": 24.67,
          "netProfitCosts": 1000,
          "netCostOfCounsel": 500,
          "vatIndicator": true,
          "boltOns": {
              "boltOnAdjournedHearing": 1
          }
        }
        """;

    postAndExpect(request, """
        {
          "feeCode": "MHL03",
          "claimId": "claim_123",
          "schemeId": "MHL_FS2013",
          "validationMessages": [
              {
                  "type": "WARNING",
                  "code": "WARMH1",
                  "message": "The claim exceeds the Escape Case Threshold. An Escape Case Claim must be submitted for further costs to be paid."
              }
          ],
          "escapeCaseFlag": true,
          "feeCalculation": {
              "totalAmount": 828.45,
              "vatIndicator": true,
              "vatRateApplied": 20.0,
              "calculatedVatAmount": 113.4,
              "disbursementAmount": 123.38,
              "requestedNetDisbursementAmount": 123.38,
              "disbursementVatAmount": 24.67,
              "fixedFeeAmount": 450.0,
              "boltOnFeeDetails": {
                  "boltOnTotalFeeAmount": 117.0,
                  "boltOnAdjournedHearingCount": 1,
                  "boltOnAdjournedHearingFee": 117.0
              }
          }
        }
        """);
  }


  @Test
  void shouldGetErrorMessageInResponse_mentalHealthDisbursementOnly() throws Exception {
    String request = """ 
        {
          "feeCode": "MHLDIS",
          "claimId": "claim_123",
          "startDate": "2012-07-29",
          "netDisbursementAmount": 1200.0,
          "disbursementVatAmount": 150.0
        }
        """;

    postAndExpect(request, """
        {
           "feeCode": "MHLDIS",
           "claimId": "claim_123",
           "validationMessages": [
               {
                   "type": "ERROR",
                   "code": "ERRCIV2",
                   "message": "Case Start Date is too far in the past."
               }
           ]
       }
       """);
  }


  @ParameterizedTest
  @CsvSource({
      "PROJ5, MAGS_COURT_FS2022",
      "YOUK2, YOUTH_COURT_FS2024",
      "PROW, SEND_HEAR_FS2022",
  })
  void shouldReturnValidationError_criminalProceedings_missingRepOrderDate(String feeCode) throws Exception {
    String request = """ 
        {
          "feeCode": "%s",
          "claimId": "claim_123",
          "uniqueFileNumber": "121219/242",
          "netDisbursementAmount": 123.38,
          "disbursementVatAmount": 24.67,
          "vatIndicator": true
        }
        """.formatted(feeCode);

    postAndExpect(request, """
        {
          "feeCode": "%s",
          "claimId": "claim_123",
          "validationMessages": [
              {
                  "type": "ERROR",
                  "code": "ERRCRM8",
                  "message": "Enter a representation order date."
              }
          ]
        }
        """.formatted(feeCode));
  }

  @ParameterizedTest
  @CsvSource({
      "CAPA, CAPA_FS2013, WAROTH2, 434.85, 47.8, 239.0",
      "CLIN, CLIN_FS2013, WAROTH3, 382.05, 39.0, 195.0",
      "COM, COM_FS2013, WAROTH4, 467.25, 53.2, 266.0",
      "DEBT, DEBT_FS2013, WAROTH5, 364.05, 36.0, 180.0",
      "EDUFIN, EDU_FS2013, WAROTH7, 474.45, 54.4, 272.0",
      "ELA, ELA_FS2024, WAROTH6,  336.45, 31.4, 157.0",
      "HOUS, HOUS_FS2013, WAROTH8, 336.45, 31.4, 157.0",
      "MISCCON, MISC_FS2013, WAROTH9, 338.85, 31.8, 159.0",
      "PUB, PUB_FS2013, WAROTH10, 458.85, 51.8, 259.0",
      "WFB1, WB_FS2025, WAROTH11, 397.65, 41.6, 208.0"
  })
  void shouldReturnValidationWarning_otherCivilCategories(String feeCode, String schemeId, String warningCode,
                                                          String expectedTotal, String expectedVatAmount,
                                                          String expectedFixedFeeAmount) throws Exception {
    String request = """ 
        {
          "feeCode": "%s",
          "claimId": "claim_123",
          "startDate": "2025-06-01",
          "netProfitCosts": 1000.0,
          "netDisbursementAmount": 123.38,
          "disbursementVatAmount": 24.67,
          "vatIndicator": true
        }
        """.formatted(feeCode);

    postAndExpect(request, """
        {
          "feeCode": "%s",
          "schemeId": "%s",
          "claimId": "claim_123",
          "validationMessages": [
            {
              "type": "WARNING",
              "code": "%s",
              "message": "The claim exceeds the Escape Case Threshold. An Escape Case Claim must be submitted for further costs to be paid."
            }
          ],
          "escapeCaseFlag": true,
          "feeCalculation": {
            "totalAmount": %s,
            "vatIndicator": true,
            "vatRateApplied": 20.0,
            "calculatedVatAmount": %s,
            "disbursementAmount": 123.38,
            "requestedNetDisbursementAmount": 123.38,
            "disbursementVatAmount": 24.67,
            "fixedFeeAmount": %s
          }
        }
        """.formatted(feeCode, schemeId, warningCode, expectedTotal, expectedVatAmount, expectedFixedFeeAmount));
  }

  @Test
  void shouldReturnValidationWarning_discrimination() throws Exception {
    String request = """ 
        {
          "feeCode": "DISC",
          "claimId": "claim_123",
          "startDate": "2019-09-30",
          "netProfitCosts": 900,
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
          "validationMessages": [
            {
              "type": "WARNING",
              "code": "WAROTH1",
              "message": "The claim exceeds the Escape Case Threshold. An Escape Case Claim must be submitted for further costs to be paid."
            }
          ],
          "escapeCaseFlag": true,
          "feeCalculation": {
            "totalAmount": 960.33,
            "vatIndicator": true,
            "vatRateApplied": 20.0,
            "calculatedVatAmount": 140.0,
            "disbursementAmount": 100.21,
            "requestedNetDisbursementAmount": 100.21,
            "disbursementVatAmount": 20.12,
            "hourlyTotalAmount": 700.0,
            "netProfitCostsAmount": 900.0,
            "requestedNetProfitCostsAmount": 900.0,
            "netCostOfCounselAmount": 79.19
          }
        }
        """);
  }

  private void postAndExpect(String requestJson, String expectedResponseJson) throws Exception {
    mockMvc
        .perform(post(URI)
            .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json(expectedResponseJson, STRICT));
  }
}
