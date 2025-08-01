package uk.gov.justice.laa.fee.scheme.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import uk.gov.justice.laa.fee.scheme.entity.FeeSchemesEntity;

@DataJpaTest
class FeeSchemesRepositoryIntegrationTest {

  @Autowired
  private FeeSchemesRepository repository;

  @Test
  void getFeeSchemesById() {
    Optional<FeeSchemesEntity> result = repository.findById("SCHEME1");

    assertThat(result.isPresent()).isTrue();

    FeeSchemesEntity feeSchemesEntity = result.get();
    assertThat(feeSchemesEntity.getSchemeCode()).isEqualTo("SCHEME1");
    assertThat(feeSchemesEntity.getSchemeName()).isEqualTo("Scheme One");
    assertThat(feeSchemesEntity.getValidFrom()).isEqualTo("2023-12-01");
    assertThat(feeSchemesEntity.getValidTo()).isNull();
  }
}
