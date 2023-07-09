CukesPlus
=========

If the java compiler has the additional command line option "-parameters" this will allow the parameter name to be used in addition to the type when producing auto complete hints.
This option is included in the root pom.xml

Adds functionality to Cucumber. This is accomplished by hooking into Cucumber before and while it runs.

- Main CLI entry point is now com.replicanet.cukesplus.Main (instead of cucumber.api.cli.Main)
- JUnit RunWith class is now com.replicanet.cukesplus.junit.CucumberPlus (instead of cucumber.api.junit.Cucumber)

* Extracts glue regex and methods before execution.

* Set system property "com.replicanet.cukesplus.server.featureEditor" to start the web based feature file editor.

To enable parameter names in the pom.xml file:

	<build>
		<plugins>
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-compiler-plugin</artifactId>
				<version>3.3</version>
				<configuration>
					<compilerArgs>
						<arg>-parameters</arg>
					</compilerArgs>
				</configuration>
			</plugin>
		</plugins>
	</build>


To enable parameter names in IntelliJ:

	* Select the module in the project tree view
	* File->Settings (Ctrl+Alt+S)
	* Build, Execution, Deployment -> Compiler -> Java Compiler
	* Additional command line parameters: -parameters



If using Selenium and ChromeDriver other than in a default place in the path, remember these extra java options:

    -Dwebdriver.chrome.driver="C:\Temp\chromedriver.exe"

