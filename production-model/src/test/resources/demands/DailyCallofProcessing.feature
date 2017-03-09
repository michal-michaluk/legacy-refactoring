Feature: daily callof processing
  User Story:
  To prevent brakes in customer production processes
  we need to process full information of daily callof document,
  and rise an alert for planing team to re-plan production accordingly.

  Domain Story:
  Daily processing of callof document:
  for all products included in callof document
  New demand are stored for further reference
  If new demand is not fulfilled by product stock and production forecast
  there is a shortage in particular days and we need to rise an alert.
  planner should be notified in that case,
  if there are locked parts on stock,
  QA task for recovering them should have high priority.

  Tech Tasks (out of test scope):
  Download daily callof document (json file from FTP server).
  Parse custom customer json format.
  Make demands persistence in our database.
  Render and send pretty notifications.
  ... how to get current stock?


  Scenario: demand is fulfilled with current stock only
    Given a callof with demand for 461952819311 starting from today:
      | 0 | 408 | 0 | 0 | 0 | 0 | 0 |
    Given current stock of 461952819311 is 854
    Given no production for 461952819311 is planed
    When the callof is processed
    Then there is no gap so no alert will be raised

  Scenario: demand is NOT fulfilled only with stock
    Given a callof with demand for 461952819311 starting from today:
      | 0 | 408 | 0 | 0 | 0 | 0 | 0 |
    Given current stock of 461952819311 is 300
    Given no production for 461952819311 is planed
    When the callof is processed
    Then there is a gap of 108 so alert will be raised

  Scenario: including production, demand is fulfilled
    Given a callof with demand for 461952819311 starting from today:
      | 0 | 408 | 0 | 0 | 0 | 0 | 0 |
    Given current stock of 461952819311 is 300
    Given production forecast for 461952819311:
      | 408 | 0 | 0 | 0 | 0 | 0 | 0 |
    When the callof is processed
    Then there is no gap so no alert will be raised

  Scenario: there is a gap even if production is included
    Given a callof with demand for 461952819311 starting from today:
      | 0 | 408 | 500 | 0 | 0 | 0 | 0 |
    Given current stock of 461952819311 is 300
    Given production forecast for 461952819311:
      | 408 | 0 | 0 | 0 | 0 | 0 | 0 |
    When the callof is processed
    Then there is a gap of 200 so alert will be raised

  Scenario: gap from first week is compensated in next
    Given a callof with demand for 461952819311 starting from today:
      | 0 | 408 | 0 | 0 | 0 | 0   | 500 |
      | 0 | 0   | 0 | 0 | 0 | 150 | 0   |
    Given current stock of 461952819311 is 300
    Given production forecast for 461952819311:
      | 408 | 0 | 0 | 0 | 0 | 0 | 0 |
      | 408 | 0 | 0 | 0 | 0 | 0 | 0 |
    When the callof is processed
    Then there is a gap of 200 so alert will be raised

# TODO MM: Scenario: there is no shortage if we need deliver at end of day
