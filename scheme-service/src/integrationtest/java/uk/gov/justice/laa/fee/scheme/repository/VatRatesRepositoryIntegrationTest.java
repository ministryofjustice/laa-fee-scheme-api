package uk.gov.justice.laa.fee.scheme.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import uk.gov.justice.laa.fee.scheme.entity.VatRatesEntity;
import uk.gov.justice.laa.fee.scheme.postgrestestcontainer.PostgresContainerTestBase;

@DataJpaTest
class VatRatesRepositoryIntegrationTest extends PostgresContainerTestBase {

  @Autowired
  private VatRatesRepository repository;

  @ParameterizedTest
  @CsvSource({
      "2025-01-01, 2011-01-04, 20.00",
      "2011-01-04, 2011-01-04, 20.00",
      "2011-01-03, 2010-01-01, 17.50",
      "2010-01-01, 2010-01-01, 17.50",
      "2009-12-31, 2008-12-01, 15.00",
      "2008-12-01, 2008-12-01, 15.00",
      "2008-11-30, 1991-03-19, 17.50",
      "1991-03-19, 1991-03-19, 17.50",
  })
  void shouldReturnVatRateForGivenDate(LocalDate date, LocalDate expectedStartDate, BigDecimal expectedVatRate) {

    VatRatesEntity result = repository.findTopByStartDateLessThanEqualOrderByStartDateDesc(date);

    assertThat(result).isNotNull();
    assertThat(result.getStartDate()).isEqualTo(expectedStartDate);
    assertThat(result.getVatRate()).isEqualTo(expectedVatRate);
  }
}
