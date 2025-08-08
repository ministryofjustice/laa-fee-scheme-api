package uk.gov.justice.laa.fee.scheme.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.fee.scheme.entity.CategoryOfLawLookUpEntity;
import uk.gov.justice.laa.fee.scheme.model.CategoryOfLawResponse;
import uk.gov.justice.laa.fee.scheme.repository.CategoryOfLawLookUpRepository;

@ExtendWith(MockitoExtension.class)
class CategoryOfLawServiceTest {

  @InjectMocks
  private CategoryOfLawService categoryOfLawService;

  @Mock
  CategoryOfLawLookUpRepository categoryOfLawLookUpRepository;

  @Test
  void getCategoryCode_shouldReturnExpectedCategoryOfLaw() {
    String feeCode = "FEE123";
    CategoryOfLawLookUpEntity categoryOfLawLookupEntity = CategoryOfLawLookUpEntity.builder()
        .feeCode("FEE123")
        .categoryCode("CAT_123")
        .areaOfLaw("Immigration")
        .fullDescription("Immigration test")
        .build();

    when(categoryOfLawLookUpRepository.findByFeeCode(any())).thenReturn(Optional.of(categoryOfLawLookupEntity));
    CategoryOfLawResponse response = categoryOfLawService.getCategoryCode(feeCode);

    assertThat(response).isNotNull();
    assertThat(response.getCategoryOfLawCode()).isEqualTo("CAT_123");
  }

  @Test
  void getCategoryCode_shouldReturnExceptionCategoryOfLawNotFound() {
    String feeCode = "FEE123";

    when(categoryOfLawLookUpRepository.findByFeeCode(any())).thenReturn(Optional.empty());

    assertThatThrownBy(() -> categoryOfLawService.getCategoryCode(feeCode))
        .hasMessageContaining(String.format("Category of code not found for fee: %s", feeCode));

  }
}