package uk.gov.justice.laa.fee.scheme.feecalculator.util.limit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner;

class LimitUtilTest {

  @ParameterizedTest
  @CsvSource(value = {
      "99, null, false",
      "99, 100, false",
      "100, 100, false",
      "101, null, false",
      "101, 100, true",
  }, nullValues = {"null"})
  void isEscapedCase_returnsResult(BigDecimal amount, BigDecimal limit, boolean expected) {
    FeeEntity feeEntity = FeeEntity.builder().escapeThresholdLimit(limit).build();
    boolean result = LimitUtil.isEscapedCase(amount, feeEntity);

    assertThat(result).isEqualTo(expected);
  }

  @ParameterizedTest
  @CsvSource(value = {
      "149, null, false",
      "149, 150, false",
      "150, 150, false",
      "151, null, false",
      "151, 150, true",
  }, nullValues = {"null"})
  void isEscapedCase_whenGivenLimit_returnsResult(BigDecimal amount, BigDecimal limit, boolean expected) {
    boolean result = LimitUtil.isEscapedCase(amount, limit);

    assertThat(result).isEqualTo(expected);
  }

  @ParameterizedTest
  @CsvSource(value = {
      "199, null, false",
      "199, 200, false",
      "200, 200, false",
      "201, null, false",
      "201, 200, true",
  }, nullValues = {"null"})
  void isOverUpperCostLimit_returnsResult(BigDecimal amount, BigDecimal limit, boolean expected) {
    FeeEntity feeEntity = FeeEntity.builder().upperCostLimit(limit).build();
    boolean result = LimitUtil.isOverUpperCostLimit(amount, feeEntity);

    assertThat(result).isEqualTo(expected);
  }

  @ParameterizedTest
  @MethodSource("limitTestData")
  void checkLimitAndCapIfExceeded_returnsResult(BigDecimal amount, BigDecimal limitAmount, String authority, BigDecimal expectedAmount,
                                                List<ValidationMessagesInner> expectedMessages) {
    LimitContext limitContext = new LimitContext(LimitType.TOTAL, limitAmount, authority, null);
    List<ValidationMessagesInner> validationMessages = new ArrayList<>();

    BigDecimal result = LimitUtil.checkLimitAndCapIfExceeded(amount, limitContext, validationMessages);

    assertThat(result).isEqualTo(expectedAmount);

    assertThat(validationMessages).isEqualTo(expectedMessages);
  }

  public static Stream<Arguments> limitTestData() {
    return Stream.of(
        arguments(new BigDecimal("90"), new BigDecimal("100"), null, new BigDecimal("90"), List.of()),
        arguments(new BigDecimal("200"), null, "priorAuth", new BigDecimal("200"), List.of()),
        arguments(new BigDecimal("200"), new BigDecimal("90"), null, new BigDecimal("90"), List.of())
    );
  }

}