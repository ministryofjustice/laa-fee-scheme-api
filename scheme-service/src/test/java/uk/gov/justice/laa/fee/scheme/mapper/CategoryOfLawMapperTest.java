package uk.gov.justice.laa.fee.scheme.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.fee.scheme.entity.CategoryOfLawLookUpEntity;
import uk.gov.justice.laa.fee.scheme.model.CategoryOfLawResponse;

@ExtendWith(MockitoExtension.class)
class CategoryOfLawMapperTest {

  @InjectMocks
  private CategoryOfLawMapper categoryOfLawMapper = new CategoryOfLawMapperImpl();

  @Test
  void toCategoryOfLawResponse_shouldReturnCategoryOfLawResponse() {
    CategoryOfLawLookUpEntity categoryOfLawLookUpEntity = CategoryOfLawLookUpEntity.builder()
        .categoryOfLawLookUpId(1L)
        .categoryCode("CAT1")
        .fullDescription("Category One Description")
        .areaOfLaw("Area of Law One")
        .feeCode("FEE1")
        .build();

    CategoryOfLawResponse result = categoryOfLawMapper.toCategoryOfLawResponse(categoryOfLawLookUpEntity);

    assertThat(result).isNotNull();
    assertThat(result.getCategoryOfLawCode()).isEqualTo("CAT1");
  }

}