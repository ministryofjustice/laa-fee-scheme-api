package uk.gov.justice.laa.fee.scheme.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The entity class for police station fees.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "police_station_fees")
public class PoliceStationFeesEntity {
  @Id
  private Long policeStationFeesId;
  private String criminalJusticeArea;
  private String psSchemeName;
  private String psSchemeId;
  private BigDecimal fixedFee;
  private BigDecimal escapeThreshold;
  private String feeSchemeCode;

}
