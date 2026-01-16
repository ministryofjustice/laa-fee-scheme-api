package uk.gov.justice.laa.fee.scheme.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.entity.FeeSchemesEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.enums.FeeType;
import uk.gov.justice.laa.fee.scheme.postgrestestcontainer.PostgresContainerTestBase;

@DataJpaTest
class FeeRepositoryIntegrationTest extends PostgresContainerTestBase {

  private final FeeRepository repository;

  @Autowired
  public FeeRepositoryIntegrationTest(FeeRepository repository) {
    this.repository = repository;
  }

  @Test
  void testFeeByCode() {
    FeeSchemesEntity feeSchemesEntity = new FeeSchemesEntity();
    feeSchemesEntity.setSchemeCode("PUB_FS2013");

    List<FeeEntity> result = repository.findByFeeCode("PUB");
    assertThat(result).hasSize(1);

    FeeEntity entity = result.getFirst();

    assertThat(entity.getFeeCode()).isEqualTo("PUB");
    assertThat(entity.getDescription()).isEqualTo("Public Law Legal Help Fixed Fee");
    assertThat(entity.getFixedFee()).isEqualTo(new BigDecimal("259.00"));
    assertThat(entity.getEscapeThresholdLimit()).isEqualTo(new BigDecimal("777.00"));
    assertThat(entity.getFeeScheme().getSchemeCode()).isEqualTo("PUB_FS2013");
    assertThat(entity.getCategoryType()).isEqualTo(CategoryType.PUBLIC_LAW);
    assertThat(entity.getFeeType()).isEqualTo(FeeType.FIXED);
  }

}