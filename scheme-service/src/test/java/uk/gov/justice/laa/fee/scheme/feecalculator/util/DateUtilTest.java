package uk.gov.justice.laa.fee.scheme.feecalculator.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.fee.scheme.util.DateUtil;

class DateUtilTest {

  @Test
  void testValidDateHavingValidDayAndMonth() {
    LocalDate result = DateUtil.toLocalDate("120325/8789");
    assertThat(result).isEqualTo(LocalDate.of(2025, 3, 12));
  }

  @Test
  void testInvalidDateHavingInvalidDayAndInvalidMonth() {
    assertThatThrownBy(() -> DateUtil.toLocalDate("999999/1234"))
        .isInstanceOf(DateTimeParseException.class)
        .hasMessageContaining("Text '999999' could not be parsed");
  }

  @Test
  void testInvalidDateHavingNonNumericCharacters() {
    assertThatThrownBy(() -> DateUtil.toLocalDate("WED299/1234"))
        .isInstanceOf(DateTimeParseException.class)
        .hasMessageContaining("Text 'WED299' could not be parsed");
  }

  @Test
  void testShortInputString() {
    assertThatThrownBy(() -> DateUtil.toLocalDate("1203"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Date string length less than 6 characters: 1203");
  }

  @Test
  void testNullInputString() {
    assertThat(DateUtil.toLocalDate(null)).isNull();
  }

  @Test
  void testDateHaving50() {
    LocalDate result = DateUtil.toLocalDate("120350/8789");
    assertThat(result).isEqualTo(LocalDate.of(1950, 3, 12));
  }

  @Test
  void testDateHavingGreaterThan50() {
    LocalDate result = DateUtil.toLocalDate("120351/8789");
    assertThat(result).isEqualTo(LocalDate.of(1951, 3, 12));
  }

  @Test
  void testDateHavingLessThan50() {
    LocalDate result = DateUtil.toLocalDate("120349/8789");
    assertThat(result).isEqualTo(LocalDate.of(2049, 3, 12));
  }
}