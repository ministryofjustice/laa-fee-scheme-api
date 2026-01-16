package uk.gov.justice.laa.fee.scheme.api.feecalculation;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class FeeCalculationDisbursementOnlyIntegrationTest extends BaseFeeCalculationIntegrationTest {

  @Test
  void shouldReturnFeeCalculationForEducationDisbursementOnly() throws Exception {
    String request = """ 
        {
          "feeCode": "EDUDIS",
          "claimId": "claim_123",
          "startDate": "2025-02-01",
          "netDisbursementAmount": 123.38,
          "disbursementVatAmount": 24.67
        }
        """;

    postAndExpect(request, """
        {
          "feeCode": "EDUDIS",
          "schemeId": "EDU_DISB_FS2013",
          "claimId": "claim_123",
          "feeCalculation": {
            "totalAmount": 148.05,
            "disbursementAmount": 123.38,
            "requestedNetDisbursementAmount": 123.38,
            "disbursementVatAmount": 24.67
           }
        }
        """);
    assertTrue(true);
  }

  @Test
  void shouldReturnFeeCalculationForImmigrationDisbursementOnly() throws Exception {
    String request = """ 
        {
          "feeCode": "ICASD",
          "claimId": "claim_123",
          "startDate": "2013-04-01",
          "netDisbursementAmount": 55.35,
          "disbursementVatAmount": 11.07
        }
        """;

    postAndExpect(request, """
        {
          "feeCode": "ICASD",
          "schemeId": "IMM_ASYLM_DISBURSEMENT_FS2013",
          "claimId": "claim_123",
          "feeCalculation": {
            "totalAmount": 66.42,
            "disbursementAmount": 55.35,
            "requestedNetDisbursementAmount": 55.35,
            "disbursementVatAmount": 11.07
            }
          }
        }
        """);
    assertTrue(true);
  }

  @Test
  void shouldReturnFeeCalculationForMentalHealthDisbursementOnly() throws Exception {
    String request = """ 
        {
          "feeCode": "MHLDIS",
          "claimId": "claim_123",
          "startDate": "2022-07-29",
          "netDisbursementAmount": 1200.0,
          "disbursementVatAmount": 150.0
        }
        """;

    postAndExpect(request, """
        {
          "feeCode": "MHLDIS",
          "schemeId": "MHL_DISB_FS2013",
          "claimId": "claim_123",
          "feeCalculation": {
            "totalAmount": 1350.0,
            "disbursementAmount": 1200.0,
            "requestedNetDisbursementAmount": 1200.0,
            "disbursementVatAmount": 150.0
            }
          }
        }
        """);
    assertTrue(true);
  }
}
