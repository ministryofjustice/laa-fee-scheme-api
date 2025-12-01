package uk.gov.justice.laa.fee.scheme;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Entry point for Fee-Scheme application.
 */
@SpringBootApplication
@EnableCaching
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
