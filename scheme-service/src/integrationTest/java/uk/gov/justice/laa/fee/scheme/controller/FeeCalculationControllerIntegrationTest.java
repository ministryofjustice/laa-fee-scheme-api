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

@SpringBootTest
@AutoConfigureMockMvc
public class FeeCalculationControllerIntegrationTest {
  @Autowired
  private MockMvc mockMvc;

  @Test
  void shouldGetFeeCalculation() throws Exception {
    mockMvc
        .perform(post("/api/v1/fee-calculation")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "feeCode": "FEE1",
                  "startDate": "2025-08-01",
                  "netProfitCosts": 1000.56,
                  "netDisbursementAmount": 200.75,
                  "netCostOfCounsel": 300.00,
                  "disbursementVatAmount": 40.15, 
                  "vatIndicator": true,
                  "disbursementPriorAuthority": "DISB123",
                  "boltOns": {
                    "boltOnAdjournedHearing": 1,
                    "boltOnDetentionTravelWaitingCosts": 0,
                    "boltOnJrFormFilling": 0,
                    "boltOnCmrhOral": 0,
                    "boltOnCrmhTelephone": 0,
                    "boltOnAdditionalTravel": 0
                  },
                  "netTravelCosts": 300.00,
                  "netWaitingCosts": 100.00,
                  "caseConcludedDate": "2025-08-01",
                  "policeCourtOrPrisonId": "12345",
                  "dutySolicitor": "SOLICITOR123",
                  "schemeId": "SCHEME1",
                  "ufn": "UFN123",
                  "numberOfMediationSessions": 0
                }
                """)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.feeCode").value("FEE123"))
        .andExpect(jsonPath("$.feeCalculation.totalAmount").value(1500.56));
  }
}
