package uk.gov.justice.laa.fee.scheme.service;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Constants for Fee codes.
 */
public final class FeeCodeConstants {

  private FeeCodeConstants() {}

  public static final List<String> FEE_CODE_PROH_TYPE =
      List.of("PROH", "PROH1", "PROH2");

  public static final String FEE_CODE_PROD = "PROD";

  public static final Pattern REP_ORDER_DATE_PATTERN = Pattern.compile(
      "^(PRO[EFKLV][1-4]|PROJ[1-8]|YOU[EFXKLY][1-4]|APP[AB]|PROW)$");

}
