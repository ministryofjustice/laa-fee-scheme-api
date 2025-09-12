package uk.gov.justice.laa.fee.scheme.controller;


import static org.junit.Assert.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ErrorLogSentryController.class)
class ErrorLogSentryControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void captureErrorLogSentry_ShouldThrowRuntimeException() {
    ErrorLogSentryController controller = new ErrorLogSentryController();

    RuntimeException ex = assertThrows(RuntimeException.class,
        controller::captureErrorLogSentry);

    // Optionally verify exception message
    org.junit.jupiter.api.Assertions.assertEquals(
        "Testing Sentry integration in Fee Scheme Application",
        ex.getMessage()
    );
  }

}