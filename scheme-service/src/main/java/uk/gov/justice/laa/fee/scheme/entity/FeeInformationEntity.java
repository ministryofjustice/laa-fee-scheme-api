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
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.enums.FeeType;

/**
 *  Entity to hold fee information values from fee_information table.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "fee_information")
public class FeeInformationEntity {

  @Id
  @Column(name = "fee_code", length = 10)
  private String feeCode;

  @Column(name = "fee_description", nullable = false)
  private String feeDescription;

  @Column(name = "fee_type", length = 15, nullable = false)
  @Enumerated(EnumType.STRING)
  private FeeType feeType;

  @Column(name = "category_type", nullable = false)
  @Enumerated(EnumType.STRING)
  private CategoryType categoryType;

}