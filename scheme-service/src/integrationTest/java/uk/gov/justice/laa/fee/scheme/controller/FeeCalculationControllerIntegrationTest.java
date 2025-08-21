package uk.gov.justice.laa.fee.scheme.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
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
  void shouldGetFeeCalculation_communityCare() throws Exception {
    mockMvc
        .perform(post("/api/v1/fee-calculation")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "feeCode": "COM",
                  "startDate": "2021-11-02",
                  "netDisbursementAmount": 123.67,
                  "disbursementVatAmount": 24.73,
                  "vatIndicator": true
                }
                """)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.feeCode").value("COM"))
        .andExpect(jsonPath("$.feeCalculation.subTotal").value(389.67))
        .andExpect(jsonPath("$.feeCalculation.totalAmount").value(467.60));
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
        .andExpect(jsonPath("$.feeCalculation.subTotal").value(268.21))
        .andExpect(jsonPath("$.feeCalculation.totalAmount").value(321.93));
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
        .andExpect(jsonPath("$.feeCalculation.subTotal").value(2111.21))
        .andExpect(jsonPath("$.feeCalculation.totalAmount").value(2533.53));
  }
}
