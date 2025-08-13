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
}
