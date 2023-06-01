package com.replicanet.cukesplus.junit;

import com.replicanet.cukesplus.GlueProcessor;
import com.replicanet.cukesplus.ExtensionRuntime;
import cucumber.api.junit.Cucumber;
import cucumber.runtime.ClassFinder;
import cucumber.runtime.RuntimeOptions;
import cucumber.runtime.io.ResourceLoader;
import cucumber.runtime.io.ResourceLoaderClassFinder;
import org.junit.runners.model.InitializationError;

import java.io.IOException;

/**
 * Proxy the normal Cucumber JUnit
 */
public class CucumberPlus extends Cucumber
{
	public CucumberPlus(Class clazz) throws InitializationError, IOException
	{
		super(clazz);
	}

	protected ExtensionRuntime createExtensionRuntime(ResourceLoader resourceLoader, ClassLoader classLoader, RuntimeOptions runtimeOptions) throws InitializationError, IOException {
		ClassFinder classFinder = new ResourceLoaderClassFinder(resourceLoader, classLoader);
		return new ExtensionRuntime(resourceLoader, classFinder, classLoader, runtimeOptions);
	}

	@Override
	protected cucumber.runtime.Runtime createRuntime(cucumber.runtime.io.ResourceLoader resourceLoader, java.lang.ClassLoader classLoader, cucumber.runtime.RuntimeOptions runtimeOptions) throws org.junit.runners.model.InitializationError, java.io.IOException
	{
//		cucumber.runtime.Runtime runtime = super.createRuntime(resourceLoader, classLoader, runtimeOptions);
		ExtensionRuntime runtime = createExtensionRuntime(resourceLoader, classLoader, runtimeOptions);
		GlueProcessor.processGlue(runtime);    // << Hook into Cucumber
		return runtime;
	}
}
