package uk.gov.justice.laa.fee.scheme.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import uk.gov.justice.laa.fee.scheme.entity.PoliceStationFeesEntity;

@DataJpaTest
class PoliceStationFeesRepositoryIntegrationTest {

  private static final Long POLICE_STATION_FEES_ID = 123L;

  @Autowired
  private PoliceStationFeesRepository repository;

  @Test
  void getPoliceStationFeesById() {
    Optional<PoliceStationFeesEntity> result = repository.findById(POLICE_STATION_FEES_ID);

    assertThat(result.isPresent()).isTrue();

    PoliceStationFeesEntity entity = result.get();
    assertThat(entity.getPoliceStationFeesId()).isEqualTo(POLICE_STATION_FEES_ID);
    assertThat(entity.getCriminalJusticeArea()).isEqualTo("Crime Area One");
    assertThat(entity.getPoliceStationName()).isEqualTo("Police Station One");
    assertThat(entity.getPoliceStationCode()).isEqualTo("PS1");
    assertThat(entity.getFixedFee()).isEqualTo(new BigDecimal("500.00"));
    assertThat(entity.getEscapeThreshold()).isEqualTo(new BigDecimal("600.00"));
  }
}
