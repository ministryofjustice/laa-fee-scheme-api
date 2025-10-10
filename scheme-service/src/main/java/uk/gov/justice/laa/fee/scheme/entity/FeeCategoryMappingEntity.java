package uk.gov.justice.laa.fee.scheme.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;
import uk.gov.justice.laa.fee.scheme.enums.FeeType;

/**
 *  Entity to hold values returned from fee_category_mapping table.
 */

@Entity
@Table(name = "fee_category_mapping")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Immutable
public class FeeCategoryMappingEntity {

  @Id
  private Long id;

  private String feeCode;

  private String feeDescription;

  @Enumerated(EnumType.STRING)
  private FeeType feeType;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "fee_scheme_category_type_id")
  private FeeSchemeCategoryTypeEntity feeSchemeCategoryType;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "category_of_law_type_id")
  private CategoryOfLawTypeEntity categoryOfLawType;

}
