package uk.gov.justice.laa.fee.scheme.logback;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.sentry.Sentry;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerMapping;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;

@ExtendWith(MockitoExtension.class)
class MdcLoggingInterceptorTest {

  private static final String FEE_CODE = "feeCode";
  private static final String HEADER_CORRELATION_ID = "X-Correlation-Id";

  @Mock
  HttpServletRequest request;

  @Mock
  HttpServletResponse response;

  @AfterEach
  void clearMdc() {
    MDC.clear();
  }

  @Test
  void shouldSetFeeCodeAndCorrelationIdInMdc_whenCorrelationIdIsNotProvided() {
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
    MDC.put("test123", "testValue");

    MdcLoggingInterceptor interceptor = new MdcLoggingInterceptor();
    interceptor.afterCompletion(request, response, null, null);

    assertThat(MDC.get("test123")).isNull();
  }

  @Test
  void preHandle_shouldSetSentryTagsForFeeCodeAndCorrelationId() {
    Map<String, String> pathVariables = new HashMap<>();
    pathVariables.put("feeCode", "ABC123");
    when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE))
        .thenReturn(pathVariables);

    String correlationId = "a51433f8-a78c-47ef-bd31-837b95467220";
    when(request.getHeader(HEADER_CORRELATION_ID)).thenReturn(correlationId);

    try (MockedStatic<Sentry> sentryMock = mockStatic(Sentry.class)) {
      MdcLoggingInterceptor interceptor = new MdcLoggingInterceptor();
      interceptor.preHandle(request, response, null);

      sentryMock.verify(() -> Sentry.setTag(FEE_CODE, "ABC123"));
      sentryMock.verify(() -> Sentry.setTag("correlationId", correlationId));
    }
  }

  @Test
  void preHandle_shouldNotSetSentryFeeCodeTag_whenFeeCodeIsNotProvided() {
    try (MockedStatic<Sentry> sentryMock = mockStatic(Sentry.class)) {
      MdcLoggingInterceptor interceptor = new MdcLoggingInterceptor();
      interceptor.preHandle(request, response, null);

      sentryMock.verify(() -> Sentry.setTag(eq(FEE_CODE), anyString()), never());
      sentryMock.verify(() -> Sentry.setTag(eq("correlationId"), anyString()));
    }
  }

  @Test
  void populateMdc_shouldSetSentryTagsForNonNullFields() {
    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode("FEE123")
        .startDate(LocalDate.of(2025, 5, 11))
        .policeStationId("PS1")
        .policeStationSchemeId("PSS1")
        .uniqueFileNumber("UFN")
        .caseConcludedDate(null)
        .representationOrderDate(null)
        .build();

    try (MockedStatic<Sentry> sentryMock = mockStatic(Sentry.class)) {
      MdcLoggingInterceptor.populateMdc(feeCalculationRequest);

      sentryMock.verify(() -> Sentry.setTag(FEE_CODE, "FEE123"));
      sentryMock.verify(() -> Sentry.setTag("startDate", "2025-05-11"));
      sentryMock.verify(() -> Sentry.setTag("policeStationId", "PS1"));
      sentryMock.verify(() -> Sentry.setTag("policeStationSchemeId", "PSS1"));
      sentryMock.verify(() -> Sentry.setTag("uniqueFileNumber", "UFN"));
      sentryMock.verify(() -> Sentry.setTag(eq("caseConcludedDate"), anyString()), never());
      sentryMock.verify(() -> Sentry.setTag(eq("representationOrderDate"), anyString()), never());
    }
  }
}