package uk.gov.justice.laa.fee.scheme.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.DISCRIMINATION;
import static uk.gov.justice.laa.fee.scheme.enums.ErrorType.ERR_CIVIL_START_DATE;
import static uk.gov.justice.laa.fee.scheme.enums.ErrorType.ERR_CIVIL_START_DATE_TOO_OLD;
import static uk.gov.justice.laa.fee.scheme.enums.ErrorType.ERR_FAMILY_LONDON_RATE;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.entity.FeeSchemesEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.enums.ErrorType;
import uk.gov.justice.laa.fee.scheme.enums.FeeType;
import uk.gov.justice.laa.fee.scheme.enums.Region;
import uk.gov.justice.laa.fee.scheme.exception.ValidationException;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.service.validation.CivilFeeValidationService;

@ExtendWith(MockitoExtension.class)
class CivilFeeValidationServiceTest {

  @InjectMocks
  private CivilFeeValidationService civilFeeValidationService;

  private FeeEntity fixedFeeEntity(String feeCode, CategoryType categoryType, FeeSchemesEntity feeSchemesEntity) {
    return FeeEntity.builder()
        .feeCode(feeCode)
        .feeScheme(feeSchemesEntity)
        .categoryType(categoryType)
        .feeType(FeeType.FIXED)
        .build();
  }

  private FeeEntity familyFeeEntity(FeeSchemesEntity feeSchemesEntity, Region region) {
    return FeeEntity.builder()
        .feeCode("FPB010")
        .feeScheme(feeSchemesEntity)
        .fixedFee(new BigDecimal("150"))
        .escapeThresholdLimit(new BigDecimal("300"))
        .region(region)
        .categoryType(CategoryType.FAMILY)
        .feeType(FeeType.FIXED)
        .build();
  }

  @Test
  void getValidFeeEntity_whenCivilFeeCodeAndStartDateIsInvalid_shouldThrowException() {
    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode("DISC")
        .startDate(LocalDate.of(2025, 5, 1))
        .netDisbursementAmount(50.50)
        .disbursementVatAmount(20.15)
        .build();

    FeeSchemesEntity feeSchemesEntity = FeeSchemesEntity.builder().schemeCode("DISC_FS2013")
        .validFrom(LocalDate.of(2025, 3, 1))
        .validTo(LocalDate.of(2025, 4, 1))
        .build();

    FeeEntity feeEntity = fixedFeeEntity("DISC", DISCRIMINATION, feeSchemesEntity);

    List<FeeEntity> feeEntityList = List.of(feeEntity);

    assertThatThrownBy(() -> civilFeeValidationService.getValidFeeEntity(feeEntityList, feeCalculationRequest))
        .isInstanceOf(ValidationException.class)
        .hasFieldOrPropertyWithValue("error", ERR_CIVIL_START_DATE)
        .hasMessage("ERRCIV1 - Fee Code is not valid for the Case Start Date.");
  }

  @Test
  void getValidFeeEntity_whenCivilFeeCodeAndDateTooFarInPast_shouldThrowException() {
    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode("DISC")
        .startDate(LocalDate.of(2025, 5, 1))
        .netDisbursementAmount(50.50)
        .disbursementVatAmount(20.15)
        .build();

    FeeSchemesEntity feeSchemesEntity = FeeSchemesEntity.builder().schemeCode("FEE_SCHEME_2025")
        .validFrom(LocalDate.of(2025, 10, 1)).build();

    FeeEntity feeEntity = fixedFeeEntity("DISC", DISCRIMINATION, feeSchemesEntity);

    List<FeeEntity> feeEntityList = List.of(feeEntity);


    assertThatThrownBy(() -> civilFeeValidationService.getValidFeeEntity(feeEntityList, feeCalculationRequest))
        .isInstanceOf(ValidationException.class)
        .hasFieldOrPropertyWithValue("error", ERR_CIVIL_START_DATE_TOO_OLD)
        .hasMessage("ERRCIV2 - Case Start Date is too far in the past.");
  }

