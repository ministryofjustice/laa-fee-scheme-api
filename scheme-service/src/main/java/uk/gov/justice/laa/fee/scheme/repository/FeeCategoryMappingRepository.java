package uk.gov.justice.laa.fee.scheme.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.justice.laa.fee.scheme.entity.FeeCategoryMappingEntity;
import uk.gov.justice.laa.fee.scheme.enums.AreaOfLawType;

/**
 * Repository for category of law and Fee details look up entities.
 */
@Repository
public interface FeeCategoryMappingRepository extends JpaRepository<FeeCategoryMappingEntity, Long> {

  @EntityGraph(attributePaths = {
      "categoryOfLawType",
      "categoryOfLawType.areaOfLawType",
      "categoryOfLawType.areaOfLawType.caseType"
  })
  Optional<FeeCategoryMappingEntity> findByFeeCodeFeeCode(String feeCode);

  @EntityGraph(attributePaths = {
      "feeCode",
      "categoryOfLawType",
      "categoryOfLawType.areaOfLawType",
      "categoryOfLawType.areaOfLawType.caseType"
  })
  List<FeeCategoryMappingEntity> findByCategoryOfLawTypeAreaOfLawTypeCode(AreaOfLawType areaOfLawType);

}