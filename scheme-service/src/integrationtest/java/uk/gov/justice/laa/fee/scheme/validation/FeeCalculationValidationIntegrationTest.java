package uk.gov.justice.laa.fee.scheme.validation;

import static org.springframework.test.json.JsonCompareMode.STRICT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
public class FeeCalculationValidationIntegrationTest extends PostgresContainerTestBase {

  private static final String AUTH_TOKEN = "int-test-token";
  private static final String URI = "/api/v1/fee-calculation";

  @Autowired
  private MockMvc mockMvc;

  @Test
  void shouldReturnValidationError_whenFeeCodeIsInvalid() throws Exception {
    mockMvc.perform(post(URI)
            .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
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
                """)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json("""
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
            """, STRICT));
  }

  @Test
  void shouldReturnValidationError_whenCivilFeeCodeAndStartDateIsTooFarInThePast() throws Exception {
    mockMvc.perform(post(URI)
            .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
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
                """)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json("""
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
            """, STRICT));
  }

  @Test
  void shouldReturnValidationError_whenCrimeFeeCodeAndStartDateIsInvalid() throws Exception {
    mockMvc.perform(post(URI)
            .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "feeCode": "INVC",
                  "claimId": "claim_123",
                  "startDate": "2012-12-12",
                  "uniqueFileNumber": "121212/242",
                  "policeStationId": "NE001",
                  "policeStationSchemeId": "1001",
                  "vatIndicator": false
                }
                """)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json("""
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
            """, STRICT));
  }

  @Test
  void shouldReturnValidationError_whenCrimeFeeCodeAndPoliceStationIdIsInvalid() throws Exception {
    mockMvc.perform(post(URI)
            .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "feeCode": "INVC",
                  "claimId": "claim_123",
                  "startDate": "2023-12-12",
                  "uniqueFileNumber": "121219/242",
                  "policeStationId": "BLAH",
                  "policeStationSchemeId": "1001",
                  "vatIndicator": false
                }
                """)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json("""
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
            """, STRICT));
  }

  @Test
  void shouldReturnValidationError_whenCrimeFeeCodeAndPoliceSchemeIdIsInvalid() throws Exception {
    mockMvc.perform(post(URI)
            .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "feeCode": "INVC",
                  "claimId": "claim_123",
                  "startDate": "2021-12-12",
                  "uniqueFileNumber": "121221/242",
                  "policeStationSchemeId": "BLAH",
                  "vatIndicator": false
                }
                """)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json("""
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
            """, STRICT));
  }

  @Test
  void shouldReturnValidationError_whenCrimeFeeCodeAndUfnIsMissing() throws Exception {
    mockMvc.perform(post(URI)
            .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "feeCode": "INVK",
                  "claimId": "claim_123",
                  "startDate": "2023-12-12",
                  "policeStationId": "NE001",
                  "policeStationSchemeId": "1001",
                  "vatIndicator": false
                }
                """)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json("""
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
            """, STRICT));
  }

  @Test
  void shouldReturnValidationError_whenCrimeFeeCodeAndRepOrderDateIsInvalid() throws Exception {
    mockMvc.perform(post(URI)
            .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "feeCode": "PROJ5",
                  "claimId": "claim_123",
                  "uniqueFileNumber": "010215/242",
                  "representationOrderDate": "2015-02-01",
                  "netDisbursementAmount": 123.38,
                  "disbursementVatAmount": 24.67,
                  "vatIndicator": true
                }
                """)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json("""
            {
              "feeCode": "PROJ5",
              "claimId": "claim_123",
              "validationMessages": [
                {
                  "type":"ERROR",
                  "code":"ERRCRM12",
                  "message":"Fee Code is not valid for the Case Start Date."
                }
              ]
            }
            """, STRICT));
  }

  @Test
  void shouldReturnValidationWarning_whenCrimeFeeCodeAndNetTravelCosts() throws Exception {
    mockMvc.perform(post(URI)
            .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "feeCode": "INVB1",
                  "claimId": "claim_123",
                  "startDate": "2021-12-12",
                  "uniqueFileNumber": "121221/242",
                  "netTravelCosts": 100.0,
                  "vatIndicator": false
                }
                """)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json("""
            {
              "feeCode": "INVB1",
              "claimId": "claim_123",
              "schemeId": "POL_FS2016",
              "validationMessages": [
                {
                  "type":"WARNING",
                  "code":"WARCRM1",
                  "message":"Cost not included. Travel costs cannot be claimed with Fee Code used."
                }
              ],
              "escapeCaseFlag": false,
              "feeCalculation": {
                "totalAmount": 28.7,
                "vatIndicator": false,
                "calculatedVatAmount": 0,
                "fixedFeeAmount": 28.7
              }
            }
            """, STRICT));
  }

  @Test
  void shouldReturnValidationWarning_whenCrimeFeeCodeAndNetWaitingCosts() throws Exception {
    mockMvc.perform(post(URI)
            .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "feeCode": "INVB1",
                  "claimId": "claim_123",
                  "startDate": "2021-12-12",
                  "uniqueFileNumber": "121221/242",
                  "netWaitingCosts": 100.0,
                  "vatIndicator": false
                }
                """)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json("""
            {
              "feeCode": "INVB1",
              "claimId": "claim_123",
              "schemeId": "POL_FS2016",
              "validationMessages": [
                {
                  "type":"WARNING",
                  "code":"WARCRM2",
                  "message":"Cost not included. Waiting costs cannot be claimed with Fee Code used."
                }
              ],
              "escapeCaseFlag": false,
              "feeCalculation": {
                "totalAmount": 28.7,
                "vatIndicator": false,
                "calculatedVatAmount": 0,
                "fixedFeeAmount": 28.7
              }
            }
            """, STRICT));
  }

  @ParameterizedTest
  @CsvSource({
      "IMCF, WARIA1, Costs have been capped at £600 without an Immigration Priority Authority Number. Disbursement costs exceed the Disbursement Limit., "
          + "2173.72, 250.6, 650.0, 600.0, 1092.0",
      "IALB, WARIA2, Costs have been capped at £400 without an Immigration Priority Authority Number. Disbursement costs exceed the Disbursement Limit., "
          + "1158.92, 114.8, 450.0, 400.0, 413.0"
  })
  void shouldReturnValidationWarning_immigrationAndAsylumFixedFee(
      String feeCode,
      String warningType,
      String warningMessage,
      double totalAmount,
      double calculatedVatAmount,
      double requestedDisbursementAmount,
      double disbursementAmount,
      double fixedFeeAmount
  ) throws Exception {

    String expectedJson = """
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
            "escapeCaseFlag": false,
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
        """.formatted(feeCode, warningType, warningMessage, totalAmount, calculatedVatAmount, requestedDisbursementAmount, disbursementAmount, fixedFeeAmount);

    mockMvc.perform(post(URI)
            .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "feeCode": "%s",
                  "claimId": "claim_123",
                  "startDate": "2024-09-30",
                  "netDisbursementAmount": %s,
                  "disbursementVatAmount": 70.12,
                  "vatIndicator": true,
                  "detentionTravelAndWaitingCosts": 111.00,
                  "jrFormFilling": 50.00
                }
                """.formatted(feeCode, requestedDisbursementAmount))
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson, STRICT));
  }

  @Test
  void shouldReturnValidationWarning_immigrationAndAsylumHourlyRate_legalHelp() throws Exception {
    mockMvc
        .perform(post(URI)
            .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "feeCode": "IMXL",
                  "claimId": "claim_123",
                  "startDate": "2015-02-11",
                  "netProfitCosts": 1160.89,
                  "netDisbursementAmount": 825.70,
                  "disbursementVatAmount": 25.14,
                  "vatIndicator": true
                }
                """)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json("""
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
            """, STRICT));
  }

  @Test
  void shouldReturnValidationWarning_immigrationAndAsylumHourlyRate_clrInterim() throws Exception {
    mockMvc
        .perform(post(URI)
            .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
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
                """)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json("""
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
            """, STRICT));
  }

  @Test
  void shouldReturnValidationWarning_immigrationAndAsylumDisbursementOnly() throws Exception {
    mockMvc
        .perform(post(URI)
            .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "feeCode": "ICASD",
                  "claimId": "claim_123",
                  "startDate": "2021-09-30",
                  "netDisbursementAmount": 2000,
                  "disbursementVatAmount": 400
                }
                """)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json("""
            {
                "feeCode": "ICASD",
                "schemeId": "IMM_ASYLM_DISBURSEMENT_FS2020",
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
            """, STRICT));
  }

  @Test
  void shouldReturnValidationWarning_policeStationFixedFee() throws Exception {
    mockMvc
        .perform(post(URI)
            .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                      {
                          "feeCode": "INVC",
                          "claimId": "claim_123",
                          "startDate": "2019-12-12",
                          "uniqueFileNumber": "12122019/2423",
                          "policeStationId": "NE001",
                          "policeStationSchemeId": "1001",
                          "netDisbursementAmount": 600,
                          "disbursementVatAmount": 120,
                          "vatIndicator": true
                      }
                """)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json("""
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
            """, STRICT));
  }

  @Test
  void shouldReturnValidationWarning_associatedCivil() throws Exception {
    mockMvc
        .perform(post(URI)
            .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
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
                """)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json("""
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
            """, STRICT));
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

    String expectedJson = """
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
        """.formatted(feeCode, warningType, warningMessage, escapeFlag, totalAmount, calculatedVatAmount, fixedFeeAmount);

    mockMvc.perform(post(URI)
            .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "feeCode": "%s",
                  "claimId": "claim_123",
                  "uniqueFileNumber": "110425/abc",
                  "netProfitCosts": 500,
                  "netDisbursementAmount": 100,
                  "disbursementVatAmount": 20,
                  "vatIndicator": true,
                  "netWaitingCosts": 150
                }
                """.formatted(feeCode))
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson, STRICT));
  }
}
