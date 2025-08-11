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
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.postgresTestContainer.PostgresContainerTestBase;

@DataJpaTest
class FeeRepositoryIntegrationTest extends PostgresContainerTestBase {

  @Autowired
  private FeeRepository repository;

  @ParameterizedTest
  @MethodSource("feeTestDataMediation")
  void testFeeByCodeMediation(String feeCode, String expectedDescription, BigDecimal expectedTotalFee, BigDecimal expectedOneMediation, BigDecimal expectedTwoMediation, String feeSchemeCode) {
    Optional<FeeEntity> result = repository.findByFeeCode(feeCode);
    assertThat(result).isPresent();

    FeeEntity entity = result.get();

    assertThat(entity.getFeeCode()).isEqualTo(feeCode);
    assertThat(entity.getDescription()).isEqualTo(expectedDescription);
    assertThat(entity.getTotalFee()).isEqualTo(expectedTotalFee);
    assertThat(entity.getMediationSessionOne()).isEqualTo(expectedOneMediation);
    assertThat(entity.getMediationSessionTwo()).isEqualTo(expectedTwoMediation);
    assertThat(entity.getFeeSchemeCode()).isEqualTo(feeSchemeCode);
  }

  static Stream<Arguments> feeTestDataMediation() {
    return Stream.of(
        Arguments.of("MAM1", "Mediation Assesment (alone)", new BigDecimal("87.00"), null, null, "MED_FS2013"),
        Arguments.of("MED8", "All issues sole -  1 party eligible, agreement on P&F only", null, new BigDecimal("262.50"), new BigDecimal("556.50"), "MED_FS2013"),
        Arguments.of("MED32", "Child only Co - single session 1 party eligible, with agreed proposal", null, new BigDecimal("293.00"), new BigDecimal("501.50"), "MED_FS2013")
    );
  }
}