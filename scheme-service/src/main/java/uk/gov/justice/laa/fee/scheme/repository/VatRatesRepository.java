package uk.gov.justice.laa.fee.scheme.repository;

import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.justice.laa.fee.scheme.entity.VatRatesEntity;

/**
 * Repository for vat rates entities.
 */
@Repository
public interface VatRatesRepository extends JpaRepository<VatRatesEntity, Long> {
  VatRatesEntity findTopByStartDateLessThanEqualOrderByStartDateDesc(LocalDate date);
}
