package uk.gov.justice.laa.fee.scheme.repository.projection;

/**
 * Projection class for category of law repository.
 */
public interface FeeCategoryProjection {

  String getCategoryCode();

  String getDescription();

  String getFeeType();
}