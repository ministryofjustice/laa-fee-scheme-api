package uk.gov.justice.laa.fee.scheme.feecalculator.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.feecalculator.util.boltons.BoltOnUtil;
import uk.gov.justice.laa.fee.scheme.model.BoltOnFeeDetails;
import uk.gov.justice.laa.fee.scheme.model.BoltOnType;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;

class BoltOnUtilTest {

  private static Stream<Arguments> boltOnTestData() {
    return Stream.of(
        arguments("All Bolt ons requested",
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
            800.00,
            2,
            2,
            2,
            2,
            200.00,
            200.00,
            200.00,
            200.00
        ),
        arguments(
            "No bolts ons requested",
            FeeCalculationRequest.builder()
                .build(),
            FeeEntity.builder()
                .hoInterviewBoltOn(BigDecimal.valueOf(100))
                .adjornHearingBoltOn(BigDecimal.valueOf(100))
                .build(),
            0.0,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        ),
        arguments(
            "Selection of bolt ons requested",
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
            500.00,
            3,
            null,
            2,
            null,
            300.00,
            null,
            200.00,
            null
        )
    );
  }

  private static Arguments arguments(String scenario, FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity,
                                     Double boltOnTotalFeeAmount, Integer boltOnAdjournedHearingCount,
                                     Integer boltOnCmrhTelephoneCount, Integer boltOnCmrhOralCount,
                                     Integer boltOnHomeOfficeInterviewCount, Double boltOnAdjournedHearingFee,
                                     Double boltOnCmrhTelephoneFee, Double boltOnCmrhOralFee, Double boltOnHomeOfficeInterviewFee) {
    return Arguments.of(scenario, feeCalculationRequest, feeEntity, boltOnTotalFeeAmount, boltOnAdjournedHearingCount,
        boltOnCmrhTelephoneCount, boltOnCmrhOralCount, boltOnHomeOfficeInterviewCount, boltOnAdjournedHearingFee,
        boltOnCmrhTelephoneFee, boltOnCmrhOralFee, boltOnHomeOfficeInterviewFee);
  }

  @ParameterizedTest
  @MethodSource("boltOnTestData")
  void shouldCalculateBoltOnAmount(String scenario, FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity,
                                   Double boltOnTotalFeeAmount, Integer boltOnAdjournedHearingCount, Integer boltOnCmrhTelephoneCount,
                                   Integer boltOnCmrhOralCount, Integer boltOnHomeOfficeInterviewCount, Double boltOnAdjournedHearingFee,
                                   Double boltOnCmrhTelephoneFee, Double boltOnCmrhOralFee, Double boltOnHomeOfficeInterviewFee) {

    BoltOnFeeDetails result = BoltOnUtil.calculateBoltOnAmounts(feeCalculationRequest, feeEntity);

    BoltOnFeeDetails expectedBoltOnFeeDetails = BoltOnFeeDetails.builder()
        .boltOnTotalFeeAmount(boltOnTotalFeeAmount)
        .boltOnAdjournedHearingCount(boltOnAdjournedHearingCount)
        .boltOnAdjournedHearingFee(boltOnAdjournedHearingFee)
        .boltOnCmrhTelephoneCount(boltOnCmrhTelephoneCount)
        .boltOnCmrhTelephoneFee(boltOnCmrhTelephoneFee)
        .boltOnCmrhOralCount(boltOnCmrhOralCount)
        .boltOnCmrhOralFee(boltOnCmrhOralFee)
        .boltOnHomeOfficeInterviewCount(boltOnHomeOfficeInterviewCount)
        .boltOnHomeOfficeInterviewFee(boltOnHomeOfficeInterviewFee)
        .build();


    assertThat(result)
        .usingRecursiveComparison()
        .isEqualTo(expectedBoltOnFeeDetails);
  }
}