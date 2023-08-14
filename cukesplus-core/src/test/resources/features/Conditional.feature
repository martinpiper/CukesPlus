Feature: Tests Conditional behaviour
  
  
  Scenario: Simple Conditional behaviour 1 : equal to
    Given set property "test.foo" equal to "false"
    When if "1" is numerically equal to "2"
      Given set property "test.foo" equal to "true"
      When if "2" is numerically equal to "3"
        Then debug print to scenario "true"
        Given set property "test.foo" equal to "true2"
      When endif
    When endif
    Then assert that "${test.foo}" is equal to "false"


  Scenario: Simple Conditional behaviour 2 : equal to
    Given set property "test.foo" equal to "false"
    When if "2" is numerically equal to "2"
      Given set property "test.foo" equal to "true"
      When if "2" is numerically equal to "3"
        Then debug print to scenario "true"
        Given set property "test.foo" equal to "true2"
      When endif
    When endif
    Then assert that "${test.foo}" is equal to "true"

  Scenario: Simple Conditional behaviour 2-2 : equal to
    Given set property "test.foo" equal to "false"
    Given set property "test.foo2" equal to "false"
    When if "1" is numerically equal to "1"
      Given set property "test.foo" equal to "true"
      When if "2" is numerically equal to "3"
        Then debug print to scenario "true"
        Given set property "test.foo" equal to "true2"
      When endif
      Given set property "test.foo2" equal to "true3"
      When if "3" is numerically equal to "4"
        Then debug print to scenario "true"
        Given set property "test.foo2" equal to "true4"
      When endif
    When endif
    Then assert that "${test.foo}:${test.foo2}" is equal to "true:true3"


  Scenario: Simple Conditional behaviour 3 : equal to
    Given set property "test.foo" equal to "false"
    When if "2" is numerically equal to "2"
      Given set property "test.foo" equal to "true"
      When if "3" is numerically equal to "3"
        Then debug print to scenario "true"
        Given set property "test.foo" equal to "true2"
      When endif
    When endif
    Then assert that "${test.foo}" is equal to "true2"
  
    
    
    

  Scenario: Simple Conditional behaviour 1 : not equal to
    Given set property "test.foo" equal to "false"
    When if "1" is numerically not equal to "1"
      Given set property "test.foo" equal to "true"
      When if "2" is numerically not equal to "2"
        Then debug print to scenario "true"
        Given set property "test.foo" equal to "true2"
      When endif
    When endif
    Then assert that "${test.foo}" is equal to "false"


  Scenario: Simple Conditional behaviour 2 : not equal to
    Given set property "test.foo" equal to "false"
    When if "2" is numerically not equal to "3"
      Given set property "test.foo" equal to "true"
      When if "2" is numerically not equal to "2"
        Then debug print to scenario "true"
        Given set property "test.foo" equal to "true2"
      When endif
    When endif
    Then assert that "${test.foo}" is equal to "true"

  Scenario: Simple Conditional behaviour 2-2 : not equal to
    Given set property "test.foo" equal to "false"
    Given set property "test.foo2" equal to "false"
    When if "2" is numerically not equal to "1"
      Given set property "test.foo" equal to "true"
      When if "3" is numerically not equal to "3"
        Then debug print to scenario "true"
        Given set property "test.foo" equal to "true2"
      When endif
      Given set property "test.foo2" equal to "true3"
      When if "4" is numerically not equal to "4"
        Then debug print to scenario "true"
        Given set property "test.foo2" equal to "true4"
      When endif
    When endif
    Then assert that "${test.foo}:${test.foo2}" is equal to "true:true3"


  Scenario: Simple Conditional behaviour 3 : not equal to
    Given set property "test.foo" equal to "false"
    When if "2" is numerically not equal to "1"
      Given set property "test.foo" equal to "true"
      When if "3" is numerically not equal to "2"
        Then debug print to scenario "true"
        Given set property "test.foo" equal to "true2"
      When endif
    When endif
    Then assert that "${test.foo}" is equal to "true2"
    
    
    
    
    
    
  Scenario: Simple Conditional behaviour 1 : greater than
    Given set property "test.foo" equal to "false"
    When if "1" is numerically greater than "2"
      Given set property "test.foo" equal to "true"
      When if "2" is numerically greater than "3"
        Then debug print to scenario "true"
        Given set property "test.foo" equal to "true2"
      When endif
    When endif
    Then assert that "${test.foo}" is equal to "false"


  Scenario: Simple Conditional behaviour 2 : greater than
    Given set property "test.foo" equal to "false"
    When if "2" is numerically greater than "1"
      Given set property "test.foo" equal to "true"
      When if "2" is numerically greater than "3"
        Then debug print to scenario "true"
        Given set property "test.foo" equal to "true2"
      When endif
    When endif
    Then assert that "${test.foo}" is equal to "true"

  Scenario: Simple Conditional behaviour 2-2 : greater than
    Given set property "test.foo" equal to "false"
    Given set property "test.foo2" equal to "false"
    When if "2" is numerically greater than "1"
      Given set property "test.foo" equal to "true"
      When if "2" is numerically greater than "3"
        Then debug print to scenario "true"
        Given set property "test.foo" equal to "true2"
      When endif
      Given set property "test.foo2" equal to "true3"
      When if "3" is numerically greater than "4"
        Then debug print to scenario "true"
        Given set property "test.foo2" equal to "true4"
      When endif
    When endif
    Then assert that "${test.foo}:${test.foo2}" is equal to "true:true3"


  Scenario: Simple Conditional behaviour 3 : greater than
    Given set property "test.foo" equal to "false"
    When if "2" is numerically greater than "1"
      Given set property "test.foo" equal to "true"
      When if "3" is numerically greater than "2"
        Then debug print to scenario "true"
        Given set property "test.foo" equal to "true2"
      When endif
    When endif
    Then assert that "${test.foo}" is equal to "true2"




  Scenario: Simple Conditional behaviour 1 : greater than or equal to
    Given set property "test.foo" equal to "false"
    When if "1" is numerically greater than or equal to "2"
      Given set property "test.foo" equal to "true"
        When if "2" is numerically greater than or equal to "3"
          Then debug print to scenario "true"
        Given set property "test.foo" equal to "true2"
      When endif
    When endif
    Then assert that "${test.foo}" is equal to "false"


  Scenario: Simple Conditional behaviour 2 : greater than or equal to
    Given set property "test.foo" equal to "false"
    When if "2" is numerically greater than or equal to "1"
      Given set property "test.foo" equal to "true"
      When if "2" is numerically greater than or equal to "3"
        Then debug print to scenario "true"
      Given set property "test.foo" equal to "true2"
      When endif
    When endif
    Then assert that "${test.foo}" is equal to "true"

  Scenario: Simple Conditional behaviour 2-2 : greater than or equal to
    Given set property "test.foo" equal to "false"
    Given set property "test.foo2" equal to "false"
    When if "2" is numerically greater than or equal to "1"
      Given set property "test.foo" equal to "true"
      When if "2" is numerically greater than or equal to "3"
        Then debug print to scenario "true"
        Given set property "test.foo" equal to "true2"
      When endif
      Given set property "test.foo2" equal to "true3"
      When if "3" is numerically greater than or equal to "4"
        Then debug print to scenario "true"
        Given set property "test.foo2" equal to "true4"
      When endif
    When endif
    Then assert that "${test.foo}:${test.foo2}" is equal to "true:true3"


  Scenario: Simple Conditional behaviour 3 : greater than or equal to
    Given set property "test.foo" equal to "false"
    When if "2" is numerically greater than or equal to "1"
      Given set property "test.foo" equal to "true"
      When if "3" is numerically greater than or equal to "2"
        Then debug print to scenario "true"
      Given set property "test.foo" equal to "true2"
      When endif
    When endif
    Then assert that "${test.foo}" is equal to "true2"



    
    
    
  Scenario: Simple Conditional behaviour 1 : less than
    Given set property "test.foo" equal to "false"
    When if "2" is numerically less than "1"
      Given set property "test.foo" equal to "true"
      When if "3" is numerically less than "2"
        Then debug print to scenario "true"
        Given set property "test.foo" equal to "true2"
      When endif
    When endif
    Then assert that "${test.foo}" is equal to "false"


  Scenario: Simple Conditional behaviour 2 : less than
    Given set property "test.foo" equal to "false"
    When if "1" is numerically less than "2"
      Given set property "test.foo" equal to "true"
      When if "3" is numerically less than "2"
        Then debug print to scenario "true"
        Given set property "test.foo" equal to "true2"
      When endif
    When endif
    Then assert that "${test.foo}" is equal to "true"

  Scenario: Simple Conditional behaviour 2-2 : less than
    Given set property "test.foo" equal to "false"
    Given set property "test.foo2" equal to "false"
    When if "1" is numerically less than "2"
      Given set property "test.foo" equal to "true"
      When if "3" is numerically less than "2"
        Then debug print to scenario "true"
        Given set property "test.foo" equal to "true2"
      When endif
      Given set property "test.foo2" equal to "true3"
      When if "4" is numerically less than "3"
        Then debug print to scenario "true"
        Given set property "test.foo2" equal to "true4"
      When endif
    When endif
    Then assert that "${test.foo}:${test.foo2}" is equal to "true:true3"


  Scenario: Simple Conditional behaviour 3 : less than
    Given set property "test.foo" equal to "false"
    When if "1" is numerically less than "2"
      Given set property "test.foo" equal to "true"
      When if "2" is numerically less than "3"
        Then debug print to scenario "true"
        Given set property "test.foo" equal to "true2"
      When endif
    When endif
    Then assert that "${test.foo}" is equal to "true2"




  Scenario: Simple Conditional behaviour 1 : less than or equal to
    Given set property "test.foo" equal to "false"
    When if "2" is numerically less than or equal to "1"
      Given set property "test.foo" equal to "true"
        When if "3" is numerically less than or equal to "2"
          Then debug print to scenario "true"
        Given set property "test.foo" equal to "true2"
      When endif
    When endif
    Then assert that "${test.foo}" is equal to "false"


  Scenario: Simple Conditional behaviour 2 : less than or equal to
    Given set property "test.foo" equal to "false"
    When if "1" is numerically less than or equal to "2"
      Given set property "test.foo" equal to "true"
      When if "3" is numerically less than or equal to "2"
        Then debug print to scenario "true"
      Given set property "test.foo" equal to "true2"
      When endif
    When endif
    Then assert that "${test.foo}" is equal to "true"

  Scenario: Simple Conditional behaviour 2-2 : less than or equal to
    Given set property "test.foo" equal to "false"
    Given set property "test.foo2" equal to "false"
    When if "1" is numerically less than or equal to "2"
      Given set property "test.foo" equal to "true"
      When if "3" is numerically less than or equal to "2"
        Then debug print to scenario "true"
        Given set property "test.foo" equal to "true2"
      When endif
      Given set property "test.foo2" equal to "true3"
      When if "4" is numerically less than or equal to "3"
        Then debug print to scenario "true"
        Given set property "test.foo2" equal to "true4"
      When endif
    When endif
    Then assert that "${test.foo}:${test.foo2}" is equal to "true:true3"


  Scenario: Simple Conditional behaviour 3 : less than or equal to
    Given set property "test.foo" equal to "false"
    When if "1" is numerically less than or equal to "2"
      Given set property "test.foo" equal to "true"
      When if "2" is numerically less than or equal to "2"
        Then debug print to scenario "true"
      Given set property "test.foo" equal to "true2"
      When endif
    When endif
    Then assert that "${test.foo}" is equal to "true2"


  Scenario: Very simple file size syntax check
    Given set property "test.foo" equal to the file size of "src/test/resources/features/Conditional.feature"
    Then assert that file "pom.xml" is binary equal to file "pom.xml"


  Scenario: Simple Conditional behaviour 4-1 : String not empty
    Given set property "test.foo" equal to "false"
    When if string "" is not empty
    Given set property "test.foo" equal to "true2"
    When endif
    Then assert that "${test.foo}" is equal to "false"

  Scenario: Simple Conditional behaviour 4-2 : String not empty
    Given set property "test.foo" equal to "false"
    When if string "1234" is not empty
    Given set property "test.foo" equal to "true2"
    When endif
    Then assert that "${test.foo}" is equal to "true2"
