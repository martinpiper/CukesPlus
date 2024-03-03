CukesPlus
=========

Adds lots of useful functionality to Java Cucumber. This is accomplished by hooking into Cucumber before and while it runs, along with some AspectJ compile time weaving to intercept and change Cucumber behaviour.

* Macros allow reusable blocks of new syntax to be created without changing java code
  * Provides abstraction of reusable behaviour using familiar step based syntax
  * Are much easier to understand and can be written by non-developers
  * Transparently reported behaviour instead of opaque java based steps
* Web based editor integrated into the framework
  * Provides code colouring
  * Test execution status
  * Step code completion hints
* Properties remapping for step parameters
* Syntax reporting provides reference documentation of steps and macros

Videos

[![Watch the video](https://img.youtube.com/vi/u-1dqnN10rE/1.jpg)](https://youtu.be/u-1dqnN10rE)
[![Watch the video](https://img.youtube.com/vi/fdeVc2q6oB0/1.jpg)](https://youtu.be/fdeVc2q6oB0)
[![Watch the video](https://img.youtube.com/vi/nQWzvSPWfV0/1.jpg)](https://youtu.be/nQWzvSPWfV0)
[![Watch the video](https://img.youtube.com/vi/-Ptq6ZY3Kxk/1.jpg)](https://youtu.be/-Ptq6ZY3Kxk)


Using
-----

* Test execution entry points:
  * Main CLI entry point is now com.replicanet.cukesplus.Main (instead of cucumber.api.cli.Main)
  * JUnit RunWith class is now com.replicanet.cukesplus.junit.CucumberPlus (instead of cucumber.api.junit.Cucumber)


* If the java compiler has the additional command line option "-parameters" this will allow the parameter name to be used in addition to the type when producing auto complete hints.
  * This option is included in the root pom.xml


* Extracts glue regex and methods before execution.


* Set system property "com.replicanet.cukesplus.server.featureEditor" to start the web based feature file editor.


* By default, the properties file "CukesPlus.properties" is loaded at runtime from the current execution directory.
  * The default properties file name can be set by the property: com.replicanet.cukesplus.default.properties
  * JUnit test runner classes can use the com.replicanet.cukesplus.junit.CucumberPlusOptions annotation to define properties files to load


* Property (variables) expansion, call explicit parameter parsing method
  * A parameter of "${test.url}" will be replaced using the value of the current System property "test.url"
  * Multiple property expansions can be in each parameter and interleaved with plain text
  * A good example is used in SeleniumGlue step methods:

        import com.replicanet.cukesplus.PropertiesResolution;
        text = PropertiesResolution.resolveInput(scenario, text);

  * See test cases: com.replicanet.cukesplus.PropertiesResolutionTest


* To enable parameter names in the pom.xml file, useful for the syntax report and code hints:

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


* To enable parameter names in IntelliJ:
  * Select the module in the project tree view
  * File->Settings (Ctrl+Alt+S)
  * Build, Execution, Deployment -> Compiler -> Java Compiler
  * Additional command line parameters: -parameters
    


* If using Selenium and ChromeDriver other than in a default place in the path, remember this extra **java** command line option. i.e. Before any -jar/-cp option:

        -Dwebdriver.chrome.driver="C:\Temp\chromedriver.exe"



* To generate html reports using the default Cucumber html report generator, use this command line option: 

        --plugin html:target/cucumber


* To generate a json report file, which can be used by the web editor or report generators, use this command line option:

        --plugin json:target/report1.json


* To enable pretty debug printing, use this command line option:

        --plugin pretty 



Building
--------

Maven should work without any issues. You'll need to build/install this first: https://github.com/martinpiper/ACEServer

* mvn clean install
* mvn -DskipTests=true clean package

