package uk.gov.justice.laa.fee.scheme.logback;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

class MdcCorrelationIdFilterTest {

  private MdcCorrelationIdFilter filter;
  private HttpServletRequest request;
  private HttpServletResponse response;
  private FilterChain filterChain;

  @BeforeEach
  void setUp() {
    filter = new MdcCorrelationIdFilter();
    request = mock(HttpServletRequest.class);
    response = mock(HttpServletResponse.class);
    filterChain = mock(FilterChain.class);
    MDC.clear();
  }
  @Test
  void shouldGenerateCorrelationId() throws ServletException, IOException {
    filter.doFilterInternal(request, response, filterChain);
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void mdcIsClearedAfterFilter() throws ServletException, IOException {
    filter.doFilterInternal(request, response, filterChain);
    assertThat(MDC.get(MdcCorrelationIdFilter.CORRELATION_ID)).isNull();
  }
}