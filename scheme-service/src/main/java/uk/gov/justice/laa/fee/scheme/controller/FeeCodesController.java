package uk.gov.justice.laa.fee.scheme.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.justice.laa.fee.scheme.api.FeeCodesApi;
import uk.gov.justice.laa.fee.scheme.config.FeatureFlagsConfig;
import uk.gov.justice.laa.fee.scheme.config.features.Feature;
import uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil;
import uk.gov.justice.laa.fee.scheme.model.FeeCodeDetailsV1;
import uk.gov.justice.laa.fee.scheme.model.FeeCodesResponseV1;
import uk.gov.justice.laa.fee.scheme.service.FeeCodesService;

/** Controller for getting fee codes and their details corresponding to area of law. */
@Slf4j
@RestController
@RequiredArgsConstructor
public class FeeCodesController implements FeeCodesApi {

  private final FeeCodesService feeCodesService;
  private final FeatureFlagsConfig featureFlagsConfig;

  @Override
  public ResponseEntity<FeeCodesResponseV1> getFeeCodesV1(String areaOfLaw) {
    log.info("Getting fee codes (v1)");
    FeeCodesResponseV1 feeCodesResponseV1 = feeCodesService.getFeeCodesV1(areaOfLaw);

    if (!featureFlagsConfig.isEnabled(Feature.INQUEST)) {
      feeCodesResponseV1.setFeeCodes(excludeInquestFeeCodes(feeCodesResponseV1.getFeeCodes()));
    }

    log.info("Successfully retrieved fee codes (v1)");

    return ResponseEntity.ok(feeCodesResponseV1);
  }

  /** Filters out fee codes belonging to the Inquest fee scheme category. */
  private List<FeeCodeDetailsV1> excludeInquestFeeCodes(List<FeeCodeDetailsV1> feeCodes) {
    return feeCodes.stream()
        .filter(feeCode -> !FeeCalculationUtil.isInquestFeeCode(feeCode.getFeeCode()))
        .toList();
  }
}
