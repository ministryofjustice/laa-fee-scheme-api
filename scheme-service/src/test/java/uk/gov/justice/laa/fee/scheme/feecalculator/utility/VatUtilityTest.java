package uk.gov.justice.laa.fee.scheme.feecalculator.utility;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class VatUtilityTest {

  @ParameterizedTest
  @CsvSource({
      "2011-01-04, 20",
      "2010-06-01, 17.5",
      "2009-06-01, 15"
  })
  void should_getVatRateForDate(String date, String expectedVatRate) {
    LocalDate localDate = LocalDate.parse(date);
    assertThat(VatUtility.getVatRateForDate(localDate)).isEqualTo(expectedVatRate);
  }

  @Test
  void should_addVatUsingRate() {
    BigDecimal value = BigDecimal.valueOf(170.50);
    BigDecimal vatRate = BigDecimal.valueOf(20);

    BigDecimal result = VatUtility.addVatUsingRate(value, vatRate);

    assertThat(result).isEqualByComparingTo("204.60");
  }

  @Test
  void should_addVat() {
    BigDecimal result = VatUtility.addVat(BigDecimal.valueOf(170.50), LocalDate.of(2011, 2, 1));
    assertThat(result).isEqualByComparingTo("204.60");
  }
}