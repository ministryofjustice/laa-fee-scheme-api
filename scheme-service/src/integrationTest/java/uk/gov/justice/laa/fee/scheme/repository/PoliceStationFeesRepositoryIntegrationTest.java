package uk.gov.justice.laa.fee.scheme.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import uk.gov.justice.laa.fee.scheme.entity.PoliceStationFeesEntity;

@DataJpaTest
class PoliceStationFeesRepositoryIntegrationTest {

  @Autowired
  private PoliceStationFeesRepository repository;


  @Test
  void getPoliceStationFeesById() {
    Optional<PoliceStationFeesEntity> result = repository.findById(1L);

    assertThat(result.isPresent()).isTrue();
    PoliceStationFeesEntity entity = result.get();
    assertThat(entity.getFeeCode()).isEqualTo("FEE1");
    Add more assertions as needed.
  }
}
