package uk.gov.justice.laa.fee.scheme.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.justice.laa.fee.scheme.postgresTestContainer.PostgresContainerTestBase;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class FeeCalculationControllerIntegrationTest extends PostgresContainerTestBase {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void shouldGetFeeCalculation_discrimination() throws Exception {
    mockMvc
        .perform(post("/api/v1/fee-calculation")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "feeCode": "DISC",
                  "startDate": "2019-09-30",
                  "netProfitCosts": 150.25,
                  "netCostOfCounsel": 79.19,
                  "travelAndWaitingCosts": 88.81,
                  "netDisbursementAmount": 100.21,
                  "disbursementVatAmount": 20.12,
                  "vatIndicator": true
                }
                """)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.feeCode").value("DISC"))
        .andExpect(jsonPath("$.feeCalculation.totalAmount").value(502.23));
  }

  @Test
  void shouldGetFeeCalculation_mediation() throws Exception {
    mockMvc
        .perform(post("/api/v1/fee-calculation")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "feeCode": "MED21",
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
        .andExpect(jsonPath("$.feeCode").value("MED21"))
        .andExpect(jsonPath("$.feeCalculation.totalAmount").value(321.93));
  }

  @ParameterizedTest
  @CsvSource({
      "CAPA, 362.38, 434.85",    // Claims Against Public Authorities
      "CLIN, 318.38, 382.05",    // Clinical Negligence
      "COM, 389.38, 467.25",      // Community Care
      "DEBT, 303.38, 364.05",     // Debt
      "ELA, 280.38, 336.45",      // Housing - HLPAS
      "HOUS, 280.38, 336.45",     // Housing
      "MISCCON, 282.38, 338.85",  // Miscellaneous
      "PUB, 382.38, 458.85"       // Public Law
  })
  void shouldGetFeeCalculation_otherCivilCategories(String feeCode, String expectedSubTotal, String expectedTotal) throws Exception {
    mockMvc
        .perform(post("/api/v1/fee-calculation")
            .contentType(MediaType.APPLICATION_JSON)
            .content(String.format("""
                {
                  "feeCode": "%s",
                  "startDate": "2025-02-01",
                  "netDisbursementAmount": 123.38,
                  "disbursementVatAmount": 24.67,
                  "vatIndicator": true
                }
                """, feeCode))
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.feeCode").value(feeCode))
        .andExpect(jsonPath("$.feeCalculation.totalAmount").value(expectedTotal));
  }

  @Test
  void shouldGetFeeCalculation_immigrationAndAsylumFixedFee() throws Exception {
    mockMvc
        .perform(post("/api/v1/fee-calculation")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "feeCode": "IMCF",
                  "startDate": "2024-09-30",
                  "netDisbursementAmount": 100.21,
                  "disbursementVatAmount": 20.12,
                  "vatIndicator": true,
                  "boltOns": {
                        "boltOnAdjournedHearing": 2.00,
                        "boltOnCmrhOral": 1.00,
                        "boltOnCrmhTelephone": 3.00
                  },
                  "detentionAndWaitingCosts": 111.00,
                  "jrFormFilling": 50.00
                }
                """)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.feeCode").value("IMCF"))
        .andExpect(jsonPath("$.feeCalculation.totalAmount").value(2533.53));
  }

  @Test
  void shouldGetFeeCalculation_mentalHealth() throws Exception {
    mockMvc
        .perform(post("/api/v1/fee-calculation")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "feeCode": "MHL03",
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
        .andExpect(jsonPath("$.feeCode").value("MHL03"))
        .andExpect(jsonPath("$.feeCalculation.totalAmount").value(1081.53));
  }

  @Test
  void shouldGetFeeCalculation_policeStation() throws Exception {
    mockMvc
        .perform(post("/api/v1/fee-calculation")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "feeCode": "INVC",
                  "startDate": "2019-12-12",
                  "uniqueFileNumber": "12122019/2423",
                  "policeStationId": "NE001",
                  "policeStationSchemeId": "1001"
                }
                """)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.feeCode").value("INVC"))
        .andExpect(jsonPath("$.feeCalculation.totalAmount").value(131.4));
  }

}
