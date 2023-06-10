package com.replicanet.cukesplus;

import cucumber.runtime.RuntimeOptions;
import cucumber.runtime.io.MultiLoader;
import cucumber.runtime.io.ResourceLoaderClassFinder;
import cucumber.runtime.java.MacroStepDefinition;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 *
 */
public class Main
{
	public static void main(String[] argv) throws Throwable
	{
		if (FeatureServerCheck.checkForFeatureServer(Main.class, argv))
		{
			return;
		}

//		cucumber.api.cli.Main.main(args);
		// .m2\repository\info\cukes\cucumber-core\1.2.0\cucumber-core-1.2.0.jar!\cucumber\api\cli\Main.class
		byte exitstatus = run(argv, Thread.currentThread().getContextClassLoader());
		System.exit(exitstatus);
	}

	public static byte run(String[] argv, ClassLoader classLoader) throws IOException
	{
		RuntimeOptions runtimeOptions = new RuntimeOptions(new ArrayList(Arrays.asList(argv)));
		MultiLoader resourceLoader = new MultiLoader(classLoader);
		ResourceLoaderClassFinder classFinder = new ResourceLoaderClassFinder(resourceLoader, classLoader);
		cucumber.runtime.Runtime runtime = new ExtensionRuntime(resourceLoader, classFinder, classLoader, runtimeOptions);

		GlueProcessor.processGlue(runtime);    // << Hook into Cucumber
		runtime.run();
		return runtime.exitStatus();
	}
}
