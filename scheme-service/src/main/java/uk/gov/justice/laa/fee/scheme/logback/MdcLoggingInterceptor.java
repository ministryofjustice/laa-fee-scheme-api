package uk.gov.justice.laa.fee.scheme.logback;

import io.sentry.Sentry;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;

/**
 * MDC logging interceptor.
 */
@Component
public class MdcLoggingInterceptor implements HandlerInterceptor {

  private static final String FEE_CODE = "feeCode";
  private static final String CORRELATION_ID = "correlationId";
  private static final String HEADER_CORRELATION_ID = "X-Correlation-Id";

  /**
   * Populates MDC with fee code and correlation id.
   */
  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    Object pathVariablesObject = request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
    if (pathVariablesObject instanceof Map<?, ?> pathVariables) {
      Optional.ofNullable(pathVariables.get(FEE_CODE))
          .map(Object::toString)
          .ifPresent(feeCode -> {
            MDC.put(FEE_CODE, feeCode);
            Sentry.setTag(FEE_CODE, feeCode);
          });
    }

    String correlationId = Optional.ofNullable(request.getHeader(HEADER_CORRELATION_ID))
        .orElse(UUID.randomUUID().toString());
    MDC.put(CORRELATION_ID, correlationId);
    Sentry.setTag(CORRELATION_ID, correlationId);
    response.setHeader(HEADER_CORRELATION_ID, correlationId);

    return true;
  }

  /**
   * Populates MDC with fee calculation request details.
   */
  public static void populateMdc(FeeCalculationRequest feeCalculationRequest) {
    if (feeCalculationRequest == null) {
      return;
    }

    Map<String, Object> mdcFields = new HashMap<>();
    mdcFields.put("feeCode", feeCalculationRequest.getFeeCode());
    mdcFields.put("startDate", feeCalculationRequest.getStartDate());
    mdcFields.put("policeStationId", feeCalculationRequest.getPoliceStationId());
    mdcFields.put("policeStationSchemeId", feeCalculationRequest.getPoliceStationSchemeId());
    mdcFields.put("uniqueFileNumber", feeCalculationRequest.getUniqueFileNumber());
    mdcFields.put("representationOrderDate", feeCalculationRequest.getRepresentationOrderDate());
    mdcFields.put("caseConcludedDate", feeCalculationRequest.getCaseConcludedDate());

    mdcFields.entrySet().stream()
        .filter(e -> e.getValue() != null)
        .forEach(e -> {
          Object value = e.getValue();
          MDC.put(e.getKey(), value.toString());
          Sentry.setTag(e.getKey(), value.toString());
        });
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    MDC.clear();
  }
}
