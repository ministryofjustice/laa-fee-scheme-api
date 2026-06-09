package uk.gov.justice.laa.fee.scheme.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import uk.gov.justice.laa.fee.scheme.exception.AreaOfLawNotFoundException;
import uk.gov.justice.laa.fee.scheme.model.FeeCodeDetailsV1;
import uk.gov.justice.laa.fee.scheme.model.FeeCodesResponseV1;
import uk.gov.justice.laa.fee.scheme.repository.FeeCategoryMappingRepository;

@ExtendWith(MockitoExtension.class)
class FeeCodesServiceTest {

  @Mock FeeCategoryMappingRepository feeCategoryMappingRepository;

  @InjectMocks private FeeCodesService feeCodesService;

  private FeeCategoryMappingEntity buildMapping(
      String feeCode,
      String description,
      FeeType feeType,
      AreaOfLawType areaOfLawType,
      String categoryCode) {

    AreaOfLawTypeEntity area =
        AreaOfLawTypeEntity.builder()
            .code(areaOfLawType)
            .description("Legal Help")
            .caseType(CaseType.CIVIL)
            .build();

    CategoryOfLawTypeEntity category =
        CategoryOfLawTypeEntity.builder().code(categoryCode).areaOfLawType(area).build();

    FeeInformationEntity feeInfo = mock(FeeInformationEntity.class);
    when(feeInfo.getFeeDescription()).thenReturn(description);
    when(feeInfo.getFeeType()).thenReturn(feeType);
    when(feeInfo.getFeeCode()).thenReturn(feeCode);

    FeeCategoryMappingEntity mapping = mock(FeeCategoryMappingEntity.class);
    when(mapping.getFeeCode()).thenReturn(feeInfo);
    when(mapping.getCategoryOfLawType()).thenReturn(category);

    return mapping;
  }

  @Test
  void getFeeCodesV1_shouldReturnExpectedResponse() {
    String area = "LEGAL_HELP";

    FeeCategoryMappingEntity mapping =
        buildMapping("FEE123", "desc", FeeType.FIXED, AreaOfLawType.LEGAL_HELP, "AAP");

    when(feeCategoryMappingRepository.findByCategoryOfLawTypeAreaOfLawTypeCode(eq(AreaOfLawType.LEGAL_HELP)))
        .thenReturn(List.of(mapping));

    FeeCodesResponseV1 response = feeCodesService.getFeeCodesV1(area);

    assertThat(response.getFeeCodes()).hasSize(1);

    FeeCodeDetailsV1 result = response.getFeeCodes().get(0);

    assertThat(result.getFeeCode()).isEqualTo("FEE123");
    assertThat(result.getFeeCodeDescription()).isEqualTo("desc");
    assertThat(result.getFeeType()).isEqualTo("FIXED");
    assertThat(result.getAreaOfLaw()).isEqualTo("Legal Help");
    assertThat(result.getCategoryOfLawCodes()).contains("AAP");
  }

  @Test
  void getFeeCodesV1_shouldThrowExceptionForNullAreaOfLaw() {
    assertThatThrownBy(() -> feeCodesService.getFeeCodesV1(null))
            .isInstanceOf(AreaOfLawNotFoundException.class);
  }

  @Test
  void getFeeCodesV1_shouldThrowExceptionForInvalidAreaOfLaw() {
    assertThatThrownBy(() -> feeCodesService.getFeeCodesV1("INVALID"))
        .isInstanceOf(AreaOfLawNotFoundException.class);
  }

  @Test
  void build_shouldHandleMultipleFeeCodes() {
    FeeCategoryMappingEntity m1 =
        buildMapping("FEE1", "desc1", FeeType.FIXED, AreaOfLawType.LEGAL_HELP, "A1");

    FeeCategoryMappingEntity m2 =
        buildMapping("FEE2", "desc1", FeeType.FIXED, AreaOfLawType.LEGAL_HELP, "A2");

    when(feeCategoryMappingRepository.findByCategoryOfLawTypeAreaOfLawTypeCode(any()))
        .thenReturn(List.of(m1, m2));

    FeeCodesResponseV1 response = feeCodesService.getFeeCodesV1("LEGAL_HELP");

    assertThat(response.getFeeCodes()).hasSize(2);

    FeeCodeDetailsV1 result1 = response.getFeeCodes().get(0);

    assertThat(result1.getCategoryOfLawCodes()).contains("A1");

    FeeCodeDetailsV1 result2 = response.getFeeCodes().get(1);

    assertThat(result2.getCategoryOfLawCodes()).contains("A2");
  }

  @Test
  void build_shouldHandleAllCategoryOfLaw() {
    FeeCategoryMappingEntity m1 =
        buildMapping("ASMS", "desc1", FeeType.FIXED, AreaOfLawType.LEGAL_HELP, "ALL");

    when(feeCategoryMappingRepository.findByCategoryOfLawTypeAreaOfLawTypeCode(any()))
        .thenReturn(List.of(m1));

    FeeCodesResponseV1 response = feeCodesService.getFeeCodesV1("LEGAL_HELP");

    assertThat(response.getFeeCodes()).hasSize(1);

    FeeCodeDetailsV1 result = response.getFeeCodes().get(0);

    assertThat(result.getCategoryOfLawCodes()).contains("INVEST", "PRISON", "APPEALS");
  }

  @Test
  void getFeeCodesV1_shouldReturnEmptyListWhenNoData() {
    when(feeCategoryMappingRepository.findByCategoryOfLawTypeAreaOfLawTypeCode(any()))
        .thenReturn(List.of());

    FeeCodesResponseV1 response = feeCodesService.getFeeCodesV1("LEGAL_HELP");

    assertThat(response.getFeeCodes()).isEmpty();
  }
}
