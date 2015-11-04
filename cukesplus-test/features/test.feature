Feature: Testing this CukesPlus
  
  Background:
    Given I run this test
    
  
  Scenario Outline: First test
    Given I run this test
    Given I run this test with more glue "<a>" here and "<b>" there

  Examples:
    | a | b |
    | 1 | 2 |
    | 3 | 4 |
