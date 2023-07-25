@noerrors
Feature: Demonstrate web automation

  This is a feature file, it contains test scenarios with expected behaviour

  Scenario: A web test
    Given a RNLobby website login page
    When attempting an incorrect login
    Then verify the login failed
