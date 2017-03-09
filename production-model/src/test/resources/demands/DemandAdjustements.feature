Feature: manual adjustments of demand

  Domain Story:
  New demand is stored for further reference
  If new demand is not fulfilled by
  current product stock and production forecast
  there is a shortage in particular days and we need to rise an alert.
  planner should be notified,
  if there are locked parts on stock,
  QA task for recovering them should have high priority.

  ADDED:
  Data from callof document should be preserved in database (DON’T OVERRIDE THEM).
  Should be possible to adjust demand even
  if there was no callof document for that product.
  Logistician note should be kept along with adjustment.


  Scenario: Stock level is sufficient
    Given 1000 parts of product on stock
    And current demand for today is 500 parts
    When today's demand is adjusted to 800 parts
    Then new demand is saved
    Then original demand from callof document is preserved in database
    Then there are no new shortages
    And no alert will be raised

  Scenario: No callof document for product
    Given 1000 parts of product on stock
    And there is no base demand from callof document
    When today's demand is adjusted to 800 parts
    Then new demand is saved
    Then original demand from callof document is preserved in datbase
    Then there are no new shortages
    And no alert will be raised

  Scenario: Demand will be not fulfilled, no plan
    Given 100 parts of product on stock
    And current demand for today is 0 parts
    When today's demand is adjusted to 800 parts
    Then there is new shortage
    And alert will be raised
