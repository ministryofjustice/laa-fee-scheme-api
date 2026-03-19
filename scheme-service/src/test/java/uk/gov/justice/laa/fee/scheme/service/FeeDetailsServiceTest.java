package uk.gov.justice.laa.fee.scheme.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.fee.scheme.entity.AreaOfLawTypeEntity;
import uk.gov.justice.laa.fee.scheme.entity.CategoryOfLawTypeEntity;
import uk.gov.justice.laa.fee.scheme.entity.FeeCategoryMappingEntity;
import uk.gov.justice.laa.fee.scheme.entity.FeeInformationEntity;
import uk.gov.justice.laa.fee.scheme.enums.AreaOfLawType;
import uk.gov.justice.laa.fee.scheme.enums.CaseType;
import uk.gov.justice.laa.fee.scheme.enums.FeeType;
import uk.gov.justice.laa.fee.scheme.exception.CategoryCodeNotFoundException;
import uk.gov.justice.laa.fee.scheme.exception.ValidationException;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeDetailsResponseV1;
import uk.gov.justice.laa.fee.scheme.model.FeeDetailsResponseV2;
import uk.gov.justice.laa.fee.scheme.repository.FeeCategoryMappingRepository;

@ExtendWith(MockitoExtension.class)
class FeeDetailsServiceTest {

  @Mock
  FeeCategoryMappingRepository feeCategoryMappingRepository;
  @InjectMocks
  private FeeDetailsService feeDetailsService;

  @Test
  void getFeeDetailsV1_shouldReturnExpectedFeeDetails() {
    String feeCode = "FEE123";

    CategoryOfLawTypeEntity categoryOfLawType = CategoryOfLawTypeEntity.builder().code("AAP").build();

    // Mock FeeInformationEntity
    FeeInformationEntity feeInformation = mock(FeeInformationEntity.class);
    when(feeInformation.getFeeDescription()).thenReturn("Claims Against Public Authorities Legal Help Fixed Fee");
    when(feeInformation.getFeeType()).thenReturn(FeeType.FIXED);

    FeeCategoryMappingEntity feeCategoryMappingEntity = mock(FeeCategoryMappingEntity.class);
    when(feeCategoryMappingEntity.getCategoryOfLawType()).thenReturn(categoryOfLawType);
    when(feeCategoryMappingEntity.getFeeCode()).thenReturn(feeInformation); // return mock

    when(feeCategoryMappingRepository.findByFeeCodeFeeCode(any())).thenReturn(Optional.of(feeCategoryMappingEntity));

    FeeDetailsResponseV1 response = feeDetailsService.getFeeDetailsV1(feeCode);

    assertThat(response.getCategoryOfLawCode()).isEqualTo("AAP");
    assertThat(response.getFeeCodeDescription()).isEqualTo("Claims Against Public Authorities Legal Help Fixed Fee");
    assertThat(response.getFeeType()).isEqualTo("FIXED");
  }

  @Test
  void getFeeDetailsV1_shouldReturnExceptionCategoryOfLawNotFound() {
    String feeCode = "FEE123";

    when(feeCategoryMappingRepository.findByFeeCodeFeeCode(any())).thenReturn(Optional.empty());

    assertThatThrownBy(() -> feeDetailsService.getFeeDetailsV1(feeCode))
        .isInstanceOf(CategoryCodeNotFoundException.class)
        .hasMessage(String.format("Category of law code not found for feeCode: %s", feeCode));

  }


  @Test
  void getFeeDetailsV2_shouldReturnExpectedFeeDetails() {
    String feeCode = "FEE123";

    CategoryOfLawTypeEntity categoryOfLawType = CategoryOfLawTypeEntity.builder().code("AAP").build();

    // Mock FeeInformationEntity
    FeeInformationEntity feeInformation = mock(FeeInformationEntity.class);
    when(feeInformation.getFeeDescription()).thenReturn("Claims Against Public Authorities Legal Help Fixed Fee");
    when(feeInformation.getFeeType()).thenReturn(FeeType.FIXED);

    FeeCategoryMappingEntity feeCategoryMappingEntity = mock(FeeCategoryMappingEntity.class);
    when(feeCategoryMappingEntity.getCategoryOfLawType()).thenReturn(categoryOfLawType);
    when(feeCategoryMappingEntity.getFeeCode()).thenReturn(feeInformation); // return mock

    when(feeCategoryMappingRepository.findByFeeCodeFeeCode(any())).thenReturn(Optional.of(feeCategoryMappingEntity));

    FeeDetailsResponseV2 response = feeDetailsService.getFeeDetailsV2(feeCode);

    assertThat(response.getCategoryOfLawCodes()).isEqualTo(List.of("AAP"));
    assertThat(response.getFeeCodeDescription()).isEqualTo("Claims Against Public Authorities Legal Help Fixed Fee");
    assertThat(response.getFeeType()).isEqualTo("FIXED");
  }

