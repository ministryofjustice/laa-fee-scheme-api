package uk.gov.justice.laa.fee.scheme.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import uk.gov.justice.laa.fee.scheme.entity.PoliceStationFeesEntity;
import uk.gov.justice.laa.fee.scheme.postgresTestContainer.PostgresContainerTestBase;

@DataJpaTest
class PoliceStationFeesRepositoryIntegrationTest extends PostgresContainerTestBase {

  @Autowired
  private PoliceStationFeesRepository repository;

  @ParameterizedTest
  @MethodSource("feeTestPoliceStation")
  void testFeeByCodeMediation(Long policeStationFeesId,
                              String criminalJusticeArea,
                              String policeStationName,
                              String policeStationCode,
                              BigDecimal fixedFee,
                              BigDecimal escapeThreshold,
                              String feeSchemeCode) {

    Optional<PoliceStationFeesEntity> result = repository.findById(policeStationFeesId);
    assertThat(result).isPresent();
    PoliceStationFeesEntity entity = result.get();
    assertThat(entity.getCriminalJusticeArea()).isEqualTo(criminalJusticeArea);
    assertThat(entity.getPsSchemeName()).isEqualTo(policeStationName);
    assertThat(entity.getPsSchemeId()).isEqualTo(policeStationCode);
    assertThat(entity.getFixedFee()).isEqualTo(fixedFee);
    assertThat(entity.getEscapeThreshold()).isEqualTo(escapeThreshold);
    assertThat(entity.getFeeSchemeCode()).isEqualTo(feeSchemeCode);
  }

  static Stream<Arguments> feeTestPoliceStation() {
    return Stream.of(
        Arguments.of(
            1L,
            "Hartlepool",
            "Hartlepool",
            "1001",
            new BigDecimal("131.40"),
            new BigDecimal("405.40"),
            "POL_FS2016"
        )
    );
  }
}
