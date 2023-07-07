@noerrors
Feature: Testing this CukesPlus2
  Scenario: First test2
    Given I run this test
    Given I run this test
    Given this step isn't implemented
    And something else


  Scenario Outline: First test2
    Given I run this test
  Examples:
    | a |
    | 1 |
    | 2 |


  Scenario: Different errors 1
    Given I am passing

  Scenario: Different errors 2
    Given I am pending

  Scenario: Different errors 3
    Given I am unimplemented

  Scenario: Different errors 4
    Given I run this test
    Given I run this test
    Given I run this test
    Given I throw an exception
    Given I throw an exception



  Scenario: Using macros 2
    Given Different errors two

  Scenario: Using macros 3
    Given Different errors three

  Scenario: Using macros 4
    Given Different errors four

