package uk.gov.justice.laa.fee.scheme.exception;

import lombok.Getter;
import uk.gov.justice.laa.fee.scheme.enums.ValidationError;

/**
 * Exception for when a validation error occurs.
 */
@Getter
public class ValidationException extends RuntimeException  {
  private final ValidationError error;
  private final FeeContext context;

  /**
   * Constructor for ValidationException.
   *
   * @param error the validation error
   * @param context the context
   */
  public ValidationException(ValidationError error, FeeContext context) {
    super(error.getErrorMessage());
    this.error = error;
    this.context = context;
  }
}