  @CsvSource({
      "ASMS, Legal Help and Associated Civil Work – Miscellaneous",
      "ASPL, Legal Help and Associated Civil Work – Public Law",
      "ASAS, Part 1 injunction Anti-Social Behaviour Crime and Policing Act 2014"
  })
  @ParameterizedTest
  void getFeeDetailsV2_whenGivenAssociatedCivilFeeCode_shouldReturnExpectedFeeDetails(String feeCode, String description) {

    CategoryOfLawTypeEntity categoryOfLawType = CategoryOfLawTypeEntity.builder().code("ALL").build();

    // Mock FeeInformationEntity
    FeeInformationEntity feeInformation = mock(FeeInformationEntity.class);
    when(feeInformation.getFeeDescription()).thenReturn(description);
    when(feeInformation.getFeeType()).thenReturn(FeeType.FIXED);

    FeeCategoryMappingEntity feeCategoryMappingEntity = mock(FeeCategoryMappingEntity.class);
    when(feeCategoryMappingEntity.getFeeCode()).thenReturn(feeInformation); // return mock

    when(feeCategoryMappingRepository.findByFeeCodeFeeCode(any())).thenReturn(Optional.of(feeCategoryMappingEntity));

    FeeDetailsResponseV2 response = feeDetailsService.getFeeDetailsV2(feeCode);

    assertThat(response.getCategoryOfLawCodes()).isEqualTo(List.of("APPEALS", "INVEST", "PRISON"));
    assertThat(response.getFeeCodeDescription()).isEqualTo(description);
    assertThat(response.getFeeType()).isEqualTo("FIXED");
  }

  @Test
  void getFeeDetailsV2_shouldReturnExceptionCategoryOfLawNotFound() {
    String feeCode = "FEE123";

    when(feeCategoryMappingRepository.findByFeeCodeFeeCode(any())).thenReturn(Optional.empty());

    assertThatThrownBy(() -> feeDetailsService.getFeeDetailsV2(feeCode))
        .isInstanceOf(CategoryCodeNotFoundException.class)
        .hasMessage(String.format("Category of law code not found for feeCode: %s", feeCode));
  }

  @Test
  void getCaseType_shouldReturnExpectedCaseType() {
    String feeCode = "FEE123";

    AreaOfLawTypeEntity areaOfLawTypeEntity = AreaOfLawTypeEntity.builder()
        .code(AreaOfLawType.LEGAL_HELP).caseType(CaseType.CIVIL).build();
    CategoryOfLawTypeEntity categoryOfLawType = CategoryOfLawTypeEntity.builder()
        .code("AAP").areaOfLawType(areaOfLawTypeEntity).build();
    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder().feeCode(feeCode).build();

    FeeCategoryMappingEntity feeCategoryMappingEntity = mock(FeeCategoryMappingEntity.class);
    when(feeCategoryMappingEntity.getCategoryOfLawType()).thenReturn(categoryOfLawType);

    when(feeCategoryMappingRepository.findByFeeCodeFeeCode(feeCode)).thenReturn(Optional.of(feeCategoryMappingEntity));

    CaseType result = feeDetailsService.getCaseType(feeCalculationRequest);

    assertThat(result).isEqualTo(CaseType.CIVIL);
  }

  @Test
  void getCaseType_shouldThrowExceptionCategoryOfLawNotFound() {
    String feeCode = "FEE123";
    when(feeCategoryMappingRepository.findByFeeCodeFeeCode(feeCode)).thenReturn(Optional.empty());
    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder().feeCode(feeCode).build();

    assertThatThrownBy(() -> feeDetailsService.getCaseType(feeCalculationRequest))
        .isInstanceOf(ValidationException.class)
        .hasMessage("ERRALL1 - Enter a valid Fee Code.");

  }
}