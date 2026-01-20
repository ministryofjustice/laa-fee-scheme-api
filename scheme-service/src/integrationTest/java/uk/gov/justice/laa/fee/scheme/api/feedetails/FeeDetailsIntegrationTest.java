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
class FeeDetailsIntegrationTest extends PostgresContainerTestBase {

  static final String INT_TEST_TOKEN = "int-test-token";
  static final String API_V_1_FEE_DETAILS_CAPA = "/api/v1/fee-details/CAPA";

  private final MockMvc mockMvc;

  @Autowired
  public FeeDetailsIntegrationTest(MockMvc mockMvc) {
    this.mockMvc = mockMvc;
  }

  private static final String HEADER_CORRELATION_ID = "X-Correlation-Id";

  @Test
  void shouldGetFeeDetailsWhenCorrelationIdProvided() throws Exception {
    String correlationId = "a51433f8-a78c-47ef-bd31-837b95467220";
    mockMvc
        .perform(get(API_V_1_FEE_DETAILS_CAPA)
            .header(HttpHeaders.AUTHORIZATION, INT_TEST_TOKEN)
            .header(HEADER_CORRELATION_ID, correlationId))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.categoryOfLawCode").value("AAP"))
        .andExpect(jsonPath("$.feeCodeDescription").value("Claims Against Public Authorities Legal Help Fixed Fee"))
        .andExpect(jsonPath("$.feeType").value("FIXED"))
        .andExpect(header().string(HEADER_CORRELATION_ID, correlationId));
  }

  @Test
  void shouldGetFeeDetailsWhenCorrelationIdNotProvided() throws Exception {
    mockMvc
        .perform(get(API_V_1_FEE_DETAILS_CAPA)
            .header(HttpHeaders.AUTHORIZATION, INT_TEST_TOKEN))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.categoryOfLawCode").value("AAP"))
        .andExpect(jsonPath("$.feeCodeDescription").value("Claims Against Public Authorities Legal Help Fixed Fee"))
        .andExpect(jsonPath("$.feeType").value("FIXED"))
        .andExpect(header().exists(HEADER_CORRELATION_ID));
  }

  @Test
  void shouldReturnUnauthorizedResponseWhenAuthorizationHeaderIsMissing() throws Exception {
    mockMvc
        .perform(post(API_V_1_FEE_DETAILS_CAPA))
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
  void shouldReturnUnauthorizedResponseWhenAuthTokenIsInvalid() throws Exception {
    mockMvc
        .perform(post(API_V_1_FEE_DETAILS_CAPA)
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
  void shouldReturnNotFoundResponseWhenFeeCodeIsInvalid() throws Exception {
    mockMvc
        .perform(get("/api/v1/fee-details/BLAH")
            .header(HttpHeaders.AUTHORIZATION, INT_TEST_TOKEN))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.error").value("Not Found"))
        .andExpect(jsonPath("$.message").value("Category of law code not found for feeCode: BLAH"))
        .andExpect(jsonPath("$.timestamp").exists());
  }
}
