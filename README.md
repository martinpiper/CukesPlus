CukesPlus
=========

If the java compiler has the additional command line option "-parameters" this will allow the parameter name to be used in addition to the type when producing auto complete hints.
This option is included in the root pom.xml

Adds functionality to Cucumber. This is accomplished by hooking into Cucumber before and while it runs.

- Main CLI entry point is now com.replicanet.cukesplus.Main (instead of cucumber.api.cli.Main)
- JUnit RunWith class is now com.replicanet.cukesplus.junit.CucumberPlus (instead of cucumber.api.junit.Cucumber)

* Extracts glue regex and methods before execution.
