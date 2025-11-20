package uk.gov.justice.laa.fee.scheme.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.fee.scheme.entity.VatRatesEntity;
import uk.gov.justice.laa.fee.scheme.repository.VatRatesRepository;

@ExtendWith(MockitoExtension.class)
class VatRatesServiceTest {

  @Mock
  VatRatesRepository vatRatesRepository;

  @InjectMocks
  private VatRatesService vatRatesService;

  @Test
  void getVatRateForDate_whenVatIndicatorIsTrue_shouldReturnVatResult() {
    LocalDate date = LocalDate.of(2025, 1, 1);

    VatRatesEntity vatRatesEntity = VatRatesEntity.builder()
        .startDate(date)
        .vatRate(new BigDecimal("20.00"))
        .build();
    when(vatRatesRepository.findTopByStartDateLessThanEqualOrderByStartDateDesc(date)).thenReturn(vatRatesEntity);


    BigDecimal result = vatRatesService.getVatRateForDate(date, true);

    assertThat(result).isNotNull();

    assertThat(result).isEqualTo(new BigDecimal("20.00"));
  }

  @Test
  void calculateVat_whenVatIndicatorIsNull_shouldReturnZeroVatResult() {
    LocalDate date = LocalDate.of(2025, 1, 1);

    BigDecimal result = vatRatesService.getVatRateForDate(date, null);

    assertThat(result).isNotNull();

    assertThat(result).isEqualTo(BigDecimal.ZERO);
  }

  @Test
  void calculateVat_whenVatIndicatorIsFalse_shouldReturnZeroVatResult() {
    LocalDate date = LocalDate.of(2025, 1, 1);

    BigDecimal result = vatRatesService.getVatRateForDate(date, false);

    assertThat(result).isNotNull();

    assertThat(result).isEqualTo(BigDecimal.ZERO);
  }

}