  @ParameterizedTest
  @CsvSource({
      "IACC, IMMIGRATION_ASYLUM, IMM_ASYLM_FS2020, 2020-04-01, 2013-04-29, ERR_IMM_ASYLUM_BETWEEN_DATE",
      "IACE, IMMIGRATION_ASYLUM, IMM_ASYLM_FS2023, 2023-04-01, 2020-04-29, ERR_IMM_ASYLUM_AFTER_DATE",

      "IACC, IMMIGRATION_ASYLUM, IMM_ASYLM_FS2020, 2020-04-01, 2011-04-29, ERR_CIVIL_START_DATE_TOO_OLD",
      "IACA, IMMIGRATION_ASYLUM, IMM_ASYLM_FS2013, 2013-04-01, 2011-04-29, ERR_CIVIL_START_DATE_TOO_OLD",
      "IACE, IMMIGRATION_ASYLUM, IMM_ASYLM_FS2023, 2023-04-01, 2011-04-29, ERR_CIVIL_START_DATE_TOO_OLD",
      "IDAS2, IMMIGRATION_ASYLUM, IMM_ASYLM_FS2013, 2013-04-01, 2011-04-29, ERR_CIVIL_START_DATE_TOO_OLD",
  })
  void getValidFeeEntity_whenCivilImmigrationAsylumFeeCodeAndDateTooFarInPast_shouldThrowException(String feeCode,
                                                                                                   CategoryType categoryType,
                                                                                                   String feeScheme,
                                                                                                   LocalDate feeSchemeDate,
                                                                                                   LocalDate claimStartDate,
                                                                                                   ErrorType expectedError) {
    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode(feeCode)
        .startDate(claimStartDate)
        .netDisbursementAmount(50.50)
        .disbursementVatAmount(20.15)
        .build();

    FeeSchemesEntity feeSchemesEntity = FeeSchemesEntity.builder().schemeCode(feeScheme)
        .validFrom(feeSchemeDate).build();

    FeeEntity feeEntity = fixedFeeEntity(feeCode, categoryType, feeSchemesEntity);

    List<FeeEntity> feeEntityList = List.of(feeEntity);

    assertThatThrownBy(() -> civilFeeValidationService.getValidFeeEntity(feeEntityList, feeCalculationRequest))
        .isInstanceOf(ValidationException.class)
        .hasFieldOrPropertyWithValue("error", expectedError)
        .hasMessageContaining(expectedError.getMessage());
  }

  @ParameterizedTest
  @CsvSource({
      "IACC, IMMIGRATION_ASYLUM, IMM_ASYLM_FS2020, 2020-04-01, 2023-04-29, 2024-04-29, ERR_IMM_ASYLUM_BETWEEN_DATE",
      "IACA, IMMIGRATION_ASYLUM, IMM_ASYLM_FS2020, 2020-04-01, 2023-04-29, 2024-04-29, ERR_IMM_ASYLUM_BEFORE_DATE",
  })
  void getValidFeeEntity_whenCivilImmigrationAsylumFeeCodeAndStartDateIsInvalid_shouldThrowException(String feeCode,
                                                                                                     CategoryType categoryType,
                                                                                                     String feeScheme,
                                                                                                     LocalDate feeSchemeStartDate,
                                                                                                     LocalDate feeSchemeEndDate,
                                                                                                     LocalDate claimStartDate,
                                                                                                     ErrorType expectedError) {
    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode(feeCode)
        .startDate(claimStartDate)
        .netDisbursementAmount(50.50)
        .disbursementVatAmount(20.15)
        .build();

    FeeSchemesEntity feeSchemesEntity = FeeSchemesEntity.builder().schemeCode(feeScheme)
        .validFrom(feeSchemeStartDate)
        .validTo(feeSchemeEndDate).build();

    FeeEntity feeEntity = fixedFeeEntity(feeCode, categoryType, feeSchemesEntity);

    List<FeeEntity> feeEntityList = List.of(feeEntity);

    assertThatThrownBy(() -> civilFeeValidationService.getValidFeeEntity(feeEntityList, feeCalculationRequest))
        .isInstanceOf(ValidationException.class)
        .hasFieldOrPropertyWithValue("error", expectedError)
        .hasMessageContaining(expectedError.getMessage());
  }

