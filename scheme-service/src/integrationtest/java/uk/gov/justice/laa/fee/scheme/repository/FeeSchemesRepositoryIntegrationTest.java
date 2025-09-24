package uk.gov.justice.laa.fee.scheme.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import uk.gov.justice.laa.fee.scheme.entity.FeeSchemesEntity;
import uk.gov.justice.laa.fee.scheme.postgrestestcontainer.PostgresContainerTestBase;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class FeeSchemesRepositoryIntegrationTest extends PostgresContainerTestBase {

  @Autowired
  private FeeSchemesRepository repository;

  @Test
  void getFeeSchemesById() {
    Optional<FeeSchemesEntity> result = repository.findById("MED_FS2013");
    assertThat(result).isPresent();

    FeeSchemesEntity fee = result.get();
    assertThat(fee.getSchemeCode()).isEqualTo("MED_FS2013");
    assertThat(fee.getSchemeName()).isEqualTo("Mediation Fee Scheme 2013");
    assertThat(fee.getValidFrom()).isEqualTo(LocalDate.parse("2013-04-01"));
    assertThat(fee.getValidTo()).isNull();
  }
}
