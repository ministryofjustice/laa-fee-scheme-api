package uk.gov.justice.laa.fee.scheme.feecalculator.util;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class VatUtilTest {

  @ParameterizedTest
  @CsvSource({
      "2011-01-04, 20",
      "2010-06-01, 17.5",
      "2009-06-01, 15"
  })
  void should_getVatRateForDate(String date, String expectedVatRate) {
    LocalDate localDate = LocalDate.parse(date);
    assertThat(VatUtil.getVatRateForDate(localDate)).isEqualTo(expectedVatRate);
  }

  @Test
  void should_getVatAmountUsingRate() {
    BigDecimal value = BigDecimal.valueOf(170.50);
    BigDecimal vatRate = BigDecimal.valueOf(20);

    BigDecimal result = VatUtil.calculateVatAmount(value, vatRate);

    assertThat(result).isEqualByComparingTo("34.10");
  }

  @Test
  void should_getVatAmount() {
    BigDecimal result = VatUtil.getVatAmount(BigDecimal.valueOf(170.50), LocalDate.of(2011, 2, 1), true);
    assertThat(result).isEqualByComparingTo("34.10");
  }
}