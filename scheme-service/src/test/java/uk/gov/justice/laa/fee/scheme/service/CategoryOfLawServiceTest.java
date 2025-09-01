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
import uk.gov.justice.laa.fee.scheme.model.CategoryOfLawResponse;
import uk.gov.justice.laa.fee.scheme.repository.CategoryOfLawLookUpRepository;
import uk.gov.justice.laa.fee.scheme.repository.projection.FeeCategoryProjection;

@ExtendWith(MockitoExtension.class)
class CategoryOfLawServiceTest {

  @Mock
  CategoryOfLawLookUpRepository categoryOfLawLookUpRepository;
  @InjectMocks
  private CategoryOfLawService categoryOfLawService;

  @Test
  void getCategoryCode_shouldReturnExpectedCategoryOfLaw() {
    String feeCode = "FEE123";

    FeeCategoryProjection feeCategoryProjection = mock(FeeCategoryProjection.class);
    when(feeCategoryProjection.getCategoryCode()).thenReturn("AAP");
    when(feeCategoryProjection.getDescription()).thenReturn("Claims Against Public Authorities Legal Help Fixed Fee");
    when(feeCategoryProjection.getFeeType()).thenReturn("FIXED");

    when(categoryOfLawLookUpRepository.findFeeCategoryInfoByFeeCode(any())).thenReturn(Optional.of(feeCategoryProjection));

    CategoryOfLawResponse response = categoryOfLawService.getCategoryCode(feeCode);

    assertThat(response.getCategoryOfLawCode()).isEqualTo("AAP");
    assertThat(response.getFeeCodeDescription()).isEqualTo("Claims Against Public Authorities Legal Help Fixed Fee");
    assertThat(response.getFeeType()).isEqualTo("FIXED");
  }

  @Test
  void getCategoryCode_shouldReturnExceptionCategoryOfLawNotFound() {
    String feeCode = "FEE123";

    when(categoryOfLawLookUpRepository.findFeeCategoryInfoByFeeCode(any())).thenReturn(Optional.empty());

    assertThatThrownBy(() -> categoryOfLawService.getCategoryCode(feeCode))
        .hasMessageContaining(String.format("Category of law code not found for fee: %s", feeCode));

  }
}