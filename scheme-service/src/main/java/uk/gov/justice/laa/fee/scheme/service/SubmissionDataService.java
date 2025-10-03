package uk.gov.justice.laa.fee.scheme.service;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.fee.scheme.mapstruct.SubmissionDataDto;
import uk.gov.justice.laa.fee.scheme.mapstruct.SubmissionDataMapperInterface;
import uk.gov.justice.laa.fee.scheme.mapstruct.SubmissionJsonData;

@Service
@Slf4j
public class SubmissionDataService {

  public List<SubmissionDataDto> generateExcel(List<SubmissionJsonData> submissionJsonDataList) {
    return submissionJsonDataList.stream()
        .map(SubmissionDataMapperInterface.INSTANCE::toDto)
        .toList();
  }
}


