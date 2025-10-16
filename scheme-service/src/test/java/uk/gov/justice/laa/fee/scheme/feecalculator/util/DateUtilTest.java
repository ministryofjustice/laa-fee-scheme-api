package uk.gov.justice.laa.fee.scheme.feecalculator.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.fee.scheme.util.DateUtil;

class DateUtilTest {

  @Test
  void testValidDateHavingValidDayAndMonth() {
    LocalDate result = DateUtil.toLocalDate("12032025/8789");
    assertEquals(LocalDate.of(2025, 3, 12), result);
  }

  @Test
  void testInvalidDateHavingInvalidDayAndInvalidMonth() {
    assertThrows(DateTimeParseException.class, () -> {
      DateUtil.toLocalDate("999999/1234"); // 31st Feb is invalid
    });
  }

  @Test
  void testInvalidDateHavingNonNumericCharacters() {
    assertThrows(DateTimeParseException.class, () -> {
      DateUtil.toLocalDate("WED299/1234"); // 31st Feb is invalid
    });
  }

  @Test
  void testShortInputString() {
    assertThrows(StringIndexOutOfBoundsException.class, () -> {
      DateUtil.toLocalDate("1203"); // less than 6 chars
    });
  }
}