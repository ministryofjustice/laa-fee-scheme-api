package uk.gov.justice.laa.fee.scheme.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.justice.laa.fee.scheme.entity.CategoryOfLawLookUpEntity;

/**
 * Repository for category of law look up entities.
 */
@Repository
public interface CategoryOfLawLookUpRepository extends JpaRepository<CategoryOfLawLookUpEntity, Long> {

  Optional<CategoryOfLawLookUpEntity> findByFeeCode(String feeCode);
}
