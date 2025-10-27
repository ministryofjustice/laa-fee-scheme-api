package uk.gov.justice.laa.fee.scheme.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import uk.gov.justice.laa.fee.scheme.enums.AreaOfLawType;
import uk.gov.justice.laa.fee.scheme.enums.CaseType;

/**
 *  Entity to hold values returned from area_of_law_type table.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Immutable
@Table(name = "area_of_law_type")
public class AreaOfLawTypeEntity {

  @Id
  @Column(name = "area_of_law_type_id", updatable = false, insertable = false)
  private Long id;

  @Enumerated(EnumType.STRING)
  private AreaOfLawType code;

  @Enumerated(EnumType.STRING)
  private CaseType caseType;

  private String description;
}

