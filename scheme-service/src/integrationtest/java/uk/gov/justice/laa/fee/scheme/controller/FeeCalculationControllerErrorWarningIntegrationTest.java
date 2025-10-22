package uk.gov.justice.laa.fee.scheme.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.justice.laa.fee.scheme.postgrestestcontainer.PostgresContainerTestBase;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class FeeCalculationControllerErrorWarningIntegrationTest extends PostgresContainerTestBase {

  @Autowired
  private MockMvc mockMvc;

  private static final String AUTH_TOKEN = "int-test-token";
  private static final String URI = "/api/v1/fee-calculation";

  @Nested
  class ImmigrationFixedAndHourlyErrorWarnings {

    @ParameterizedTest
    @CsvSource({
        "IMCF, WARIA1, Costs have been capped at £600 without an Immigration Priority Authority Number. Disbursement costs exceed the Disbursement Limit.",
        "IALB, WARIA2, Costs have been capped at £400 without an Immigration Priority Authority Number. Disbursement costs exceed the Disbursement Limit.",
    })
    void shouldGetFeeCalculationWarnings_immigrationAndAsylumFixedFee(String feeCode, String warningCode, String warningMessage) throws Exception {
      mockMvc
          .perform(post(URI)
              .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                {
                  "feeCode": "%s",
                  "claimId": "claim_123",
                  "startDate": "2024-09-30",
                  "netDisbursementAmount": 600.21,
                  "disbursementVatAmount": 70.12,
                  "vatIndicator": true,
                  "boltOns": {
                        "boltOnAdjournedHearing": 2.00,
                        "boltOnCmrhOral": 1.00,
                        "boltOnCmrhTelephone": 3.00
                  },
                  "detentionTravelAndWaitingCosts": 111.00,
                  "jrFormFilling": 50.00
                }
                """.formatted(feeCode))
              .accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.validationMessages[0].type").value("WARNING"))
          .andExpect(jsonPath("$.validationMessages[0].code").value(warningCode))
          .andExpect(jsonPath("$.validationMessages[0].message").value(warningMessage));
    }

    @ParameterizedTest
    @CsvSource({
        "IMXL, WARIA6, Costs have been capped. The amount entered exceeds the Total Cost Limit. An Immigration Prior Authority number must be entered., 0",
        "IMXL, WARIA7, Costs have been capped without an Immigration Priority Authority Number. Disbursement costs exceed the Disbursement Limit., 1",
        "IMXL, WARIA9, Costs not included. Detention Travel and Waiting costs on hourly rates cases should be reported as Profit Costs., 2",
        "IMXL, WARIA10, Costs have been included. JR/ form filling costs should only be completed for standard fee cases. Hourly rates costs "
            + "should be reported in the Profit Costs., 3",
        "IA100, WARIA8, Costs have been capped. Costs for the Fee Code used cannot exceed £100., 0",
        "IA100, WARIA9, Costs not included. Detention Travel and Waiting costs on hourly rates cases should be reported as Profit Costs., 1",
        "IA100, WARIA10, Costs have been included. JR/ form filling costs should only be completed for standard fee cases. Hourly rates costs "
            + "should be reported in the Profit Costs., 2",
    })
    void shouldGetFeeCalculationWarnings_immigrationAndAsylumHourlyRate_legalHelp(String feeCode, String warningCode,
                                                                                  String warningMessage, int messageIndex) throws Exception {
      mockMvc
          .perform(post(URI)
              .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                {
                  "feeCode": "%s",
                  "claimId": "claim_123",
                  "startDate": "2015-02-11",
                  "netProfitCosts": 766.89,
                  "netDisbursementAmount": 410.70,
                  "disbursementVatAmount": 82.14,
                  "vatIndicator": true,
                  "detentionTravelAndWaitingCosts": 111.00,
                  "jrFormFilling": 50.00
                }
                """.formatted(feeCode))
              .accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.validationMessages[%d].type".formatted(messageIndex)).value("WARNING"))
          .andExpect(jsonPath("$.validationMessages[%d].code".formatted(messageIndex)).value(warningCode))
          .andExpect(jsonPath("$.validationMessages[%d].message".formatted(messageIndex)).value(warningMessage));
    }

    @Test
    void shouldGetFeeCalculationWarnings_immigrationAndAsylumHourlyRate_clr() throws Exception {
      mockMvc
          .perform(post(URI)
              .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                {
                  "feeCode": "IAXC",
                  "claimId": "claim_123",
                  "startDate": "2015-02-11",
                  "netProfitCosts": 1116.89,
                  "netCostOfCounsel": 700.90,
                  "netDisbursementAmount": 125.70,
                  "disbursementVatAmount": 25.14,
                  "vatIndicator": true
                }
                """)
              .accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.validationMessages[0].type").value("WARNING"))
          .andExpect(jsonPath("$.validationMessages[0].code").value("WARIA4"))
          .andExpect(jsonPath("$.validationMessages[0].message").value(
              "Costs have been capped. The amount entered exceeds the Total Cost Limit. An Immigration Prior Authority number must be entered."
          ));
    }

    @Test
    void shouldGetFeeCalculationWarnings_immigrationAndAsylumHourlyRate_clrInterim() throws Exception {
      mockMvc
          .perform(post(URI)
              .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                {
                  "feeCode": "IACD",
                  "claimId": "claim_123",
                  "startDate": "2021-02-11",
                  "netProfitCosts": 1116.89,
                  "netCostOfCounsel": 756.90,
                  "netDisbursementAmount": 125.70,
                  "disbursementVatAmount": 25.14,
                  "boltOns": {
                      "boltOnAdjournedHearing": 1,
                      "boltOnCmrhOral": 2,
                      "boltOnCmrhTelephone": 1,
                      "boltOnSubstantiveHearing": true
                  },
                  "vatIndicator": true
                }
                """)
              .accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.validationMessages[0].type").value("WARNING"))
          .andExpect(jsonPath("$.validationMessages[0].code").value("WARIA5"))
          .andExpect(jsonPath("$.validationMessages[0].message").value(
              "Costs have been capped. The amount entered exceeds the Total Cost Limit. An Immigration Prior Authority number must be entered."
          ));
    }

  }



}
