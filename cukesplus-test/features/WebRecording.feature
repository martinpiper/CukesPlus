@noerrors
Feature: Demonstrate web automation
  Scenario: A web test
    Given open the web page "${test.url}"
    When click on the web element "${test.account.name}"
    When enter text "hello" into web element "${test.account.name}"
    When enter text "a password" into web element "${test.account.password}"
    When take web page screenshot
