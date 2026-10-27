Feature: How the expo policy decides which ticket fires
  As a kitchen manager
  I want to run the same rush under different expo policies
  So that I can see which one gets food out faster

  Scenario Outline: The <policy> policy picks which ticket fires first
    Given a kitchen using the <policy> policy
    And these tickets:
      | table | arrival | items        |
      | 4     | 0       | Steak, Salad |
      | 7     | 0       | Salad        |
    When the kitchen runs until done
    Then ticket <first> is fired first
    And every ticket is served

    Examples:
      | policy   | first |
      | fifo     | 1     |
      | shortest | 2     |
