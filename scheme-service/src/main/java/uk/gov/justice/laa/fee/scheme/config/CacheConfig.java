package uk.gov.justice.laa.fee.scheme.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * cache configuration class.
 */
@Configuration
public class CacheConfig {

  /**
   * cache configuration.
   */
  @Bean
  public CaffeineCacheManager cacheManager() {
    CaffeineCacheManager cacheManager = new CaffeineCacheManager(
        "vatRates",
        "feeEntities",
        "feeCategoryMapping",
        "policeStationFees"
    );

    cacheManager.registerCustomCache("vatRates", Caffeine.newBuilder()
        .expireAfterWrite(Duration.ofHours(1))
        .maximumSize(20)
        .build());

    cacheManager.registerCustomCache("feeEntities", Caffeine.newBuilder()
        .expireAfterWrite(Duration.ofHours(1))
        .maximumSize(1000)
        .build());

    cacheManager.registerCustomCache("feeCategoryMapping", Caffeine.newBuilder()
        .expireAfterWrite(Duration.ofHours(1))
        .maximumSize(500)
        .build());

    cacheManager.registerCustomCache("policeStationFees", Caffeine.newBuilder()
        .expireAfterWrite(Duration.ofHours(1))
        .maximumSize(2000)
        .build());

    return cacheManager;
  }
}