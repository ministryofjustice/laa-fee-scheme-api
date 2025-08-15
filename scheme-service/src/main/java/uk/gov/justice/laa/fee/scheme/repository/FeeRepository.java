package uk.gov.justice.laa.fee.scheme.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.entity.FeeSchemesEntity;

/**
 * Repository for fee entities.
 */
@Repository
public interface FeeRepository extends JpaRepository<FeeEntity, Long> {
  Optional<FeeEntity> findByFeeCode(String feeCode);

  Optional<FeeEntity> findByFeeCodeAndFeeSchemeCode(String feeCode, FeeSchemesEntity schemeCode);

}
