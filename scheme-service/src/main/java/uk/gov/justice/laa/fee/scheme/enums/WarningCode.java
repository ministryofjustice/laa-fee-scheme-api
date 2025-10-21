package uk.gov.justice.laa.fee.scheme.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * For warning codes and messages.
 */

@Getter
@RequiredArgsConstructor
public enum WarningCode {

  WARCRM1("WARCRM1", "Cost not included. Travel costs cannot be claimed with Fee Code used."),
  WARCRM2("WARCRM2", "Cost not included. Waiting costs cannot be claimed with Fee Code used.");

  private final String code;
  private final String message;
}
