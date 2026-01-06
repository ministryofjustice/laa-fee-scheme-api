package uk.gov.justice.laa.fee.scheme.api.feecalculation;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.justice.laa.fee.scheme.config.FeeSchemeTestConfig;

@SpringBootTest
@Import(FeeSchemeTestConfig.class)
@Testcontainers
class FeeCalculationDisbursementOnlyIntegrationTest extends BaseFeeCalculationIntegrationTest {

  @Test
  void shouldGetFeeCalculation_educationDisbursementOnly() throws Exception {
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
  }

  @Test
  void shouldGetFeeCalculation_immigrationDisbursementOnly() throws Exception {
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
  }

  @Test
  void shouldGetFeeCalculation_mentalHealthDisbursementOnly() throws Exception {
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
  }
}
