package TestGlue;

import com.replicanet.cukesplus.PropertiesResolution;
import cucumber.api.Scenario;
import cucumber.api.java.Before;
import cucumber.api.java.en.When;

public class DebugPrint {
    Scenario scenario;
    @Before
    public void beforeHook(Scenario scenario) {
        this.scenario = scenario;
    }

    @When("^debug print to scenario \"(.*)\"$")
    public void debugPrintToScenario(String value) throws Throwable {
        scenario.write(PropertiesResolution.resolveInput(scenario,value));
    }
}
