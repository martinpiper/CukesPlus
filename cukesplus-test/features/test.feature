Feature: Testing this CukesPlus
  
  Background:
#    Given this is a macro test with one parameter "hello"
    Given I run this test
    
  
  Scenario Outline: First test
    Given I run this test
    Given I run this test with more glue "<a>" here and "<b>" there

  Examples:
    | a | b |
    | 1 | 2 |
    | 3 | 4 |

  Scenario:
    When this value foooo matches the string (hello) then dance
  