package uk.gov.justice.laa.fee.scheme.util;

import java.util.HashMap;
import java.util.Map;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;

/**
 * Utility class for logging messages with context information.
 */
public final class LoggingUtil {

  private LoggingUtil() {
  }

  /**
   * Constructs a log message with context information from the FeeCalculationRequest.
   *
   * @param message the base log message
   * @param request the FeeCalculationRequest containing context data
   * @return a formatted log message with context
   */
  public static String getLogMessage(String message, FeeCalculationRequest request) {
    Map<String, Object> context = new HashMap<>();
    context.put("feeCode", request.getFeeCode());
    context.put("startDate", request.getStartDate());
    return String.format("%s - %s", message, context);
  }

  /**
   * Constructs a log message with context information using only the fee code.
   *
   * @param message the base log message
   * @param feeCode the fee code to include in the context
   * @return a formatted log message with context
   */
  public static String getLogMessage(String message, String feeCode) {
    Map<String, Object> context = new HashMap<>();
    context.put("feeCode", feeCode);
    return String.format("%s - %s", message, context);
  }
}
