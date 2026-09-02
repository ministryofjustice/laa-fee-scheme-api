package uk.gov.justice.laa.fee.scheme.feecalculator.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.EDUCATION;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.MENTAL_HEALTH;
import static uk.gov.justice.laa.fee.scheme.enums.FeeType.DISB_ONLY;
import static uk.gov.justice.laa.fee.scheme.enums.FeeType.FIXED;
import static uk.gov.justice.laa.fee.scheme.enums.FeeType.HOURLY;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import uk.gov.justice.laa.fee.scheme.config.FeatureFlagsConfig;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.entity.FeeSchemesEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.feecalculator.EducationFeeCalculator;
import uk.gov.justice.laa.fee.scheme.feecalculator.disbursement.EducationDisbursementOnlyCalculator;
import uk.gov.justice.laa.fee.scheme.feecalculator.fixed.standard.EducationFixedFeeCalculator;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;

@ExtendWith(MockitoExtension.class)
class EducationFeeCalculatorTest {

  @InjectMocks
  EducationFeeCalculator educationFeeCalculator;

  @Mock
  EducationFixedFeeCalculator educationFixedFeeCalculator;

  @Mock
  EducationDisbursementOnlyCalculator educationDisbursementOnlyCalculator;

  @Mock
  FeatureFlagsConfig featureFlagsConfig;

  @Test
  void getFee_whenEducationFeeFixed_shouldReturnFeeCalculationResponse() {

    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode("EDUFIN")
        .startDate(LocalDate.of(2017, 7, 29))
        .vatIndicator(true)
        .netDisbursementAmount(50.50)
        .disbursementVatAmount(20.15)
        .build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("EDUFIN")
        .feeScheme(FeeSchemesEntity.builder().schemeCode("EDU_FS2013").build())
        .fixedFee(new BigDecimal("321.0"))
        .categoryType(MENTAL_HEALTH)
        .feeType(FIXED)
        .build();

    FeeCalculation expectedCalculation = FeeCalculation.builder()
        .totalAmount(311.32)
        .vatIndicator(Boolean.TRUE)
        .vatRateApplied(20.0)
        .calculatedVatAmount(40.11)
        .disbursementAmount(50.5)
        .disbursementVatAmount(20.15)
        .fixedFeeAmount(200.56)
        .calculatedVatAmount(40.11)
        .build();

    FeeCalculationResponse expectedResponse = FeeCalculationResponse.builder()
        .feeCode("EDUFIN")
        .schemeId("EDU_FS2013")
        .escapeCaseFlag(false)
        .feeCalculation(expectedCalculation)
        .build();

    when(featureFlagsConfig.getIsFeatureEnabled()).thenReturn(true);
    when(educationFixedFeeCalculator.calculate(feeCalculationRequest, feeEntity)).thenReturn(expectedResponse);

    FeeCalculationResponse result = educationFeeCalculator.calculate(feeCalculationRequest, feeEntity);

    verify(featureFlagsConfig).getIsFeatureEnabled();
    verify(educationFixedFeeCalculator).calculate(feeCalculationRequest, feeEntity);
    assertThat(result).isNotNull();
    assertThat(result.getFeeCode()).isEqualTo("EDUFIN");
    assertThat(result.getFeeCalculation()).isNotNull();
    assertThat(result.getFeeCalculation().getTotalAmount()).isEqualTo(311.32);
  }

  @Test
  void getFee_whenEducationFeeDisbursement_shouldReturnFeeCalculationResponse() {

    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode("EDUDIS")
        .startDate(LocalDate.of(2017, 7, 29))
        .vatIndicator(true)
        .netDisbursementAmount(50.50)
        .disbursementVatAmount(20.15)
        .build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("EDUDIS")
        .feeScheme(FeeSchemesEntity.builder().schemeCode("EDU_FS2013").build())
        .categoryType(MENTAL_HEALTH)
        .feeType(DISB_ONLY)
        .disbursementLimit(new BigDecimal("400.00"))
        .build();

    FeeCalculation expectedCalculation = FeeCalculation.builder()
        .totalAmount(311.32)
        .build();

    FeeCalculationResponse expectedResponse = FeeCalculationResponse.builder()
        .feeCode("EDUDIS")
        .schemeId("EDU_FS2013")
        .escapeCaseFlag(false)
        .feeCalculation(expectedCalculation)
        .build();

    when(featureFlagsConfig.getIsFeatureEnabled()).thenReturn(false);
    when(educationDisbursementOnlyCalculator.calculate(feeCalculationRequest, feeEntity)).thenReturn(expectedResponse);

    FeeCalculationResponse result = educationFeeCalculator.calculate(feeCalculationRequest, feeEntity);

    verify(featureFlagsConfig).getIsFeatureEnabled();
    verify(educationDisbursementOnlyCalculator).calculate(feeCalculationRequest, feeEntity);
    assertThat(result).isNotNull();
    assertThat(result.getFeeCode()).isEqualTo("EDUDIS");
    assertThat(result.getFeeCalculation()).isNotNull();
    assertThat(result.getFeeCalculation().getTotalAmount()).isEqualTo(311.32);
  }

