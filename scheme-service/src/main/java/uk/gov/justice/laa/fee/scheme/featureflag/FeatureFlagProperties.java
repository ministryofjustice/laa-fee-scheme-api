package uk.gov.justice.laa.fee.scheme.featureflag;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Feature flags bound from configuration, keyed by feature name.
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "feature-flags")
public class FeatureFlagProperties {

  private Map<String, Boolean> flags = new HashMap<>();

}
