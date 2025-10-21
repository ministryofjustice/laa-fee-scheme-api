package uk.gov.justice.laa.fee.scheme.exception;

import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import uk.gov.justice.laa.fee.scheme.enums.ErrorCode;

/**
 * Exception for when a validation error occurs.
 */
@Getter
public class ValidationException extends RuntimeException  {
  private final ErrorCode error;
  private final FeeContext context;

  /**
   * Constructor for ValidationException.
   *
   * @param error the validation error
   * @param context the context
   */
  public ValidationException(ErrorCode error, FeeContext context) {
    super(error.getCode() + " - " + error.getMessage());
    this.error = error;
    this.context = context;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("error", error)
        .append("context", context)
        .toString();
  }
}
