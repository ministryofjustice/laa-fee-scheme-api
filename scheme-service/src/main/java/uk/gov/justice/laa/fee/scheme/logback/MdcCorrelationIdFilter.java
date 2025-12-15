package uk.gov.justice.laa.fee.scheme.logback;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Populate correlationId in MDC.
 */
@Component
public class MdcCorrelationIdFilter extends OncePerRequestFilter {

  public static final String CORRELATION_ID = "correlationId";

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain
  ) throws ServletException, IOException {

    String correlationId = UUID.randomUUID().toString();
    MDC.put(CORRELATION_ID, correlationId);

    try {
      filterChain.doFilter(request, response);
    } finally {
      MDC.clear();
    }
  }
}

