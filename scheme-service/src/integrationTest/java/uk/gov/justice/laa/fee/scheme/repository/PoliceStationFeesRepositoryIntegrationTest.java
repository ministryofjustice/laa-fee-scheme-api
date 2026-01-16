package uk.gov.justice.laa.fee.scheme.repository;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import uk.gov.justice.laa.fee.scheme.entity.PoliceStationFeesEntity;
import uk.gov.justice.laa.fee.scheme.postgrestestcontainer.PostgresContainerTestBase;

@DataJpaTest
class PoliceStationFeesRepositoryIntegrationTest extends PostgresContainerTestBase {

  private final PoliceStationFeesRepository repository;

  @Autowired
  public PoliceStationFeesRepositoryIntegrationTest(PoliceStationFeesRepository repository) {
    super();
    this.repository = repository;
  }

  @Test
  void testFindById() {

    Optional<PoliceStationFeesEntity> result = repository.findById(1L);

    assertThat(result).isPresent();
    if (result.isEmpty()) {
      throw new EntityNotFoundException();
    }
    PoliceStationFeesEntity entity = result.get();
    assertThat(entity.getPsSchemeName()).isEqualTo("Hartlepool");
    assertThat(entity.getPsSchemeId()).isEqualTo("1001");
    assertThat(entity.getFixedFee()).isEqualTo(new BigDecimal("131.40"));
    assertThat(entity.getEscapeThreshold()).isEqualTo(new BigDecimal("405.40"));
    assertThat(entity.getFeeSchemeCode()).isEqualTo("POL_FS2016");
  }
}
