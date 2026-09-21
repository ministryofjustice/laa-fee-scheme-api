package uk.gov.justice.laa.fee.scheme.api.feecalculation;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.justice.laa.fee.scheme.postgrestestcontainer.PostgresContainerTestBase;

/**
 * Full-stack (real Spring context, real database, real {@code FeatureFlagsConfig} property
 * binding) coverage for the Inquest feature flag gate on {@code /api/v1/fee-calculation}.
 */
@Testcontainers
@SpringBootTest(properties = "feature-flags.is-inquest-feature-enabled=false")
@AutoConfigureMockMvc
class FeeCalculationInquestFeatureFlagIntegrationTest extends PostgresContainerTestBase {

  private static final String AUTH_TOKEN = "int-test-token";
  private static final String URI = "/api/v1/fee-calculation";

  @Autowired
  private MockMvc mockMvc;

  @ParameterizedTest
  @ValueSource(strings = {"INQ", "COMINQ", "CAPAINQ"})
  void shouldRejectInquestFeeCodeWhenInquestFeatureDisabled(String feeCode) throws Exception {
    String request = """
        {
          "feeCode": "%s",
          "claimId": "claim_123",
          "startDate": "2026-12-10",
          "netProfitCosts": 239.06,
          "netDisbursementAmount": 123.38,
          "disbursementVatAmount": 24.67,
          "vatIndicator": true,
          "caseConcludedDate": "2027-01-01"
        }
        """.formatted(feeCode);

    mockMvc
        .perform(post(URI)
            .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .content(request)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.error").value("Not Found"))
        .andExpect(jsonPath("$.message").value("Feature is not available: INQUEST"))
        .andExpect(jsonPath("$.timestamp").exists());
  }

  @Test
  void shouldCalculateNonInquestFeeCodeWhenInquestFeatureDisabled() throws Exception {
    String request = """
        {
          "feeCode": "CAPA",
          "claimId": "claim_123",
          "startDate": "2025-02-01",
          "netProfitCosts": 239.06,
          "netDisbursementAmount": 123.38,
          "disbursementVatAmount": 24.67,
          "vatIndicator": true,
          "caseConcludedDate": "2026-02-01"
        }
        """;

    mockMvc
        .perform(post(URI)
            .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .content(request)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.feeCode").value("CAPA"))
        .andExpect(jsonPath("$.schemeId").value("CAPA_FS2013"))
        .andExpect(jsonPath("$.feeCalculation.totalAmount").value(434.85));
  }
}
