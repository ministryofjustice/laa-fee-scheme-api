package uk.gov.justice.laa.fee.scheme.enums;

import static uk.gov.justice.laa.fee.scheme.enums.AreaOfLawType.CRIME_LOWER;
import static uk.gov.justice.laa.fee.scheme.enums.AreaOfLawType.LEGAL_HELP;
import static uk.gov.justice.laa.fee.scheme.enums.AreaOfLawType.MEDIATION;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *   For Category of Law look up purpose.
 */

@Getter
@RequiredArgsConstructor
public enum CategoryOfLawType {
  CRIME("Crime", CRIME_LOWER),
  ALL("All Classes", CRIME_LOWER),
  INVEST("Criminal Investigations and Criminal Proceedings", CRIME_LOWER),
  PRISON("Prison Law", CRIME_LOWER),
  APPEALS("Appeals and Reviews", CRIME_LOWER),

  MEDI("Mediation", MEDIATION),

  MAT("Family", LEGAL_HELP),
  DISC("Discrimination", LEGAL_HELP),
  PUB("Public Law", LEGAL_HELP),
  MED("Clinical Negligence", LEGAL_HELP),
  IMMAS("Immigration - Asylum", LEGAL_HELP),
  CON("Consumer General Contract", LEGAL_HELP),
  HOU("Housing", LEGAL_HELP),
  ELA("Early Legal Advice", LEGAL_HELP),
  AAP("Claims Against Public Authorities", LEGAL_HELP),
  WB("Welfare Benefits", LEGAL_HELP),
  EDU("Education", LEGAL_HELP),
  COM("Community Care", LEGAL_HELP),
  DEB("Debt", LEGAL_HELP),
  IMMOT("Immigration", LEGAL_HELP),
  MHE("Mental Health", LEGAL_HELP),
  EMP("Employment", LEGAL_HELP),
  MSC("Residual (Miscellaneous)", LEGAL_HELP),
  PI("Personal Injury", LEGAL_HELP),
  RESIDUAL("Residual List", LEGAL_HELP);

  private final String displayName;
  private final AreaOfLawType areaOfLawType;


}

