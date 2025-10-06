package uk.gov.justice.laa.fee.scheme.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.justice.laa.fee.scheme.mapstruct.SubmissionDataDto;
import uk.gov.justice.laa.fee.scheme.mapstruct.SubmissionJsonData;
import uk.gov.justice.laa.fee.scheme.service.SubmissionDataService;


/**
 * Controller
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class GetSubmissionData  {

  private final SubmissionDataService submissionDataService;

  @PostMapping("/api/v1/submission-data")
  public ResponseEntity<List<SubmissionDataDto>> getSubmissionData(
      @RequestBody List<SubmissionJsonData> submissionJsonDataList) {

    System.out.println(submissionJsonDataList);

    // Map to DTO using MapStruct
    List<SubmissionDataDto> dtoList = submissionDataService.generateExcel(submissionJsonDataList);
    return ResponseEntity.ok(dtoList);
  }

}