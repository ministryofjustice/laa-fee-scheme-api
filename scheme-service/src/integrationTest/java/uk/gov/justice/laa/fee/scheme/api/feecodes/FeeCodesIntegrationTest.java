package uk.gov.justice.laa.fee.scheme.api.feecodes;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.json.JsonCompareMode.LENIENT;
import static org.springframework.test.json.JsonCompareMode.STRICT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import uk.gov.justice.laa.fee.scheme.postgrestestcontainer.PostgresContainerTestBase;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class FeeCodesIntegrationTest extends PostgresContainerTestBase {

  private static final String INT_TEST_TOKEN = "int-test-token";

  private static final String API_V1_FEE_CODES = "/api/v1/fee-codes/";

  private static final String API_V1_FEE_CODES_LEGAL_HELP = API_V1_FEE_CODES + "LEGAL_HELP";

  private static final String HEADER_CORRELATION_ID = "X-Correlation-Id";

  private final MockMvc mockMvc;

  @Autowired
  public FeeCodesIntegrationTest(MockMvc mockMvc) {
    this.mockMvc = mockMvc;
  }

  @Test
  void shouldGetFeeCodesWhenCorrelationIdProvided() throws Exception {

    String correlationId = "a51433f8-a78c-47ef-bd31-837b95467220";

    mockMvc
        .perform(
            get(API_V1_FEE_CODES_LEGAL_HELP)
                .header(HttpHeaders.AUTHORIZATION, INT_TEST_TOKEN)
                .header(HEADER_CORRELATION_ID, correlationId))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.feeCodes").isArray())
        .andExpect(jsonPath("$.feeCodes.length()").value(is(87)))
        .andExpect(jsonPath("$.feeCodes[0].feeCode").exists())
        .andExpect(jsonPath("$.feeCodes[0].areaOfLaw").value("Legal Help"))
        .andExpect(jsonPath("$.feeCodes[0].categoryOfLawCodes").isArray())
        .andExpect(jsonPath("$.feeCodes[0].feeType").exists())
        .andExpect(header().string(HEADER_CORRELATION_ID, correlationId));
  }

  @Test
  void shouldGetFeeCodesWhenCorrelationIdNotProvided() throws Exception {

    mockMvc
        .perform(get(API_V1_FEE_CODES_LEGAL_HELP).header(HttpHeaders.AUTHORIZATION, INT_TEST_TOKEN))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.feeCodes").isArray())
        .andExpect(header().exists(HEADER_CORRELATION_ID));
  }

  private static Stream<Arguments> testAreaOfLawResponses() {

    return Stream.of(Arguments.of("LEGAL_HELP", "Legal Help"));
  }

  @MethodSource("testAreaOfLawResponses")
  @ParameterizedTest
  void shouldReturnFeeCodesForAreaOfLaw(String areaOfLaw, String expectedAreaDescription)
      throws Exception {

    mockMvc
        .perform(
            get(API_V1_FEE_CODES + areaOfLaw).header(HttpHeaders.AUTHORIZATION, INT_TEST_TOKEN))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.feeCodes").isArray())
        .andExpect(jsonPath("$.feeCodes[0].areaOfLaw").value(expectedAreaDescription));
  }

  @Test
  void shouldReturnNotFoundWhenAreaOfLawInvalid() throws Exception {

    mockMvc
        .perform(
            get(API_V1_FEE_CODES + "INVALID").header(HttpHeaders.AUTHORIZATION, INT_TEST_TOKEN))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.error").value("Not Found"))
        .andExpect(jsonPath("$.message").value("Area of law not found for: INVALID"))
        .andExpect(jsonPath("$.timestamp").exists());
  }

  @ValueSource(strings = {API_V1_FEE_CODES_LEGAL_HELP})
  @ParameterizedTest
  void shouldReturnUnauthorizedWhenAuthorizationHeaderMissing(String endpoint) throws Exception {

    mockMvc
        .perform(post(endpoint))
        .andExpect(status().isUnauthorized())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(
            content()
                .json(
                    """
            {
              "code": 401,
              "status": "UNAUTHORIZED",
              "message": "No API access token provided."
            }
            """,
                    STRICT));
  }

  @ValueSource(strings = {API_V1_FEE_CODES_LEGAL_HELP})
  @ParameterizedTest
  void shouldReturnUnauthorizedWhenAuthTokenInvalid(String endpoint) throws Exception {

    mockMvc
        .perform(post(endpoint).header(HttpHeaders.AUTHORIZATION, "INVALID"))
        .andExpect(status().isUnauthorized())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(
            content()
                .json(
                    """
            {
              "code": 401,
              "status": "UNAUTHORIZED",
              "message": "Invalid API access token provided."
            }
            """,
                    STRICT));
  }

  @ValueSource(strings = {API_V1_FEE_CODES_LEGAL_HELP})
  @ParameterizedTest
  void shouldReturnMethodNotAllowedWhenHttpMethodNotGet(String endpoint) throws Exception {

    mockMvc
        .perform(post(endpoint).header(HttpHeaders.AUTHORIZATION, INT_TEST_TOKEN))
        .andExpect(status().isMethodNotAllowed())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(
            content()
                .json(
                    """
            {
              "status": 405,
              "error": "Method Not Allowed",
              "message": "Request method 'POST' is not supported"
            }
            """,
                    LENIENT));
  }
}
