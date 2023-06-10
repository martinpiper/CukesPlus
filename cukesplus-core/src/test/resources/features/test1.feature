#include "../macros/test4.macro"
Feature: Test 1
  Scenario Outline: Scenario 1
    #> 4 : src/test/resources/features/test1.macroFeature
    Given this is not a macro
    #> 5 : src/test/resources/features/test1.macroFeature
    ##__#__## Given I do this thing
    #>> 9 : src/test/resources/macros/test4.macro
    #>>> 10
    * it does this step:
      """
      With this text block
      And this line
      """
    #> 6 : src/test/resources/features/test1.macroFeature
    ##__#__## And I do this thing
    #>> 9 : src/test/resources/macros/test4.macro
    #>>> 10
    * it does this step:
      """
      With this text block
      And this line
      """
    #> 7 : src/test/resources/features/test1.macroFeature
    And this is the last step
    #> 8 : src/test/resources/features/test1.macroFeature
    ##__#__## Given this is a step definition with a simple parameter for FOO in the middle
    #>> 1 : src/test/resources/macros/test4.macro
    #>>> 3
    Then it does this step
    #>>> 4
    And this step
    #>>> 5
    * this is a step with parameter FOO
    #>>> 6
    Then it does this step with an escaped $name in the step
    #> 9 : src/test/resources/features/test1.macroFeature
    And this thing <a> and <b>

    #> 11 : src/test/resources/features/test1.macroFeature
    Examples:
      | a | b |
      | 1 | 2 |
      | 3 | 4 |


  Scenario:
    #> 18 : src/test/resources/features/test1.macroFeature
    ##__#__## Given I do this macro
    #>> 18 : src/test/resources/macros/test4.macro
    #>>> 19
    ##__#__## Then I do the other macro
    #>> 22 : src/test/resources/macros/test4.macro
    #>>> 23
    Then this is the real step
