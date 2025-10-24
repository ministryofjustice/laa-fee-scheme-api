package uk.gov.justice.laa.fee.scheme.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * For Fee Scheme Category Look up purpose.
 */

@Getter
@RequiredArgsConstructor
public enum FeeSchemeCategoryType {

  ADVOCACY_ASSISTANCE("Advocacy Assistance"),
  ASSOCIATED_CIVIL("Associated Civil"),
  CLAIMS_AGAINST_PUBLIC_AUTHORITIES("Claims Against Public Authorities"),
  CLINICAL_NEGLIGENCE("Clinical Negligence"),
  COMMUNITY_CARE("Community Care"),
  DEBT("Debt"),
  DISCRIMINATION("Discrimination"),
  EDUCATION("Education"),
  FAMILY("Family"),
  HOUSING("Housing"),
  HOUSING_HLPAS("Housing - HLPAS"),
  IMMIGRATION_ASYLUM("Immigration & Asylum"),
  MAGISTRATES_YOUTH_COURT("Magistrates & Youth Court"),
  MEDIATION("Mediation"),
  MENTAL_HEALTH("Mental Health"),
  MISCELLANEOUS("Miscellaneous"),
  POLICE_OTHER("Police Other"),
  POLICE_STATION("Police Station"),
  PRISON_LAW("Prison Law"),
  PRE_ORDER_COVER("Pre Order Cover"),
  PUBLIC_LAW("Public Law"),
  WELFARE_BENEFITS("Welfare Benefits");

  private final String displayName;

}
