package uk.gov.justice.laa.fee.scheme.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import uk.gov.justice.laa.fee.scheme.enums.FeeType;

/**
 *  Entity to hold values returned from fee_category_mapping table.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Immutable
@Table(name = "fee_category_mapping")
public class FeeCategoryMappingEntity {

  @Id
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "fee_scheme_category_type_id")
  private FeeSchemeCategoryTypeEntity feeSchemeCategoryType;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "category_of_law_type_id")
  private CategoryOfLawTypeEntity categoryOfLawType;


  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "fee_id")
  private FeeEntity fee; // Reference to fee table

  @Transient
  private String feeCode;
  @Transient
  private String description;
  @Transient
  private FeeType feeType;

  /**
   * Getter for fee description.
   */
  public String getFeeDescription() {
    if (description != null) {
      return description;
    }
    return fee != null ? fee.getDescription() : null;
  }

  /**
   * Getter for fee category type.
   */
  public String getFeeCode() {
    if (feeCode != null) {
      return feeCode;
    }
    return fee != null ? fee.getFeeCode() : null;
  }

  /**
   * Getter for fee type.
   */
  public FeeType getFeeType() {
    if (feeType != null) {
      return feeType;
    }
    return fee != null ? fee.getFeeType() : null;
  }



}
