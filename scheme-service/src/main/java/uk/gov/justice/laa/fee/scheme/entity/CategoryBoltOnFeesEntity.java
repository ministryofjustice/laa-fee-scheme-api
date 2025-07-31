package uk.gov.justice.laa.fee.scheme.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * The entity class for fees.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "category_bolt_on_fees")
public class CategoryBoltOnFeesEntity {
  @Id
  private Long categoryBoltOnFeesId;
  private String categoryCode;
  private BigDecimal hoInterviewBoltOn;
  private BigDecimal oralCmrhBoltOn;
  private BigDecimal telephoneCmrhBoltOn;
  private BigDecimal substantiveHearingBoltOn;
  private BigDecimal adjornHearing_bolt_on;
  private String region;
}
