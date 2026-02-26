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
    LocalDate result = DateUtil.toLocalDate("120325/8789");
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

  @Test
  void testDateHaving50() {
    LocalDate result = DateUtil.toLocalDate("120350/8789");
    assertEquals(LocalDate.of(1950, 3, 12), result);
  }

  @Test
  void testDateHavingGreaterThan50() {
    LocalDate result = DateUtil.toLocalDate("120351/8789");
    assertEquals(LocalDate.of(1951, 3, 12), result);
  }

  @Test
  void testDateHavingLessThan50() {
    LocalDate result = DateUtil.toLocalDate("120349/8789");
    assertEquals(LocalDate.of(2049, 3, 12), result);
  }
}