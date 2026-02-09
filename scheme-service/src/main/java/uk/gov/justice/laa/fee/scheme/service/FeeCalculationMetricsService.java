package uk.gov.justice.laa.fee.scheme.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.fee.scheme.enums.CaseType;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.enums.ErrorType;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeeCalculationMetricsService {

    private final MeterRegistry meterRegistry;
    
    private static final String METRIC_NAME = "fee_calculation_requests_total";
    private static final String CATEGORY_TAG = "category";
    private static final String CASE_TYPE_TAG = "case_type";
    private static final String STATUS_TAG = "status";

    private static final String VALIDATION_ERROR_METRIC =
        "fee_calculation_validation_errors_total";
    private static final String ERROR_CODE_TAG = "error_code";


    public void recordSuccessfulCalculation(CategoryType category, CaseType caseType) {
        Counter.builder(METRIC_NAME)
                .tag(CATEGORY_TAG, category.name())
                .tag(CASE_TYPE_TAG, caseType.name())
                .tag(STATUS_TAG, "success")
                .register(meterRegistry)
                .increment();
    }

    public void recordValidationError(ErrorType error) {
        Counter.builder(VALIDATION_ERROR_METRIC)
            .tag(ERROR_CODE_TAG, error.getCode())
            .tag("error_name", error.getMessage())
            .register(meterRegistry)
            .increment();
    }

    public void recordValidationError(String error) {
        Counter.builder(VALIDATION_ERROR_METRIC)
            .tag(ERROR_CODE_TAG, error)
            .tag("error_name", error)
            .register(meterRegistry)
            .increment();
    }

}
