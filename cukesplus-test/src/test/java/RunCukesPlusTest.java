import cucumber.api.CucumberOptions;
import com.replicanet.cukesplus.junit.CucumberPlus;
import org.junit.runner.RunWith;

@RunWith(CucumberPlus.class)
@CucumberOptions(monochrome = true , glue = "TestGlue", format = {"pretty" , "html:target/cucumber"} , features = "features")
public class RunCukesPlusTest
{
}
