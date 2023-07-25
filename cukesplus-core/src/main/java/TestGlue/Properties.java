package TestGlue;

import com.replicanet.cukesplus.IgnoreConditionalExecution;
import com.replicanet.cukesplus.PropertiesResolution;
import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;

public class Properties {
    static Scenario scenario;
    @Before
    public void beforeHook(Scenario scenario) {
        this.scenario = scenario;
    }

    @Given("^set property \"([^\"]*)\" equal to \"([^\"]*)\"$")
    public void set_property_equal_to(String arg1, String arg2) throws Throwable {
        arg1 = PropertiesResolution.resolveInput(scenario,arg1);
        arg2 = PropertiesResolution.resolveInput(scenario,arg2);

        System.setProperty(arg1,arg2);
    }
}
