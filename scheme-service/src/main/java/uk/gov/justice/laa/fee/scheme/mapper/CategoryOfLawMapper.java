package uk.gov.justice.laa.fee.scheme.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.justice.laa.fee.scheme.entity.CategoryOfLawLookUpEntity;
import uk.gov.justice.laa.fee.scheme.model.CategoryOfLawResponse;

/**
 * Mapper class for category of law.
 */
@Mapper(componentModel = "spring")
public interface CategoryOfLawMapper {

  /**
   * Maps the given category of law lookup entity to an item.
   *
   * @param entity the category of law look up entity
   * @return the item
   */
  @Mapping(target = "categoryOfLawCode", source = "categoryCode")
  CategoryOfLawResponse toCategoryOfLawResponse(CategoryOfLawLookUpEntity entity);
}
