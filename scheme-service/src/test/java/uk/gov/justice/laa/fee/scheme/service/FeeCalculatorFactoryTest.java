package uk.gov.justice.laa.fee.scheme.service;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.feecalculator.FeeCalculator;
import uk.gov.justice.laa.fee.scheme.feecalculator.FeeCalculatorFactory;

@ExtendWith(MockitoExtension.class)
class FeeCalculatorFactoryTest {

  @Mock
  FeeCalculator immigrationCalculator;
  @Mock
  FeeCalculator standardCalculator;

  private FeeCalculatorFactory factory;

  @BeforeEach
  void setUp() {
    // Mock calculators
    when(immigrationCalculator.getSupportedCategories())
        .thenReturn(Set.of(CategoryType.IMMIGRATION_ASYLUM));
    when(standardCalculator.getSupportedCategories())
        .thenReturn(Set.of(CategoryType.COMMUNITY_CARE, CategoryType.HOUSING, CategoryType.EDUCATION));

    // Create factory with the mocked calculators
    factory = new FeeCalculatorFactory(List.of(immigrationCalculator, standardCalculator));
  }

  @Test
  void getCalculator_ShouldReturnCorrectCalculator_ForKnownCategory() {
    FeeCalculator calc1 = factory.getCalculator(CategoryType.IMMIGRATION_ASYLUM);
    assertSame(immigrationCalculator, calc1);
    FeeCalculator calc2 = factory.getCalculator(CategoryType.COMMUNITY_CARE);
    assertSame(standardCalculator, calc2);
    FeeCalculator calc3 = factory.getCalculator(CategoryType.HOUSING);
    assertSame(standardCalculator, calc3);
    FeeCalculator calc4 = factory.getCalculator(CategoryType.EDUCATION);
    assertSame(standardCalculator, calc4);
  }


  @Test
  void factory_ShouldMapAllSupportedCategories() {
    // Arrange + Act
    FeeCalculator calc1 = factory.getCalculator(CategoryType.HOUSING);
    FeeCalculator calc2 = factory.getCalculator(CategoryType.COMMUNITY_CARE);

    // Assert
    assertSame(standardCalculator, calc1);
    assertSame(standardCalculator, calc2);
    verify(standardCalculator, never()).calculate(any(), any());
  }
}
