package uk.gov.justice.laa.fee.scheme.controller;


import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.justice.laa.fee.scheme.model.BoltOnType;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.service.FeeCalculationService;

@WebMvcTest(value = FeeCalculationController.class)
@AutoConfigureMockMvc(addFilters = false) // disable security filter for testing
class FeeCalculationControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private FeeCalculationService feeCalculationService;

  private FeeCalculationRequest feeCalculationRequest;

  @BeforeEach
  void setUp() {
    feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode("FEE123")
        .startDate(LocalDate.of(2025, 7, 29))
        .netProfitCosts(1000.50)
        .netDisbursementAmount(200.75)
        .disbursementVatAmount(40.15)
        .vatIndicator(true)
        .immigrationPriorAuthorityNumber("AUTH123")
        .boltOns(BoltOnType.builder()
            .boltOnHomeOfficeInterview(2)
            .boltOnAdjournedHearing(1)
            .boltOnCmrhOral(1)
            .boltOnCmrhTelephone(3)
            .build())
        .build();
  }

  @Test
  void getFeeCalculation() throws Exception {

    FeeCalculationResponse responseDto = FeeCalculationResponse.builder()
        .feeCode("FEE123")
        .feeCalculation(FeeCalculation.builder()
            .totalAmount(1500.12)
            .build())
        .build();

    when(feeCalculationService.calculateFee(feeCalculationRequest))
        .thenReturn(responseDto);

    mockMvc.perform(post("/api/v1/fee-calculation")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(feeCalculationRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.feeCode").value("FEE123"))
        .andExpect(jsonPath("$.feeCalculation.totalAmount").value(1500));
  }

  @Test
  void getFeeCalculation_whenGivenPoliceStationIds() throws Exception {

    feeCalculationRequest.setPoliceStationId("PS1");
    feeCalculationRequest.setPoliceStationSchemeId("PSS1");
    feeCalculationRequest.setUniqueFileNumber("UFN1");

    FeeCalculationResponse responseDto = FeeCalculationResponse.builder()
        .feeCode("FEE123")
        .feeCalculation(FeeCalculation.builder()
            .totalAmount(1500.12)
            .build())
        .build();

    when(feeCalculationService.calculateFee(feeCalculationRequest))
        .thenReturn(responseDto);

    mockMvc.perform(post("/api/v1/fee-calculation")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(feeCalculationRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.feeCode").value("FEE123"))
        .andExpect(jsonPath("$.feeCalculation.totalAmount").value(1500));
  }

}