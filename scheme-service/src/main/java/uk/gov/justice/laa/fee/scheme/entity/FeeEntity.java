package uk.gov.justice.laa.fee.scheme.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.enums.CourtDesignationType;
import uk.gov.justice.laa.fee.scheme.enums.FeeBandType;
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
  @ManyToOne
  @JoinColumn(name = "fee_scheme_code", referencedColumnName = "scheme_code")
  private FeeSchemesEntity feeScheme;
  private BigDecimal fixedFee;
  private BigDecimal profitCostLimit;
  private BigDecimal disbursementLimit;
  private BigDecimal escapeThresholdLimit;
  private BigDecimal totalLimit;
  private BigDecimal upperCostLimit;
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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "fee_code", referencedColumnName = "fee_code", insertable = false, updatable = false)
  private FeeInformationEntity feeInformation;
  @Column(name = "fee_code", nullable = false)
  private String feeCode;
  @Transient
  private String description;
  @Transient
  private CategoryType categoryType;
  @Transient
  private FeeType feeType;
  @Transient
  @Enumerated(EnumType.STRING)
  private FeeBandType feeBandType;
  @Transient
  @Enumerated(EnumType.STRING)
  private CourtDesignationType courtDesignationType;

  /**
   * Getter for fee description.
   */
  public String getDescription() {
    if (description != null) {
      return description;
    }
    return feeInformation != null ? feeInformation.getFeeDescription() : null;
  }

  /**
   * Getter for fee category type.
   */
  public CategoryType getCategoryType() {
    if (categoryType != null) {
      return categoryType;
    }
    return feeInformation != null ? feeInformation.getCategoryType() : null;
  }

  /**
   * Getter for fee type.
   */
  public FeeType getFeeType() {
    if (feeType != null) {
      return feeType;
    }
    return feeInformation != null ? feeInformation.getFeeType() : null;
  }

  /**
   * Getter for feeBandType.
   */
  public FeeBandType getFeeBandType() {
    if (feeBandType != null) {
      return feeBandType;
    }
    return feeInformation != null ? feeInformation.getFeeBandType() : null;
  }

  /**
   * Getter for courtDesignationType.
   */
  public CourtDesignationType getCourtDesignationType() {
    if (feeType != null) {
      return courtDesignationType;
    }
    return feeInformation != null ? feeInformation.getCourtDesignationType() : null;
  }
}
