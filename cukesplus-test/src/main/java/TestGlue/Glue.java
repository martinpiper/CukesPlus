package TestGlue;

import cucumber.api.PendingException;
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

	@Given("^I am passing$")
	public void i_am_passing() throws Throwable
	{
//		throw new PendingException();
	}

	@Given("^I am pending$")
	public void i_am_pending() throws Throwable
	{
		throw new PendingException();
	}

//	@Given("^I am unimplemented")
	public void i_am_unimplemented() throws Throwable
	{
	}

	@Given("^I throw an exception$")
	public void i_throw_an_exception() throws Throwable
	{
		throw new Exception("Ooops!");
	}
}
