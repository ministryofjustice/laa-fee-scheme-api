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
                  "detentionAndWaitingCosts": 111.00,
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
                "detentionAndWaitingCostsAmount": 111.00,
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
                  "jrFormFilling": 25.00,
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
                "totalAmount": 321.11,
                "vatIndicator": true,
                "vatRateApplied": 20.00,
                "calculatedVatAmount": 28.38,
                "disbursementAmount": 125.70,
                "requestedNetDisbursementAmount": 125.70,
                "disbursementVatAmount": 25.14,
                "hourlyTotalAmount": 141.89,
                "netProfitCostsAmount": 116.89,
                "requestedNetProfitCostsAmount": 116.89,
                "jrFormFillingAmount": 25.00
              }
            }
            """, STRICT));
  }

  @Test
  void shouldGetFeeCalculationWithWarnings_immigrationAndAsylumHourlyRate_legalHelp() throws Exception {
    mockMvc
        .perform(post(URI)
            .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "feeCode": "IMXL",
                  "claimId": "claim_123",
                  "startDate": "2015-02-11",
                  "netProfitCosts": 766.89,
                  "jrFormFilling": 25.00,
                  "netDisbursementAmount": 410.70,
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
                    type: "WARNING",
                    message: "warning net profit costs"
                  },
                  {
                    type: "WARNING",
                    message: "warning net disbursements"
                  }
              ],
              "feeCalculation": {
                "totalAmount": 1055.14,
                "vatIndicator": true,
                "vatRateApplied": 20.00,
                "calculatedVatAmount": 105.00,
                "disbursementAmount": 400.,
                "requestedNetDisbursementAmount": 410.70,
                "disbursementVatAmount": 25.14,
                "hourlyTotalAmount": 525.00,
                "netProfitCostsAmount": 500.00,
                "requestedNetProfitCostsAmount": 766.89,
                "jrFormFillingAmount": 25.00
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
                  "uniqueFileNumber": "12122019/2423",
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
              "schemeId": "POL_FS2016",
              "escapeCaseFlag": false,
              "feeCalculation": {
                "totalAmount": 131.40,
                "vatIndicator": false,
                "vatRateApplied": 20.00,
                "calculatedVatAmount": 0,
                "disbursementAmount": 0,
                "requestedNetDisbursementAmount" : 0,
                "disbursementVatAmount": 0,
                "fixedFeeAmount": 131.40
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
                  "uniqueFileNumber": "041122/6655",
                  "policeStationId": "NE024",
                  "policeStationSchemeId": "1007",
                  "netProfitCosts": 34.56,
                  "netDisbursementAmount": 50.5,
                  "disbursementVatAmount": 20.15,
                  "travelAndWaitingCosts": 12.45,
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
              "feeCalculation": {
                "totalAmount": 187.66,
                "vatIndicator": true,
                "vatRateApplied": 20.0,
                "calculatedVatAmount": 19.5,
                "disbursementAmount": 50.5,
                "requestedNetDisbursementAmount": 50.5,
                "disbursementVatAmount": 20.15,
                "hourlyTotalAmount": 97.51,
                "netProfitCostsAmount": 34.56,
                "requestedNetProfitCostsAmount": 34.56,
                "travelAndWaitingCostAmount": 12.45
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
          "netWaitingCosts": %s,
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

}
