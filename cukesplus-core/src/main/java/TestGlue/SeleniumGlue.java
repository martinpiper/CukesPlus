package TestGlue;

import com.replicanet.cukesplus.PropertiesResolution;
import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Martin on 15/11/2015.
 */
public class SeleniumGlue
{
	static WebDriver driver = null;
	Scenario scenario;

	@Before
	public void beforeHook(Scenario scenario) {
		this.scenario = scenario;
	}


	@After
	public void afterHook(Scenario scenario)
	{
		if (scenario.isFailed())
		{
			screenshotToReport(scenario);
		}
		if (System.getProperty("com.replicanet.cukesplus.recording.selenium") != null)
		{
			JavascriptExecutor js = (JavascriptExecutor) driver;

			// This code sits in the browser and monitors mouse clicks and key presses.
			// Java Selenium will then grab these events and process them into steps to report back to the ACEServer feature editor.
			// TODO: Load this code from a resource
			/*

$x = function(xpath, opt_startNode)
    {
        var doc = (opt_startNode && opt_startNode.ownerDocument) || document;
        var result = doc.evaluate(xpath, opt_startNode || doc, null, XPathResult.ANY_TYPE, null);
        switch (result.resultType) {
        case XPathResult.NUMBER_TYPE:
            return result.numberValue;
        case XPathResult.STRING_TYPE:
            return result.stringValue;
        case XPathResult.BOOLEAN_TYPE:
            return result.booleanValue;
        default:
            var nodes = [];
            var node;
            while (node = result.iterateNext())
                nodes.push(node);
            return nodes;
        }
    }

eventLog = [];
elementsToMatch = [
"//*[@id='layout']/tbody/tr[1]/td/form/table/tbody/tr[1]/td[2]/input" ,
"//*[@id='layout']/tbody/tr[1]/td/form/table/tbody/tr[2]/td[2]/input"
];
window.onclick = function(e){
eventLog.push("Left mouse click:");
for (var i = 0 ; i < elementsToMatch.length ; i++) { if ($x(elementsToMatch[i])[0] == e.toElement) { eventLog.push("Matched with: " + elementsToMatch[i]); } }
console.log(eventLog);
}
window.onkeypress = function(e){
eventLog.push("keyCode: " + e.keyCode + " keyIdentifier: " + e.keyIdentifier);
console.log(eventLog);
}
			*/
			String code =
							"$x = function(xpath, opt_startNode)\n" +
									"    {\n" +
									"        var doc = (opt_startNode && opt_startNode.ownerDocument) || document;\n" +
									"        var result = doc.evaluate(xpath, opt_startNode || doc, null, XPathResult.ANY_TYPE, null);\n" +
									"        switch (result.resultType) {\n" +
									"        case XPathResult.NUMBER_TYPE:\n" +
									"            return result.numberValue;\n" +
									"        case XPathResult.STRING_TYPE:\n" +
									"            return result.stringValue;\n" +
									"        case XPathResult.BOOLEAN_TYPE:\n" +
									"            return result.booleanValue;\n" +
									"        default:\n" +
									"            var nodes = [];\n" +
									"            var node;\n" +
									"            while (node = result.iterateNext())\n" +
									"                nodes.push(node);\n" +
									"            return nodes;\n" +
									"        }\n" +
									"    }\n" +
									"\n" +
									"eventLog = [];\n" +
									"elementsToMatch = [\n" +
									"\"//*[@id='layout']/tbody/tr[1]/td/form/table/tbody/tr[1]/td[2]/input\" ,\n" +
									"\"//*[@id='layout']/tbody/tr[1]/td/form/table/tbody/tr[2]/td[2]/input\"\n" +
									"];\n" +
									"window.onclick = function(e){\n" +
									"eventLog.push(\"Left mouse click:\");\n" +
									"for (var i = 0 ; i < elementsToMatch.length ; i++) { if ($x(elementsToMatch[i])[0] == e.toElement) { eventLog.push(\"Matched with: \" + elementsToMatch[i]); } }\n" +
									"console.log(eventLog);\n" +
									"}\n" +
									"window.onkeypress = function(e){\n" +
									"eventLog.push(\"keyCode: \" + e.keyCode + \" keyIdentifier: \" + e.keyIdentifier);\n" +
									"console.log(eventLog);\n" +
									"}";
			js.executeScript(code);

			ArrayList<String> previousEvents = new ArrayList<>();
			boolean recording = true;
			while(recording)
			{
				try
				{
					js = (JavascriptExecutor) driver;
					code = "return eventLog;";
					Object ret = js.executeScript(code);
					ArrayList<String> events = (ArrayList<String>) ret;

					if (!CollectionUtils.isEqualCollection(previousEvents,events))
					{
						System.out.println("");
						System.out.println("New list...");
						String toRet = "";
						for (String event : events)
						{
							System.out.println(event);
							if (!toRet.isEmpty())
							{
								toRet += " ,\n";
							}
							toRet += "\"" + StringEscapeUtils.escapeJava(event) + "\"";
						}
						previousEvents = events;

						try
						{
							File outEvents = new File("target/events.txt");
							FileUtils.writeStringToFile(outEvents, toRet);
						}
						catch(Exception e)
						{
						}
					}

					Thread.sleep(1000);
				}
				catch (Exception e)
				{
					recording = false;
				}
			}
		}
		try
		{
			driver.close();
		}
		catch (Exception e)
		{
		}
		try
		{
			driver.quit();
		}
		catch (Exception e)
		{
		}
		driver = null;
	}

	private static void screenshotToReport(Scenario scenario) {
		TakesScreenshot ts = (TakesScreenshot) driver;

		byte[] src = ts.getScreenshotAs(OutputType.BYTES);
		scenario.embed(src, "image/png");
	}

	@Given("^open the web page \"([^\"]*)\"$")
	public void iOpenTheWebPage(String url) throws Throwable
	{
		url = PropertiesResolution.resolveInput(scenario, url);

		driver = new ChromeDriver();
		driver.get(url);
	}

	@When("^click on the web element \"([^\"]*)\"$")
	public void iClickOnTheWebElementLayout(String locator) throws Throwable
	{
		locator = PropertiesResolution.resolveInput(scenario, locator);

		WebElement webElement = driver.findElement(By.xpath(locator));
		webElement.click();
	}

	@When("^enter text \"([^\"]*)\" into web element \"([^\"]*)\"$")
	public void iEnterTextIntoWebElement(String text, String locator) throws Throwable
	{
		text = PropertiesResolution.resolveInput(scenario, text);
		locator = PropertiesResolution.resolveInput(scenario, locator);

		WebElement webElement = driver.findElement(By.xpath(locator));
		webElement.sendKeys(text);
	}

	@When("^take web page screenshot$")
	public void takeWebBrowserScreenshot() throws Throwable
	{
		screenshotToReport(scenario);
	}
}
