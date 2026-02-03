package uk.gov.justice.laa.fee.scheme.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * blah.
 */
public class TestZapFailure {

  @GetMapping("/test-zap-fail")
  public ResponseEntity<String> testZapFail() {
    return ResponseEntity.ok("password=12345");
  }
}
