package com.replicanet.cukesplus.junit;

import com.replicanet.cukesplus.GlueProcessor;
import cucumber.api.junit.Cucumber;
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

	@Override
	protected cucumber.runtime.Runtime createRuntime(cucumber.runtime.io.ResourceLoader resourceLoader, java.lang.ClassLoader classLoader, cucumber.runtime.RuntimeOptions runtimeOptions) throws org.junit.runners.model.InitializationError, java.io.IOException
	{
		cucumber.runtime.Runtime runtime = super.createRuntime(resourceLoader, classLoader, runtimeOptions);
		GlueProcessor.processGlue(runtime);	// << Hook into Cucumber
		return runtime;
	}
}
