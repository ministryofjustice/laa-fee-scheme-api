package uk.gov.justice.laa.fee.scheme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;

import java.util.List;

public interface FeeRepository extends JpaRepository<FeeEntity, String> {
  List<FeeEntity> getByFeeCode(String feeCode);
}
