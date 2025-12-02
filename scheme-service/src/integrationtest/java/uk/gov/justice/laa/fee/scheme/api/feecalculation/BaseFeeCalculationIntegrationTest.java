package uk.gov.justice.laa.fee.scheme.api.feecalculation;

import static org.springframework.test.json.JsonCompareMode.STRICT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.justice.laa.fee.scheme.postgrestestcontainer.PostgresContainerTestBase;

public abstract class BaseFeeCalculationIntegrationTest extends PostgresContainerTestBase{

  protected static final String AUTH_TOKEN = "int-test-token";
  protected static final String URI = "/api/v1/fee-calculation";

  @Autowired
  protected MockMvc mockMvc;

  protected void postAndExpect(String requestJson, String expectedResponseJson) throws Exception {
    mockMvc
        .perform(post(URI)
            .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json(expectedResponseJson, STRICT));
  }
}
