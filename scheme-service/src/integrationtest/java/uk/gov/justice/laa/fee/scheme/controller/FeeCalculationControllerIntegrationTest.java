package uk.gov.justice.laa.fee.scheme.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.json.JsonCompareMode.LENIENT;
import static org.springframework.test.json.JsonCompareMode.STRICT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
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
class FeeCalculationControllerIntegrationTest extends PostgresContainerTestBase {

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
    String request = """ 
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
        """;

    postAndExpect(request, """
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
        """);
  }

  @Test
  void shouldGetFeeCalculation_discrimination() throws Exception {
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

  @Test
  void shouldGetFeeCalculation_family() throws Exception {
    String request = """ 
        {
          "feeCode": "FPB010",
          "claimId": "claim_123",
          "startDate": "2022-02-01",
          "netDisbursementAmount": 123.38,
          "disbursementVatAmount": 24.67,
          "londonRate": true,
          "vatIndicator": true
        }
        """;

    postAndExpect(request, """
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
        """);
  }

  @ParameterizedTest
  @CsvSource(value = {
      // feeCode, startDate, feeScheme, total, vat, fixedFee, boltOn, boltOnFee
      "IACA, 2022-09-30, IMM_ASYLM_FS2020, 785.13, 110.8, 227.0, CmrhOral, 166.0",
      "IACB, 2022-09-30, IMM_ASYLM_FS2020, 1555.53, 239.2, 869.0, CmrhOral, 166.0",
      "IACC, 2022-09-30, IMM_ASYLM_FS2020, 1627.53, 251.2, 929.0, CmrhOral, 166.0",
      "IACE, 2025-12-22, IMM_ASYLM_FS2025, 1502.73, 230.4, 808.0, CmrhOral, 183.0", // uplift 2025
      "IACF, 2025-12-22, IMM_ASYLM_FS2025, 2394.33, 379.0, 1551.0, CmrhOral, 183.0", // uplift 2025
      "IALB, 2025-12-22, IMM_ASYLM_FS2025, 1416.33, 216.0, 559.0, HomeOfficeInterview, 360", // uplift 2025
      "IMCA, 2022-09-30, IMM_ASYLM_FS2020, 785.13, 110.8, 227.0, CmrhOral, 166.0",
      "IMCB, 2022-09-30, IMM_ASYLM_FS2020, 1341.93, 203.6, 691.0, CmrhOral, 166.0",
      "IMCC, 2022-09-30, IMM_ASYLM_FS2020, 1429.53, 218.2, 764.0, CmrhOral, 166.0",
      "IMCE, 2025-12-22, IMM_ASYLM_FS2025,  1443.93, 220.6, 759.0, CmrhOral, 183.0", // uplift 2025
      "IMCF, 2025-12-22, IMM_ASYLM_FS2025, 2085.93, 327.6, 1294.0, CmrhOral, 183.0", // uplift 2025
      "IMLB, 2025-12-22, IMM_ASYLM_FS2025, 1125.93, 167.6, 317.0, HomeOfficeInterview, 360", // uplift 2025
      "IDAS1, 2025-12-22, IMM_ASYLM_FS2025, 612.33, 82.0, 249.0, null, 0", // uplift 2025
      "IDAS2, 2025-12-22, IMM_ASYLM_FS2025, 909.93, 131.6, 497.0, null, 0" // uplift 2025
  }, nullValues = {"null"})
  void shouldGetFeeCalculation_immigrationAndAsylumFixedFee(String feeCode, LocalDate startDate, String feeScheme,
                                                            double total, double vat, double fixedFee,
                                                            String boltOn, double boltOnFee) throws Exception {

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
            "disbursementAmount": 100.21,
            "requestedNetDisbursementAmount": 100.21,
            "disbursementVatAmount": 20.12,
            "fixedFeeAmount": %s,
            "detentionTravelAndWaitingCostsAmount": 111.00,
            "jrFormFillingAmount": 50
          }
        }
        """.formatted(feeCode, feeScheme, total, vat, fixedFee);

    String expectedJsonWithBoltOns = """
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
            "disbursementAmount": 100.21,
            "requestedNetDisbursementAmount": 100.21,
            "disbursementVatAmount": 20.12,
            "fixedFeeAmount": %s,
            "detentionTravelAndWaitingCostsAmount": 111.00,
            "jrFormFillingAmount": 50,
            "boltOnFeeDetails": {
               "boltOnTotalFeeAmount": %s,
               "boltOn%sCount": 1,
               "boltOn%sFee": %s
            }
          }
        }
        """.formatted(feeCode, feeScheme, total, vat, fixedFee, boltOnFee, boltOn, boltOn, boltOnFee);

    String request = """
        {
          "feeCode": "%s",
          "claimId": "claim_123",
          "startDate": "%s",
          "netDisbursementAmount": 100.21,
          "disbursementVatAmount": 20.12,
          "vatIndicator": true,
          "detentionTravelAndWaitingCosts": 111.00,
          "jrFormFilling": 50.00
        """.formatted(feeCode, startDate);
    request = (boltOn != null) ? request + ", \"boltOns\": { \"boltOn%s\": 1 }}".formatted(boltOn) : request + "}";

    postAndExpect(request, boltOn != null ? expectedJsonWithBoltOns : expectedJson);
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

  @Test
  void shouldGetFeeCalculation_mediation() throws Exception {
    String request = """ 
        {
          "feeCode": "MDAS2B",
          "claimId": "claim_123",
          "startDate": "2019-09-30",
          "netDisbursementAmount": 100.21,
          "disbursementVatAmount": 20.12,
          "vatIndicator": true,
          "numberOfMediationSessions": 1
        }
        """;

    postAndExpect(request, """
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
        """);
  }

  @Test
  void shouldGetFeeCalculation_mentalHealth() throws Exception {
    String request = """ 
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
        """;

    postAndExpect(request, """
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
        """);
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
    String request = """ 
        {
          "feeCode": "%s",
          "claimId": "claim_123",
          "startDate": "2025-02-01",
          "netProfitCosts": 239.06,
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
        """.formatted(feeCode, schemeId, expectedTotal, expectedVatAmount, fixedFeeAmount));
  }

  @ParameterizedTest
  @CsvSource({
      "211225/242, POL_FS2024, 223.52",
      "221225/242, POL_FS2025, 320.0"
  })
  void shouldGetFeeCalculation_policeStationFixedFee(String ufn, String feeScheme, double feeTotal) throws Exception {
    String request = """ 
        {
          "feeCode": "INVC",
          "claimId": "claim_123",
          "uniqueFileNumber": "%s",
          "policeStationId": "NE001",
          "policeStationSchemeId": "1001",
          "vatIndicator": false
        }
        """.formatted(ufn);

    postAndExpect(request, """
        {
          "feeCode": "INVC",
          "claimId": "claim_123",
          "schemeId": "%s",
          "escapeCaseFlag": false,
          "feeCalculation": {
            "totalAmount": %s,
            "vatIndicator": false,
            "calculatedVatAmount": 0,
            "fixedFeeAmount": %s
          }
        }
        """.formatted(feeScheme, feeTotal, feeTotal));
  }

  @Test
  void shouldGetFeeCalculation_policeOtherFixedFee() throws Exception {
    String request = """ 
        {
          "feeCode": "INVB1",
          "claimId": "claim_123",
          "uniqueFileNumber": "12122019/242",
          "policeStationId": "NE001",
          "policeStationSchemeId": "1001",
          "vatIndicator": true
        }
        """;

    postAndExpect(request, """
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
        """);
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
      "PROJ5, 2025-12-21, MAGS_COURT_FS2022, 491.27, 57.2, 286.02",
      "YOUK2, 2025-12-21, YOUTH_COURT_FS2024, 427.09, 46.51, 232.53",
      "PROJ5, 2025-12-22, MAGS_COURT_FS2025, 525.59, 62.92, 314.62",
      "YOUK2, 2025-12-22, YOUTH_COURT_FS2025, 454.99, 51.16, 255.78"
  })
  void shouldGetFeeCalculation_designatedMagsOrYouthCourt(
      String feeCode,
      String repOrderDate,
      String schemeId,
      String expectedTotal,
      String expectedVatAmount,
      String fixedFeeAmount
  ) throws Exception {
    LocalDate representationOrderDate = LocalDate.parse(repOrderDate);
    String request = """
        {
          "feeCode": "%s",
          "claimId": "claim_123",
          "representationOrderDate": "%s",
          "netDisbursementAmount": 123.38,
          "disbursementVatAmount": 24.67,
          "vatIndicator": true
        }
        """.formatted(feeCode, representationOrderDate);

    postAndExpect(request, """
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
        """.formatted(feeCode, schemeId, expectedTotal, expectedVatAmount, fixedFeeAmount)
    );
  }

  @ParameterizedTest
  @CsvSource({
      "PROE1, 2025-12-21, MAGS_COURT_FS2022, 669.91, 86.98, 100, 111, 223.88",
      "YOUE1, 2025-12-21, YOUTH_COURT_FS2024, 1388.21, 206.69, 100, 111, 822.47",
      "PROE1, 2025-12-22, MAGS_COURT_FS2025, 696.77, 91.45, 100, 111, 246.27",
      "YOUE1, 2025-12-22, YOUTH_COURT_FS2025, 1486.91, 223.14, 100, 111, 904.72",
  })
  void shouldGetFeeCalculation_undesignatedMagsOrYouthCourt(
      String feeCode,
      String repOrderDate,
      String schemeId,
      String expectedTotal,
      String expectedVatAmount,
      String netWaitingCosts,
      String netTravelCosts,
      String fixedFeeAmount
  ) throws Exception {
    LocalDate representationOrderDate = LocalDate.parse(repOrderDate);
    String request = """ 
        {
          "feeCode": "%s",
          "claimId": "claim_123",
          "representationOrderDate": "%s",
          "netDisbursementAmount": 123.38,
          "disbursementVatAmount": 24.67,
          "vatIndicator": true,
          "netWaitingCosts": %s,
          "netTravelCosts": %s
        }
        """.formatted(feeCode, representationOrderDate, netWaitingCosts, netTravelCosts);

    postAndExpect(request, """
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
        """.formatted(feeCode, schemeId, expectedTotal, expectedVatAmount,
        netWaitingCosts, netTravelCosts, fixedFeeAmount));
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
    String request = """ 
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
        """.formatted(feeCode, netProfitCostsAmount);

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
  void shouldGetFeeCalculation_educationDisbursementOnly() throws Exception {
    String request = """ 
        {
          "feeCode": "EDUDIS",
          "claimId": "claim_123",
          "startDate": "2025-02-01",
          "netDisbursementAmount": 123.38,
          "disbursementVatAmount": 24.67
        }
        """;

    postAndExpect(request, """
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
        """);
  }

  @Test
  void shouldGetFeeCalculation_immigrationDisbursementOnly() throws Exception {
    String request = """ 
        {
          "feeCode": "ICASD",
          "claimId": "claim_123",
          "startDate": "2021-09-30",
          "netDisbursementAmount": 55.35,
          "disbursementVatAmount": 11.07
        }
        """;

    postAndExpect(request, """
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
        }
        """);
  }

  @Test
  void shouldGetFeeCalculation_mentalHealthDisbursementOnly() throws Exception {
    String request = """ 
        {
          "feeCode": "MHLDIS",
          "claimId": "claim_123",
          "startDate": "2025-07-29",
          "netDisbursementAmount": 1200.0,
          "disbursementVatAmount": 150.0
        }
        """;

    postAndExpect(request, """
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
        }
        """);
  }

  @ParameterizedTest
  @CsvSource({
      "PROW, 010120/456, SEND_HEAR_FS2020, 2020-12-21, 217.68, 36.28, 181.4",
      "PROW, 051222/678, SEND_HEAR_FS2022, 2022-12-21, 250.33, 41.72, 208.61",
      "PROW, 261225/934, SEND_HEAR_FS2025, 2025-12-26, 275.36, 45.89, 229.47"
  })
  void shouldGetFeeCalculation_sendingHearing(
      String feeCode,
      String uniqueFileNumber,
      String schemeId,
      String representationOrderDate,
      String expectedTotal,
      String expectedVatAmount,
      String fixedFeeAmount
  ) throws Exception {
    String request = """
        {
          "feeCode": "%s",
          "claimId": "claim_123",
          "uniqueFileNumber": "%s",
          "representationOrderDate": "%s",
          "vatIndicator": true
        }
        """.formatted(feeCode, uniqueFileNumber, representationOrderDate);

    postAndExpect(request, """
        {
          "feeCode": "%s",
          "schemeId": "%s",
          "claimId": "claim_123",
          "feeCalculation": {
            "totalAmount": %s,
            "vatIndicator": true,
            "vatRateApplied": 20.00,
            "calculatedVatAmount": %s,
            "fixedFeeAmount": %s
          }
        }
        """.formatted(feeCode, schemeId, expectedTotal, expectedVatAmount, fixedFeeAmount)
    );
  }

  @ParameterizedTest
  @CsvSource({
      "PROP1, 211225/123, POC_FS2022",
      "PROP1, 221225/123, POC_FS2025",
      "PROP2, 211225/123, POC_FS2022",
      "PROP2, 221225/123, POC_FS2025",
  })
  void shouldGetFeeCalculation_preOrderCover(String feeCode, String ufn, String feeScheme) throws Exception {
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

  @Test
  void shouldGetFeeCalculation_adviceAssistanceAdvocacy() throws Exception {
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

  @ParameterizedTest
  @CsvSource({
      "PROU, 211225/456, EC_RMT_FS2022, 31.48, 5.25, 26.23",
      "PROU, 221225/456, EC_RMT_FS2025, 34.62, 5.77, 28.85"
  })
  void shouldGetFeeCalculation_earlyCoverOrRefusedMeans(
      String feeCode,
      String uniqueFileNumber,
      String schemeId,
      String expectedTotal,
      String expectedVatAmount,
      String fixedFeeAmount
  ) throws Exception {
    String request = """
        {
          "feeCode": "%s",
          "claimId": "claim_123",
          "uniqueFileNumber": "%s",
          "netDisbursementAmount": 123.38,
          "disbursementVatAmount": 24.67,
          "vatIndicator": true
        }
        """.formatted(feeCode, uniqueFileNumber);

    postAndExpect(request, """
        {
          "feeCode": "%s",
          "schemeId": "%s",
          "claimId": "claim_123",
          "feeCalculation": {
            "totalAmount": %s,
            "vatIndicator": true,
            "vatRateApplied": 20.00,
            "calculatedVatAmount": %s,
            "fixedFeeAmount": %s
          }
        }
        """.formatted(feeCode, schemeId, expectedTotal, expectedVatAmount, fixedFeeAmount)
    );
  }
}
