package uk.gov.justice.laa.fee.scheme.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The entity class for legal help categories.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "legal_help_categories")
public class LegalHelpCategoriesEntity {
  @Id
  private Long legalHelpCategoriesId;
  private String categoryCode;
  private String fullDescription;
  private String areaOfLaw;
}
