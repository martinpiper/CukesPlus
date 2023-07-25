@noerrors
Feature: Demonstrate web automation
  Scenario: A web test
    Given open the web page "${test.url}"
    When get text from web element "${test.account.header}" and set property "test.value"
    Then assert that "${test.value}" is equal to "Login to an account"
    When click on the web element "${test.account.name}"
    When enter text "hello" into web element "${test.account.name}"
    When enter text "a password" into web element "${test.account.password}"
    When take web page screenshot
    When get text attribute "value" from web element "${test.account.password}" and set property "test.value"
    Then assert that "${test.value}" is equal to "a password"
