package uk.gov.justice.laa.fee.scheme.config.features;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import uk.gov.justice.laa.fee.scheme.annotations.RequiresFeatureFlag;
import uk.gov.justice.laa.fee.scheme.config.FeatureFlagsConfig;

/** Enforces method-level feature requirements, falling back to controller-level requirements. */
@Component
@ConditionalOnBean(FeatureFlagsConfig.class)
@RequiredArgsConstructor
public class FeatureFlagInterceptor implements HandlerInterceptor {

  private final FeatureFlagsConfig featureFlagsConfig;

  @Override
  public boolean preHandle(
      HttpServletRequest request, HttpServletResponse response, Object handler) {
    if (!(handler instanceof HandlerMethod handlerMethod)) {
      return true;
    }
    RequiresFeatureFlag annotation = handlerMethod.getMethodAnnotation(RequiresFeatureFlag.class);
    if (annotation == null) {
      annotation = handlerMethod.getBeanType().getAnnotation(RequiresFeatureFlag.class);
    }
    if (annotation != null) {
      featureFlagsConfig.checkEnabled(annotation.value());
    }
    return true;
  }
}
