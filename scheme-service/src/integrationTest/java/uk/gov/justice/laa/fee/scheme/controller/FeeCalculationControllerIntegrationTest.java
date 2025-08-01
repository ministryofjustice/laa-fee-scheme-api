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
import uk.gov.justice.laa.fee.scheme.FeeSchemeApplication;

@SpringBootTest(classes = FeeSchemeApplication.class)
@AutoConfigureMockMvc
public class FeeCalculationControllerIntegrationTest {
  @Autowired
  private MockMvc mockMvc;

  @Test
  void shouldGetFeeCalculation() throws Exception {
    mockMvc
        .perform(post("/api/v1/fee-calculation")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{" +
                "\"feeCode\": \"string\"," +
                "\"startDate\": \"2025-08-01\"," +
                "\"netProfitCosts\": 0.1," +
                "\"netDisbursementAmount\": 0.1," +
                "\"netCostOfCounsel\": 0.1," +
                "\"disbursementVatAmount\": 0.1," +
                "\"vatIndicator\": true," +
                "\"disbursementPriorAuthority\": \"string\"," +
                "\"boltOns\": {" +
                "  \"boltOnAdjournedHearing\": 0," +
                "  \"boltOnDetentionTravelWaitingCosts\": 0," +
                "  \"boltOnJrFormFilling\": 0," +
                "  \"boltOnCmrhOral\": 0," +
                "  \"boltOnCrmhTelephone\": 0," +
                "  \"boltOnAdditionalTravel\": 0" +
                "}," +
                "\"netTravelCosts\": \"string\"," +
                "\"netWaitingCosts\": \"string\"," +
                "\"caseConcludedDate\": \"2025-08-01\"," +
                "\"policeCourtOrPrisonId\": \"string\"," +
                "\"dutySolicitor\": \"string\"," +
                "\"schemeId\": \"string\"," +
                "\"ufn\": \"string\"," +
                "\"numberOfMediationSessions\": 0" +
                "}")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.feeCode").value("FEE123"))
        .andExpect(jsonPath("$.feeCalculation.totalAmount").value(1500.56));
  }
}
