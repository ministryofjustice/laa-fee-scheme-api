package uk.gov.justice.laa.fee.scheme.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.fee.scheme.entity.VatRatesEntity;
import uk.gov.justice.laa.fee.scheme.repository.VatRatesRepository;
import uk.gov.justice.laa.fee.scheme.service.model.VatResult;

@ExtendWith(MockitoExtension.class)
class VatServiceTest {

  @Mock
  VatRatesRepository vatRatesRepository;

  @InjectMocks
  private VatService vatService;

  @Test
  void calculateVat_whenVatIndicatorIsTrue_shouldReturnVatResult() {
    LocalDate date = LocalDate.of(2025, 1, 1);

    VatRatesEntity vatRatesEntity = VatRatesEntity.builder()
        .startDate(date)
        .vatRate(new BigDecimal("20.00"))
        .build();
    when(vatRatesRepository.findTopByStartDateLessThanEqualOrderByStartDateDesc(date)).thenReturn(vatRatesEntity);


    VatResult result = vatService.calculateVat(new BigDecimal("50.00"), date, true);

    assertThat(result).isNotNull();

    assertThat(result.vatAmount()).isEqualTo(new BigDecimal("10.00"));
    assertThat(result.vatRateApplied()).isEqualTo(new BigDecimal("20.00"));
  }

  @Test
  @CsvSource(value = {"null", "false"},  nullValues = {"null" })
  void calculateVat_whenVatIndicatorIsNotTrue_shouldReturnZeroVatResult() {
    LocalDate date = LocalDate.of(2025, 1, 1);

    VatResult result = vatService.calculateVat(new BigDecimal("50.00"), date, false);

    assertThat(result).isNotNull();

    assertThat(result.vatAmount()).isEqualTo(BigDecimal.ZERO);
    assertThat(result.vatRateApplied()).isEqualTo(BigDecimal.ZERO);
  }
}