package uk.gov.justice.laa.fee.scheme.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The entity class for category of law look up.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "category_of_law_look_up")
public class CategoryOfLawLookUpEntity {
  @Id
  private Long categoryOfLawLookUpId;
  private String categoryCode;
  private String fullDescription;
  private String areaOfLaw;
  private String feeCode;
}
