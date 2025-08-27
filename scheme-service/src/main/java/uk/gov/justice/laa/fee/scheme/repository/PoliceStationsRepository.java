package uk.gov.justice.laa.fee.scheme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.justice.laa.fee.scheme.entity.PoliceStationsEntity;

/**
 * Repository for police station entities.
 */
@Repository
public interface PoliceStationsRepository extends JpaRepository<PoliceStationsEntity, Long> {
}
