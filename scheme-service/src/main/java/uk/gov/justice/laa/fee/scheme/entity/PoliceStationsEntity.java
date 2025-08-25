package uk.gov.justice.laa.fee.scheme.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The entity class for police stations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "police_stations")
public class PoliceStationsEntity {
  @Id
  private Long id;
  private String policeStationId;
  private String policeStationName;
  private String psSchemeId;
  private String psSchemeName;
}
