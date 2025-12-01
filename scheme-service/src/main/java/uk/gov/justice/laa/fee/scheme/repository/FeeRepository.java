package uk.gov.justice.laa.fee.scheme.repository;

import java.util.List;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;

/**
 * Repository for fee entities.
 */
@Repository
public interface FeeRepository extends JpaRepository<FeeEntity, Long> {

  @EntityGraph(attributePaths = {
      "feeInformation",
      "feeScheme"
  })
  @Cacheable(cacheNames = "feeEntities", key = "#feeCode")
  List<FeeEntity> findByFeeCode(String feeCode);

}
