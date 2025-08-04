package uk.gov.justice.laa.fee.scheme.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import uk.gov.justice.laa.fee.scheme.entity.CategoryOfLawLookUpEntity;

@DataJpaTest
public class CategoryOfLawLookUpRepositoryIntegrationTest {

  private static final Long CATEGORY_OF_LAW_LOOK_UP_ID = 456L;

  @Autowired
  private CategoryOfLawLookUpRepository repository;

  @Test
  void getCategoryOfLawLookupRepositoryById() {
    Optional<CategoryOfLawLookUpEntity> result = repository.findById(CATEGORY_OF_LAW_LOOK_UP_ID);

    assertThat(result).isPresent();

    CategoryOfLawLookUpEntity categoryOfLawLookUpEntity = result.get();
    assertThat(categoryOfLawLookUpEntity.getCategoryOfLawLookUpId()).isEqualTo(CATEGORY_OF_LAW_LOOK_UP_ID);
    assertThat(categoryOfLawLookUpEntity.getCategoryCode()).isEqualTo("CAT1");
    assertThat(categoryOfLawLookUpEntity.getFullDescription()).isEqualTo("Category One Description");
    assertThat(categoryOfLawLookUpEntity.getAreaOfLaw()).isEqualTo("Area of Law One");
    assertThat(categoryOfLawLookUpEntity.getFeeCode()).isEqualTo("FEE1");
  }
}
