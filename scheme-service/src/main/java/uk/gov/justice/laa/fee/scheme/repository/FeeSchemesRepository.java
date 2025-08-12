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

  @Query(value = """
      SELECT *
      FROM fee_scheme.fee_schemes
      WHERE :feeCode = ANY(fee_codes)
        AND valid_from <= :inputDate
        AND (valid_to IS NULL OR valid_to >= :inputDate)
      ORDER BY valid_from DESC
      LIMIT 1
      """, nativeQuery = true)
  Optional<FeeSchemesEntity> findValidSchemeForDate(@Param("feeCode") String feeCode,
                                                    @Param("inputDate") LocalDate inputDate);

}

