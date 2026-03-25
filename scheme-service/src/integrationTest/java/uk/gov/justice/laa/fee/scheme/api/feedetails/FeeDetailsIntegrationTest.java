package uk.gov.justice.laa.fee.scheme.api.feedetails;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.json.JsonCompareMode.LENIENT;
import static org.springframework.test.json.JsonCompareMode.STRICT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
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
class FeeDetailsIntegrationTest extends PostgresContainerTestBase {

  static final String INT_TEST_TOKEN = "int-test-token";
  static final String API_V_1_FEE_DETAILS = "/api/v1/fee-details/";
  static final String API_V_2_FEE_DETAILS = "/api/v2/fee-details/";
  static final String API_V_1_FEE_DETAILS_CAPA = API_V_1_FEE_DETAILS + "CAPA";
  static final String API_V_2_FEE_DETAILS_CAPA = API_V_2_FEE_DETAILS + "CAPA";

  private final MockMvc mockMvc;

  @Autowired
  public FeeDetailsIntegrationTest(MockMvc mockMvc) {
    this.mockMvc = mockMvc;
  }

  private static final String HEADER_CORRELATION_ID = "X-Correlation-Id";

  @Test
  void shouldGetFeeDetailsV1WhenCorrelationIdProvided() throws Exception {
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
  void shouldGetFeeDetailsV1WhenCorrelationIdNotProvided() throws Exception {
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

  private static Stream<Arguments> testCategoryOfLawCodes() {
    return Stream.of(
        Arguments.of("CAPA", "Claims Against Public Authorities Legal Help Fixed Fee", List.of("AAP")),
        Arguments.of("ASMS", "Legal Help and Associated Civil Work – Miscellaneous", List.of("APPEALS", "INVEST", "PRISON")),
        Arguments.of("ASPL", "Legal Help and Associated Civil Work – Public Law", List.of("APPEALS", "INVEST", "PRISON")),
        Arguments.of("ASAS", "Part 1 injunction Anti-Social Behaviour Crime and Policing Act 2014", List.of("APPEALS", "INVEST", "PRISON"))
    );
  }

  @MethodSource("testCategoryOfLawCodes")
  @ParameterizedTest
  void shouldGetFeeDetailsV2WhenCorrelationIdProvided(String feeCode, String description, List<String> categoryOfLawCodes) throws Exception {
    String correlationId = "a51433f8-a78c-47ef-bd31-837b95467220";
    mockMvc
        .perform(get(API_V_2_FEE_DETAILS + feeCode)
            .header(HttpHeaders.AUTHORIZATION, INT_TEST_TOKEN)
            .header(HEADER_CORRELATION_ID, correlationId))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.categoryOfLawCodes").value(is(categoryOfLawCodes)))
        .andExpect(jsonPath("$.feeCodeDescription").value(description))
        .andExpect(jsonPath("$.feeType").value("FIXED"))
        .andExpect(header().string(HEADER_CORRELATION_ID, correlationId));
  }

  @MethodSource("testCategoryOfLawCodes")
  @ParameterizedTest
  void shouldGetFeeDetailsV2WhenCorrelationIdNotProvided(String feeCode, String description, List<String> categoryOfLawCodes) throws Exception {
    mockMvc
        .perform(get(API_V_2_FEE_DETAILS + feeCode)
            .header(HttpHeaders.AUTHORIZATION, INT_TEST_TOKEN))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.categoryOfLawCodes").value(is(categoryOfLawCodes)))
        .andExpect(jsonPath("$.feeCodeDescription").value(description))
        .andExpect(jsonPath("$.feeType").value("FIXED"))
        .andExpect(header().exists(HEADER_CORRELATION_ID));
  }

  @ValueSource(strings = {API_V_1_FEE_DETAILS, API_V_2_FEE_DETAILS})
  @ParameterizedTest
  void shouldReturnNotFoundResponseWhenFeeCodeIsInvalid(String endpoint) throws Exception {
    mockMvc
        .perform(get(endpoint + "BLAH")
            .header(HttpHeaders.AUTHORIZATION, INT_TEST_TOKEN))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.error").value("Not Found"))
        .andExpect(jsonPath("$.message").value("Category of law code not found for feeCode: BLAH"))
        .andExpect(jsonPath("$.timestamp").exists());
  }

  @ValueSource(strings = {API_V_1_FEE_DETAILS_CAPA, API_V_2_FEE_DETAILS_CAPA})
  @ParameterizedTest
  void shouldReturnUnauthorizedResponseWhenAuthorizationHeaderIsMissing(String endpoint) throws Exception {
    mockMvc
        .perform(post(endpoint))
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

  @ValueSource(strings = {API_V_1_FEE_DETAILS_CAPA, API_V_2_FEE_DETAILS_CAPA})
  @ParameterizedTest
  void shouldReturnUnauthorizedResponseWhenAuthTokenIsInvalid(String endpoint) throws Exception {
    mockMvc
        .perform(post(endpoint)
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

  @ValueSource(strings = {API_V_1_FEE_DETAILS_CAPA, API_V_2_FEE_DETAILS_CAPA})
  @ParameterizedTest
  void shouldReturnMethodNotAllowedErrorWhenHttpNotGetRequestMethod(String endpoint) throws Exception {
    mockMvc
        .perform(post(endpoint)
            .header(HttpHeaders.AUTHORIZATION, INT_TEST_TOKEN))
        .andExpect(status().isMethodNotAllowed())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json("""
            {
              "status": 405,
              "error": "Method Not Allowed",
              "message": "Request method 'POST' is not supported"
            }
            """, LENIENT));
  }
}
