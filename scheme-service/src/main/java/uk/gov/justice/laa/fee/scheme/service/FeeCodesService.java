package uk.gov.justice.laa.fee.scheme.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.fee.scheme.exception.AreaOfLawNotFoundException;
import uk.gov.justice.laa.fee.scheme.model.FeeCodeDetailsV1;
import uk.gov.justice.laa.fee.scheme.model.FeeCodesResponseV1;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for retrieving fee codes based on area of law.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class FeeCodesService {


    /**
     * Get category of fee code, law code, fee type, fee code description, area of law for all fee codes within an area of law.
     *
     * @param areaOfLaw the area of law
     * @return the fee codes response (v1)
     * @throws AreaOfLawNotFoundException if the area of law is not found
     */
    public FeeCodesResponseV1 getFeeCodesV1(String areaOfLaw) {
        log.info("Get fee codes (v1)");

        List<FeeCodeDetailsV1> feeCodes = new ArrayList<>();

        return FeeCodesResponseV1.builder().feeCodes(feeCodes).build();
    }
}
