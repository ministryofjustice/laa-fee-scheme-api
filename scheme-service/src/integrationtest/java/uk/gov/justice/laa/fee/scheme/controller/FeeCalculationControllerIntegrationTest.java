package uk.gov.justice.laa.fee.scheme.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.json.JsonCompareMode.LENIENT;
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
public class FeeCalculationControllerIntegrationTest extends PostgresContainerTestBase {

  @Autowired
  private MockMvc mockMvc;

  private static final String AUTH_TOKEN = "int-test-token";
  private static final String URI = "/api/v1/fee-calculation";

  @Test
  void shouldGetBadResponse_whenDuplicateField() throws Exception {
    mockMvc.perform(post(URI)
            .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "feeCode": "MDAS2B",
                  "feeCode": "MDAS2B",
                  "claimId": "claim_123",
                  "startDate": "2019-09-30",
                  "netDisbursementAmount": 100.21,
                  "disbursementVatAmount": 20.12,
                  "vatIndicator": true,
                  "numberOfMediationSessions": 1
                }
                """)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(content().json("""
            {
              "status": 400,
              "error": "Bad Request",
              "message": "JSON parse error: Duplicate field 'feeCode'"
            }
            """, LENIENT));
  }

  @Test
  void shouldGetBadResponse_whenMissingField() throws Exception {
    mockMvc.perform(post(URI)
            .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "claimId": "claim_123",
                  "startDate": "2019-09-30",
                  "netDisbursementAmount": 100.21,
                  "disbursementVatAmount": 20.12,
                  "vatIndicator": true,
                  "numberOfMediationSessions": 1
                }
                """)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(content().json("""
            {
              "status": 400,
              "error": "Bad Request"
            }
            """, LENIENT))
        .andExpect(result ->
            assertTrue(result.getResponse().getContentAsString().contains("default message [feeCode]]; default message [must not be null]]"))
        );
  }

  @Test
  void shouldGetFeeCalculation_associatedCivil() throws Exception {
    mockMvc
        .perform(post(URI)
            .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "feeCode": "ASMS",
                  "claimId": "claim_123",
                  "uniqueFileNumber": "020416/001",
                  "netProfitCosts": 27.8,
                  "netTravelCosts": 10.0,
                  "netWaitingCosts": 11.5,
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
              "escapeCaseFlag": false,
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

  @Test
  void shouldGetFeeCalculation_discrimination() throws Exception {
    mockMvc
        .perform(post(URI)
            .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
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
                """)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json("""
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
            """, STRICT));
  }

  @Test
  void shouldGetFeeCalculation_family() throws Exception {
    mockMvc
        .perform(post(URI)
            .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "feeCode": "FPB010",
                  "claimId": "claim_123",
                  "startDate": "2022-02-01",
                  "netDisbursementAmount": 123.38,
                  "disbursementVatAmount": 24.67,
                  "londonRate": true,
                  "vatIndicator": true
                }
                """)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json("""
            {
              "feeCode": "FPB010",
              "claimId": "claim_123",
              "schemeId": "FAM_LON_FS2011",
              "escapeCaseFlag": false,
              "feeCalculation": {
                  "totalAmount": 306.45,
                  "vatIndicator": true,
                  "vatRateApplied": 20.0,
                  "calculatedVatAmount": 26.4,
                  "disbursementAmount": 123.38,
                  "requestedNetDisbursementAmount": 123.38,
                  "disbursementVatAmount": 24.67,
                  "fixedFeeAmount": 132.0
                }
              }
            """, STRICT));
  }

  @Test
  void shouldGetFeeCalculation_immigrationAndAsylumFixedFee() throws Exception {
    mockMvc
        .perform(post(URI)
            .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "feeCode": "IMCF",
                  "claimId": "claim_123",
                  "startDate": "2024-09-30",
                  "netDisbursementAmount": 100.21,
                  "disbursementVatAmount": 20.12,
                  "vatIndicator": true,
                  "boltOns": {
                        "boltOnAdjournedHearing": 2.00,
                        "boltOnCmrhOral": 1.00,
                        "boltOnCmrhTelephone": 3.00
                  },
                  "detentionTravelAndWaitingCosts": 111.00,
                  "jrFormFilling": 50.00
                }
                """)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json("""
            {
              "feeCode": "IMCF",
              "schemeId": "IMM_ASYLM_FS2023",
              "claimId": "claim_123",
              "escapeCaseFlag": false,
              "feeCalculation": {
                "totalAmount": 2533.53,
                "vatIndicator": true,
                "vatRateApplied": 20.00,
                "calculatedVatAmount": 402.20,
                "disbursementAmount": 100.21,
                "requestedNetDisbursementAmount": 100.21,
                "disbursementVatAmount": 20.12,
                "fixedFeeAmount": 1092.00,
                "detentionTravelAndWaitingCostsAmount": 111.00,
                "jrFormFillingAmount": 50,
                "boltOnFeeDetails": {
                  "boltOnTotalFeeAmount": 758.00,
                  "boltOnAdjournedHearingCount": 2,
                  "boltOnAdjournedHearingFee": 322.00,
                  "boltOnCmrhOralCount": 1,
                  "boltOnCmrhOralFee": 166.00,
                  "boltOnCmrhTelephoneCount": 3,
                  "boltOnCmrhTelephoneFee": 270.00
                }
              }
            }
            """, STRICT));
  }

  @Test
  void shouldGetFeeCalculation_immigrationAndAsylumHourlyRate_legalHelp() throws Exception {
    mockMvc
        .perform(post(URI)
            .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "feeCode": "IMXL",
                  "claimId": "claim_123",
                  "startDate": "2015-02-11",
                  "netProfitCosts": 116.89,
                  "netDisbursementAmount": 125.70,
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
              "feeCalculation": {
                "totalAmount": 291.11,
                "vatIndicator": true,
                "vatRateApplied": 20.00,
                "calculatedVatAmount": 23.38,
                "disbursementAmount": 125.70,
                "requestedNetDisbursementAmount": 125.70,
                "disbursementVatAmount": 25.14,
                "hourlyTotalAmount": 242.59,
                "netProfitCostsAmount": 116.89,
                "requestedNetProfitCostsAmount": 116.89
              }
            }
            """, STRICT));
  }

  @Test
  void shouldGetFeeCalculation_immigrationAndAsylumHourlyRate_clr() throws Exception {
    mockMvc
        .perform(post(URI)
            .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "feeCode": "IAXC",
                  "claimId": "claim_123",
                  "startDate": "2015-02-11",
                  "netProfitCosts": 116.89,
                  "netCostOfCounsel": 356.90,
                  "netDisbursementAmount": 125.70,
                  "disbursementVatAmount": 25.14,
                  "vatIndicator": true
                }
                """)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json("""
            {
              "feeCode": "IAXC",
              "schemeId": "IMM_ASYLM_FS2013",
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
            """, STRICT));
  }

  @Test
  void shouldGetFeeCalculation_immigrationAndAsylumHourlyRate_clrInterim() throws Exception {
    mockMvc
        .perform(post(URI)
            .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "feeCode": "IACD",
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
                """)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json("""
            {
              "feeCode": "IACD",
              "schemeId": "IMM_ASYLM_FS2020",
              "claimId": "claim_123",
              "feeCalculation": {
                "totalAmount": 1781.39,
                "vatIndicator": true,
                "vatRateApplied": 20.00,
                "calculatedVatAmount": 271.76,
                "disbursementAmount": 125.70,
                "requestedNetDisbursementAmount": 125.70,
                "disbursementVatAmount": 25.14,
                "hourlyTotalAmount": 1484.49,
                "netProfitCostsAmount": 116.89,
                "requestedNetProfitCostsAmount": 116.89,
                "netCostOfCounselAmount": 356.90,
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
  void shouldGetFeeCalculation_mediation() throws Exception {
    mockMvc
        .perform(post(URI)
            .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "feeCode": "MDAS2B",
                  "claimId": "claim_123",
                  "startDate": "2019-09-30",
                  "netDisbursementAmount": 100.21,
                  "disbursementVatAmount": 20.12,
                  "vatIndicator": true,
                  "numberOfMediationSessions": 1
                }
                """)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json("""
            {
              "feeCode": "MDAS2B",
              "schemeId": "MED_FS2013",
              "claimId": "claim_123",
              "escapeCaseFlag": false,
              "feeCalculation": {
                "totalAmount": 321.93,
                "vatIndicator": true,
                "vatRateApplied": 20.00,
                "calculatedVatAmount": 33.60,
                "disbursementAmount": 100.21,
                "requestedNetDisbursementAmount": 100.21,
                "disbursementVatAmount": 20.12,
                "fixedFeeAmount": 168.00
              }
            }
            """, STRICT));
  }

  @Test
  void shouldGetFeeCalculation_mentalHealth() throws Exception {
    mockMvc
        .perform(post(URI)
            .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "feeCode": "MHL03",
                  "claimId": "claim_123",
                  "startDate": "2021-11-05",
                  "netDisbursementAmount": 100.21,
                  "disbursementVatAmount": 20.12,
                  "vatIndicator": true,
                  "boltOns": {
                    "boltOnAdjournedHearing": 3.00
                  }
                }
                """)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json("""
            {
              "feeCode": "MHL03",
              "schemeId": "MHL_FS2013",
              "claimId": "claim_123",
              "escapeCaseFlag": false,
              "feeCalculation": {
                "totalAmount": 1081.53,
                "vatIndicator": true,
                "vatRateApplied": 20.00,
                "calculatedVatAmount": 160.20,
                "disbursementAmount": 100.21,
                "requestedNetDisbursementAmount": 100.21,
                "disbursementVatAmount": 20.12,
                "fixedFeeAmount": 450.00,
                "boltOnFeeDetails": {
                  "boltOnTotalFeeAmount": 351.00,
                  "boltOnAdjournedHearingCount": 3,
                  "boltOnAdjournedHearingFee": 351.00
                }
              }
            }
            """, STRICT));

  }

  @ParameterizedTest
  @CsvSource({
      "CAPA, CAPA_FS2013, 434.85, 47.8, 239.0",
      "CLIN, CLIN_FS2013, 382.05, 39.0, 195.0",
      "COM, COM_FS2013, 467.25, 53.2, 266.0",
      "DEBT, DEBT_FS2013, 364.05, 36.0, 180.0",
      "EDUFIN, EDU_FS2013, 474.45, 54.4, 272.0",
      "ELA, ELA_FS2024, 336.45, 31.4, 157.0",
      "HOUS, HOUS_FS2013, 336.45, 31.4, 157.0",
      "MISCCON, MISC_FS2013, 338.85, 31.8, 159.0",
      "PUB, PUB_FS2013, 458.85, 51.8, 259.0",
      "WFB1, WB_FS2023, 397.65, 41.6, 208.0"
  })
  void shouldGetFeeCalculation_otherCivilCategories(String feeCode,
                                                    String schemeId,
                                                    String expectedTotal,
                                                    String expectedVatAmount,
                                                    String fixedFeeAmount) throws Exception {
    String expectedJson = """
        {
          "feeCode": "%s",
          "schemeId": "%s",
          "claimId": "claim_123",
          "escapeCaseFlag": false,
          "feeCalculation": {
            "totalAmount": %s,
            "vatIndicator": true,
            "vatRateApplied": 20.00,
            "calculatedVatAmount": %s,
            "disbursementAmount": 123.38,
            "requestedNetDisbursementAmount": 123.38,
            "disbursementVatAmount": 24.67,
            "fixedFeeAmount": %s
          }
        }
        """.formatted(feeCode, schemeId, expectedTotal, expectedVatAmount, fixedFeeAmount);

    mockMvc.perform(post(URI)
            .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "feeCode": "%s",
                  "claimId": "claim_123",
                  "startDate": "2025-02-01",
                  "netProfitCosts": 239.06,
                  "netDisbursementAmount": 123.38,
                  "disbursementVatAmount": 24.67,
                  "vatIndicator": true
                }
                """.formatted(feeCode))
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json(expectedJson, STRICT));
  }

  @Test
  void shouldGetFeeCalculation_policeStationFixedFee() throws Exception {
    mockMvc
        .perform(post(URI)
            .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "feeCode": "INVC",
                  "claimId": "claim_123",
                  "startDate": "2019-12-12",
                  "uniqueFileNumber": "121219/242",
                  "policeStationId": "NE001",
                  "policeStationSchemeId": "1001",
                  "vatIndicator": false
                }
                """)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json("""
            {
              "feeCode": "INVC",
              "claimId": "claim_123",
              "schemeId": "POL_FS2016",
              "escapeCaseFlag": false,
              "feeCalculation": {
                "totalAmount": 131.40,
                "vatIndicator": false,
                "calculatedVatAmount": 0,
                "fixedFeeAmount": 131.40
              }
            }
            """, STRICT));
  }

  @Test
  void shouldGetFeeCalculation_policeOtherFixedFee() throws Exception {
    mockMvc
        .perform(post(URI)
            .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "feeCode": "INVB1",
                  "claimId": "claim_123",
                  "startDate": "2019-12-12",
                  "uniqueFileNumber": "12122019/242",
                  "policeStationId": "NE001",
                  "policeStationSchemeId": "1001",
                  "vatIndicator": true
                }
                """)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json("""
            {
              "feeCode": "INVB1",
              "claimId": "claim_123",
              "schemeId": "POL_FS2016",
              "escapeCaseFlag": false,
              "feeCalculation": {
              "totalAmount": 34.44,
              "vatIndicator": true,
              "vatRateApplied": 20.0,
              "calculatedVatAmount": 5.74,
              "fixedFeeAmount": 28.7
              }
            }
            """, STRICT));
  }

  @Test
  void shouldGetFeeCalculation_policeStationHourlyRate() throws Exception {
    mockMvc
        .perform(post(URI)
            .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "feeCode": "INVH",
                  "claimId": "claim_123",
                  "startDate": "2019-12-12",
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
                """)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json("""
            {
              "feeCode": "INVH",
              "schemeId": "POL_FS2022",
              "claimId": "claim_123",
              "feeCalculation": {
                  "totalAmount": 208.72,
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
            """, STRICT));
  }

  @ParameterizedTest
  @CsvSource({
      "PROJ5, MAGS_COURT_FS2022, 491.27, 57.2, 286.02",
      "YOUK2, YOUTH_COURT_FS2024, 427.09, 46.51, 232.53",
  })
  void shouldGetFeeCalculation_designatedMagsOrYouthCourt(String feeCode,
                                                          String schemeId,
                                                          String expectedTotal,
                                                          String expectedVatAmount,
                                                          String fixedFeeAmount) throws Exception {
    String expectedJson = """
        {
          "feeCode": "%s",
          "schemeId": "%s",
          "claimId": "claim_123",
          "feeCalculation": {
            "totalAmount": %s,
            "vatIndicator": true,
            "vatRateApplied": 20.00,
            "calculatedVatAmount": %s,
            "disbursementAmount": 123.38,
            "requestedNetDisbursementAmount": 123.38,
            "disbursementVatAmount": 24.67,
            "fixedFeeAmount": %s
          }
        }
        """.formatted(feeCode, schemeId, expectedTotal, expectedVatAmount, fixedFeeAmount);

    mockMvc.perform(post(URI)
            .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "feeCode": "%s",
                  "claimId": "claim_123",
                  "uniqueFileNumber": "121219/242",
                  "representationOrderDate": "2025-02-01",
                  "netDisbursementAmount": 123.38,
                  "disbursementVatAmount": 24.67,
                  "vatIndicator": true
                }
                """.formatted(feeCode))
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json(expectedJson, STRICT));
  }

  @ParameterizedTest
  @CsvSource({
      "PROE1, MAGS_COURT_FS2022, 669.91, 86.98, 100, 111, 223.88",
      "YOUE1, YOUTH_COURT_FS2024, 1388.21, 206.69, 100, 111, 822.47",
  })
  void shouldGetFeeCalculation_undesignatedMagsOrYouthCourt(
      String feeCode,
      String schemeId,
      String expectedTotal,
      String expectedVatAmount,
      String netWaitingCosts,
      String netTravelCosts,
      String fixedFeeAmount
  ) throws Exception {

    String expectedJson = """
        {
          "feeCode": "%s",
          "schemeId": "%s",
          "claimId": "claim_123",
          "feeCalculation": {
            "totalAmount": %s,
            "vatIndicator": true,
            "vatRateApplied": 20.00,
            "calculatedVatAmount": %s,
            "disbursementAmount": 123.38,
            "requestedNetDisbursementAmount": 123.38,
            "disbursementVatAmount": 24.67,
            "netWaitingCostsAmount": %s,
            "netTravelCostsAmount": %s,
            "fixedFeeAmount": %s
          }
        }
        """.formatted(feeCode, schemeId, expectedTotal, expectedVatAmount, netWaitingCosts, netTravelCosts, fixedFeeAmount);

    mockMvc.perform(post(URI)
            .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
              {
                "feeCode": "%s",
                "claimId": "claim_123",
                "uniqueFileNumber": "121219/242",
                "representationOrderDate": "2025-02-01",
                "netDisbursementAmount": 123.38,
                "disbursementVatAmount": 24.67,
                "vatIndicator": true,
                "netWaitingCosts": %s,
                "netTravelCosts": %s
              }
              """.formatted(feeCode, netWaitingCosts, netTravelCosts))
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json(expectedJson, STRICT));
  }

  @ParameterizedTest
  @CsvSource({
      "PROH, AAR_FS2022, 810.0, 120.0, 600.0, 500.0, 500.0",
      "APPA, AAR_FS2022, 261.6, 28.6, 143.0, 43.0, 43.0",
      "APPB, AAR_FS2022, 354.0, 44.0, 220.0, 120.0, 120.0",
  })
  void shouldGetFeeCalculation_advocacyAppealsReview(String feeCode,
                                                     String schemeId,
                                                     String expectedTotal,
                                                     String expectedVatAmount,
                                                     String expectedHourlyTotalAmount,
                                                     String netProfitCostsAmount,
                                                     String requestedNetProfitCostsAmount
  ) throws Exception {
    String expectedJson = """
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
        """.formatted(feeCode, schemeId, expectedTotal, expectedVatAmount, expectedHourlyTotalAmount, netProfitCostsAmount, requestedNetProfitCostsAmount);

    mockMvc.perform(post(URI)
            .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "feeCode": "%s",
                  "uniqueFileNumber": "110425/123",
                  "netProfitCosts": %s,
                  "netDisbursementAmount": 80,
                  "disbursementVatAmount": 10,
                  "vatIndicator": true,
                  "netTravelCosts": 50,
                  "netWaitingCosts": 50
                }
                """.formatted(feeCode, netProfitCostsAmount))
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json(expectedJson, STRICT));
  }

  @Test
  void shouldGetFeeCalculation_educationDisbursementOnly() throws Exception {
    mockMvc
        .perform(post(URI)
            .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "feeCode": "EDUDIS",
                  "claimId": "claim_123",
                  "startDate": "2025-02-01",
                  "netDisbursementAmount": 123.38,
                  "disbursementVatAmount": 24.67
                }
                """)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json("""
            {
              "feeCode": "EDUDIS",
              "schemeId": "EDU_DISB_FS2024",
              "claimId": "claim_123",
              "feeCalculation": {
                  "totalAmount": 148.05,
                  "disbursementAmount": 123.38,
                  "requestedNetDisbursementAmount": 123.38,
                  "disbursementVatAmount": 24.67
                }
              }
            """, STRICT));
  }

  @Test
  void shouldGetFeeCalculation_immigrationDisbursementOnly() throws Exception {
    mockMvc
        .perform(post(URI)
            .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "feeCode": "ICASD",
                  "claimId": "claim_123",
                  "startDate": "2021-09-30",
                  "netDisbursementAmount": 55.35,
                  "disbursementVatAmount": 11.07
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
              "feeCalculation": {
                "totalAmount": 66.42,
                "disbursementAmount": 55.35,
                "requestedNetDisbursementAmount": 55.35,
                "disbursementVatAmount": 11.07
                }
              }
            """, STRICT));
  }

  @Test
  void shouldGetFeeCalculation_mentalHealthDisbursementOnly() throws Exception {
    mockMvc
        .perform(post(URI)
            .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "feeCode": "MHLDIS",
                  "claimId": "claim_123",
                  "startDate": "2025-07-29",
                  "netDisbursementAmount": 1200.0,
                  "disbursementVatAmount": 150.0
                }
                """)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json("""
            {
              "feeCode": "MHLDIS",
              "schemeId": "MHL_DISB_FS2020",
              "claimId": "claim_123",
              "feeCalculation": {
                  "totalAmount": 1350.0,
                  "disbursementAmount": 1200.0,
                  "requestedNetDisbursementAmount": 1200.0,
                  "disbursementVatAmount": 150.0
                }
              }
            """, STRICT));
  }

  @Test
  void should_GetErrorCodeAndMessage_WhenLondonRateIsNotSupplied_InFamilyClaimRequest() throws Exception {
    mockMvc
        .perform(post(URI)
            .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "feeCode": "FPB010",
                  "startDate": "2022-02-01",
                  "netDisbursementAmount": 123.38,
                  "disbursementVatAmount": 24.67,
                  "vatIndicator": true
                }
                """)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json("""
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
            """, STRICT));
  }

  @Test
  void shouldGetFeeCalculation_sendingHearing() throws Exception {
    mockMvc
        .perform(post(URI)
            .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "feeCode": "PROW",
                  "claimId": "claim_123",
                  "representationOrderDate": "2025-02-01",
                  "uniqueFileNumber": "010225/001",
                  "netDisbursementAmount": 123.38,
                  "disbursementVatAmount": 24.67,
                  "vatIndicator": true
                }
                """)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json("""
            {
              "feeCode": "PROW",
              "schemeId": "SEND_HEAR_FS2022",
              "claimId": "claim_123",
              "feeCalculation": {
                  "totalAmount": 398.38,
                  "vatIndicator": true,
                  "vatRateApplied": 20.0,
                  "calculatedVatAmount": 41.72,
                  "disbursementAmount": 123.38,
                  "requestedNetDisbursementAmount": 123.38,
                  "disbursementVatAmount": 24.67,
                  "fixedFeeAmount": 208.61
              }
              }
            """, STRICT));
  }

  @Test
  void shouldGetFeeCalculation_preOrderCover() throws Exception {
    mockMvc
        .perform(post(URI)
            .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "feeCode": "PROP1",
                  "claimId": "claim_123",
                  "uniqueFileNumber": "110425/123",
                  "netProfitCosts": 10.56,
                  "netDisbursementAmount": 20.5,
                  "disbursementVatAmount": 5.15,
                  "netTravelCosts": 11.35,
                  "netWaitingCosts": 12.22,
                  "vatIndicator": true
                }
                """)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json("""
            {
                "feeCode": "PROP1",
                "schemeId": "POC_FS2022",
                "claimId": "claim_123",
                "feeCalculation": {
                    "totalAmount": 66.61,
                    "vatIndicator": true,
                    "vatRateApplied": 20.0,
                    "calculatedVatAmount": 6.83,
                    "disbursementAmount": 20.5,
                    "requestedNetDisbursementAmount": 20.5,
                    "disbursementVatAmount": 5.15,
                    "hourlyTotalAmount": 34.13,
                    "netProfitCostsAmount": 10.56,
                    "requestedNetProfitCostsAmount": 10.56,
                    "netTravelCostsAmount": 11.35,
                    "netWaitingCostsAmount": 12.22
                }
            }
            """, STRICT));
  }

  @Test
  void shouldGetFeeCalculation_adviceAssistanceAdvocacy() throws Exception {
    mockMvc
        .perform(post(URI)
            .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
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
                """)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json("""
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
            """, STRICT));
  }

}
