package TestGlue;

import com.replicanet.cukesplus.PropertiesResolution;
import cucumber.api.Scenario;
import cucumber.api.java.Before;
import cucumber.api.java.en.Then;
import org.apache.commons.io.FileUtils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class Files {
    static Scenario scenario;
    @Before
    public void beforeHook(Scenario scenario) {
        this.scenario = scenario;
    }

    @Then("^assert that file \"([^\"]*)\" is binary equal to file \"([^\"]*)\"$")
    public void assert_that_file_is_binary_equal_to_file(String filename1, String filename2) throws Throwable {
        filename1 = PropertiesResolution.resolveInput(scenario,filename1);
        filename2 = PropertiesResolution.resolveInput(scenario,filename2);

        byte[] fileData1 = FileUtils.readFileToByteArray(FileUtils.getFile(filename1));
        byte[] fileData2 = FileUtils.readFileToByteArray(FileUtils.getFile(filename2));
        assertThat(fileData1 , is(equalTo(fileData2)));
    }
}
