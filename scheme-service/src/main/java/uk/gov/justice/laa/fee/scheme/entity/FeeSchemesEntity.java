package uk.gov.justice.laa.fee.scheme.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The entity class for fee schemes.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "fee_schemes")
public class FeeSchemesEntity {
  @Id
  private String schemeCode;
  private String schemeName;
  private LocalDate validFrom;
  private LocalDate validTo;
  private String feeCodes;
}
