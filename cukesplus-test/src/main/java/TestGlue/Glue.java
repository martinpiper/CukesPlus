package TestGlue;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;

/**
 *
 */
public class Glue
{
	@Given("^I run this test$")
	public void i_run_this_test() throws Throwable
	{
//		throw new PendingException();
	}

	@When("^this value (.*) matches the string \\(hello\\) then dance$")
	public void t1(String label) throws Throwable
	{
	}
}
