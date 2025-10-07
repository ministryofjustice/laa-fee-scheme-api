package uk.gov.justice.laa.fee.scheme.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.fee.scheme.excelwriter.ConvertToExcelCrimeLower;
import uk.gov.justice.laa.fee.scheme.excelwriter.ConvertToExcelLegalHelp;
import uk.gov.justice.laa.fee.scheme.mapstruct.SubmissionDataDto;
import uk.gov.justice.laa.fee.scheme.mapstruct.SubmissionDataMapperInterface;
import uk.gov.justice.laa.fee.scheme.mapstruct.SubmissionJsonData;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubmissionDataService {

  private final ConvertToExcelLegalHelp legalHelpWriter;
  private final ConvertToExcelCrimeLower crimeLowerWriter;

  public List<SubmissionDataDto> generateExcel(List<SubmissionJsonData> submissionJsonDataList) {

    List<SubmissionJsonData> legalHelpList = submissionJsonDataList.stream()
        .filter(submission -> "legalHelp".equalsIgnoreCase(submission.getAreaOfLaw()))
        .toList();

    List<SubmissionJsonData> crimeLowerList = submissionJsonDataList.stream()
        .filter(submission -> "crimeLower".equalsIgnoreCase(submission.getAreaOfLaw()))
        .toList();

    List<SubmissionDataDto> legalHelpDto = legalHelpList.stream()
        .map(SubmissionDataMapperInterface.INSTANCE::toDto)
        .toList();

    List<SubmissionDataDto> crimeDto = crimeLowerList.stream()
        .map(SubmissionDataMapperInterface.INSTANCE::toDto)
        .toList();

    if (!legalHelpDto.isEmpty()) {
      legalHelpWriter.writeExcel(legalHelpDto);
    }

    if (!crimeDto.isEmpty()) {
      crimeLowerWriter.writeExcel(crimeDto);
    }

    return submissionJsonDataList.stream()
        .map(SubmissionDataMapperInterface.INSTANCE::toDto)
        .toList();
  }
}


