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
    private List<ParameterInfo> parameterInfos;

    public MacroStepDefinition(Pattern pattern, long timeoutMillis) {
        this.pattern = pattern;
        this.timeoutMillis = timeoutMillis;
        this.argumentMatcher = new JdkPatternArgumentMatcher(pattern);
    }

    // Originally from cucumber.runtime.java.JavaStepDefinition
    public void execute(I18n i18n, Object[] args) throws Throwable {
        // TODO: Implement some tracking of this macro based step?
        int i = 0;
    }

    public List<Argument> matchedArguments(Step step) {
        return this.argumentMatcher.argumentsFrom(step.getName());
    }

    public String getLocation(boolean detail) {
//        MethodFormat format = detail ? MethodFormat.FULL : MethodFormat.SHORT;
//        return format.format(this.method);
        return "MacroStepDefinition.fake";
    }

    public Integer getParameterCount() {
        return this.parameterInfos.size();
    }

    public ParameterInfo getParameterType(int n, Type argumentType) {
        return (ParameterInfo)this.parameterInfos.get(n);
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
