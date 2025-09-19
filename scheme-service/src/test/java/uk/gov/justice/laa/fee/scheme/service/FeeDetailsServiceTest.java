package uk.gov.justice.laa.fee.scheme.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.fee.scheme.model.FeeDetailsResponse;
import uk.gov.justice.laa.fee.scheme.repository.FeeDetailsLookUpRepository;
import uk.gov.justice.laa.fee.scheme.repository.projection.FeeDetailsProjection;

@ExtendWith(MockitoExtension.class)
class FeeDetailsServiceTest {

  @Mock
  FeeDetailsLookUpRepository feeDetailsLookUpRepository;
  @InjectMocks
  private FeeDetailsService feeDetailsService;

  @Test
  void getFeeDetails_shouldReturnExpectedFeeDetails() {
    String feeCode = "FEE123";

    FeeDetailsProjection feeDetailsProjection = mock(FeeDetailsProjection.class);
    when(feeDetailsProjection.getCategoryCode()).thenReturn("AAP");
    when(feeDetailsProjection.getDescription()).thenReturn("Claims Against Public Authorities Legal Help Fixed Fee");
    when(feeDetailsProjection.getFeeType()).thenReturn("FIXED");

    when(feeDetailsLookUpRepository.findFeeCategoryInfoByFeeCode(any())).thenReturn(Optional.of(feeDetailsProjection));

    FeeDetailsResponse response = feeDetailsService.getFeeDetails(feeCode);

    assertThat(response.getCategoryOfLawCode()).isEqualTo("AAP");
    assertThat(response.getFeeCodeDescription()).isEqualTo("Claims Against Public Authorities Legal Help Fixed Fee");
    assertThat(response.getFeeType()).isEqualTo("FIXED");
  }

  @Test
  void getFeeDetails_shouldReturnExceptionCategoryOfLawNotFound() {
    String feeCode = "FEE123";

    when(feeDetailsLookUpRepository.findFeeCategoryInfoByFeeCode(any())).thenReturn(Optional.empty());

    assertThatThrownBy(() -> feeDetailsService.getFeeDetails(feeCode))
        .hasMessageContaining(String.format("Category of law code not found for feeCode: %s", feeCode));

  }
}