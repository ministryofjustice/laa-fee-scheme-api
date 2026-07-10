package uk.gov.justice.laa.fee.scheme.feecalculator;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.fee.scheme.service.VatRatesService;

/**
 * Helper test class for Vat service.
 */
@ExtendWith(MockitoExtension.class)
public abstract class BaseFeeCalculatorTest {

  @Mock
  VatRatesService vatRatesService;

  protected void mockVatRatesService(Boolean vatIndicator) {
    BigDecimal vatRate = vatIndicator ? new BigDecimal("20.00") : BigDecimal.ZERO;
    lenient().when(vatRatesService.getVatRateForDate(any(), any())).thenReturn(vatRate);
    lenient().when(vatRatesService.getVatRateForRequest(any())).thenReturn(vatRate);
    lenient().when(vatRatesService.getVatRateForRequest(any(), any())).thenReturn(new BigDecimal("20.00"));
  }

  protected void mockVatRatesVatIndicatorTrue() {
    when(vatRatesService.getVatRateForDate(any(LocalDate.class), eq(true)))
        .thenReturn(new BigDecimal("20.00"));
  }
}
