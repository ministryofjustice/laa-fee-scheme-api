package uk.gov.justice.laa.fee.scheme.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

/**
 * Calculate fee for a given fee calculation request.
 */
public class VulnerableLogTest {
  private static final Logger logger = (Logger) LogManager.getLogger(VulnerableLogTest.class);

  public void testLogging() {
    logger.info("Testing vulnerable Log4j 2.14.1");
    logger.error("This should trigger CVE-2021-44228 detection");
  }
}