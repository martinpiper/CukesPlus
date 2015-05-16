CukesPlus
=========

Adds functionality to Cucumber. This is accomplished by hooking into Cucumber before and while it runs.

- Main CLI entry point is now com.replicanet.cukesplus.Main (instead of cucumber.api.cli.Main)
- JUnit RunWith class is now com.replicanet.cukesplus.junit.CucumberPlus (instead of cucumber.api.junit.Cucumber)

* Extracts glue regex and methods before execution.
