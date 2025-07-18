package uk.gov.justice.laa.fee.scheme;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for Fee-Scheme application.
 */
@SpringBootApplication
public class FeeSchemeApplication {

  /**
   * The application main method.
   *
   * @param args the application arguments.
   */
  public static void main(String[] args) {
    SpringApplication.run(FeeSchemeApplication.class, args);
  }
}
