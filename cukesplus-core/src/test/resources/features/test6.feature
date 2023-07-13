Feature: A new feature 6


  #> 4 : src/test/resources/features/test6.macroFeature
  Scenario: Using a macro with text block or table 1
    #> 5 : src/test/resources/features/test6.macroFeature
    Given something here
    #> 6 : src/test/resources/features/test6.macroFeature
    ##__#__## Given this is a macro with an inline table or text block
    #>> 1 : src/test/resources/macros/test6.macro
    #>>> 2 , 1
    Given this is the first step
    #>>> 3 , 1
    When this step should see the table or text block
    ## <<<
      """
      This is a text block
      It has multiple lines
      """
    #>>> 5 , 1
    Then this is the last step
    #> 11 : src/test/resources/features/test6.macroFeature
    Given other thing


  #> 14 : src/test/resources/features/test6.macroFeature
  Scenario: Using a macro with text block or table 2
    #> 15 : src/test/resources/features/test6.macroFeature
    Given something here
    #> 16 : src/test/resources/features/test6.macroFeature
    ##__#__## Given this is a macro with an inline table or text block that ony inserts the first two rows
    #>> 8 : src/test/resources/macros/test6.macro
    #>>> 9 , 1
    Given this is the first step
    #>>> 10 , 1
    When this step should see the table or text block
    ## <<< 2
      | column1        | column2   |
      | something here | not here  |
    #>>> 12 , 1
    Then this is the last step
      | and here       | not there |
    #> 20 : src/test/resources/features/test6.macroFeature
    Given other thing


  #> 23 : src/test/resources/features/test6.macroFeature
  Scenario: Using a macro with text block or table 3
    #> 24 : src/test/resources/features/test6.macroFeature
    Given something here
    #> 25 : src/test/resources/features/test6.macroFeature
    ##__#__## Given this is a macro with an inline table or text block that inserts the first two rows then the rest of the rows
    #>> 15 : src/test/resources/macros/test6.macro
    #>>> 16 , 1
    Given this is the first step
    #>>> 17 , 1
    When this step should see the table or text block
    ## <<< ${test.insert.rows}
      | column1        | column2   |
      | something here | not here  |
    #>>> 19 , 1
    Then this is the penultimate step
    ## <<<
    #>>> 21 , 1
    Then this is the last step
      | and here       | not there |
    #> 29 : src/test/resources/features/test6.macroFeature
    Given other thing


  #> 32 : src/test/resources/features/test6.macroFeature
  Scenario: Using a macro with text block or table 4
    #> 33 : src/test/resources/features/test6.macroFeature
    Given something here
    #> 34 : src/test/resources/features/test6.macroFeature
    ##__#__## Given this is a macro with an inline table or text block that inserts the first two rows then the rest of the table is used in the feature file
    #>> 24 : src/test/resources/macros/test6.macro
    #>>> 25 , 1
    Given this is the first step
    #>>> 26 , 1
    When this step should see the table or text block
    ## <<< ${test.insert.rows}
      | column1        | column2   |
      | something here | not here  |
    #>>> 28 , 1
    Then this is the last step
      | and here       | not there |
    #> 38 : src/test/resources/features/test6.macroFeature
    Given other thing
