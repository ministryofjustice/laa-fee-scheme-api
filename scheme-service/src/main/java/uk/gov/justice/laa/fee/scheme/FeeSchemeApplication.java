package uk.gov.justice.laa.fee.scheme;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Entry point for Fee-Scheme application.
 */
@SpringBootApplication
@EnableAsync
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
