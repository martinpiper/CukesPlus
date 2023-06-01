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
    private Method method;
    Pattern pattern;
    private long timeoutMillis;
    private JdkPatternArgumentMatcher argumentMatcher;
    private List<ParameterInfo> parameterInfos;

    public MacroStepDefinition(Pattern pattern, long timeoutMillis) {
        method = getMethodCopy("NewNameHere");
        this.pattern = pattern;
        this.timeoutMillis = timeoutMillis;
        this.argumentMatcher = new JdkPatternArgumentMatcher(pattern);
        this.parameterInfos = ParameterInfo.fromMethod(method);
    }

    java.lang.reflect.Method getMethod() {
        return method;
    }

    public Method getMethodCopy(String newName) {

        // This uses a very hacky way to get a new Method and alter its contents
        Method method = null;
        try {
            method = this.getClass().getDeclaredMethod("getMethodCopy", String.class);
        } catch (NoSuchMethodException e) {
        }

        try {
            Method methodCopy = Method.class.getDeclaredMethod("leafCopy");
            methodCopy.setAccessible(true);
            Object obj = methodCopy.invoke(method);
            method = (Method) obj;
        } catch (Exception e) {
        }

        method.setAccessible(true);

        try {
            Field f = method.getClass().getDeclaredField("name");
            f.setAccessible(true);
            f.set(method, new String(newName));
        } catch (Exception e) {
        }

        return method;
    }

    // Originally from cucumber.runtime.java.JavaStepDefinition
    public void execute(I18n i18n, Object[] args) throws Throwable {
        // TODO: Implement some execution of this macro based step
        int i = 0;

        // Test injection of new runtime step
        Step step = new Step(null,"Given", "I run this test", 1234,null,null);
//        ExtensionRuntime.runtime.runMacroStep("path/fakeMacro.macro", step, ExtensionRuntime.reporter, ExtensionRuntime.i18n);
    }

    public List<Argument> matchedArguments(Step step) {
        return this.argumentMatcher.argumentsFrom(step.getName());
    }

    public String getLocation(boolean detail) {
        MethodFormat format = detail ? MethodFormat.FULL : MethodFormat.SHORT;
        return format.format(this.method);
    }

    public Integer getParameterCount() {
        return this.parameterInfos.size();
    }

    public ParameterInfo getParameterType(int n, Type argumentType) {
        return (ParameterInfo)this.parameterInfos.get(n);
    }

    public boolean isDefinedAt(StackTraceElement e) {
        return e.getClassName().equals(this.method.getDeclaringClass().getName()) && e.getMethodName().equals(this.method.getName());
    }

    public String getPattern() {
        return this.pattern.pattern();
    }

    public boolean isScenarioScoped() {
        return false;
    }
}
