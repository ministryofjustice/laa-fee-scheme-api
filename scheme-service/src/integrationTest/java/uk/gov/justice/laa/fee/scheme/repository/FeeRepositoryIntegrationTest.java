package uk.gov.justice.laa.fee.scheme.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;

@DataJpaTest
class FeeRepositoryIntegrationTest {

  @Autowired
  private FeeRepository repository;


  @Test
  void getFeeById() {
    Optional<FeeEntity> result = repository.findById(1L);

    assertThat(result.isPresent()).isTrue();

    FeeEntity feeEntity = result.get();
    assertThat(feeEntity.getFeeCode()).isEqualTo("FEE1");
    assertThat(feeEntity.getFeeSchemeCode()).isEqualTo("SCHEME1");
    assertThat(feeEntity.getTotalFee()).isEqualTo(new BigDecimal("1000.10"));
    assertThat(feeEntity.getProfitCostLimit()).isEqualTo(new BigDecimal("2000.00"));
    assertThat(feeEntity.getDisbursementLimit()).isEqualTo(new BigDecimal("3000.00"));
    assertThat(feeEntity.getEscapeThresholdLimit()).isEqualTo(new BigDecimal("4000.00"));
    assertThat(feeEntity.getPriorAuthorityApplicable()).isEqualTo(false);
    assertThat(feeEntity.getScheduleReference()).isEqualTo(true);
  }
}