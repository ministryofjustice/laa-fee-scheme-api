package uk.gov.justice.laa.fee.scheme.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.fee.scheme.model.CategoryOfLawResponse;

@ExtendWith(MockitoExtension.class)
class CategoryOfLawServiceTest {

  @InjectMocks
  private CategoryOfLawService categoryOfLawService;

  @Test
  void getCategoryCode_shouldReturnExpectedCategoryOfLaw() {
    String feeCode = "FEE1";

    CategoryOfLawResponse response = categoryOfLawService.getCategoryCode(feeCode);

    assertThat(response).isNotNull();
    assertThat(response.getCategoryOfLawCode()).isEqualTo("ASY");
  }
}