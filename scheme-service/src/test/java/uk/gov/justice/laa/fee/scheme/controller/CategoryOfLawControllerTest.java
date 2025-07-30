package uk.gov.justice.laa.fee.scheme.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.justice.laa.fee.scheme.model.CategoryOfLawResponse;
import uk.gov.justice.laa.fee.scheme.service.FeeService;

@WebMvcTest(CategoryOfLawController.class)
class CategoryOfLawControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private FeeService feeService;

  @Test
  void getFeeByCode() throws Exception {

    when(feeService.getCategoryCode(any())).thenReturn(CategoryOfLawResponse.builder()
        .categoryOfLawCode("ASY")
        .build());

    mockMvc.perform(get("/api/v1/category-of-law/FEE123")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.categoryOfLawCode").value("ASY"));
  }
}
