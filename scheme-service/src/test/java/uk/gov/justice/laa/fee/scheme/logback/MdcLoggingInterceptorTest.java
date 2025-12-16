package uk.gov.justice.laa.fee.scheme.logback;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;

class MdcLoggingInterceptorTest {

  private static final String FEE_CODE = "feeCode";
  public static final String HEADER_CORRELATION_ID = "X-Correlation-Id";

  @AfterEach
  void clearMdc() {
    MDC.clear();
  }

  @Test
  void shouldSetFeeCodeAndCorrelationIdInMdc_whenCorrelationIdIsNotProvided() {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getParameter(FEE_CODE)).thenReturn("ABC123");

    MdcLoggingInterceptor interceptor = new MdcLoggingInterceptor();
    interceptor.preHandle(request, response, null);

    assertEquals("ABC123", MDC.get(FEE_CODE));
    verify(response).setHeader(eq(HEADER_CORRELATION_ID), anyString());
  }

  @Test
  void shouldSetFeeCodeAndCorrelationIdInMdc_whenCorrelationIdIsProvided() {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getParameter(FEE_CODE)).thenReturn("ABC123");
    String correlationId = "a51433f8-a78c-47ef-bd31-837b95467220";
    when(request.getHeader(HEADER_CORRELATION_ID)).thenReturn(correlationId);

    MdcLoggingInterceptor interceptor = new MdcLoggingInterceptor();
    interceptor.preHandle(request, response, null);

    assertEquals("ABC123", MDC.get(FEE_CODE));
    verify(response).setHeader(HEADER_CORRELATION_ID, correlationId);
  }

  @Test
  void shouldPopulateMdcWithFeeCalculationRequestDetails() {
    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode("FEE123")
        .startDate(LocalDate.now())
        .policeStationId("PS1")
        .policeStationSchemeId("PSS1")
        .uniqueFileNumber("UFN")
        .build();

    MdcLoggingInterceptor.populateMdc(feeCalculationRequest);

    assertEquals("FEE123", MDC.get(FEE_CODE));
    assertEquals(feeCalculationRequest.getStartDate().toString(), MDC.get("startDate"));
    assertEquals("PS1", MDC.get("policeStationId"));
    assertEquals("PSS1", MDC.get("policeStationSchemeId"));
    assertEquals("UFN", MDC.get("uniqueFileNumber"));
  }

  @Test
  void shouldNotPutStartDateInMdc_whenStartDateIsNull() {
    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode("FEE123")
        .startDate(null)
        .policeStationId("PS1")
        .policeStationSchemeId("PSS1")
        .uniqueFileNumber("UFN")
        .build();

    MdcLoggingInterceptor.populateMdc(feeCalculationRequest);

    assertEquals("FEE123", MDC.get(FEE_CODE));
    assertThat(MDC.get("startDate")).isNull();
    assertEquals("PS1", MDC.get("policeStationId"));
    assertEquals("PSS1", MDC.get("policeStationSchemeId"));
    assertEquals("UFN", MDC.get("uniqueFileNumber"));
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