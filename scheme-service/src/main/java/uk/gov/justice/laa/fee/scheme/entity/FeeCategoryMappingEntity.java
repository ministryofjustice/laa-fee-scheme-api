package uk.gov.justice.laa.fee.scheme.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

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

  @OneToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "fee_information", referencedColumnName = "fee_code")
  private FeeInformationEntity feeInformation;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "fee_scheme_category_type_id")
  private FeeSchemeCategoryTypeEntity feeSchemeCategoryType;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "category_of_law_type_id")
  private CategoryOfLawTypeEntity categoryOfLawType;
}
