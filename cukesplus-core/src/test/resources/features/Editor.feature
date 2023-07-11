Feature: Tests the web editor functionality


  Scenario: Create and rune a new feature file
    Given a new web editor instance running in "target/WebEditorInstance"
      Given ensure an empty directory in "target/WebEditorInstance"
      Given ensure an empty directory in "target/WebEditorInstance/features"
      Given ensure an empty directory in "target/WebEditorInstance/macros"
      Given start web editor in "target/WebEditorInstance" without waiting
      When open the web page "http://127.0.0.1:8001/ace-builds-master/demo/autocompletion.html"

    When web editor edit new file "macros/test.macros"
    When web editor type in file:
      """
      Given Different errors two
        Given I am pending



      Given Different errors three
        Given I am unimplemented



      Given Different errors four
        Given I throw an exception
      """
    When web editor save file

    When web editor edit new file "features/states.feature"
    When web editor type in file:
      """
      Feature: Test1

        Scenario: Different errors 1
          Given I am passing

        Scenario: Different errors 2
          Given I am pending

        Scenario: Different errors 3
          Given I am unimplemented

        Scenario: Using macros 2
          Given Different errors two

        Scenario: Using macros 3
          Given Different errors three

        Scenario: Using macros 4
          Given Different errors four
      """
    When web editor save file
    When web editor run file
