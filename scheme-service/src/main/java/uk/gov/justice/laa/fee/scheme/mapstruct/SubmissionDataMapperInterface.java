package uk.gov.justice.laa.fee.scheme.mapstruct;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SubmissionDataMapperInterface {
  SubmissionDataMapperInterface INSTANCE = Mappers.getMapper(SubmissionDataMapperInterface.class);

  @Mapping(source = "feeCalculation.totalAmount", target = "totalAmount")
  SubmissionDataDto toDto(SubmissionJsonData submissionJsonData);
}
