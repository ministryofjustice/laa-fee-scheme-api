package uk.gov.justice.laa.fee.scheme.entity;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.fee.scheme.enums.FeeType;

class FeeCategoryMappingEntityTest {

  @Test
  void testFeeCategoryEntityGetters() {
    FeeEntity fee = FeeEntity.builder()
        .feeCode("INVA")
        .description("Advice and Assistance (not at the police station)")
        .feeType(FeeType.HOURLY)
        .build();

    FeeCategoryMappingEntity feeCategoryMappingEntity = FeeCategoryMappingEntity.builder()
        .id(1L)
        .fee(fee)
        .build();

    assertThat(feeCategoryMappingEntity.getId()).isEqualTo(1L);
    assertThat(feeCategoryMappingEntity.getFee()).isEqualTo(fee);
    assertThat(feeCategoryMappingEntity.getFeeCode()).isEqualTo("INVA");
    assertThat(feeCategoryMappingEntity.getFeeDescription()).isEqualTo("Advice and Assistance (not at the police station)");
    assertThat(feeCategoryMappingEntity.getFeeType()).isEqualTo(FeeType.HOURLY);
  }

  @Test
  void testFeeCategoryEntityGetters_whenNull() {
    FeeCategoryMappingEntity mapping = FeeCategoryMappingEntity.builder()
        .id(2L)
        .build();

    assertThat(mapping.getFeeCode()).isNull();
    assertThat(mapping.getFeeDescription()).isNull();
    assertThat(mapping.getFeeType()).isNull();
  }
}