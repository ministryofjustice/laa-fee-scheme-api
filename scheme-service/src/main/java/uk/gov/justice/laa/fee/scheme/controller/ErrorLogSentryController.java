package uk.gov.justice.laa.fee.scheme.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.justice.laa.fee.scheme.api.ErrorLogSentryApi;

/**
 * Controller to test Sentry Integration.
 */
@RestController
@RequiredArgsConstructor
public class ErrorLogSentryController implements ErrorLogSentryApi {

  public ResponseEntity<Void> captureErrorLogSentry() {
    throw new RuntimeException("Testing Sentry integration in Fee Scheme Application");
  }

}