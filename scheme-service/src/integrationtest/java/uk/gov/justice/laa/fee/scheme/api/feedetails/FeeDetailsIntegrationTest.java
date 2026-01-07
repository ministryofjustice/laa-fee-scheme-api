package uk.gov.justice.laa.fee.scheme.api.feedetails;

import static org.springframework.test.json.JsonCompareMode.STRICT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.justice.laa.fee.scheme.config.FeeSchemeTestConfig;
import uk.gov.justice.laa.fee.scheme.postgrestestcontainer.PostgresContainerTestBase;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(FeeSchemeTestConfig.class)
@Testcontainers
class FeeDetailsIntegrationTest extends PostgresContainerTestBase {

  @Autowired
  MockMvc mockMvc;

  private static final String HEADER_CORRELATION_ID = "X-Correlation-Id";

  @Test
  void shouldGetFeeDetails_correlationIdProvided() throws Exception {
    String correlationId = "a51433f8-a78c-47ef-bd31-837b95467220";
    mockMvc
        .perform(get("/api/v1/fee-details/CAPA")
            .header(HttpHeaders.AUTHORIZATION, "int-test-token")
            .header(HEADER_CORRELATION_ID, correlationId))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.categoryOfLawCode").value("AAP"))
        .andExpect(jsonPath("$.feeCodeDescription").value("Claims Against Public Authorities Legal Help Fixed Fee"))
        .andExpect(jsonPath("$.feeType").value("FIXED"))
        .andExpect(header().string(HEADER_CORRELATION_ID, correlationId));
  }

  @Test
  void shouldGetFeeDetails_correlationIdNotProvided() throws Exception {
    mockMvc
        .perform(get("/api/v1/fee-details/CAPA")
            .header(HttpHeaders.AUTHORIZATION, "int-test-token"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.categoryOfLawCode").value("AAP"))
        .andExpect(jsonPath("$.feeCodeDescription").value("Claims Against Public Authorities Legal Help Fixed Fee"))
        .andExpect(jsonPath("$.feeType").value("FIXED"))
        .andExpect(header().exists(HEADER_CORRELATION_ID));
  }

  @Test
  void shouldGetUnauthorizedResponse_whenMissingAuthorizationHeader() throws Exception {
    mockMvc
        .perform(post("/api/v1/fee-details/CAPA"))
        .andExpect(status().isUnauthorized())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json("""
            {
              "code": 401,
              "status": "UNAUTHORIZED",
              "message": "No API access token provided."
            }
            """, STRICT));
  }

  @Test
  void shouldGetUnauthorizedResponse_whenAuthTokenIsInvalid() throws Exception {
    mockMvc
        .perform(post("/api/v1/fee-details/CAPA")
            .header(HttpHeaders.AUTHORIZATION, "BLAH"))
        .andExpect(status().isUnauthorized())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json("""
            {
              "code": 401,
              "status": "UNAUTHORIZED",
              "message": "Invalid API access token provided."
            }
            """, STRICT));
  }

  @Test
  void shouldGetNotFoundResponse_whenFeeCodeIsInvalid() throws Exception {
    mockMvc
        .perform(get("/api/v1/fee-details/BLAH")
            .header(HttpHeaders.AUTHORIZATION, "int-test-token"))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.error").value("Not Found"))
        .andExpect(jsonPath("$.message").value("Category of law code not found for feeCode: BLAH"))
        .andExpect(jsonPath("$.timestamp").exists());
  }
}
