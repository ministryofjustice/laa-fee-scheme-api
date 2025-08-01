package uk.gov.justice.laa.fee.scheme.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import uk.gov.justice.laa.fee.scheme.entity.FeeSchemesEntity;

@DataJpaTest
class FeeSchemesRepositoryIntegrationTest {

  private static final String SCHEME_CODE = "SCHEME1";

  @Autowired
  private FeeSchemesRepository repository;

  @Test
  void getFeeSchemesById() {
    Optional<FeeSchemesEntity> result = repository.findById(SCHEME_CODE);

    assertThat(result.isPresent()).isTrue();

    FeeSchemesEntity feeSchemesEntity = result.get();
    assertThat(feeSchemesEntity.getSchemeCode()).isEqualTo(SCHEME_CODE);
    assertThat(feeSchemesEntity.getSchemeName()).isEqualTo("Scheme One");
    assertThat(feeSchemesEntity.getValidFrom()).isEqualTo("2023-12-01");
    assertThat(feeSchemesEntity.getValidTo()).isNull();
  }
}
