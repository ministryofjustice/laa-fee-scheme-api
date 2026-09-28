Feature: Magistrates higher standard fee validation

  @api
  Scenario Outline: Reject higher standard magistrates claims when profit costs do not exceed the lower fee limit
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
      | PROF1   | 2020-01-01              | 272.34         |
      | PROF2   | 2020-01-01              | 272.34         |
      | PROF3   | 2020-01-01              | 467.84         |
      | PROJ3   | 2020-01-01              | 272.34         |
      | PROJ4   | 2020-01-01              | 272.34         |
      | PROV2   | 2020-01-01              | 272.34         |
      | PROV4   | 2020-01-01              | 467.84         |
      | PROL1   | 2020-01-01              | 272.34         |
      | PROL2   | 2020-01-01              | 272.34         |
      | PROL3   | 2020-01-01              | 467.84         |
      | PROJ7   | 2020-01-01              | 272.34         |
      | PROJ8   | 2020-01-01              | 272.34         |
      | PROF1   | 2025-12-21              | 313.19         |
      | PROF2   | 2025-12-21              | 313.19         |
      | PROF3   | 2025-12-21              | 538.02         |
      | PROJ3   | 2025-12-21              | 313.19         |
      | PROJ4   | 2025-12-21              | 313.19         |
      | PROV2   | 2025-12-21              | 313.19         |
      | PROV4   | 2025-12-21              | 538.02         |
      | PROL1   | 2025-12-21              | 313.19         |
      | PROL2   | 2025-12-21              | 313.19         |
      | PROL3   | 2025-12-21              | 538.02         |
      | PROJ7   | 2025-12-21              | 313.19         |
      | PROJ8   | 2025-12-21              | 313.19         |
      | PROF1   | 2025-12-22              | 344.51         |
      | PROF2   | 2025-12-22              | 344.51         |
      | PROF3   | 2025-12-22              | 591.82         |
      | PROJ3   | 2025-12-22              | 344.51         |
      | PROJ4   | 2025-12-22              | 344.51         |
      | PROV2   | 2025-12-22              | 344.51         |
      | PROV4   | 2025-12-22              | 591.82         |
      | PROL1   | 2025-12-22              | 344.51         |
      | PROL2   | 2025-12-22              | 344.51         |
      | PROL3   | 2025-12-22              | 591.82         |
      | PROJ7   | 2025-12-22              | 344.51         |
      | PROJ8   | 2025-12-22              | 344.51         |

  @api
  Scenario Outline: Calculate magistrates and youth court fixed fees when the higher fee validation passes or does not apply
    Given I have an initialized API client
    And a fee calculation payload with:
      | feeCode                 | <feeCode>                 |
      | representationOrderDate | <representationOrderDate> |
      | netProfitCosts          | <netProfitCosts>          |
      | netDisbursementAmount   | 123.38                    |
      | disbursementVatAmount   | 24.67                     |
      | vatIndicator            | true                      |
      | netTravelCosts          | <netTravelCosts>          |
      | netWaitingCosts         | <netWaitingCosts>         |
      | caseConcludedDate       | 2026-02-01                |
    When I POST "/api/v1/fee-calculation" with the payload
    Then the response status should be 200
    And the JSON path "schemeId" should equal "<schemeId>"
    And the JSON path "feeCalculation.totalAmount" should equal number <expectedTotal>
    And the JSON path "feeCalculation.fixedFeeAmount" should equal number <fixedFeeAmount>

    Examples:
      | feeCode | representationOrderDate | netProfitCosts | netTravelCosts | netWaitingCosts | schemeId           | expectedTotal | fixedFeeAmount |
      | PROF1   | 2020-01-01              | 1000.00        | 111            | 100             | MAGS_COURT_FS2016  | 896.01        | 412.30         |
      | PROF2   | 2020-01-01              | 1000.00        | 111            | 100             | MAGS_COURT_FS2016  | 858.09        | 380.70         |
      | PROF3   | 2020-01-01              | 1000.00        | 111            | 100             | MAGS_COURT_FS2016  | 1170.38       | 640.94         |
      | PROJ3   | 2020-01-01              | 1000.00        | 111            | 100             | MAGS_COURT_FS2016  | 896.01        | 412.30         |
      | PROJ4   | 2020-01-01              | 1000.00        | 111            | 100             | MAGS_COURT_FS2016  | 858.09        | 380.70         |
      | PROV2   | 2020-01-01              | 1000.00        | 111            | 100             | MAGS_COURT_FS2016  | 858.09        | 380.70         |
      | PROV4   | 2020-01-01              | 1000.00        | 111            | 100             | MAGS_COURT_FS2016  | 1170.38       | 640.94         |
      | PROL1   | 2020-01-01              | 1000.00        |                |                 | MAGS_COURT_FS2016  | 714.22        | 471.81         |
      | PROL2   | 2020-01-01              | 1000.00        |                |                 | MAGS_COURT_FS2016  | 670.82        | 435.64         |
      | PROL3   | 2020-01-01              | 1000.00        |                |                 | MAGS_COURT_FS2016  | 1016.07       | 723.35         |
      | PROJ7   | 2020-01-01              | 1000.00        |                |                 | MAGS_COURT_FS2016  | 714.22        | 471.81         |
      | PROJ8   | 2020-01-01              | 1000.00        |                |                 | MAGS_COURT_FS2016  | 670.82        | 435.64         |
      | PROF1   | 2025-12-21              | 1000.00        | 111            | 100             | MAGS_COURT_FS2022  | 970.23        | 474.15         |
      | PROF2   | 2025-12-21              | 1000.00        | 111            | 100             | MAGS_COURT_FS2022  | 926.62        | 437.81         |
      | PROF3   | 2025-12-21              | 1000.00        | 111            | 100             | MAGS_COURT_FS2022  | 1285.75       | 737.08         |
      | PROJ3   | 2025-12-21              | 1000.00        | 111            | 100             | MAGS_COURT_FS2022  | 970.23        | 474.15         |
      | PROJ4   | 2025-12-21              | 1000.00        | 111            | 100             | MAGS_COURT_FS2022  | 926.62        | 437.81         |
      | PROV2   | 2025-12-21              | 1000.00        | 111            | 100             | MAGS_COURT_FS2022  | 926.62        | 437.81         |
      | PROV4   | 2025-12-21              | 1000.00        | 111            | 100             | MAGS_COURT_FS2022  | 1285.75       | 737.08         |
      | PROL1   | 2025-12-21              | 1000.00        |                |                 | MAGS_COURT_FS2022  | 799.15        | 542.58         |
      | PROL2   | 2025-12-21              | 1000.00        |                |                 | MAGS_COURT_FS2022  | 749.24        | 500.99         |
      | PROL3   | 2025-12-21              | 1000.00        |                |                 | MAGS_COURT_FS2022  | 1146.27       | 831.85         |
      | PROJ7   | 2025-12-21              | 1000.00        |                |                 | MAGS_COURT_FS2022  | 799.15        | 542.58         |
      | PROJ8   | 2025-12-21              | 1000.00        |                |                 | MAGS_COURT_FS2022  | 749.24        | 500.99         |
      | PROF1   | 2025-12-22              | 1000.00        | 111            | 100             | MAGS_COURT_FS2025  | 1027.13       | 521.57         |
      | PROF2   | 2025-12-22              | 1000.00        | 111            | 100             | MAGS_COURT_FS2025  | 979.16        | 481.59         |
      | PROF3   | 2025-12-22              | 1000.00        | 111            | 100             | MAGS_COURT_FS2025  | 1374.20       | 810.79         |
      | PROJ3   | 2025-12-22              | 1000.00        | 111            | 100             | MAGS_COURT_FS2025  | 1027.13       | 521.57         |
      | PROJ4   | 2025-12-22              | 1000.00        | 111            | 100             | MAGS_COURT_FS2025  | 979.16        | 481.59         |
      | PROV2   | 2025-12-22              | 1000.00        | 111            | 100             | MAGS_COURT_FS2025  | 979.16        | 481.59         |
      | PROV4   | 2025-12-22              | 1000.00        | 111            | 100             | MAGS_COURT_FS2025  | 1374.20       | 810.79         |
      | PROL1   | 2025-12-22              | 1000.00        |                |                 | MAGS_COURT_FS2025  | 864.26        | 596.84         |
      | PROL2   | 2025-12-22              | 1000.00        |                |                 | MAGS_COURT_FS2025  | 809.36        | 551.09         |
      | PROL3   | 2025-12-22              | 1000.00        |                |                 | MAGS_COURT_FS2025  | 1246.10       | 915.04         |
      | PROJ7   | 2025-12-22              | 1000.00        |                |                 | MAGS_COURT_FS2025  | 864.26        | 596.84         |
      | PROJ8   | 2025-12-22              | 1000.00        |                |                 | MAGS_COURT_FS2025  | 809.36        | 551.09         |
      | PROE1   | 2025-12-21              | 100.00         | 111            | 100             | MAGS_COURT_FS2022  | 669.91        | 223.88         |
      | YOUF1   | 2025-12-22              | 100.00         | 111            | 100             | YOUTH_COURT_FS2025 | 1817.26       | 1180.01        |
