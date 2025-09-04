package uk.gov.justice.laa.fee.scheme.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
class CategoryOfLawControllerIntegrationTest extends PostgresContainerTestBase {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void shouldGetCategoryOfLaw() throws Exception {
    mockMvc
        .perform(get("/api/v1/fee-details/CAPA"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.categoryOfLawCode").value("AAP"))
        .andExpect(jsonPath("$.feeCodeDescription").value("Claims Against Public Authorities Legal Help Fixed Fee"))
        .andExpect(jsonPath("$.feeType").value("FIXED"));
  }
}
