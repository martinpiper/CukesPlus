Feature: Tests Conditional behaviour

  Scenario: Simple Conditional behaviour 1
    Given set property "test.foo" equal to "false"
    When if "1" is numerically greater than "2"
      Given set property "test.foo" equal to "true"
      When if "2" is numerically greater than "3"
        Then debug print to scenario "true"
        Given set property "test.foo" equal to "true2"
      When endif
    When endif
    Then assert that "${test.foo}" is equal to "false"


  Scenario: Simple Conditional behaviour 2
    Given set property "test.foo" equal to "false"
    When if "2" is numerically greater than "1"
      Given set property "test.foo" equal to "true"
      When if "2" is numerically greater than "3"
        Then debug print to scenario "true"
        Given set property "test.foo" equal to "true2"
      When endif
    When endif
    Then assert that "${test.foo}" is equal to "true"

  Scenario: Simple Conditional behaviour 2-2
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


  Scenario: Simple Conditional behaviour 3
    Given set property "test.foo" equal to "false"
    When if "2" is numerically greater than "1"
      Given set property "test.foo" equal to "true"
      When if "3" is numerically greater than "2"
        Then debug print to scenario "true"
        Given set property "test.foo" equal to "true2"
      When endif
    When endif
    Then assert that "${test.foo}" is equal to "true2"
