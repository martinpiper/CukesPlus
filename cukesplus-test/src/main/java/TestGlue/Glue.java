package TestGlue;

import cucumber.api.DataTable;
import cucumber.api.PendingException;
import cucumber.api.Scenario;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;

/**
 *
 */
public class Glue
{
	Scenario scenario;
	@Before
	public void before(Scenario scenario) {
		this.scenario = scenario;
	}

	@Given("^I run this test$")
	public void i_run_this_test() throws Throwable {
		scenario.write("This is: I run this test");
//		throw new PendingException();
	}

	@When("^this value (.*) matches the string \\(hello\\) then dance$")
	public void t1(String label) throws Throwable {
		scenario.write("This is: " + label);
	}

	@Given("^I am passing$")
	public void i_am_passing() throws Throwable {
	scenario.write("This is: passing");
//		throw new PendingException();
	}

	@Given("^I am pending$")
	public void i_am_pending() throws Throwable {
		scenario.write("This is: pending");
		throw new PendingException();
	}

//	@Given("^I am unimplemented")
	public void i_am_unimplemented() throws Throwable {
		scenario.write("This is: unimplemented");
	}

	@Given("^I throw an exception$")
	public void i_throw_an_exception() throws Throwable {
		scenario.write("This is: Exception");
		throw new Exception("Ooops!");
	}

	@Given("^some data table:$")
	public void someDataTable(DataTable table) {
		for (String value : table.asList(String.class)) {
			scenario.write("This is: data table: " + value);
		}
	}

	@Given("^some text block:$")
	public void someTextBlock(String text) {
		scenario.write("This is: text: " + text);
	}
}