  @ParameterizedTest()
  @CsvSource(value = {
      "true, LONDON, FAM_LON_FS2011",
      "false, NON_LONDON, FAM_NON_FS2011"
  })
  void getValidFeeEntity_whenFamilyCategoryAndGivenLondonRate_shouldReturnCorrectFeeEntity(Boolean isLondonRate,
                                                                                           Region expectedRegion,
                                                                                           String expectedFeeScheme) {

    FeeSchemesEntity feeSchemesEntity = FeeSchemesEntity.builder().schemeCode("FAM_LON_FS2011")
        .validFrom(LocalDate.of(2011, 1, 1)).build();

    FeeSchemesEntity feeSchemesEntity2 = FeeSchemesEntity.builder().schemeCode("FAM_NON_FS2011")
        .validFrom(LocalDate.of(2011, 1, 1)).build();

    FeeEntity feeEntity1 = familyFeeEntity(feeSchemesEntity, Region.LONDON);

    FeeEntity feeEntity2 = familyFeeEntity(feeSchemesEntity2, Region.NON_LONDON);

    List<FeeEntity> feeEntityList = List.of(feeEntity1, feeEntity2);

    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode("FPB010")
        .startDate(LocalDate.of(2025, 2, 11))
        .vatIndicator(Boolean.TRUE)
        .netDisbursementAmount(50.50)
        .disbursementVatAmount(20.15)
        .londonRate(isLondonRate)
        .build();

    FeeEntity result = civilFeeValidationService.getValidFeeEntity(feeEntityList, feeCalculationRequest);

    assertThat(result).isNotNull();
    assertThat(result.getFeeCode()).isEqualTo("FPB010");
    assertThat(result.getFixedFee()).isEqualTo("150");
    assertThat(result.getFeeScheme().getSchemeCode()).isEqualTo(expectedFeeScheme);
    assertThat(result.getRegion()).isEqualTo(expectedRegion);
  }

  @Test
  void getValidFeeEntity_whenFamilyCategoryAndLondonRateIsMissing_shouldThrowException() {
    FeeSchemesEntity feeSchemesEntity = FeeSchemesEntity.builder().schemeCode("FAM_LON_FS2011")
        .validFrom(LocalDate.of(2011, 1, 1)).build();

    FeeSchemesEntity feeSchemesEntity2 = FeeSchemesEntity.builder().schemeCode("FAM_NON_FS2011")
        .validFrom(LocalDate.of(2011, 1, 1)).build();

    FeeEntity feeEntity1 = familyFeeEntity(feeSchemesEntity, Region.LONDON);

    FeeEntity feeEntity2 = familyFeeEntity(feeSchemesEntity2, Region.NON_LONDON);

    List<FeeEntity> feeEntityList = List.of(feeEntity1, feeEntity2);

    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode("FPB010")
        .startDate(LocalDate.of(2025, 2, 11))
        .vatIndicator(Boolean.TRUE)
        .netDisbursementAmount(50.50)
        .disbursementVatAmount(20.15)
        .build();

    assertThatThrownBy(() -> civilFeeValidationService.getValidFeeEntity(feeEntityList, feeCalculationRequest))
        .isInstanceOf(ValidationException.class)
        .hasFieldOrPropertyWithValue("error", ERR_FAMILY_LONDON_RATE)
        .hasMessage("ERRFAM1 - London/non-London rate must be entered for the Fee Code used.");
  }
}