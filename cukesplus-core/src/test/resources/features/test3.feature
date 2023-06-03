@test
Feature: Test 3

  This tests in place macro expansion

  Scenario Outline: Scenario 1
    Given this is not a macro
    Given I do this thing
    And I do this thing
    And this is the last step
    Given this is a step definition with a simple parameter for FOO in the middle
    And this thing <a> and <b>

    Examples:
    | a | b |
    | 1 | 2 |
    | 3 | 4 |


  Scenario:
    Given I do this macro
