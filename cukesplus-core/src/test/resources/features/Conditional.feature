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


  Scenario: Complex text block matching
    Given set property "test.foo" equal to:
      """
      .C:0452  EA          NOP
      .C:0453  EA          NOP
      .C:0454  EA          NOP
      .C:0455  AC 00 DD    LDY $DD00
      .C:0458  B9 00 06    LDA $0600,Y
      .C:045b  AC 00 DD    LDY $DD00
      .C:045e  19 08 06    ORA $0608,Y
      .C:0461  AC 00 DD    LDY $DD00
      .C:0464  19 10 06    ORA $0610,Y
      .C:0467  AC 00 DD    LDY $DD00
      .C:046a  19 18 06    ORA $0618,Y
      .C:046d  60          RTS
      .C:046e  41 52       EOR ($52,X)
      .C:0470  4D 41 4C    EOR $4C41
      """

#    Given set property "test.foo" equal to "${test.foo}" aligning to single newlines
#    Given set property "test.foo" where "${test.foo}" contains the string "LDY $DD00" until it contains the string "RTS"
    * if string "${test.foo}" matches regex ".*LD[XY] \$DD00.*\R.*LDA \$....,[XY].*\R.*LD[XY] \$DD00.*\R.*ORA \$....,[XY].*\R.*LD[XY] \$DD00.*\R.*ORA \$....,[XY].*\R.*LD[XY] \$DD00.*\R.*ORA \$....,[XY].*\R"
    * debug print to scenario "matched!"
    * endif
