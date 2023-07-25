package TestGlue;

import com.replicanet.cukesplus.PropertiesResolution;
import cucumber.api.Scenario;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class Assert {
    static Scenario scenario;
    @Before
    public void beforeHook(Scenario scenario) {
        this.scenario = scenario;
    }

    @Then("^assert that \"(.*)\" is equal to \"(.*)\"$")
    public void assert_that_is_equal_to(String arg1, String arg2) throws Throwable {
        arg1 = PropertiesResolution.resolveInput(scenario,arg1);
        arg2 = PropertiesResolution.resolveInput(scenario,arg2);

        assertThat(arg1 , is(equalTo(arg2)));
    }
}
