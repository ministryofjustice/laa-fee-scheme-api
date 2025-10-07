package uk.gov.justice.laa.fee.scheme.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.justice.laa.fee.scheme.service.SubmissionDataService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TestGetExcel  {

  private final SubmissionDataService submissionDataService;

  @PostMapping("/api/v1/generate-excel")
  public ResponseEntity<Void> getSubmissionData() {
    log.info("beginning Generating Excel Submission Data");
    submissionDataService.generateExcel();
    return ResponseEntity.accepted().build();
  }
}