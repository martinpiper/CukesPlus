import cucumber.api.CucumberOptions;
import com.replicanet.cukesplus.junit.CucumberPlus;
import org.junit.runner.RunWith;

@RunWith(CucumberPlus.class)
@CucumberOptions(monochrome = true , glue = "TestGlue", format = {"pretty" , "html:target/cucumber"} , features = "features/test.feature")
public class RunCukesPlusTest
{
}
