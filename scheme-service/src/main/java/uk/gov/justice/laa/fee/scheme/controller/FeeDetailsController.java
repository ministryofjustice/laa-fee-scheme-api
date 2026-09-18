package uk.gov.justice.laa.fee.scheme.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.justice.laa.fee.scheme.api.FeeDetailsApi;
import uk.gov.justice.laa.fee.scheme.config.FeatureFlagsConfig;
import uk.gov.justice.laa.fee.scheme.config.features.Feature;
import uk.gov.justice.laa.fee.scheme.exception.FeatureNotEnabledException;
import uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil;
import uk.gov.justice.laa.fee.scheme.model.FeeDetailsResponseV1;
import uk.gov.justice.laa.fee.scheme.model.FeeDetailsResponseV2;
import uk.gov.justice.laa.fee.scheme.service.FeeDetailsService;

/**
 * Controller for getting category of law code and fee details corresponding to fee code.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class FeeDetailsController implements FeeDetailsApi {

  private final FeeDetailsService feeDetailsService;
  private final FeatureFlagsConfig featureFlagsConfig;

  @Override
  public ResponseEntity<FeeDetailsResponseV1> getFeeDetailsV1(String feeCode) {

    log.info("Getting fee details (v1)");
    FeeDetailsResponseV1 feeDetailsV1 = feeDetailsService.getFeeDetailsV1(feeCode);
    log.info("Successfully retrieved fee details (v1)");

    return ResponseEntity.ok(feeDetailsV1);
  }

  @Override
  public ResponseEntity<FeeDetailsResponseV2> getFeeDetailsV2(String feeCode) {
    log.info("Getting fee details (v2)");

    // Reject requests for Inquest fee scheme category fee codes when the Inquest feature is disabled
    rejectDisabledInquestFeeCode(feeCode);
    FeeDetailsResponseV2 feeDetailsV2 = feeDetailsService.getFeeDetailsV2(feeCode);
    log.info("Successfully retrieved fee details (v2)");

    return ResponseEntity.ok(feeDetailsV2);
  }

  /**
   * Rejects requests for Inquest fee scheme category fee codes when the Inquest feature
   * is disabled, before the service layer is invoked.
   */
  private void rejectDisabledInquestFeeCode(String feeCode) {
    if (FeeCalculationUtil.isInquestFeeCode(feeCode) && !featureFlagsConfig.isEnabled(Feature.INQUEST)) {
      throw new FeatureNotEnabledException(Feature.INQUEST);
    }
  }
}
