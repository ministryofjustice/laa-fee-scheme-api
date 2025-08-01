package uk.gov.justice.laa.fee.scheme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.justice.laa.fee.scheme.entity.FeeSchemesEntity;

/**
 * Repository for fee schemes entities.
 */
@Repository
public interface FeeSchemesRepository extends JpaRepository<FeeSchemesEntity, String> {
}
