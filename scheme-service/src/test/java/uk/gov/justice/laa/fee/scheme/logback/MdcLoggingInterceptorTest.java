package uk.gov.justice.laa.fee.scheme.logback;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerMapping;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;

class MdcLoggingInterceptorTest {

  private static final String FEE_CODE = "feeCode";
  private static final String HEADER_CORRELATION_ID = "X-Correlation-Id";

  @AfterEach
  void clearMdc() {
    MDC.clear();
  }

  @Test
  void shouldSetFeeCodeAndCorrelationIdInMdc_whenCorrelationIdIsNotProvided() {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    Map<String, String> pathVariables = new HashMap<>();
    pathVariables.put("feeCode", "ABC123");
    when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE))
        .thenReturn(pathVariables);

    MdcLoggingInterceptor interceptor = new MdcLoggingInterceptor();
    interceptor.preHandle(request, response, null);

    assertEquals("ABC123", MDC.get(FEE_CODE));
    verify(response).setHeader(eq(HEADER_CORRELATION_ID), anyString());
  }

  @Test
  void shouldSetFeeCodeAndCorrelationIdInMdc_whenCorrelationIdIsProvided() {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    Map<String, String> pathVariables = new HashMap<>();
    pathVariables.put("feeCode", "ABC123");
    when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE))
        .thenReturn(pathVariables);

    String correlationId = "a51433f8-a78c-47ef-bd31-837b95467220";
    when(request.getHeader(HEADER_CORRELATION_ID)).thenReturn(correlationId);

    MdcLoggingInterceptor interceptor = new MdcLoggingInterceptor();
    interceptor.preHandle(request, response, null);

    assertEquals("ABC123", MDC.get(FEE_CODE));
    verify(response).setHeader(HEADER_CORRELATION_ID, correlationId);
  }

  @Test
  void shouldNotSetFeeCodeMdc_whenFeeCodeIsNotProvided() {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    MdcLoggingInterceptor interceptor = new MdcLoggingInterceptor();
    interceptor.preHandle(request, response, null);

    assertThat(MDC.get("feeCode")).isNull();
  }

  @Test
  void shouldPopulateMdcWithFeeCalculationRequestDetails() {
    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode("FEE123")
        .startDate(LocalDate.now())
        .policeStationId("PS1")
        .policeStationSchemeId("PSS1")
        .uniqueFileNumber("UFN")
        .caseConcludedDate(LocalDate.now())
        .representationOrderDate(LocalDate.now())
        .build();

    MdcLoggingInterceptor.populateMdc(feeCalculationRequest);

    assertEquals("FEE123", MDC.get(FEE_CODE));
    assertEquals(feeCalculationRequest.getStartDate().toString(), MDC.get("startDate"));
    assertEquals("PS1", MDC.get("policeStationId"));
    assertEquals("PSS1", MDC.get("policeStationSchemeId"));
    assertEquals("UFN", MDC.get("uniqueFileNumber"));
    assertEquals(feeCalculationRequest.getCaseConcludedDate().toString(), MDC.get("caseConcludedDate"));
    assertEquals(feeCalculationRequest.getRepresentationOrderDate().toString(), MDC.get("representationOrderDate"));
  }

  @Test
  void shouldNotPutDatesInMdc_whenDatesAreNull() {
    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode("FEE123")
        .startDate(null)
        .policeStationId("PS1")
        .policeStationSchemeId("PSS1")
        .uniqueFileNumber("UFN")
        .caseConcludedDate(null)
        .representationOrderDate(null)
        .build();

    MdcLoggingInterceptor.populateMdc(feeCalculationRequest);

    assertEquals("FEE123", MDC.get(FEE_CODE));
    assertThat(MDC.get("startDate")).isNull();
    assertEquals("PS1", MDC.get("policeStationId"));
    assertEquals("PSS1", MDC.get("policeStationSchemeId"));
    assertEquals("UFN", MDC.get("uniqueFileNumber"));
    assertThat(MDC.get("caseConcludedDate")).isNull();
    assertThat(MDC.get("representationOrderDate")).isNull();
  }

  @Test
  void shouldClearMdcAfterCompletion() {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    MDC.put("test123", "testValue");

    MdcLoggingInterceptor interceptor = new MdcLoggingInterceptor();
    interceptor.afterCompletion(request, response, null, null);

    assertThat(MDC.get("test123")).isNull();
  }
}