package uk.gov.justice.laa.fee.scheme.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.justice.laa.fee.scheme.exception.CategoryCodeNotFoundException;
import uk.gov.justice.laa.fee.scheme.model.FeeDetailsResponse;
import uk.gov.justice.laa.fee.scheme.service.FeeDetailsService;

@WebMvcTest(FeeDetailsController.class)
@AutoConfigureMockMvc(addFilters = false) // disable security filter for testing
class FeeDetailsControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private FeeDetailsService feeDetailsService;

  @Test
  void getFeeDetailsFeeByCode() throws Exception {

    when(feeDetailsService.getFeeDetails(any())).thenReturn(FeeDetailsResponse.builder()
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
  void throwExceptionWhenCategoryOfLawNotFound() throws Exception {
    when(feeDetailsService.getFeeDetails(anyString())).thenThrow(new CategoryCodeNotFoundException("FEE123"));

    mockMvc.perform(get("/api/v1/fee-details/FEE123")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.message").value("Category of law code not found for feeCode: FEE123"));
  }
}
