package uk.gov.justice.laa.fee.scheme.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.gov.justice.laa.fee.scheme.entity.CategoryOfLawLookUpEntity;
import uk.gov.justice.laa.fee.scheme.repository.projection.FeeDetailsProjection;

/**
 * Repository for category of law and Fee details look up entities.
 */
@Repository
public interface FeeDetailsLookUpRepository extends JpaRepository<CategoryOfLawLookUpEntity, Long> {

  @Query("""
      SELECT categoryLookup.categoryCode AS categoryCode,
             fee.description AS description,
             fee.feeType AS feeType
      FROM CategoryOfLawLookUpEntity categoryLookup
      JOIN FeeEntity fee ON fee.feeCode = categoryLookup.feeCode
      WHERE categoryLookup.feeCode = :feeCode
      """)
  Optional<FeeDetailsProjection> findFeeCategoryInfoByFeeCode(@Param("feeCode") String feeCode);
}