package uk.gov.justice.laa.fee.scheme.service;

import static uk.gov.justice.laa.fee.scheme.mapstruct.convertToExcelCrimeLower.writeExcelCrimeLower;
import static uk.gov.justice.laa.fee.scheme.mapstruct.convertToExcelLegalHelp.writeExcelLegalHelp;

import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.fee.scheme.mapstruct.SubmissionDataDto;
import uk.gov.justice.laa.fee.scheme.mapstruct.SubmissionDataMapperInterface;
import uk.gov.justice.laa.fee.scheme.mapstruct.SubmissionJsonData;

@Service
@Slf4j
public class SubmissionDataService {

  public List<SubmissionDataDto> generateExcel(List<SubmissionJsonData> submissionJsonDataList) throws IOException {

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
      writeExcelLegalHelp("submission_data_legal_help.xlsx", legalHelpDto);
    }

    if (!crimeDto.isEmpty()) {
      writeExcelCrimeLower("submission_data_crime_lower.xlsx", crimeDto);
    }

    return submissionJsonDataList.stream()
        .map(SubmissionDataMapperInterface.INSTANCE::toDto)
        .toList();
  }
}


