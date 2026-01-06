package uk.gov.justice.laa.fee.scheme.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@TestConfiguration
public class FeeSchemeTestConfig {

  @Bean
  TestRestTemplate testRestTemplate() {
    return new TestRestTemplate();
  }

  @Bean
  MockMvc mockMvc(WebApplicationContext context) {
    return MockMvcBuilders.webAppContextSetup(context).build();
  }
}

