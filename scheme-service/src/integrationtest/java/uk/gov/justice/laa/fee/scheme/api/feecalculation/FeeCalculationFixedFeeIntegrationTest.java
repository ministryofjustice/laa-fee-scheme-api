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
public class FeeCalculationFixedFeeIntegrationTest extends BaseFeeCalculationIntegrationTest {

  @Test
  void shouldGetFeeCalculation_associatedCivilFixedFee() throws Exception {
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

  @ParameterizedTest
  @CsvSource({
      "PROJ5, 2025-12-21, MAGS_COURT_FS2022, 491.27, 57.2, 286.02",
      "YOUK2, 2025-12-21, YOUTH_COURT_FS2024, 427.09, 46.51, 232.53",
      "PROJ5, 2025-12-22, MAGS_COURT_FS2025, 525.59, 62.92, 314.62",
      "YOUK2, 2025-12-22, YOUTH_COURT_FS2025, 454.99, 51.16, 255.78"
  })
  void shouldGetFeeCalculation_designatedMagsOrYouthCourtFixedFee(String feeCode,
                                                                  String repOrderDate,
                                                                  String schemeId,
                                                                  String expectedTotal,
                                                                  String expectedVatAmount,
                                                                  String fixedFeeAmount) throws Exception {
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
      "PROU, 211225/456, EC_RMT_FS2022, 31.48, 5.25, 26.23",
      "PROU, 221225/456, EC_RMT_FS2025, 34.62, 5.77, 28.85"
  })
  void shouldGetFeeCalculation_earlyCoverOrRefusedMeansFixedFee(String feeCode,
                                                                String uniqueFileNumber,
                                                                String schemeId,
                                                                String expectedTotal,
                                                                String expectedVatAmount,
                                                                String fixedFeeAmount) throws Exception {
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

  @Test
  void shouldGetFeeCalculation_familyFixedFee() throws Exception {
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

  @Test
  void shouldGetFeeCalculation_mediationFixedFee() throws Exception {
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
  void shouldGetFeeCalculation_mentalHealthFixedFee() throws Exception {
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

  @Test
  void shouldGetFeeCalculation_educationFixedFee() throws Exception {
    String request = """ 
        {
          "feeCode": "EDUFIN",
          "claimId": "claim_123",
          "startDate": "2025-02-01",
          "netProfitCosts": 239.06,
          "netDisbursementAmount": 123.38,
          "disbursementVatAmount": 24.67,
          "vatIndicator": true
        }
        """;

    postAndExpect(request, """
        {
          "feeCode": "EDUFIN",
          "schemeId": "EDU_FS2013",
          "claimId": "claim_123",
          "escapeCaseFlag": false,
          "feeCalculation": {
            "totalAmount": 474.45,
            "vatIndicator": true,
            "vatRateApplied": 20.00,
            "calculatedVatAmount": 54.4,
            "disbursementAmount": 123.38,
            "requestedNetDisbursementAmount": 123.38,
            "disbursementVatAmount": 24.67,
            "fixedFeeAmount": 272.0
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
      "ELA, ELA_FS2024, 336.45, 31.4, 157.0",
      "HOUS, HOUS_FS2013, 336.45, 31.4, 157.0",
      "MISCCON, MISC_FS2013, 338.85, 31.8, 159.0",
      "PUB, PUB_FS2013, 458.85, 51.8, 259.0",
      "WFB1, WB_FS2023, 397.65, 41.6, 208.0"
  })
  void shouldGetFeeCalculation_otherCivilFixedFee(String feeCode,
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

  @ParameterizedTest
  @CsvSource({
      "211225/242, POL_FS2024, 223.52",
      "221225/242, POL_FS2025, 320.0"
  })
  void shouldGetFeeCalculation_policeStationFixedFee(String ufn,
                                                     String feeScheme,
                                                     double feeTotal) throws Exception {
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

  @ParameterizedTest
  @CsvSource({
      "PRIA, 211225/123, PRISON_FS2016, 360.9, 40.15, 200.75",
      "PRIA, 221225/123, PRISON_FS2025, 418.72, 49.79, 248.93",
      "PRIB1, 211225/123, PRISON_FS2016, 364.72, 40.79, 203.93",
      "PRIB1, 221225/123, PRISON_FS2025, 423.44, 50.57, 252.87",
      "PRIB2, 211225/123, PRISON_FS2016, 796.99, 112.83, 564.16",
      "PRIB2, 221225/123, PRISON_FS2025, 959.47, 139.91, 699.56",
      "PRIC1, 211225/123, PRISON_FS2016, 644.65, 87.44, 437.21",
      "PRIC1, 221225/123, PRISON_FS2025, 770.57, 108.43, 542.14",
      "PRIC2, 211225/123, PRISON_FS2016, 1865.33, 290.89, 1454.44",
      "PRIC2, 221225/123, PRISON_FS2025, 2284.21, 360.7, 1803.51",
      "PRID1, 211225/123, PRISON_FS2016, 364.72, 40.79, 203.93",
      "PRID1, 221225/123, PRISON_FS2025, 423.44, 50.57, 252.87",
      "PRID2, 211225/123, PRISON_FS2016, 796.99, 112.83, 564.16",
      "PRID2, 221225/123, PRISON_FS2025, 959.47, 139.91, 699.56",
      "PRIE1, 211225/123, PRISON_FS2016, 644.65, 87.44, 437.21",
      "PRIE2, 221225/123, PRISON_FS2025, 2284.21, 360.7, 1803.51",
  })
  void shouldGetFeeCalculation_prisonLawFixedFee(String feeCode, String ufn, String feeScheme,
                                                 double total, double vat, double fixedFee) throws Exception {
    String request = """ 
        {
          "feeCode": "%s",
          "claimId": "claim_123",
          "uniqueFileNumber": "%s",
          "netProfitCosts": 100,
          "netTravelCosts": 200,
          "netWaitingCosts": 200,
          "netDisbursementAmount": 100,
          "disbursementVatAmount": 20,
          "vatIndicator": true
        }
        """.formatted(feeCode, ufn);

    postAndExpect(request, """
        {
          "feeCode": "%s",
          "schemeId": "%s",
          "claimId": "claim_123",
          "escapeCaseFlag": false,
          "feeCalculation": {
            "totalAmount": %s,
            "vatIndicator": true,
            "vatRateApplied": 20.0,
            "calculatedVatAmount": %s,
            "disbursementAmount": 100.0,
            "requestedNetDisbursementAmount": 100.0,
            "disbursementVatAmount": 20.0,
            "fixedFeeAmount": %s
          }
        }
        """.formatted(feeCode, feeScheme, total, vat, fixedFee));
  }

  @ParameterizedTest
  @CsvSource({
      "PROW, 010120/456, SEND_HEAR_FS2020, 2020-12-21, 217.68, 36.28, 181.4",
      "PROW, 051222/678, SEND_HEAR_FS2022, 2022-12-21, 250.33, 41.72, 208.61",
      "PROW, 261225/934, SEND_HEAR_FS2025, 2025-12-26, 275.36, 45.89, 229.47"
  })
  void shouldGetFeeCalculation_sendingHearing(String feeCode,
                                              String uniqueFileNumber,
                                              String schemeId,
                                              String representationOrderDate,
                                              String expectedTotal,
                                              String expectedVatAmount,
                                              String fixedFeeAmount) throws Exception {
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
      "PROE1, 2025-12-21, MAGS_COURT_FS2022, 669.91, 86.98, 100, 111, 223.88",
      "YOUE1, 2025-12-21, YOUTH_COURT_FS2024, 1388.21, 206.69, 100, 111, 822.47",
      "PROE1, 2025-12-22, MAGS_COURT_FS2025, 696.77, 91.45, 100, 111, 246.27",
      "YOUE1, 2025-12-22, YOUTH_COURT_FS2025, 1486.91, 223.14, 100, 111, 904.72",
  })
  void shouldGetFeeCalculation_undesignatedMagsOrYouthCourt(String feeCode,
                                                            String repOrderDate,
                                                            String schemeId,
                                                            String expectedTotal,
                                                            String expectedVatAmount,
                                                            String netWaitingCosts,
                                                            String netTravelCosts,
                                                            String fixedFeeAmount) throws Exception {
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
}
