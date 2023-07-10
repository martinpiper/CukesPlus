package com.replicanet.cukesplus.junit;

import com.replicanet.cukesplus.FeatureServerCheck;
import com.replicanet.cukesplus.GlueProcessor;
import com.replicanet.cukesplus.ExtensionRuntime;
import cucumber.api.junit.Cucumber;
import cucumber.runtime.ClassFinder;
import cucumber.runtime.RuntimeOptions;
import cucumber.runtime.io.ResourceLoader;
import cucumber.runtime.io.ResourceLoaderClassFinder;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.TestClass;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

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
		List<String> glue = runtimeOptions.getGlue();
		FeatureServerCheck.buildFileList(glue.toArray(new String[0]));
		return new ExtensionRuntime(resourceLoader, classFinder, classLoader, runtimeOptions);
	}

	@Override
	protected cucumber.runtime.Runtime createRuntime(cucumber.runtime.io.ResourceLoader resourceLoader, java.lang.ClassLoader classLoader, cucumber.runtime.RuntimeOptions runtimeOptions) throws org.junit.runners.model.InitializationError, java.io.IOException
	{
		// Load any properties from the calling test runner class annotations
		Class foo = getTestClass().getJavaClass();
		CucumberPlusOptions options = (CucumberPlusOptions) foo.getAnnotation(CucumberPlusOptions.class);
		if (null != options) {
			for (String file : options.properties()) {
				try {
					System.getProperties().load(new FileInputStream(file));
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
		}

//		cucumber.runtime.Runtime runtime = super.createRuntime(resourceLoader, classLoader, runtimeOptions);
		ExtensionRuntime runtime = createExtensionRuntime(resourceLoader, classLoader, runtimeOptions);
		GlueProcessor.hookIntoCucumberBeforeExecution(runtime);    // << Hook into Cucumber
		return runtime;
	}
}
