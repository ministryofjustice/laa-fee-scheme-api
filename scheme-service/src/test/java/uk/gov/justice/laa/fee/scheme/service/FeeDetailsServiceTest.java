package uk.gov.justice.laa.fee.scheme.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.fee.scheme.entity.CategoryOfLawTypeEntity;
import uk.gov.justice.laa.fee.scheme.entity.FeeCategoryMappingEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryOfLawType;
import uk.gov.justice.laa.fee.scheme.enums.FeeType;
import uk.gov.justice.laa.fee.scheme.model.FeeDetailsResponse;
import uk.gov.justice.laa.fee.scheme.repository.FeeCategoryMappingRepository;
import uk.gov.justice.laa.fee.scheme.repository.projection.FeeDetailsProjection;

@ExtendWith(MockitoExtension.class)
class FeeDetailsServiceTest {

  @Mock
  FeeCategoryMappingRepository feeCategoryMappingRepository;
  @InjectMocks
  private FeeDetailsService feeDetailsService;

  @Test
  void getFeeDetails_shouldReturnExpectedFeeDetails() {
    String feeCode = "FEE123";

    CategoryOfLawTypeEntity categoryOfLawType  = CategoryOfLawTypeEntity.builder().code("AAP").build();
    FeeType feeType = FeeType.FIXED;

    FeeCategoryMappingEntity feeCategoryMappingEntity = mock(FeeCategoryMappingEntity.class);
    when(feeCategoryMappingEntity.getCategoryOfLawType()).thenReturn(categoryOfLawType);
    when(feeCategoryMappingEntity.getFeeDescription()).thenReturn("Claims Against Public Authorities Legal Help Fixed Fee");
    when(feeCategoryMappingEntity.getFeeType()).thenReturn(feeType);

    when(feeCategoryMappingRepository.findFeeCategoryMappingByFeeCode(any())).thenReturn(Optional.of(feeCategoryMappingEntity));

    FeeDetailsResponse response = feeDetailsService.getFeeDetails(feeCode);

    assertThat(response.getCategoryOfLawCode()).isEqualTo("AAP");
    assertThat(response.getFeeCodeDescription()).isEqualTo("Claims Against Public Authorities Legal Help Fixed Fee");
    assertThat(response.getFeeType()).isEqualTo("FIXED");
  }

  @Test
  void getFeeDetails_shouldReturnExceptionCategoryOfLawNotFound() {
    String feeCode = "FEE123";

    when(feeCategoryMappingRepository.findFeeCategoryMappingByFeeCode(any())).thenReturn(Optional.empty());

    assertThatThrownBy(() -> feeDetailsService.getFeeDetails(feeCode))
        .hasMessageContaining(String.format("Category of law code not found for feeCode: %s", feeCode));

  }
}