package uk.gov.justice.laa.fee.scheme.service;

import java.util.Set;
import java.util.regex.Pattern;

/**
 * Constants for Fee codes.
 */
public final class FeeCodeConstants {

  private FeeCodeConstants() {}

  public static final Set<String> FEE_CODE_PROH_TYPE =
      Set.of("PROH", "PROH1", "PROH2");

  public static final String FEE_CODE_PROD = "PROD";

}
