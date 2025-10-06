package uk.gov.justice.laa.fee.scheme.mapstruct;

import java.util.Map;
import java.util.stream.Collectors;

public class dataMappingUtil {

  private dataMappingUtil() {
  }

  public static final Map<String, String> SCHEME_ID_MAP = Map.of(
      "IMM_ASYLM_FS2023", "Immigration and Asylum",
      "AAR_FS2022", "Advocacy Appeals and Reviews"
  );

  public static String mapValidationMessages(SubmissionJsonData submissionJsonData) {
    if (submissionJsonData.getValidationMessages() == null || submissionJsonData.getValidationMessages().isEmpty()) {
      return "";
    }
    return submissionJsonData.getValidationMessages().stream()
        .map(vm -> vm.getType() + ": " + vm.getMessage())
        .collect(Collectors.joining("; "));
  }

  public static String mapSchemeId(SubmissionJsonData submissionJsonData) {
    if (submissionJsonData.getSchemeId() == null) {
      return "";
    }
    return SCHEME_ID_MAP.get(submissionJsonData.getSchemeId());
  }
}
