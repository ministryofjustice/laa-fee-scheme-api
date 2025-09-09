package uk.gov.justice.laa.fee.scheme.feecalculator.utility;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.fee.scheme.util.NumberUtility;

class NumberUtilityTest {

  @Test
  void toDecimal_givenDouble_returnsBigDecimal() {
    BigDecimal result = NumberUtility.toBigDecimal(99.99);

    assertThat(result).isEqualTo(new BigDecimal("99.99"));
  }

  @Test
  void toDecimal_givenNull_returnsZero() {
    BigDecimal result = NumberUtility.toBigDecimal(null);

    assertThat(result).isEqualTo(BigDecimal.ZERO);
  }

  @Test
  void toDouble_givenBigDecimal_returnsDouble() {
    Double result = NumberUtility.toDouble(new BigDecimal("129.13"));

    assertThat(result).isEqualTo(129.13);
  }

  @Test
  void toDouble_givenNull_returnsZero() {
    Double result = NumberUtility.toDouble(null);

    assertThat(result).isEqualTo(0);
  }

  @Test
  void defaultToZeroIfNull_givenNull_returnsZero() {
    BigDecimal result = NumberUtility.defaultToZeroIfNull(null);

    assertThat(result).isEqualTo(BigDecimal.ZERO);
  }

  @Test
  void defaultToZeroIfNull_givenValue_returnsValue() {
    BigDecimal value = new BigDecimal("129.13");

    BigDecimal result = NumberUtility.defaultToZeroIfNull(value);

    assertThat(result).isEqualTo(value);
  }
}