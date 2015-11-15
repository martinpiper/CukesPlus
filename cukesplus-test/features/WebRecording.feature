Feature: Demonstrate web recording
  Scenario: A web test
    Given I open the web page "http://www.rnlobby.com/test/Login.asp"
    When I click on the web element "//*[@id='layout']/tbody/tr[1]/td/form/table/tbody/tr[1]/td[2]/input"
    When I enter text "hello" into web element "//*[@id='layout']/tbody/tr[1]/td/form/table/tbody/tr[1]/td[2]/input"
    When I click on the web element "//*[@id='layout']/tbody/tr[1]/td/form/table/tbody/tr[2]/td[2]/input"
