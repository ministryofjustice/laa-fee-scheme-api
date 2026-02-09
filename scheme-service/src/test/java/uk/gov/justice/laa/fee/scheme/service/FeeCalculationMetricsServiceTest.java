package uk.gov.justice.laa.fee.scheme.service;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.fee.scheme.enums.CaseType;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;

import static org.assertj.core.api.Assertions.assertThat;

class FeeCalculationMetricsServiceTest {

    private MeterRegistry meterRegistry;
    private FeeCalculationMetricsService metricsService;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        metricsService = new FeeCalculationMetricsService(meterRegistry);
    }

    @Test
    void recordSuccessfulCalculation_shouldIncrementCounter() {
        CategoryType category = CategoryType.FAMILY;
        CaseType caseType = CaseType.CIVIL;

        metricsService.recordSuccessfulCalculation(category, caseType);

        assertThat(meterRegistry.get("fee_calculation_requests_total")
                .tag("category", "FAMILY")
                .tag("case_type", "CIVIL")
                .tag("status", "success")
                .counter()
                .count()).isEqualTo(1.0);
    }
}
