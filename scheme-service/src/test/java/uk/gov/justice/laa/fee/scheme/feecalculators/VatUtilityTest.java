package uk.gov.justice.laa.fee.scheme.feecalculators;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class VatUtilityTest {

  @Test
  void should_getVatRateForDate() {
    assertThat(VatUtility.getVatRateForDate(LocalDate.of(2011, 1, 4)))
        .isEqualByComparingTo("20.00");
    assertThat(VatUtility.getVatRateForDate(LocalDate.of(2010, 6, 1)))
        .isEqualByComparingTo("17.50");
    assertThat(VatUtility.getVatRateForDate(LocalDate.of(2009, 6, 1)))
        .isEqualByComparingTo("15.00");
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