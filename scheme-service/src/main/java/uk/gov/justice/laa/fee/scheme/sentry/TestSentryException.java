package uk.gov.justice.laa.fee.scheme.sentry;

/**
 * Test exception to trigger Sentry alert. (To test Sentry integration only, will be removed later)
 */
public class TestSentryException extends RuntimeException {
  /**
   * Constructor for TestSentryException.
   *
   * @param message the message
   */
  public TestSentryException(String message) {
    super(message);
  }
}

