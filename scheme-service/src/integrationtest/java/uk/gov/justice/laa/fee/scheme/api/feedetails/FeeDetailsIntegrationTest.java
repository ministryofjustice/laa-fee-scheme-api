package uk.gov.justice.laa.fee.scheme.api.feedetails;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.justice.laa.fee.scheme.api.feecalculation.BaseFeeCalculationIntegrationTest;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class FeeDetailsIntegrationTest extends BaseFeeCalculationIntegrationTest {

  @Test
  void shouldGetFeeDetails() throws Exception {
    mockMvc
        .perform(get("/api/v1/fee-details/CAPA")
            .header(HttpHeaders.AUTHORIZATION, "int-test-token"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.categoryOfLawCode").value("AAP"))
        .andExpect(jsonPath("$.feeCodeDescription").value("Claims Against Public Authorities Legal Help Fixed Fee"))
        .andExpect(jsonPath("$.feeType").value("FIXED"));
  }
}
