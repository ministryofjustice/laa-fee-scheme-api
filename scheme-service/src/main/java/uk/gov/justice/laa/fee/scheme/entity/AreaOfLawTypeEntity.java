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
 *  Entity to hold values returned from area_of_law_type table.
 */

@Entity
@Table(name = "area_of_law_type")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Immutable
public class AreaOfLawTypeEntity {

  @Id
  @Column(name = "area_of_law_type_id", updatable = false, insertable = false)
  private Long id;

  private String code;

  private String description;
}

