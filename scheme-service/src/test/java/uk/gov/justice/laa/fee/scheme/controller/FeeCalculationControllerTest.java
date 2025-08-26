package uk.gov.justice.laa.fee.scheme.controller;


import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.justice.laa.fee.scheme.model.BoltOnType;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.service.FeeService;

@WebMvcTest(FeeCalculationController.class)
class FeeCalculationControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private FeeService feeService;

  private static FeeCalculationRequest getFeeCalculationRequestDto() {
    FeeCalculationRequest requestDto = new FeeCalculationRequest();
    requestDto.setFeeCode("FEE123");
    requestDto.setStartDate(LocalDate.of(2025, 7, 29));
    requestDto.setNetProfitCosts(1000.50);
    requestDto.setNetDisbursementAmount(200.75);
    requestDto.setDisbursementVatAmount(40.15);
    requestDto.setVatIndicator(true);
    requestDto.setDisbursementPriorAuthority("AUTH123");
    requestDto.boltOns(BoltOnType.builder()
        .boltOnHomeOfficeInterview(2)
        .boltOnAdjournedHearing(1)
        .boltOnCmrhOral(1)
        .boltOnCrmhTelephone(3)
        .build());
    return requestDto;
  }

  @Test
  void getFeeCalculation() throws Exception {
    FeeCalculationRequest requestDto = getFeeCalculationRequestDto();

    FeeCalculationResponse responseDto = FeeCalculationResponse.builder()
        .feeCode("FEE123")
        .feeCalculation(FeeCalculation.builder()
            .totalAmount(1500.12)
            .build())
        .build();

    when(feeService.getFeeCalculation(ArgumentMatchers.any(FeeCalculationRequest.class)))
        .thenReturn(responseDto);

    mockMvc.perform(post("/api/v1/fee-calculation")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.feeCode").value("FEE123"))
        .andExpect(jsonPath("$.feeCalculation.totalAmount").value(1500));
  }

}