package uk.gov.justice.laa.fee.scheme.feecalculator.utility;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.model.BoltOnType;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;

class BoltOnUtilityTest {

  private static Stream<Arguments> boltOnTestData() {
    return Stream.of(
        // All bolt ons requested
        Arguments.of(
            FeeCalculationRequest.builder()
                .boltOns(BoltOnType.builder()
                    .boltOnAdjournedHearing(2)
                    .boltOnCmrhTelephone(2)
                    .boltOnCmrhOral(2)
                    .boltOnHomeOfficeInterview(2)
                    .build())
                .build(),
            FeeEntity.builder()
                .hoInterviewBoltOn(BigDecimal.valueOf(100))
                .adjornHearingBoltOn(BigDecimal.valueOf(100))
                .telephoneCmrhBoltOn(BigDecimal.valueOf(100))
                .oralCmrhBoltOn(BigDecimal.valueOf(100))
                .build(),
            BigDecimal.valueOf(800)
        ),
        // when no bolt ons are requested
        Arguments.of(
            FeeCalculationRequest.builder()
                .build(),
            FeeEntity.builder()
                .hoInterviewBoltOn(BigDecimal.valueOf(100))
                .adjornHearingBoltOn(BigDecimal.valueOf(100))
                .build(),
            BigDecimal.ZERO
        ),
        // Selection of bolt ons requested
        Arguments.of(
            FeeCalculationRequest.builder()
                .boltOns(BoltOnType.builder()
                    .boltOnAdjournedHearing(3)
                    .boltOnCmrhOral(2)
                    .boltOnCmrhTelephone(null)
                    .boltOnHomeOfficeInterview(null)
                    .build())
                .build(),
            FeeEntity.builder()
                .adjornHearingBoltOn(BigDecimal.valueOf(100))
                .telephoneCmrhBoltOn(BigDecimal.valueOf(100))
                .oralCmrhBoltOn(BigDecimal.valueOf(100))
                .hoInterviewBoltOn(BigDecimal.valueOf(100))
                .build(),
            BigDecimal.valueOf(500)
        )
    );
  }

  @ParameterizedTest
  @MethodSource("boltOnTestData")
  void shouldCalculateBoltOnAmount(FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity,
                                   BigDecimal expected) {

    BigDecimal result = BoltOnUtility.calculateBoltOnAmount(feeCalculationRequest, feeEntity);
    assertEquals(expected, result);
  }
}