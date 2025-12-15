package uk.gov.justice.laa.fee.scheme.logback;

import static java.util.Objects.nonNull;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;

/**
 * MDC logging interceptor.
 */
@Component
public class MdcLoggingInterceptor implements HandlerInterceptor {

  /**
   * Populates MDC with fee calculation request details.
   */
  public static void populateMdc(FeeCalculationRequest feeCalculationRequest) {
    if (feeCalculationRequest == null) {
      return;
    }

    MDC.put("feeCode", feeCalculationRequest.getFeeCode());

    if (nonNull(feeCalculationRequest.getStartDate())) {
      MDC.put("startDate", feeCalculationRequest.getStartDate().toString());
    }

    if (nonNull(feeCalculationRequest.getPoliceStationId())) {
      MDC.put("policeStationId", feeCalculationRequest.getPoliceStationId());
    }

    if (nonNull(feeCalculationRequest.getPoliceStationSchemeId())) {
      MDC.put("policeStationSchemeId", feeCalculationRequest.getPoliceStationSchemeId());
    }

    if (nonNull(feeCalculationRequest.getUniqueFileNumber())) {
      MDC.put("uniqueFileNumber", feeCalculationRequest.getUniqueFileNumber());
    }
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    MDC.clear();
  }
}
