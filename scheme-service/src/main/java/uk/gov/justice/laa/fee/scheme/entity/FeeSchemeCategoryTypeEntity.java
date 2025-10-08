package uk.gov.justice.laa.fee.scheme.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

/**
 *  Entity to hold values returned from fee_scheme_category_type table.
 */

@Entity
@Table(name = "fee_scheme_category_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Immutable
public class FeeSchemeCategoryTypeEntity {

  @Id
  @Column(name = "fee_scheme_category_type_id")
  private Long id;

  @Column(name = "fee_scheme_category_name")
  private String name;
}
