package uk.gov.justice.laa.fee.scheme.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.justice.laa.fee.scheme.entity.FeeCategoryMappingEntity;

/**
 * Repository for category of law and Fee details look up entities.
 */
@Repository
public interface FeeCategoryMappingRepository extends JpaRepository<FeeCategoryMappingEntity, Long> {

  Optional<FeeCategoryMappingEntity> findByFeeCodeFeeCode(String feeCode);

}