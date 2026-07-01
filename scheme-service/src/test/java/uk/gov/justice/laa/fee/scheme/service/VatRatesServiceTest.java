package uk.gov.justice.laa.fee.scheme.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.fee.scheme.entity.VatRatesEntity;
import uk.gov.justice.laa.fee.scheme.exception.CaseConcludedDateRequiredException;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
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

    assertThat(result).isEqualTo(new BigDecimal("20.00"));
  }

  @NullSource
  @ValueSource(booleans = {false})
  @ParameterizedTest
  void calculateVat_whenVatIndicatorIsNullOrFalse_shouldReturnZeroVatResult(Boolean vatIndicator) {
    LocalDate date = LocalDate.of(2025, 1, 1);

    BigDecimal result = vatRatesService.getVatRateForDate(date, vatIndicator);

    assertThat(result).isEqualTo(BigDecimal.ZERO);
  }

  @Test
  void getVatRateForRequest_whenVatIndicatorIsTrueAndCaseConcludedDateIsSet_shouldReturnVatRate() {
    LocalDate caseConcludedDate = LocalDate.of(2025, 6, 1);
    FeeCalculationRequest request = FeeCalculationRequest.builder()
        .feeCode("ABC")
        .vatIndicator(true)
        .caseConcludedDate(caseConcludedDate)
        .build();

    VatRatesEntity vatRatesEntity = VatRatesEntity.builder()
        .startDate(caseConcludedDate)
        .vatRate(new BigDecimal("20.00"))
        .build();
    when(vatRatesRepository.findTopByStartDateLessThanEqualOrderByStartDateDesc(caseConcludedDate))
        .thenReturn(vatRatesEntity);

    BigDecimal result = vatRatesService.getVatRateForRequest(request);

    assertThat(result).isEqualTo(new BigDecimal("20.00"));
  }

  @Test
  void getVatRateForRequest_whenVatIndicatorIsFalse_shouldReturnZero() {
    FeeCalculationRequest request = FeeCalculationRequest.builder()
        .feeCode("ABC")
        .vatIndicator(false)
        .caseConcludedDate(null)
        .build();

    BigDecimal result = vatRatesService.getVatRateForRequest(request);

    assertThat(result).isEqualTo(BigDecimal.ZERO);
  }

  @Test
  void getVatRateForRequest_whenVatIndicatorIsNull_shouldReturnZero() {
    FeeCalculationRequest request = FeeCalculationRequest.builder()
        .feeCode("ABC")
        .vatIndicator(null)
        .caseConcludedDate(null)
        .build();

    BigDecimal result = vatRatesService.getVatRateForRequest(request);

    assertThat(result).isEqualTo(BigDecimal.ZERO);
  }

  @Test
  void getVatRateForRequest_whenVatIndicatorIsTrueAndCaseConcludedDateIsNull_shouldThrowValidationException() {
    FeeCalculationRequest request = FeeCalculationRequest.builder()
        .feeCode("ABC")
        .vatIndicator(true)
        .caseConcludedDate(null)
        .build();

    assertThatThrownBy(() -> vatRatesService.getVatRateForRequest(request))
        .isInstanceOf(CaseConcludedDateRequiredException.class)
        .hasMessageContaining("Case Concluded Date is required for feeCode: ABC");
  }
}