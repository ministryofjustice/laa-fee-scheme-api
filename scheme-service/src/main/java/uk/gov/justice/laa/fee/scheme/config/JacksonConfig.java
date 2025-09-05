package uk.gov.justice.laa.fee.scheme.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
  public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
    return builder -> builder
        .serializationInclusion(JsonInclude.Include.NON_EMPTY)
        .featuresToEnable(JsonParser.Feature.STRICT_DUPLICATE_DETECTION);
  }
}