package uk.gov.justice.laa.fee.scheme.repository;

import java.time.LocalDate;
import java.util.Optional;
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
        SELECT fs
        FROM FeeEntity f
        JOIN f.feeSchemeCode fs
        WHERE f.feeCode = :feeCode
          AND fs.validFrom <= :inputDate
          AND (fs.validTo IS NULL OR fs.validTo >= :inputDate)
        ORDER BY fs.validFrom DESC
        """)
  Optional<FeeSchemesEntity> findValidSchemeForDate(
      @Param("feeCode") String feeCode,
      @Param("inputDate") LocalDate inputDate
  );
}

