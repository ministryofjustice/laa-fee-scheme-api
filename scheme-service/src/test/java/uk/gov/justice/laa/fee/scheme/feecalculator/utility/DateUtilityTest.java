package uk.gov.justice.laa.fee.scheme.feecalculator.utility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import org.junit.jupiter.api.Test;

class DateUtilityTest {

  @Test
  void testValidDateHavingValidDayAndMonth() {
    LocalDate result = DateUtility.toLocalDate("120325/8789");
    assertEquals(LocalDate.of(2025, 3, 12), result);
  }

  @Test
  void testInvalidDateHavingInvalidDayAndInvalidMonth() {
    assertThrows(DateTimeParseException.class, () -> {
      DateUtility.toLocalDate("999999/1234"); // 31st Feb is invalid
    });
  }

  @Test
  void testInvalidDateHavingNonNumericCharacters() {
    assertThrows(DateTimeParseException.class, () -> {
      DateUtility.toLocalDate("WED299/1234"); // 31st Feb is invalid
    });
  }

  @Test
  void testShortInputString() {
    assertThrows(StringIndexOutOfBoundsException.class, () -> {
      DateUtility.toLocalDate("1203"); // less than 6 chars
    });
  }
}