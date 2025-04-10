#include "../macros/test4.macro"
@Demo1
Feature: Test 1
  @Demo2
  Scenario Outline: Scenario 1
    #> 6 : src/test/resources/features/test1.macroFeature
    Given this is not a macro
    #> 7 : src/test/resources/features/test1.macroFeature
    ##__#__## Given I do this thing
    #>> 9 : src/test/resources/macros/test4.macro
    #>>> 10 , 1
    * it does this step:
      """
      With this text block
      And this line
      """
    #> 8 : src/test/resources/features/test1.macroFeature
    ##__#__## And I do this thing
    #>> 9 : src/test/resources/macros/test4.macro
    #>>> 10 , 1
    * it does this step:
      """
      With this text block
      And this line
      """
    #> 9 : src/test/resources/features/test1.macroFeature
    And this is the last step
    #> 10 : src/test/resources/features/test1.macroFeature
    Given this is a step definition with a simple parameter for FOO in the middle
    #> 11 : src/test/resources/features/test1.macroFeature
    And this thing <a> and <b>

    #> 13 : src/test/resources/features/test1.macroFeature
    Examples:
      | a | b |
      | 1 | 2 |
      | 3 | 4 |

  #> 18 : src/test/resources/features/test1.macroFeature
  @Demo3
  Scenario:
    #> 20 : src/test/resources/features/test1.macroFeature
    ##__#__## Given I do this macro
    #>> 18 : src/test/resources/macros/test4.macro
    #>>> 19 , 1
    ##__#__## Then I do the other macro
    #>> 22 : src/test/resources/macros/test4.macro
    #>>> 23 , 2
    Then this is the real step
