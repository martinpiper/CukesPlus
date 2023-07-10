import com.replicanet.cukesplus.junit.CucumberPlusOptions;
import cucumber.api.CucumberOptions;
import com.replicanet.cukesplus.junit.CucumberPlus;
import org.junit.runner.RunWith;

@RunWith(CucumberPlus.class)
@CucumberPlusOptions(properties = {"some.properties"})
@CucumberOptions(monochrome = true , glue = {"macros","TestGlue"}, format = {"pretty" , "html:target/cucumber"} , features = "features/test.feature")
public class RunCukesPlusTest
{
}
