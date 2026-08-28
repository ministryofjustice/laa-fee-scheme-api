package uk.gov.justice.laa.fee.scheme.featureflag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Prevents access to controller handlers whose required feature is disabled.
 */
@RequiredArgsConstructor
public class FeatureFlagInterceptor implements HandlerInterceptor {

  private final FeatureFlagService featureFlagService;

  @Override
  public boolean preHandle(
      HttpServletRequest request, HttpServletResponse response, Object handler) {
    if (!(handler instanceof HandlerMethod handlerMethod)) {
      return true;
    }

    RequiresFeatureFlag requiredFeature = findRequiredFeature(handlerMethod);
    if (requiredFeature != null && !featureFlagService.isEnabled(requiredFeature.value())) {
      throw new FeatureDisabledException(requiredFeature.value());
    }

    return true;
  }

  private RequiresFeatureFlag findRequiredFeature(HandlerMethod handlerMethod) {
    RequiresFeatureFlag methodAnnotation = AnnotatedElementUtils.findMergedAnnotation(
        handlerMethod.getMethod(), RequiresFeatureFlag.class);
    if (methodAnnotation != null) {
      return methodAnnotation;
    }

    return AnnotatedElementUtils.findMergedAnnotation(
        handlerMethod.getBeanType(), RequiresFeatureFlag.class);
  }
}
