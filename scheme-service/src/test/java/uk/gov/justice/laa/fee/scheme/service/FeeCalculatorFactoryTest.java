package uk.gov.justice.laa.fee.scheme.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FeeCalculatorFactoryTest {

  private FeeCalculator immigrationCalculator;
  private FeeCalculator standardCalculator;
  private FeeCalculatorFactory factory;

  @BeforeEach
  void setUp() {
    // Mock calculators
    immigrationCalculator = mock(FeeCalculator.class);
    when(immigrationCalculator.getSupportedCategories())
        .thenReturn(Set.of(CategoryType.IMMIGRATION_ASYLUM));

    standardCalculator = mock(FeeCalculator.class);
    when(standardCalculator.getSupportedCategories())
        .thenReturn(Set.of(CategoryType.COMMUNITY_CARE, CategoryType.HOUSING, CategoryType.EDUCATION));

    // Create factory with the mocked calculators
    factory = new FeeCalculatorFactory(List.of(immigrationCalculator, standardCalculator));
  }

  @Test
  void getCalculator_ShouldReturnCorrectCalculator_ForKnownCategory() {
    // Act
    FeeCalculator calc1 = factory.getCalculator(CategoryType.IMMIGRATION_ASYLUM);
    FeeCalculator calc2 = factory.getCalculator(CategoryType.COMMUNITY_CARE);
    FeeCalculator calc3 = factory.getCalculator(CategoryType.HOUSING);
    FeeCalculator calc4 = factory.getCalculator(CategoryType.EDUCATION);

    // Assert
    assertSame(immigrationCalculator, calc1);
    assertSame(standardCalculator, calc2);
    assertSame(standardCalculator, calc3);
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
    verify(standardCalculator, never()).calculate(any());
  }
}
