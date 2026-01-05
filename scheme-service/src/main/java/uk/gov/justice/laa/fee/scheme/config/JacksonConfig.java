package uk.gov.justice.laa.fee.scheme.config;

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.core.StreamReadFeature;

/**
 * Jackson config.
 */
@Configuration
public class JacksonConfig {

  /**
   * Null values omitted from response.
   * Duplicate fields not permitted in request.
   */
  @Bean
  public JsonMapperBuilderCustomizer jsonCustomizer() {
    return builder ->
        builder.configure(StreamReadFeature.STRICT_DUPLICATE_DETECTION, true);
  }
}