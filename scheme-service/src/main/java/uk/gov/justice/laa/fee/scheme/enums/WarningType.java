package uk.gov.justice.laa.fee.scheme.enums;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * For warning codes and messages.
 */

@RequiredArgsConstructor
@Getter
public enum WarningType {
  WARN_CRIME_TRAVEL_COSTS("WARCRM1", "Cost not included. Travel costs cannot be claimed with Fee Code used.",
      List.of("INVB1", "INVB2", "PROT", "PROU", "PROW", "PRIA", "PRIB1", "PRIB2", "PRIC1", "PRIC2",
          "PRID1", "PRID2", "PRID1", "PRID2", "PRIE1", "PRIE2")),
  WARN_CRIME_WAITING_COSTS("WARCRM2", "Cost not included. Waiting costs cannot be claimed with Fee Code used.",
      List.of("INVB1", "INVB2", "PROT", "PROU", "PROW")),

  WARN_FAMILY_NET_PROFIT_COST("WARFAM1", "The claim exceeds the Escape Case Threshold."
                                         + " An Escape Case Claim must be submitted for further costs to be paid.");

  WarningType(String code, String message) {
    this.code = code;
    this.message = message;
    this.feeCodes = List.of();
  }

  public boolean containsFeeCode(String code) {
    return feeCodes.contains(code);
  }

  private final String code;
  private final String message;
  private final List<String> feeCodes;
}
