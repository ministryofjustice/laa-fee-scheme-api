package uk.gov.justice.laa.fee.scheme.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

/**
 *  Entity to hold values returned from category_of_law_type table.
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Immutable
@Table(name = "category_of_law_type")
public class CategoryOfLawTypeEntity {

  @Id
  @Column(name = "category_of_law_type_id", updatable = false, insertable = false)
  private Long id;

  private String code;

  private String description;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "area_of_law_type_id", nullable = false, updatable = false, insertable = false)
  private AreaOfLawTypeEntity areaOfLawType;
}

