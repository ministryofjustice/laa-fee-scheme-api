package uk.gov.justice.laa.fee.scheme.repository;

import java.util.List;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.gov.justice.laa.fee.scheme.entity.PoliceStationFeesEntity;

/**
 * Repository for police station fees entities.
 */
@Repository
public interface PoliceStationFeesRepository extends JpaRepository<PoliceStationFeesEntity, Long> {

  @Cacheable(cacheNames = "policeStationFees", key = "#psSchemeId + '-' + #feeSchemeCode")
  List<PoliceStationFeesEntity> findPoliceStationFeeByPsSchemeIdAndFeeSchemeCode(String psSchemeId, String feeSchemeCode);

  @Query("""
      SELECT policeStationFee
      FROM PoliceStationFeesEntity policeStationFee
           JOIN PoliceStationsEntity policeStation
      ON policeStationFee.psSchemeId = policeStation.psSchemeId
      WHERE policeStation.policeStationId = :policeStationId
        AND policeStationFee.feeSchemeCode = :feeSchemeCode
      """)
  @Cacheable(cacheNames = "policeStationFees", key = "#policeStationId + '-' + #feeSchemeCode")
  List<PoliceStationFeesEntity> findPoliceStationFeeByPoliceStationIdAndFeeSchemeCode(@Param("policeStationId") String policeStationId,
                                                                              @Param("feeSchemeCode") String feeSchemeCode);
}
