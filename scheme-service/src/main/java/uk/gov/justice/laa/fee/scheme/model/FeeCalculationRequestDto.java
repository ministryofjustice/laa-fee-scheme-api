package uk.gov.justice.laa.fee.scheme.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class FeeCalculationRequestDto {

    // I&A initial model
    private String feeCode;
    private LocalDate startDate;
    private BigDecimal netProfitCosts;
    private BigDecimal netDisbursementAmount;
    private BigDecimal netCostOfCounsel; //hourly only
    private BigDecimal disbursementVatAmount;
    private boolean vatIndicator;
    private String disbursementPriorAuthority;
    private int boltOnAdjournedHearing;
    private int boltOnDetentionTravelWaitingCosts;
    private int boltOnJrFormFilling;
    private int boltOnCmrhOral;
    private int boltOnCrmhTelephone;
}
