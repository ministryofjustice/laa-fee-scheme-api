package uk.gov.justice.laa.fee.scheme.exception;

import lombok.Getter;
import uk.gov.justice.laa.fee.scheme.enums.ErrorType;

/**
 * Exception for when a validation error occurs.
 */
@Getter
public class ValidationException extends RuntimeException  {
  private final transient ErrorType error;
  private final transient FeeContext context;

  /**
   * Constructor for ValidationException.
   *
   * @param error the validation error
   * @param context the context
   */
  public ValidationException(ErrorType error, FeeContext context) {
    super(error.getCode() + " - " + error.getMessage());
    this.error = error;
    this.context = context;
  }
}
