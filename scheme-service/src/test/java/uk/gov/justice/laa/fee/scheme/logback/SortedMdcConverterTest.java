package uk.gov.justice.laa.fee.scheme.logback;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.spi.ILoggingEvent;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SortedMdcConverterTest {

  @Mock
  ILoggingEvent event;

  SortedMdcConverter converter = new SortedMdcConverter();

  @Test
  public void convert_givenNonEmptyMdc_shouldReturnFormattedMdcString() {
    when(event.getMDCPropertyMap()).thenReturn(Map.of("feeCode", "FEE123", "startDate", "15-02-11"));

    String result = converter.convert(event);

    assertThat(result).isEqualTo(":: {feeCode=FEE123, startDate=15-02-11}");
  }

  @Test
  public void convert_givenNullMdc_shouldReturnEmptyString() {
    when(event.getMDCPropertyMap()).thenReturn(null);

    String result = converter.convert(event);

    assertThat(result).isEqualTo("");
  }

  @Test
  public void convert_givenEmptyMdc_shouldReturnEmptyString() {
    when(event.getMDCPropertyMap()).thenReturn(Map.of());

    String result = converter.convert(event);

    assertThat(result).isEqualTo("");
  }

}