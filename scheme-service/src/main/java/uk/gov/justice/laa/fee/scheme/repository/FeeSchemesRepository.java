package uk.gov.justice.laa.fee.scheme.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.gov.justice.laa.fee.scheme.entity.FeeSchemesEntity;

/**
 * Repository for fee schemes entities.
 */
@Repository
public interface FeeSchemesRepository extends JpaRepository<FeeSchemesEntity, String> {

  @Query("""
      SELECT scheme
      FROM FeeEntity fee
           JOIN fee.feeSchemeCode scheme
      WHERE fee.feeCode = :feeCode
        AND scheme.validFrom <= :inputDate
        AND (scheme.validTo IS NULL OR scheme.validTo >= :inputDate)
      ORDER BY scheme.validFrom DESC
      """)
  List<FeeSchemesEntity> findValidSchemeForDate(@Param("feeCode") String feeCode,
                                                @Param("inputDate") LocalDate inputDate,
                                                Pageable pageable);
}