package uk.gov.justice.laa.fee.scheme.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.core.JsonProcessingException;
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

  @Test
  void getFeeCalculation_andLogFeeRequest() throws Exception {
    Logger feeLogger = (Logger) LoggerFactory.getLogger(FeeCalculationController.class);
    ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
    listAppender.start();
    feeLogger.addAppender(listAppender);

    FeeCalculationResponse responseDto = FeeCalculationResponse.builder()
        .feeCode("FEE123")
        .feeCalculation(FeeCalculation.builder()
            .totalAmount(1500.12)
            .build())
        .build();

    when(feeCalculationService.calculateFee(feeCalculationRequest)).thenReturn(responseDto);

    mockMvc.perform(post("/api/v1/fee-calculation")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(feeCalculationRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.feeCode").value("FEE123"))
        .andExpect(jsonPath("$.feeCalculation.totalAmount").value(1500));

    List<ILoggingEvent> logsList = listAppender.list;
    assertThat(logsList.stream().anyMatch(event -> event.getLevel() == Level.INFO
        && event.getFormattedMessage().contains("\"feeCode\":\"FEE123\""))).isTrue();

    listAppender.stop();
    feeLogger.detachAppender(listAppender);
  }

  @Test
  void logFeeRequest_shouldLogWarning_whenSerializationFails() throws Exception {
    FeeCalculationController controller = new FeeCalculationController(feeCalculationService) {
      @Override
      public ResponseEntity<FeeCalculationResponse> getFeeCalculation(FeeCalculationRequest feeCalculationRequest) {
        return super.getFeeCalculation(feeCalculationRequest);
      }
    };

    ObjectMapper failingMapper = mock(ObjectMapper.class);
    when(failingMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("Serialization failed") {});

    Field objectMapperField = FeeCalculationController.class.getDeclaredField("objectMapper");
    objectMapperField.setAccessible(true);
    objectMapperField.set(controller, failingMapper);

    Logger feeLogger = (Logger) LoggerFactory.getLogger(FeeCalculationController.class);
    ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
    listAppender.start();
    feeLogger.addAppender(listAppender);

    FeeCalculationResponse responseDto = FeeCalculationResponse.builder()
        .feeCode("FEE123")
        .feeCalculation(FeeCalculation.builder().totalAmount(1500.12).build())
        .build();

    when(feeCalculationService.calculateFee(feeCalculationRequest)).thenReturn(responseDto);

    controller.getFeeCalculation(feeCalculationRequest);

    List<ILoggingEvent> logsList = listAppender.list;
    assertThat(logsList.stream().anyMatch(event -> event.getLevel() == Level.WARN
        && event.getFormattedMessage().contains("could not serialize object"))).isTrue();

    listAppender.stop();
    feeLogger.detachAppender(listAppender);
  }

}