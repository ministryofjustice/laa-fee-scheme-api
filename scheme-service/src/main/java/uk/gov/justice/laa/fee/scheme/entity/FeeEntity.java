package uk.gov.justice.laa.fee.scheme.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The entity class for fees.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "fee")
public class FeeEntity {
  @Id
  private Long feeId;
  private String feeCode;
  private String description;
  private String feeSchemeCode;
  private BigDecimal totalFee;
  private BigDecimal profitCostLimit;
  private BigDecimal disbursementLimit;
  private BigDecimal escapeThresholdLimit;
  private Boolean priorAuthorityApplicable;
  private Boolean scheduleReference;
  private BigDecimal hoInterviewBoltOn;
  private BigDecimal oralCmrhBoltOn;
  private BigDecimal telephoneCmrhBoltOn;
  private BigDecimal substantiveHearingBoltOn;
  private BigDecimal adjornHearingBoltOn;
  private BigDecimal mediationSessionOne;
  private BigDecimal mediationSessionTwo;
  private String region;
  private String description;
}
