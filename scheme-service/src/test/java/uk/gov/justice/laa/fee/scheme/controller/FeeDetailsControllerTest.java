package uk.gov.justice.laa.fee.scheme.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.justice.laa.fee.scheme.exception.CategoryCodeNotFoundException;
import uk.gov.justice.laa.fee.scheme.model.FeeDetailsResponseV1;
import uk.gov.justice.laa.fee.scheme.model.FeeDetailsResponseV2;
import uk.gov.justice.laa.fee.scheme.service.FeeDetailsService;

@WebMvcTest(FeeDetailsController.class)
@AutoConfigureMockMvc(addFilters = false) // disable security filter for testing
class FeeDetailsControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private FeeDetailsService feeDetailsService;

  @Test
  void getFeeDetailsV1FeeByCode() throws Exception {
    when(feeDetailsService.getFeeDetailsV1("FEE123")).thenReturn(FeeDetailsResponseV1.builder()
        .categoryOfLawCode("ASY")
        .feeCodeDescription("fee_code_description")
        .feeType("FIXED")
        .build());

    mockMvc.perform(get("/api/v1/fee-details/FEE123")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.categoryOfLawCode").value("ASY"))
        .andExpect(jsonPath("$.feeCodeDescription").value("fee_code_description"))
        .andExpect(jsonPath("$.feeType").value("FIXED"));
  }

  @Test
  void getFeeDetailsV1FeeByCodeThrowsExceptionWhenCategoryOfLawNotFound() throws Exception {
    when(feeDetailsService.getFeeDetailsV1("FEE123")).thenThrow(new CategoryCodeNotFoundException("FEE123"));

    mockMvc.perform(get("/api/v1/fee-details/FEE123")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.timestamp").exists())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.error").value("Not Found"))
        .andExpect(jsonPath("$.message").value("Category of law code not found for feeCode: FEE123"));
  }

  @Test
  void getFeeDetailsV2FeeByCode() throws Exception {
    when(feeDetailsService.getFeeDetailsV2("FEE123")).thenReturn(FeeDetailsResponseV2.builder()
        .categoryOfLawCodes(List.of("CAT1", "CAT2", "CAT3"))
        .feeCodeDescription("fee_code_description")
        .feeType("FIXED")
        .build());

    mockMvc.perform(get("/api/v2/fee-details/FEE123")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.categoryOfLawCodes.*").value(Matchers.is(List.of("CAT1", "CAT2", "CAT3"))))
        .andExpect(jsonPath("$.feeCodeDescription").value("fee_code_description"))
        .andExpect(jsonPath("$.feeType").value("FIXED"));
  }

  @Test
  void getFeeDetailsV2FeeByCodeThrowsExceptionWhenCategoryOfLawNotFound() throws Exception {
    when(feeDetailsService.getFeeDetailsV2("FEE123")).thenThrow(new CategoryCodeNotFoundException("FEE123"));

    mockMvc.perform(get("/api/v2/fee-details/FEE123")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.timestamp").exists())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.error").value("Not Found"))
        .andExpect(jsonPath("$.message").value("Category of law code not found for feeCode: FEE123"));
  }

}
