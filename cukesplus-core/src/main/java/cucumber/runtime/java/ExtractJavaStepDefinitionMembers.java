package cucumber.runtime.java;

import cucumber.runtime.ParameterInfo;
import cucumber.runtime.StepDefinition;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * To enable access to cucumber.runtime.java.JavaStepDefinition
 */
public class ExtractJavaStepDefinitionMembers
{
	// If any of these methods fails then null is returned and the code will gracefully fall back to a less descriptive or featured execution

	public static Method extractMethod(StepDefinition step)
	{
		try
		{
			JavaStepDefinition javaStep = (JavaStepDefinition) step;

			Field f = javaStep.getClass().getDeclaredField("method");
			f.setAccessible(true);
			return (Method) f.get(javaStep);
		}
		catch (Throwable e)
		{
			try
			{
				MacroStepDefinition macroStep = (MacroStepDefinition) step;
				return macroStep.getMethod();
			}
			catch (Throwable f) {
				int i = 0;
			}
		}
		return null;
	}

	public static List<ParameterInfo> extractParameterInfos(StepDefinition step)
	{
		try
		{
			JavaStepDefinition javaStep = (JavaStepDefinition) step;

			Field f = javaStep.getClass().getDeclaredField("parameterInfos");
			f.setAccessible(true);
			return (List<ParameterInfo>) f.get(javaStep);
		}
		catch (Throwable e)
		{
		}
		return null;
	}
}
