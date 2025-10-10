package uk.gov.justice.laa.fee.scheme.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *   For Area of Law look up purpose.
 */

@Getter
@RequiredArgsConstructor
public enum AreaOfLawType {

  LEGAL_HELP("Legal Help"),
  CRIME_LOWER("Crime Lower"),
  MEDIATION("Mediation");

  private final String displayName;

}
