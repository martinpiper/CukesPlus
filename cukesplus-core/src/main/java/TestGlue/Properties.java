package TestGlue;

import com.replicanet.cukesplus.IgnoreConditionalExecution;
import com.replicanet.cukesplus.PropertiesResolution;
import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import org.apache.commons.io.FileUtils;

public class Properties {
    static int testIteration = 0;
    static Scenario scenario;
    @Before
    public void beforeHook(Scenario scenario) {
        this.scenario = scenario;

        testIteration++;
        System.setProperty("test.cukesplus.testIteration" , Integer.toString(testIteration));
        System.out.println("Set test.cukesplus.testIteration="+Integer.toString(testIteration));
    }


    @Given("^set property \"(.*)\" equal to \"(.*)\"$")
    public void set_property_equal_to(String propertyName, String theValue) throws Throwable {
        propertyName = PropertiesResolution.resolveInput(scenario,propertyName);
        theValue = PropertiesResolution.resolveInput(scenario,theValue);

        System.setProperty(propertyName,theValue);
    }

    @Given("^set property \"(.*)\" equal to \"(.*)\" aligning to single newlines$")
    public void set_property_aigning_to_newline(String propertyName, String theValue) throws Throwable {
        propertyName = PropertiesResolution.resolveInput(scenario,propertyName);
        theValue = PropertiesResolution.resolveInput(scenario,theValue);

        theValue = theValue.replaceAll("\r" , "\n");
        theValue = theValue.replaceAll("\n\n" , "\n");

        System.setProperty(propertyName,theValue);
    }

    @Given("^set property \"(.*)\" equal to:$")
    public void set_property_equal_to_block(String propertyName, String theValue) throws Throwable {
        propertyName = PropertiesResolution.resolveInput(scenario,propertyName);
        theValue = PropertiesResolution.resolveInput(scenario,theValue);

        System.setProperty(propertyName,theValue);
    }

    @Given("^set property \"(.*)\" equal to the file size of \"(.*)\"$")
    public void set_property_equal_to_the_file_size_of(String propertyName, String filename) throws Throwable {
        propertyName = PropertiesResolution.resolveInput(scenario,propertyName);
        filename = PropertiesResolution.resolveInput(scenario,filename);

        System.setProperty(propertyName, String.valueOf(FileUtils.getFile(filename).length()));
    }
}
