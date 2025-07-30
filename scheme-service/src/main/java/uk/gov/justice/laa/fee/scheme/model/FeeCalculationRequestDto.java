package uk.gov.justice.laa.fee.scheme.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

/**
 * model for request date that will be used in fee calculation.
 * Currently, has civil model (I&A and Mental health).
 * Crime to be added.
 */
@Data
public class FeeCalculationRequestDto {

  // Civil initial model
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
  private int boltOnAdditionalTravel; // Mental health only
  // crime specific
  private BigDecimal netTravelCosts;
  private BigDecimal netWaitingCosts;
  private LocalDate caseConcludedDate;
  private String policeCourtOrPrisonId;
  private String dutySolicitor;
  private String schemeId;
}
