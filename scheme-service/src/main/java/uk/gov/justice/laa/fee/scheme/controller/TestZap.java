package uk.gov.justice.laa.fee.scheme.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller.
 */
public class TestZap {

  @GetMapping("/zap-test")
  public ResponseEntity<String> zapTest() {
    return ResponseEntity.ok("password=12345");
  }
}
