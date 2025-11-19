package uk.gov.justice.laa.fee.scheme.feecalculator;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.fee.scheme.service.VatRatesService;

@ExtendWith(MockitoExtension.class)
public abstract class BaseFeeCalculatorTest {

  @Mock
  VatRatesService vatRatesService;

  protected void mockVatRatesService(Boolean vatIndicator) {
    when(vatRatesService.getVatRateForDate(any(), any()))
        .thenReturn(vatIndicator ? new BigDecimal("20.00") : BigDecimal.ZERO);
  }
}
