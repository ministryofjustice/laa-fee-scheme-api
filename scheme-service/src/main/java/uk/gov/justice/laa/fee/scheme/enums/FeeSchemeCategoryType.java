package uk.gov.justice.laa.fee.scheme.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * For Fee Scheme Category Look up purpose.
 */

@Getter
@RequiredArgsConstructor
public enum FeeSchemeCategoryType {

  IMMIGRATION_ASYLUM("Immigration & Asylum"),
  MENTAL_HEALTH("Mental Health"),
  FAMILY("Family"),
  MEDIATION("Mediation"),
  COMMUNITY_CARE("Community Care"),
  CLAIMS_AGAINST_PUBLIC_AUTHORITIES("Claims Against Public Authorities"),
  CLINICAL_NEGLIGENCE("Clinical Negligence"),
  DEBT("Debt"),
  DISCRIMINATION("Discrimination"),
  EDUCATION("Education"),
  HOUSING_HLPAS("Housing - HLPAS"),
  HOUSING("Housing"),
  MISCELLANEOUS("Miscellaneous"),
  PUBLIC_LAW("Public Law"),
  WELFARE_BENEFITS("Welfare Benefits"),
  POLICE_STATION("Police Station"),
  POLICE_OTHER("Police Other"),
  MAGISTRATES_YOUTH_COURT("Magistrates & Youth Court"),
  PRISON_LAW("Prison Law"),
  ADVOCACY_ASSISTANCE("Advocacy Assistance"),
  ASSOCIATED_CIVIL("Associated Civil");

  private final String displayName;

}
