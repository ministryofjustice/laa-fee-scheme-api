package uk.gov.justice.laa.fee.scheme.feecalculator.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.justice.laa.fee.scheme.feecalculator.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.feecalculator.model.FeeCalculationRequestDto;
import uk.gov.justice.laa.fee.scheme.feecalculator.model.FeeCalculationResponseDto;
import uk.gov.justice.laa.fee.scheme.feecalculator.service.FeeService;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FeeCalculationController.class)
class FeeCalculationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private FeeService feeService;

    @Test
    void getFeeCalculation() throws Exception {
        FeeCalculationRequestDto requestDto = getFeeCalculationRequestDto();

        FeeCalculationResponseDto responseDto = FeeCalculationResponseDto.builder()
                .feeCode("FEE123")
                .feeCalculation(FeeCalculation.builder()
                        .subTotal(new BigDecimal("1234"))
                        .finalTotal(new BigDecimal("1500"))
                        .build())
                .build();

        when(feeService.getFeeCalculation(ArgumentMatchers.any(FeeCalculationRequestDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/api/fee-calculation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.feeCode").value("FEE123"))
                .andExpect(jsonPath("$.feeCalculation.subTotal").value(1234))
                .andExpect(jsonPath("$.feeCalculation.finalTotal").value(1500));
    }

    private static FeeCalculationRequestDto getFeeCalculationRequestDto() {
        FeeCalculationRequestDto requestDto = new FeeCalculationRequestDto();
        requestDto.setFeeCode("FEE123");
        requestDto.setStartDate(LocalDate.of(2025, 7, 29));
        requestDto.setNetProfitCosts(new BigDecimal("1000.50"));
        requestDto.setNetDisbursementAmount(new BigDecimal("200.75"));
        requestDto.setDisbursementVatAmount(new BigDecimal("40.15"));
        requestDto.setVatIndicator(true);
        requestDto.setDisbursementPriorAuthority("AUTH123");
        requestDto.setBoltOnAdjournedHearing(1);
        requestDto.setBoltOnDetentionTravelWaitingCosts(2);
        requestDto.setBoltOnJrFormFilling(0);
        requestDto.setBoltOnCmrhOral(1);
        requestDto.setBoltOnCrmhTelephone(3);
        return requestDto;
    }

}