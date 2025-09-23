package uk.gov.justice.laa.fee.scheme.logback;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Used to sort and specify the format of the MDC key/value pairs logging output.
 */
public class SortedMdcConverter extends ClassicConverter {

  /**
   * Converts the given ILoggingEvent event to string containing sorted  MDC key/value pairs.
   *
   * @param event the ILoggingEvent
   * @return the conversion string
   */
  @Override
  public String convert(ILoggingEvent event) {
    Map<String, String> mdcMap = event.getMDCPropertyMap();
    if (mdcMap == null || mdcMap.isEmpty()) {
      return "";
    }

    Map<String, String> sorted = new TreeMap<>(mdcMap);
    return ":: {" + sorted.entrySet()
        .stream()
        .map(e -> e.getKey() + "=" + e.getValue())
        .collect(Collectors.joining(", ")) + "}";
  }
}
