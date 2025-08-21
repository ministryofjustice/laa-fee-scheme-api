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
import uk.gov.justice.laa.fee.scheme.entity.FeeSchemesEntity;
import uk.gov.justice.laa.fee.scheme.postgresTestContainer.PostgresContainerTestBase;

@DataJpaTest
class FeeRepositoryIntegrationTest extends PostgresContainerTestBase {

  @Autowired
  private FeeRepository repository;

  @ParameterizedTest
  @MethodSource("feeTestDataMediation")
  void testFeeByCodeMediation(String feeCode, String expectedDescription, BigDecimal expectedFixedFee, BigDecimal expectedOneMediation, BigDecimal expectedTwoMediation, String feeSchemeCode) {
    FeeSchemesEntity feeSchemesEntity = new FeeSchemesEntity();
    feeSchemesEntity.setSchemeCode(feeSchemeCode);

    Optional<FeeEntity> result = repository.findByFeeCodeAndFeeSchemeCode(feeCode, feeSchemesEntity);
    assertThat(result).isPresent();

    FeeEntity entity = result.get();

    assertThat(entity.getFeeCode()).isEqualTo(feeCode);
    assertThat(entity.getDescription()).isEqualTo(expectedDescription);
    assertThat(entity.getFixedFee()).isEqualTo(expectedFixedFee);
    assertThat(entity.getMediationSessionOne()).isEqualTo(expectedOneMediation);
    assertThat(entity.getMediationSessionTwo()).isEqualTo(expectedTwoMediation);
    assertThat(entity.getFeeSchemeCode().getSchemeCode()).isEqualTo(feeSchemeCode);
  }

  static Stream<Arguments> feeTestDataMediation() {
    return Stream.of(
        Arguments.of("MAM1", "Mediation Assesment (alone)", new BigDecimal("87.00"), null, null, "MED_FS2013"),
        Arguments.of("MED8", "All issues sole -  1 party eligible, agreement on P&F only", null, new BigDecimal("262.50"), new BigDecimal("556.50"), "MED_FS2013"),
        Arguments.of("MED32", "Child only Co - single session 1 party eligible, with agreed proposal", null, new BigDecimal("293.00"), new BigDecimal("501.50"), "MED_FS2013")
    );
  }

  @ParameterizedTest
  @MethodSource("feeTestDataImmigrationFixedFee")
  void testFeeByCodeImmigrationFixedFee(String feeCode, String expectedDescription, BigDecimal expectedFixedFee,
                                        BigDecimal hoInterviewBoltOn, BigDecimal oralCmrhBoltOn, BigDecimal telephoneCmrhBoltOn,
                                        BigDecimal adjournedHearingBoltOn, BigDecimal disbursementLimit, String feeSchemeCode) {
    FeeSchemesEntity feeSchemesEntity = new FeeSchemesEntity();
    feeSchemesEntity.setSchemeCode(feeSchemeCode);

    Optional<FeeEntity> result = repository.findByFeeCodeAndFeeSchemeCode(feeCode, feeSchemesEntity);
    assertThat(result).isPresent();

    FeeEntity entity = result.get();

    assertThat(entity.getFeeCode()).isEqualTo(feeCode);
    assertThat(entity.getDescription()).isEqualTo(expectedDescription);
    assertThat(entity.getFixedFee()).isEqualTo(expectedFixedFee);
    assertThat(entity.getHoInterviewBoltOn()).isEqualTo(hoInterviewBoltOn);
    assertThat(entity.getOralCmrhBoltOn()).isEqualTo(oralCmrhBoltOn);
    assertThat(entity.getTelephoneCmrhBoltOn()).isEqualTo(telephoneCmrhBoltOn);
    assertThat(entity.getAdjornHearingBoltOn()).isEqualTo(adjournedHearingBoltOn);
    assertThat(entity.getDisbursementLimit()).isEqualTo(disbursementLimit);
    assertThat(entity.getFeeSchemeCode().getSchemeCode()).isEqualTo(feeSchemeCode);
  }

  static Stream<Arguments> feeTestDataImmigrationFixedFee() {
    return Stream.of(
        Arguments.of("IACA", "Asylum CLR Fixed Fee 2a",
            new BigDecimal("227.00"), null, new BigDecimal("166.00"), new BigDecimal("90.00"), null,
            new BigDecimal("600.00"), "I&A_FS2013"),
        Arguments.of("IMCC", "Standard Fee - Immigration CLR (2c + advocacy substantive hearing fee)",
            new BigDecimal("764.00"), null, new BigDecimal("166.00"), new BigDecimal("90.00"),
            new BigDecimal("161.00"), new BigDecimal("600.00"), "I&A_FS2020"),
        Arguments.of("IDAS2", "Detained Duty Advice Scheme (5+ clients seen)",
            new BigDecimal("360.00"), null, null, null, null, null, "I&A_FS2023")
    );
  }
}