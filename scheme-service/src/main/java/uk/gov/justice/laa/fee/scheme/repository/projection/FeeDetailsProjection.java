package uk.gov.justice.laa.fee.scheme.repository.projection;

/**
 * Projection class for Fee details repository.
 */
public interface FeeDetailsProjection {

  String getCategoryCode();

  String getDescription();

  String getFeeType();
}