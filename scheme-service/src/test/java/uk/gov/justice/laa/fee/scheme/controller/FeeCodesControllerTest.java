package uk.gov.justice.laa.fee.scheme.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.justice.laa.fee.scheme.config.FeatureFlagsConfig;
import uk.gov.justice.laa.fee.scheme.config.features.Feature;
import uk.gov.justice.laa.fee.scheme.exception.AreaOfLawNotFoundException;
import uk.gov.justice.laa.fee.scheme.model.FeeCodeDetailsV1;
import uk.gov.justice.laa.fee.scheme.model.FeeCodesResponseV1;
import uk.gov.justice.laa.fee.scheme.service.FeeCodesService;

@WebMvcTest(FeeCodesController.class)
@AutoConfigureMockMvc(addFilters = false) // disable security filter for testing
class FeeCodesControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private FeeCodesService feeCodesService;
  @MockitoBean private FeatureFlagsConfig featureFlagsConfig;

  private final FeeCodeDetailsV1 feeCodeDetails =
      FeeCodeDetailsV1.builder()
          .feeCode("FEE123")
          .feeCodeDescription("fee_code_description")
          .feeType("FIXED")
          .categoryOfLawCodes(List.of("ASY"))
          .areaOfLaw("LEGAL_HELP")
          .build();

  private final FeeCodeDetailsV1 feeCodeDetails2 =
      FeeCodeDetailsV1.builder()
          .feeCode("FEE122")
          .feeCodeDescription("fee_code_desc")
          .feeType("FIXED")
          .categoryOfLawCodes(List.of("PWD"))
          .areaOfLaw("LEGAL_HELP")
          .build();

  private final FeeCodeDetailsV1 inquestFeeCodeDetails =
      FeeCodeDetailsV1.builder()
          .feeCode("COMINQ")
          .feeCodeDescription("Community Care Inquests Legal Help Fixed Fee")
          .feeType("FIXED")
          .categoryOfLawCodes(List.of("COM"))
          .areaOfLaw("LEGAL_HELP")
          .build();

  // "INQ" is both the standalone Inquest fee code and the Inquest category of law code,
  // so this fixture verifies filtering by fee code works correctly for that overlap.
  private final FeeCodeDetailsV1 standaloneInquestFeeCodeDetails =
      FeeCodeDetailsV1.builder()
          .feeCode("INQ")
          .feeCodeDescription("Inquests Legal Help Fixed Fee")
          .feeType("FIXED")
          .categoryOfLawCodes(List.of("INQ"))
          .areaOfLaw("LEGAL_HELP")
          .build();

  // Fee code that is unrelated to Inquest but whose categoryOfLawCodes coincidentally
  // contains "INQ", proving filtering is driven by fee code, not category of law code.
  private final FeeCodeDetailsV1 nonInquestFeeCodeWithInquestCategoryCode =
      FeeCodeDetailsV1.builder()
          .feeCode("FEE124")
          .feeCodeDescription("unrelated_fee_code_description")
          .feeType("FIXED")
          .categoryOfLawCodes(List.of("INQ"))
          .areaOfLaw("LEGAL_HELP")
          .build();

  @Test
  void getFeeCodesByAreaOfLaw() throws Exception {
    when(featureFlagsConfig.isEnabled(Feature.INQUEST)).thenReturn(true);

    FeeCodesResponseV1 response =
        FeeCodesResponseV1.builder().feeCodes(List.of(feeCodeDetails)).build();

    when(feeCodesService.getFeeCodesV1("LEGAL_HELP")).thenReturn(response);

    mockMvc
        .perform(get("/api/v1/fee-codes/LEGAL_HELP").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.feeCodes[0].feeCode").value("FEE123"))
        .andExpect(jsonPath("$.feeCodes[0].feeCodeDescription").value("fee_code_description"))
        .andExpect(jsonPath("$.feeCodes[0].feeType").value("FIXED"))
        .andExpect(jsonPath("$.feeCodes[0].categoryOfLawCodes[0]").value("ASY"))
        .andExpect(jsonPath("$.feeCodes[0].areaOfLaw").value("LEGAL_HELP"));
  }

  @Test
  void getFeeCodesByAreaOfLawMultipleCodes() throws Exception {
    when(featureFlagsConfig.isEnabled(Feature.INQUEST)).thenReturn(true);

    FeeCodesResponseV1 response =
        FeeCodesResponseV1.builder().feeCodes(List.of(feeCodeDetails, feeCodeDetails2)).build();

    when(feeCodesService.getFeeCodesV1("LEGAL_HELP")).thenReturn(response);

    mockMvc
        .perform(get("/api/v1/fee-codes/LEGAL_HELP").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.feeCodes[0].feeCode").value("FEE123"))
        .andExpect(jsonPath("$.feeCodes[0].feeCodeDescription").value("fee_code_description"))
        .andExpect(jsonPath("$.feeCodes[0].feeType").value("FIXED"))
        .andExpect(jsonPath("$.feeCodes[0].categoryOfLawCodes[0]").value("ASY"))
        .andExpect(jsonPath("$.feeCodes[0].areaOfLaw").value("LEGAL_HELP"))
        .andExpect(jsonPath("$.feeCodes[1].feeCode").value("FEE122"))
        .andExpect(jsonPath("$.feeCodes[1].feeCodeDescription").value("fee_code_desc"))
        .andExpect(jsonPath("$.feeCodes[1].feeType").value("FIXED"))
        .andExpect(jsonPath("$.feeCodes[1].categoryOfLawCodes[0]").value("PWD"))
        .andExpect(jsonPath("$.feeCodes[1].areaOfLaw").value("LEGAL_HELP"));
  }

  @Test
  void getFeeCodesV1ByAreaOfLawThrowsExceptionWhenAreaOfLawNotFound() throws Exception {
    when(featureFlagsConfig.isEnabled(Feature.INQUEST)).thenReturn(true);
    when(feeCodesService.getFeeCodesV1("FEE123"))
        .thenThrow(new AreaOfLawNotFoundException("FEE123"));

    mockMvc
        .perform(get("/api/v1/fee-codes/FEE123").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.timestamp").exists())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.error").value("Not Found"))
        .andExpect(jsonPath("$.message").value("Area of law not found for: FEE123"));
  }

  @Test
  void getFeeCodesV1ExcludesInquestFeeCodesWhenFeatureDisabled() throws Exception {
    when(featureFlagsConfig.isEnabled(Feature.INQUEST)).thenReturn(false);

    FeeCodesResponseV1 response =
        FeeCodesResponseV1.builder()
            .feeCodes(List.of(feeCodeDetails, inquestFeeCodeDetails))
            .build();

    when(feeCodesService.getFeeCodesV1("LEGAL_HELP")).thenReturn(response);

    mockMvc
        .perform(get("/api/v1/fee-codes/LEGAL_HELP").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.feeCodes.length()").value(1))
        .andExpect(jsonPath("$.feeCodes[0].feeCode").value("FEE123"));
  }

  @Test
  void getFeeCodesV1IncludesInquestFeeCodesWhenFeatureEnabled() throws Exception {
    when(featureFlagsConfig.isEnabled(Feature.INQUEST)).thenReturn(true);

    FeeCodesResponseV1 response =
        FeeCodesResponseV1.builder()
            .feeCodes(List.of(feeCodeDetails, inquestFeeCodeDetails))
            .build();

    when(feeCodesService.getFeeCodesV1("LEGAL_HELP")).thenReturn(response);

    mockMvc
        .perform(get("/api/v1/fee-codes/LEGAL_HELP").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.feeCodes.length()").value(2))
        .andExpect(jsonPath("$.feeCodes[1].feeCode").value("COMINQ"));
  }

  @Test
  void getFeeCodesV1ExcludesStandaloneInqFeeCodeButKeepsUnrelatedCategoryOfLawCodeWhenFeatureDisabled()
      throws Exception {
    when(featureFlagsConfig.isEnabled(Feature.INQUEST)).thenReturn(false);

    FeeCodesResponseV1 response =
        FeeCodesResponseV1.builder()
            .feeCodes(
                List.of(standaloneInquestFeeCodeDetails, nonInquestFeeCodeWithInquestCategoryCode))
            .build();

    when(feeCodesService.getFeeCodesV1("LEGAL_HELP")).thenReturn(response);

    mockMvc
        .perform(get("/api/v1/fee-codes/LEGAL_HELP").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.feeCodes.length()").value(1))
        .andExpect(jsonPath("$.feeCodes[0].feeCode").value("FEE124"))
        .andExpect(jsonPath("$.feeCodes[0].categoryOfLawCodes[0]").value("INQ"));
  }
}
