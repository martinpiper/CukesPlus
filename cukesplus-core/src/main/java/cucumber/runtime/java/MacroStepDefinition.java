package cucumber.runtime.java;

import com.replicanet.cukesplus.ExtensionRuntime;
import cucumber.runtime.*;
import gherkin.I18n;
import gherkin.formatter.Argument;
import gherkin.formatter.model.Step;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.regex.Pattern;

public class MacroStepDefinition implements StepDefinition {
    Pattern pattern;
    private long timeoutMillis;
    private JdkPatternArgumentMatcher argumentMatcher;
    private String location;

    public MacroStepDefinition(String location, Pattern pattern, long timeoutMillis) {
        this.location = location;
        this.pattern = pattern;
        this.timeoutMillis = timeoutMillis;
        this.argumentMatcher = new JdkPatternArgumentMatcher(pattern);
    }

    // Originally from cucumber.runtime.java.JavaStepDefinition
    public void execute(I18n i18n, Object[] args) throws Throwable {
        throw getRuntimeException();
    }

    private static RuntimeException getRuntimeException() {
        return new RuntimeException("This should never execute. If this tries to execute the AspectJ weaving failed for com.replicanet.cukesplus.AspectE.theRunStep. Try rebuilding the entire maven pom and reloading the project in IntelliJ to pickup the AspectJ woven classes.");
    }

    public List<Argument> matchedArguments(Step step) {
        return this.argumentMatcher.argumentsFrom(step.getName());
    }

    public String getLocation(boolean detail) {
        return "MacroStepDefinition." + location;
    }

    public Integer getParameterCount() {
        throw getRuntimeException();
    }

    public ParameterInfo getParameterType(int n, Type argumentType) {
        throw getRuntimeException();
    }

    public boolean isDefinedAt(StackTraceElement e) {
        return false;
    }

    public String getPattern() {
        return this.pattern.pattern();
    }

    public boolean isScenarioScoped() {
        return false;
    }
}
