package TestGlue;

import com.replicanet.cukesplus.IgnoreConditionalExecution;
import com.replicanet.cukesplus.PropertiesResolution;
import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.When;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Conditional {
    static Scenario scenario;
    static int level = 0;

    public static int getLevelToIgnore() {
        if (levelToIgnore > 0) {
            showSkipped();
        }
        return levelToIgnore;
    }

    static int levelToIgnore = 0;

    @Before
    public void beforeHook(Scenario scenario) {
        this.scenario = scenario;
        level = 0;
        levelToIgnore = 0;
    }


    @After
    public void afterHook(Scenario scenario) throws Exception {
        // We do not report exceptions in level if the scenario failed because the endif will of course not match the if level since it wasn't executed
        if (scenario.isFailed()) {
            return;
        }
        if (level != 0 || levelToIgnore != 0) {
            reportLevelException();
        }
    }

    private void reportLevelException() throws Exception {
        scenario.write("Conditional level is invalid (if and endif counts do not match?): level = " + level + " levelToIgnore = " + levelToIgnore);
        throw new Exception("Invalid Conditional level: " + level + " " + levelToIgnore);
    }

    @IgnoreConditionalExecution
    @When("^if \"(.*)\" is numerically equal to \"(.*)\"$")
    public void ifIsNumericallyEqualTo(String first, String second) throws Throwable {
        if (conditionalNeedsToReturnNow()) return;

        first = PropertiesResolution.resolveInput(scenario,first);
        second = PropertiesResolution.resolveInput(scenario,second);

        Double firstDouble = Double.parseDouble(first);
        Double secondDouble = Double.parseDouble(second);

        if (!(Double.compare(firstDouble,secondDouble) == 0)) {
            levelToIgnore++;
        }
    }

    @IgnoreConditionalExecution
    @When("^if \"(.*)\" is numerically not equal to \"(.*)\"$")
    public void ifIsNumericallyNotEqualTo(String first, String second) throws Throwable {
        if (conditionalNeedsToReturnNow()) return;

        first = PropertiesResolution.resolveInput(scenario,first);
        second = PropertiesResolution.resolveInput(scenario,second);

        Double firstDouble = Double.parseDouble(first);
        Double secondDouble = Double.parseDouble(second);

        if (!(Double.compare(firstDouble,secondDouble) != 0)) {
            levelToIgnore++;
        }
    }

    @IgnoreConditionalExecution
    @When("^if \"(.*)\" is numerically greater than \"(.*)\"$")
    public void ifIsNumericallyGreaterThan(String first, String second) throws Throwable {
        if (conditionalNeedsToReturnNow()) return;

        first = PropertiesResolution.resolveInput(scenario,first);
        second = PropertiesResolution.resolveInput(scenario,second);

        Double firstDouble = Double.parseDouble(first);
        Double secondDouble = Double.parseDouble(second);

        if (!(Double.compare(firstDouble,secondDouble) > 0)) {
            levelToIgnore++;
        }
    }

    @IgnoreConditionalExecution
    @When("^if \"(.*)\" is numerically greater than or equal to \"(.*)\"$")
    public void ifIsNumericallyGreaterThanOrEqualTo(String first, String second) throws Throwable {
        if (conditionalNeedsToReturnNow()) return;

        first = PropertiesResolution.resolveInput(scenario,first);
        second = PropertiesResolution.resolveInput(scenario,second);

        Double firstDouble = Double.parseDouble(first);
        Double secondDouble = Double.parseDouble(second);

        if (!(Double.compare(firstDouble,secondDouble) >= 0)) {
            levelToIgnore++;
        }
    }

    @IgnoreConditionalExecution
    @When("^if \"(.*)\" is numerically less than \"(.*)\"$")
    public void ifIsNumericallyLessThan(String first, String second) throws Throwable {
        if (conditionalNeedsToReturnNow()) return;

        first = PropertiesResolution.resolveInput(scenario,first);
        second = PropertiesResolution.resolveInput(scenario,second);

        Double firstDouble = Double.parseDouble(first);
        Double secondDouble = Double.parseDouble(second);

        if (!(Double.compare(firstDouble,secondDouble) < 0)) {
            levelToIgnore++;
        }
    }

    @IgnoreConditionalExecution
    @When("^if \"(.*)\" is numerically less than or equal to \"(.*)\"$")
    public void ifIsNumericallyLessThanOrEqualTo(String first, String second) throws Throwable {
        if (conditionalNeedsToReturnNow()) return;

        first = PropertiesResolution.resolveInput(scenario,first);
        second = PropertiesResolution.resolveInput(scenario,second);

        Double firstDouble = Double.parseDouble(first);
        Double secondDouble = Double.parseDouble(second);

        if (!(Double.compare(firstDouble,secondDouble) <= 0)) {
            levelToIgnore++;
        }
    }

    @IgnoreConditionalExecution
    @When("^if string \"(.*)\" is not empty$")
    public void if_string_is_not_empty(String stringValue) throws Throwable {
        if (conditionalNeedsToReturnNow()) return;

        stringValue = PropertiesResolution.resolveInput(scenario,stringValue);

//        System.out.println("*** Debug: '"+stringValue+"'");

        if (stringValue.isEmpty()) {
            levelToIgnore++;
        }
    }

    @IgnoreConditionalExecution
    @When("^if string \"(.*)\" matches regex \"(.*)\"$")
    public void if_string_matches_regex(String first, String second) throws Throwable {
        if (conditionalNeedsToReturnNow()) return;

        first = PropertiesResolution.resolveInput(scenario,first);
        second = PropertiesResolution.resolveInput(scenario,second);

        Pattern pattern = Pattern.compile(second , Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(first);
//        System.out.println("*** Debug: '"+stringValue+"'");

        if (!matcher.find()) {
            levelToIgnore++;
        }
    }


    private static boolean conditionalNeedsToReturnNow() {
        level++;

        if (levelToIgnore > 0) {
            // Ignore any other conditional checks if there is already an ignored state active
            // This check is needed because of @IgnoreConditionalExecution
            showSkipped();
            levelToIgnore++;
            return true;
        }
        return false;
    }


    @IgnoreConditionalExecution
    @When("^endif$")
    public void endif() throws Throwable {
        if (levelToIgnore > 0) {
            levelToIgnore--;
            showSkipped();
        }

        level--;
        if (level < 0) {
            reportLevelException();
        }
    }

    private static void showSkipped() {
        if (levelToIgnore > 0) {
            scenario.write("Skipped level: " + levelToIgnore);
        }
    }
}
