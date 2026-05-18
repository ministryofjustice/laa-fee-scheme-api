package uk.gov.justice.laa.fee.scheme.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.justice.laa.fee.scheme.api.FeeCodesApi;
import uk.gov.justice.laa.fee.scheme.model.FeeCodesResponseV1;
import uk.gov.justice.laa.fee.scheme.model.FeeDetailsResponseV1;

/**
 * Controller for getting fee codes and their details corresponding to area of law.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class FeeCodesController implements FeeCodesApi {

    @Override
    public ResponseEntity<FeeCodesResponseV1> getFeeCodesV1(String areaOfLaw) {
        log.info("Getting fee codes (v1)");
//        service call here
        log.info("Successfully retrieved fee codes (v1)");
//        return the fee codes and their details
        return null;
    }

}
