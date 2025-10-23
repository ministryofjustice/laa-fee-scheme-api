package uk.gov.justice.laa.fee.scheme.validation;

import static org.springframework.test.json.JsonCompareMode.STRICT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
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
public class FeeCalculationValidationIntegrationTest extends PostgresContainerTestBase {

  private static final String AUTH_TOKEN = "int-test-token";
  private static final String URI = "/api/v1/fee-calculation";

  @Autowired
  private MockMvc mockMvc;

  @Test
  void shouldReturnValidationError_whenFeeCodeIsInvalid() throws Exception {
    mockMvc.perform(post(URI)
            .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "feeCode": "BLAH",
                  "claimId": "claim_123",
                  "startDate": "2019-09-30",
                  "netProfitCosts": 239.06,
                  "netCostOfCounsel": 79.19,
                  "netDisbursementAmount": 100.21,
                  "disbursementVatAmount": 20.12,
                  "vatIndicator": true
                }
                """)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json("""
            {
              "feeCode": "BLAH",
              "claimId": "claim_123",
              "validationMessages": [
                {
                  "type": "ERROR",
                  "code": "ERRALL1",
                  "message": "Enter a valid Fee Code."
                }
              ]
            }
            """, STRICT));
  }

  @Test
  void shouldReturnValidationError_whenCivilFeeCodeAndStartDateIsTooFarInThePast() throws Exception {
    mockMvc.perform(post(URI)
            .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "feeCode": "DISC",
                  "claimId": "claim_123",
                  "startDate": "2012-09-30",
                  "netProfitCosts": 239.06,
                  "netCostOfCounsel": 79.19,
                  "netDisbursementAmount": 100.21,
                  "disbursementVatAmount": 20.12,
                  "vatIndicator": true
                }
                """)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json("""
            {
              "feeCode": "DISC",
              "claimId": "claim_123",
              "validationMessages": [
                {
                  "type": "ERROR",
                  "code": "ERRCIV2",
                  "message": "Case Start Date is too far in the past."
                }
              ]
            }
            """, STRICT));
  }

  @Test
  void shouldReturnValidationError_whenCrimeFeeCodeAndStartDateIsInvalid() throws Exception {
    mockMvc.perform(post(URI)
            .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "feeCode": "INVC",
                  "claimId": "claim_123",
                  "startDate": "2023-12-12",
                  "uniqueFileNumber": "121223/242",
                  "policeStationId": "NE001",
                  "policeStationSchemeId": "1001",
                  "vatIndicator": false
                }
                """)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json("""
            {
              "feeCode": "INVC",
              "claimId": "claim_123",
              "validationMessages": [
                {
                  "type": "ERROR",
                  "code": "ERRCRM1",
                  "message": "Fee Code is not valid for the Case Start Date."
                }
              ]
            }
            """, STRICT));
  }

  @Test
  void shouldReturnValidationError_whenCrimeFeeCodeAndPoliceStationIdIsInvalid() throws Exception {
    mockMvc.perform(post(URI)
            .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "feeCode": "INVC",
                  "claimId": "claim_123",
                  "startDate": "2023-12-12",
                  "uniqueFileNumber": "121219/242",
                  "policeStationId": "BLAH",
                  "policeStationSchemeId": "1001",
                  "vatIndicator": false
                }
                """)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json("""
            {
              "feeCode": "INVC",
              "claimId": "claim_123",
              "validationMessages": [
                {
                  "type": "ERROR",
                  "code": "ERRCRM3",
                  "message": "Enter a valid Police station ID, Court ID, or Prison ID."
                }
              ]
            }
            """, STRICT));
  }

  @Test
  void shouldReturnValidationError_whenCrimeFeeCodeAndPoliceSchemeIdIsInvalid() throws Exception {
    mockMvc.perform(post(URI)
            .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "feeCode": "INVC",
                  "claimId": "claim_123",
                  "startDate": "2021-12-12",
                  "uniqueFileNumber": "121221/242",
                  "policeStationSchemeId": "BLAH",
                  "vatIndicator": false
                }
                """)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json("""
            {
              "feeCode": "INVC",
              "claimId": "claim_123",
              "validationMessages": [
                {
                  "type": "ERROR",
                  "code": "ERRCRM4",
                  "message": "Enter a valid Scheme ID."
                }
              ]
            }
            """, STRICT));
  }

  @Test
  void shouldReturnValidationError_whenCrimeFeeCodeAndUfnIsMissing() throws Exception {
    mockMvc.perform(post(URI)
            .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "feeCode": "INVK",
                  "claimId": "claim_123",
                  "startDate": "2023-12-12",
                  "policeStationId": "NE001",
                  "policeStationSchemeId": "1001",
                  "vatIndicator": false
                }
                """)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json("""
            {
              "feeCode": "INVK",
              "claimId": "claim_123",
              "validationMessages": [
                {
                  "type": "ERROR",
                  "code": "ERRCRM7",
                  "message": "Enter a UFN."
                }
              ]
            }
            """, STRICT));
  }

  @Test
  void shouldReturnValidationError_whenCrimeFeeCodeAndRepOrderDateIsInvalid() throws Exception {
    mockMvc.perform(post(URI)
            .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "feeCode": "PROJ5",
                  "claimId": "claim_123",
                  "uniqueFileNumber": "010215/242",
                  "representationOrderDate": "2015-02-01",
                  "netDisbursementAmount": 123.38,
                  "disbursementVatAmount": 24.67,
                  "vatIndicator": true
                }
                """)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json("""
            {
              "feeCode": "PROJ5",
              "claimId": "claim_123",
              "validationMessages": [
                {
                  "type": "ERROR",
                  "code": "ERRCRM12",
                  "message": "Fee Code is not valid for the Case Start Date."
                }
              ]
            }
            """, STRICT));
  }

  @Test
  void shouldReturnValidationWarning_whenCrimeFeeCodeAndNetTravelCosts() throws Exception {
    mockMvc.perform(post(URI)
            .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "feeCode": "INVB1",
                  "claimId": "claim_123",
                  "startDate": "2021-12-12",
                  "uniqueFileNumber": "121221/242",
                  "netTravelCosts": 100.0,
                  "vatIndicator": false
                }
                """)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json("""
            {
              "feeCode": "INVB1",
              "claimId": "claim_123",
              "schemeId": "POL_FS2016",
              "validationMessages": [
                {
                  "type": "WARNING",
                  "code": "WARCRM1",
                  "message": "Cost not included. Travel costs cannot be claimed with Fee Code used."
                }
              ],
              "escapeCaseFlag": false,
              "feeCalculation": {
                "totalAmount": 28.7,
                "vatIndicator": false,
                "vatRateApplied": 20.00,
                "calculatedVatAmount": 0,
                "fixedFeeAmount": 28.7
              }
            }
            """, STRICT));
  }

  @Test
  void shouldReturnValidationWarning_whenCrimeFeeCodeAndNetWaitingCosts() throws Exception {
    mockMvc.perform(post(URI)
            .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "feeCode": "INVB1",
                  "claimId": "claim_123",
                  "startDate": "2021-12-12",
                  "uniqueFileNumber": "121221/242",
                  "netWaitingCosts": 100.0,
                  "vatIndicator": false
                }
                """)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json("""
            {
              "feeCode": "INVB1",
              "claimId": "claim_123",
              "schemeId": "POL_FS2016",
              "validationMessages": [
                {
                  "type": "WARNING",
                  "code": "WARCRM2",
                  "message": "Cost not included. Waiting costs cannot be claimed with Fee Code used."
                }
              ],
              "escapeCaseFlag": false,
              "feeCalculation": {
                "totalAmount": 28.7,
                "vatIndicator": false,
                "vatRateApplied": 20.00,
                "calculatedVatAmount": 0,
                "fixedFeeAmount": 28.7
              }
            }
            """, STRICT));
  }

  @Test
  void shouldReturnValidationError_whenFamilyFeeCodeAndLondonRateIsMissing() throws Exception {
    mockMvc
        .perform(post(URI)
            .header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "feeCode": "FPB010",
                  "claimId": "claim_123",
                  "startDate": "2022-02-01",
                  "netDisbursementAmount": 123.38,
                  "disbursementVatAmount": 24.67,
                  "vatIndicator": true
                }
                """)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json("""
            {
              "feeCode": "FPB010",
              "claimId": "claim_123",
              "validationMessages": [
                {
                  "type": "ERROR",
                  "code": "ERRFAM1",
                  "message": "London/Non-London rate must be entered for the Fee Code used."
                }
              ]
            }
            """, STRICT));
  }
}