  @Test
  void calculate_shouldThrowIllegalStateException_whenFeeTypeIsHourly() {
    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode("EDUFIN")
        .build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("EDUFIN")
        .feeType(HOURLY)
        .build();

    when(featureFlagsConfig.getIsFeatureEnabled()).thenReturn(false);

    assertThatThrownBy(() -> educationFeeCalculator.calculate(feeCalculationRequest, feeEntity))
        .isInstanceOf(UnsupportedOperationException.class)
        .hasMessage("Hourly rate fee is not supported for Education category.");
  }

  @Test
  void getSupportedCategories_shouldReturnEducationCategory() {
    Set<CategoryType> result = educationFeeCalculator.getSupportedCategories();

    assertThat(result).isEqualTo(Set.of(EDUCATION));
  }

  @Test
  void calculate_whenFeatureEnabledAndFeeTypeIsFixed_shouldOnlyUseFixedCalculator() {
    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode("EDUFIX")
        .build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("EDUFIX")
        .feeType(FIXED)
        .build();

    FeeCalculationResponse expectedResponse = FeeCalculationResponse.builder()
        .feeCode("EDUFIX")
        .build();

    when(featureFlagsConfig.getIsFeatureEnabled()).thenReturn(true);
    when(educationFixedFeeCalculator.calculate(feeCalculationRequest, feeEntity)).thenReturn(expectedResponse);

    FeeCalculationResponse result = educationFeeCalculator.calculate(feeCalculationRequest, feeEntity);

    assertThat(result).isEqualTo(expectedResponse);
    verify(featureFlagsConfig).getIsFeatureEnabled();
    verify(educationFixedFeeCalculator).calculate(feeCalculationRequest, feeEntity);
    verifyNoInteractions(educationDisbursementOnlyCalculator);
  }

  @Test
  void calculate_whenFeatureFlagIsNull_shouldStillUseDisbursementCalculatorForDisbursementFeeType() {
    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode("EDUDIS")
        .build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("EDUDIS")
        .feeType(DISB_ONLY)
        .build();

    FeeCalculationResponse expectedResponse = FeeCalculationResponse.builder()
        .feeCode("EDUDIS")
        .build();

    when(featureFlagsConfig.getIsFeatureEnabled()).thenReturn(null);
    when(educationDisbursementOnlyCalculator.calculate(feeCalculationRequest, feeEntity)).thenReturn(expectedResponse);

    FeeCalculationResponse result = educationFeeCalculator.calculate(feeCalculationRequest, feeEntity);

    assertThat(result).isEqualTo(expectedResponse);
    verify(featureFlagsConfig).getIsFeatureEnabled();
    verify(educationDisbursementOnlyCalculator).calculate(feeCalculationRequest, feeEntity);
    verifyNoInteractions(educationFixedFeeCalculator);
  }

  @Test
  void calculate_whenFeatureEnabled_logsEnabledConditionalMessage() {
    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder().feeCode("EDUFIX").build();
    FeeEntity feeEntity = FeeEntity.builder().feeCode("EDUFIX").feeType(FIXED).build();
    FeeCalculationResponse expectedResponse = FeeCalculationResponse.builder().feeCode("EDUFIX").build();

    Logger logger = (Logger) LoggerFactory.getLogger(EducationFeeCalculator.class);
    ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
    listAppender.start();
    logger.addAppender(listAppender);

    when(featureFlagsConfig.getIsFeatureEnabled()).thenReturn(true);
    when(educationFixedFeeCalculator.calculate(feeCalculationRequest, feeEntity)).thenReturn(expectedResponse);

    educationFeeCalculator.calculate(feeCalculationRequest, feeEntity);

    assertThat(listAppender.list).anyMatch(event ->
        event.getLevel() == Level.INFO
            && event.getFormattedMessage().contains("Feature enabled. Using conditional statement"));

    listAppender.stop();
    logger.detachAppender(listAppender);
  }

  @Test
  void calculate_whenFeatureDisabled_logsDisabledConditionalMessage() {
    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder().feeCode("EDUDIS").build();
    FeeEntity feeEntity = FeeEntity.builder().feeCode("EDUDIS").feeType(DISB_ONLY).build();
    FeeCalculationResponse expectedResponse = FeeCalculationResponse.builder().feeCode("EDUDIS").build();

    Logger logger = (Logger) LoggerFactory.getLogger(EducationFeeCalculator.class);
    ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
    listAppender.start();
    logger.addAppender(listAppender);

    when(featureFlagsConfig.getIsFeatureEnabled()).thenReturn(false);
    when(educationDisbursementOnlyCalculator.calculate(feeCalculationRequest, feeEntity)).thenReturn(expectedResponse);

    educationFeeCalculator.calculate(feeCalculationRequest, feeEntity);

    assertThat(listAppender.list).anyMatch(event ->
        event.getLevel() == Level.INFO
            && event.getFormattedMessage().contains("Feature disabled. Using conditional statement"));

    listAppender.stop();
    logger.detachAppender(listAppender);
  }
}