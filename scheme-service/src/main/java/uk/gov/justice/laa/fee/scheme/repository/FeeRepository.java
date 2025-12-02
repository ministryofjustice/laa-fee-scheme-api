package uk.gov.justice.laa.fee.scheme.repository;

import java.util.List;
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
  List<FeeEntity> findByFeeCode(String feeCode);

}
