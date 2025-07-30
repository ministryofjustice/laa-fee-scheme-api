package uk.gov.justice.laa.fee.scheme.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.justice.laa.fee.scheme.service.FeeService;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(CategoryOfLawController.class)
class FeeCategoryOfLawControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FeeService feeService;

    @Test
    void getFeeByCode() throws Exception {

//        when(feeService.getCategoryCode(any())).thenReturn(CategoryOfLawResponseDto.builder()
//                .categoryLawCode("asylum 123")
//                .feeCode("FEE123")
//                .build());
//
//        mockMvc.perform(get("/api/fee/FEE123")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.categoryLawCode").value("asylum 123"))
//                .andExpect(jsonPath("$.feeCode").value("FEE123"));
//
//
}
}
