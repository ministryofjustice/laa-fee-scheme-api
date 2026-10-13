Feature: Youth Court higher standard fee validation

  @api
  Scenario Outline: Reject higher standard Youth Court claims when profit costs do not exceed the lower fee limit
    Given I have an initialized API client
    And a fee calculation payload with:
      | feeCode                 | <feeCode>                 |
      | representationOrderDate | <representationOrderDate> |
      | netProfitCosts          | <netProfitCosts>          |
      | netDisbursementAmount   | 123.38                    |
      | disbursementVatAmount   | 24.67                     |
      | vatIndicator            | true                      |
      | netTravelCosts          | 111                       |
      | netWaitingCosts         | 100                       |
      | caseConcludedDate       | 2026-02-01                |
    When I POST "/api/v1/fee-calculation" with the payload
    Then the response status should be 200
    And the JSON path "validationMessages.0.type" should equal "ERROR"
    And the JSON path "validationMessages.0.code" should equal "ERRCRM14"
    And the JSON path "validationMessages.0.message" should equal "The Higher Standard Fee code has been claimed incorrectly. The costs entered are less than the Lower Standard Fee Limit. Resubmit your claim with correct fee code for the Lower Standard fee."

    Examples:
      | feeCode | representationOrderDate | netProfitCosts |
      | YOUF1   | 2025-12-21              | 313.19         |
      | YOUF2   | 2025-12-21              | 313.19         |
      | YOUF3   | 2025-12-21              | 538.02         |
      | YOUF4   | 2025-12-21              | 538.02         |
      | YOUL1   | 2025-12-21              | 313.19         |
      | YOUL2   | 2025-12-21              | 313.19         |
      | YOUL3   | 2025-12-21              | 538.02         |
      | YOUL4   | 2025-12-21              | 538.02         |
      | YOUX3   | 2025-12-21              | 313.19         |
      | YOUX4   | 2025-12-21              | 313.19         |
      | YOUY3   | 2025-12-21              | 313.19         |
      | YOUY4   | 2025-12-21              | 313.19         |
      | YOUF1   | 2025-12-22              | 344.51         |
      | YOUF2   | 2025-12-22              | 344.51         |
      | YOUF3   | 2025-12-22              | 591.82         |
      | YOUF4   | 2025-12-22              | 591.82         |
      | YOUL1   | 2025-12-22              | 344.51         |
      | YOUL2   | 2025-12-22              | 344.51         |
      | YOUL3   | 2025-12-22              | 591.82         |
      | YOUL4   | 2025-12-22              | 591.82         |
      | YOUX3   | 2025-12-22              | 344.51         |
      | YOUX4   | 2025-12-22              | 344.51         |
      | YOUY3   | 2025-12-22              | 344.51         |
      | YOUY4   | 2025-12-22              | 344.51         |
      | YOUF1   | 2025-12-21              | 313.18         |
      | YOUL1   | 2025-12-21              | 313.18         |
      | YOUF3   | 2025-12-22              | 591.81         |
      | YOUL3   | 2025-12-22              | 591.81         |

  @api
  Scenario Outline: Calculate Youth Court fixed fees when profit costs exceed the lower fee limit or the fee band is lower
    Given I have an initialized API client
    And a fee calculation payload with:
      | feeCode                 | <feeCode>                 |
      | representationOrderDate | <representationOrderDate> |
      | netProfitCosts          | <netProfitCosts>          |
      | netDisbursementAmount   | 123.38                    |
      | disbursementVatAmount   | 24.67                     |
      | vatIndicator            | true                      |
      | netTravelCosts          | 111                       |
      | netWaitingCosts         | 100                       |
      | caseConcludedDate       | 2026-02-01                |
    When I POST "/api/v1/fee-calculation" with the payload
    Then the response status should be 200
    And the JSON path "schemeId" should equal "<schemeId>"
    And the JSON path "feeCalculation.fixedFeeAmount" should equal number <fixedFeeAmount>

    Examples:
      | feeCode | representationOrderDate | netProfitCosts | schemeId           | fixedFeeAmount |
      | YOUF1   | 2025-12-21              | 313.20         | YOUTH_COURT_FS2024 | 1072.74        |
      | YOUF2   | 2025-12-21              | 313.20         | YOUTH_COURT_FS2024 | 437.81         |
      | YOUF3   | 2025-12-21              | 538.03         | YOUTH_COURT_FS2024 | 1335.67        |
      | YOUF4   | 2025-12-21              | 538.03         | YOUTH_COURT_FS2024 | 737.08         |
      | YOUL1   | 2025-12-21              | 313.20         | YOUTH_COURT_FS2024 | 1141.17        |
      | YOUL2   | 2025-12-21              | 313.20         | YOUTH_COURT_FS2024 | 500.99         |
      | YOUL3   | 2025-12-21              | 538.03         | YOUTH_COURT_FS2024 | 1430.44        |
      | YOUL4   | 2025-12-21              | 538.03         | YOUTH_COURT_FS2024 | 831.85         |
      | YOUX3   | 2025-12-21              | 313.20         | YOUTH_COURT_FS2024 | 1072.74        |
      | YOUX4   | 2025-12-21              | 313.20         | YOUTH_COURT_FS2024 | 437.81         |
      | YOUY3   | 2025-12-21              | 313.20         | YOUTH_COURT_FS2024 | 1141.17        |
      | YOUY4   | 2025-12-21              | 313.20         | YOUTH_COURT_FS2024 | 500.99         |
      | YOUF1   | 2025-12-22              | 344.52         | YOUTH_COURT_FS2025 | 1180.01        |
      | YOUF2   | 2025-12-22              | 344.52         | YOUTH_COURT_FS2025 | 481.59         |
      | YOUF3   | 2025-12-22              | 591.83         | YOUTH_COURT_FS2025 | 1469.24        |
      | YOUF4   | 2025-12-22              | 591.83         | YOUTH_COURT_FS2025 | 810.79         |
      | YOUL1   | 2025-12-22              | 344.52         | YOUTH_COURT_FS2025 | 1255.29        |
      | YOUL2   | 2025-12-22              | 344.52         | YOUTH_COURT_FS2025 | 551.09         |
      | YOUL3   | 2025-12-22              | 591.83         | YOUTH_COURT_FS2025 | 1573.48        |
      | YOUL4   | 2025-12-22              | 591.83         | YOUTH_COURT_FS2025 | 915.04         |
      | YOUX3   | 2025-12-22              | 344.52         | YOUTH_COURT_FS2025 | 1180.01        |
      | YOUX4   | 2025-12-22              | 344.52         | YOUTH_COURT_FS2025 | 481.59         |
      | YOUY3   | 2025-12-22              | 344.52         | YOUTH_COURT_FS2025 | 1255.29        |
      | YOUY4   | 2025-12-22              | 344.52         | YOUTH_COURT_FS2025 | 551.09         |
      | YOUE1   | 2025-12-21              | 100.00         | YOUTH_COURT_FS2024 | 822.47         |
      | YOUK1   | 2025-12-21              | 100.00         | YOUTH_COURT_FS2024 | 884.61         |
      | YOUE1   | 2025-12-22              | 100.00         | YOUTH_COURT_FS2025 | 904.72         |
      | YOUK1   | 2025-12-22              | 100.00         | YOUTH_COURT_FS2025 | 973.07         |
