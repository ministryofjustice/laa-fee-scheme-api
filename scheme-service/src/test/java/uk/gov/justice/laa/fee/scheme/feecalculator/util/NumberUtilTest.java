package uk.gov.justice.laa.fee.scheme.feecalculator.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.fee.scheme.util.NumberUtil;

class NumberUtilTest {

  @Test
  void toDecimal_givenDouble_returnsBigDecimal() {
    BigDecimal result = NumberUtil.toBigDecimal(99.99);

    assertThat(result).isEqualTo(new BigDecimal("99.99"));
  }

  @Test
  void toDecimal_givenNull_returnsZero() {
    BigDecimal result = NumberUtil.toBigDecimal(null);

    assertThat(result).isEqualTo(BigDecimal.ZERO);
  }

  @Test
  void toDouble_givenBigDecimal_returnsDouble() {
    Double result = NumberUtil.toDouble(new BigDecimal("129.13"));

    assertThat(result).isEqualTo(129.13);
  }

  @Test
  void toDouble_givenNull_returnsZero() {
    Double result = NumberUtil.toDouble(null);

    assertThat(result).isZero();
  }

  @Test
  void toDoubleOrNull_givenZero_returnsNull() {
    Double result = NumberUtil.toDoubleOrNull(BigDecimal.ZERO);

    assertThat(result).isNull();
  }

  @Test
  void defaultToZeroIfNull_givenNull_returnsZero() {
    BigDecimal result = NumberUtil.defaultToZeroIfNull(null);

    assertThat(result).isEqualTo(BigDecimal.ZERO);
  }

  @Test
  void defaultToZeroIfNull_givenValue_returnsValue() {
    BigDecimal value = new BigDecimal("129.13");

    BigDecimal result = NumberUtil.defaultToZeroIfNull(value);

    assertThat(result).isEqualTo(value);
  }
}