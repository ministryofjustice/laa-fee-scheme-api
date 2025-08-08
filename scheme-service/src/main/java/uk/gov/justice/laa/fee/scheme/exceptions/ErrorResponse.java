package uk.gov.justice.laa.fee.scheme.exceptions;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

/**
 * Standard error response for failed requests.
 */
@Data
@Builder
@Schema(description = "Standard error response returned for failed requests")
public class ErrorResponse {

  @Schema(description = "Time when the error occurred", example = "2025-08-08T13:15:30")
  private LocalDateTime timestamp;

  @Schema(description = "HTTP status code associated with the error", example = "404")
  private int status;

  @Schema(description = "Description of the HTTP error", example = "Not Found")
  private String error;

  @Schema(description = "Description/explanation of the error", example = "Category code not found for fee code: X123")
  private String message;
}
