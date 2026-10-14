Feature: Prison law higher standard fee validation

  @api
  Scenario Outline: Reject higher standard prison law claims when combined profit and waiting costs do not exceed the lower fee limit
    Given I have an initialized API client
    And a fee calculation payload with:
      | feeCode                 | <feeCode>          |
      | uniqueFileNumber        | <uniqueFileNumber> |
      | netProfitCosts          | <netProfitCosts>   |
      | netWaitingCosts         | <netWaitingCosts>  |
      | netDisbursementAmount   | 123.38             |
      | disbursementVatAmount   | 24.67              |
      | vatIndicator            | true               |
      | caseConcludedDate       | 2026-02-01         |
    When I POST "/api/v1/fee-calculation" with the payload
    Then the response status should be 200
    And the JSON path "validationMessages.0.type" should equal "ERROR"
    And the JSON path "validationMessages.0.code" should equal "ERRCRM14"
    And the JSON path "validationMessages.0.message" should equal "The Higher Standard Fee code has been claimed incorrectly. The costs entered are less than the Lower Standard Fee Limit. Resubmit your claim with correct fee code for the Lower Standard fee."

    Examples:
      | feeCode | uniqueFileNumber | netProfitCosts | netWaitingCosts |
      | PRIB2   | 211225/123       | 157.06         | 200.00          |
      | PRIC2   | 211225/123       | 733.93         | 200.00          |
      | PRID2   | 221225/123       | 242.75         | 200.00          |
      | PRIE2   | 221225/123       | 958.07         | 200.00          |
      | PRIB2   | 211225/123       | 157.05         | 200.00          |
      | PRIC2   | 211225/123       | 733.92         | 200.00          |
      | PRID2   | 221225/123       | 242.74         | 200.00          |
      | PRIE2   | 221225/123       | 958.06         | 200.00          |

  @api
  Scenario Outline: Calculate prison law higher standard fees when combined profit and waiting costs exceed the lower fee limit
    Given I have an initialized API client
    And a fee calculation payload with:
      | feeCode                 | <feeCode>          |
      | uniqueFileNumber        | <uniqueFileNumber> |
      | netProfitCosts          | <netProfitCosts>   |
      | netWaitingCosts         | <netWaitingCosts>  |
      | netDisbursementAmount   | 100                |
      | disbursementVatAmount   | 20                 |
      | vatIndicator            | true               |
      | caseConcludedDate       | 2026-02-01         |
    When I POST "/api/v1/fee-calculation" with the payload
    Then the response status should be 200
    And the JSON path "schemeId" should equal "<schemeId>"
    And the JSON path "feeCalculation.fixedFeeAmount" should equal number <fixedFeeAmount>

    Examples:
      | feeCode | uniqueFileNumber | netProfitCosts | netWaitingCosts | schemeId       | fixedFeeAmount |
      | PRIB2   | 211225/123       | 157.07         | 200.00          | PRISON_FS2016  | 564.16         |
      | PRIC2   | 211225/123       | 733.94         | 200.00          | PRISON_FS2016  | 1454.44        |
      | PRID2   | 221225/123       | 242.76         | 200.00          | PRISON_FS2025  | 699.56         |
      | PRIE2   | 221225/123       | 958.08         | 200.00          | PRISON_FS2025  | 1803.51        |
