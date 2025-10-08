package uk.gov.justice.laa.fee.scheme.controller;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.gov.justice.laa.fee.scheme.mapstruct.SubmissionJsonData;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestSubmissionDataClient {

  private final RestTemplate restTemplate;

  private static final String BASE_URL = "https://65df234e-f48f-4b3a-896f-a658c4e17a25.mock.pstmn.io";

  public List<SubmissionJsonData> getSubmissionData() {
    String url = BASE_URL + "/submission-data";
    log.info("Get Submission Data from mock claim API");
    ResponseEntity<SubmissionJsonData[]> response = restTemplate.getForEntity(url, SubmissionJsonData[].class);

    SubmissionJsonData[] submissionList = response.getBody();
    return submissionList != null ? Arrays.asList(submissionList) : Collections.emptyList();
  }
}


