package uk.gov.justice.laa.fee.scheme.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.enums.FeeType;
import uk.gov.justice.laa.fee.scheme.enums.Region;

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
  @ManyToOne
  @JoinColumn(name = "fee_scheme_code", referencedColumnName = "scheme_code")
  private FeeSchemesEntity feeSchemeCode;
  private BigDecimal fixedFee;
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
  private BigDecimal mediationFeeLower;
  private BigDecimal mediationFeeHigher;
  @Enumerated(EnumType.STRING)
  private Region region;
  @Enumerated(EnumType.STRING)
  private CategoryType categoryType;
  @Enumerated(EnumType.STRING)
  private FeeType feeType;
}
