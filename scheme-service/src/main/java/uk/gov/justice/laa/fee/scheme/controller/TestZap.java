package uk.gov.justice.laa.fee.scheme.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller.
 */
public class TestZap {

  @GetMapping("/zap-test")
  public String zapTest(@RequestParam String input) {
    return "<html><body>" + input + "</body></html>";
  }
}
