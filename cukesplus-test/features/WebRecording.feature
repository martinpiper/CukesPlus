@noerrors
Feature: Demonstrate web recording
  Scenario: A web test
    Given open the web page "http://www.rnlobby.com/test/Login.asp"
    When click on the web element "//*[@id='layout']/tbody/tr[1]/td/form/table/tbody/tr[1]/td[2]/input"
    When enter text "hello" into web element "//*[@id='layout']/tbody/tr[1]/td/form/table/tbody/tr[1]/td[2]/input"
    When click on the web element "//*[@id='layout']/tbody/tr[1]/td/form/table/tbody/tr[2]/td[2]/input"
    When take web browser screenshot
