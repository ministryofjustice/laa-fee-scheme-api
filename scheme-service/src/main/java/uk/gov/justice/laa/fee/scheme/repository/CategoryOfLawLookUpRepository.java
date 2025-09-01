package uk.gov.justice.laa.fee.scheme.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.gov.justice.laa.fee.scheme.entity.CategoryOfLawLookUpEntity;
import uk.gov.justice.laa.fee.scheme.repository.projection.FeeCategoryProjection;

/**
 * Repository for category of law look up entities.
 */
@Repository
public interface CategoryOfLawLookUpRepository extends JpaRepository<CategoryOfLawLookUpEntity, Long> {

  Optional<CategoryOfLawLookUpEntity> findByFeeCode(String feeCode);

  @Query("""
      SELECT categoryLookup.categoryCode AS categoryCode,
             fee.description AS description,
             fee.feeType AS feeType
      FROM CategoryOfLawLookUpEntity categoryLookup
      JOIN FeeEntity fee ON fee.feeCode = categoryLookup.feeCode
      WHERE categoryLookup.feeCode = :feeCode
      """)
  Optional<FeeCategoryProjection> findFeeCategoryInfoByFeeCode(@Param("feeCode") String feeCode);
}