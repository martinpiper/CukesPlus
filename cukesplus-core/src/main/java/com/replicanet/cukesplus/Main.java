package com.replicanet.cukesplus;

import com.replicanet.ACEServer;
import cucumber.runtime.*;
import cucumber.runtime.Runtime;
import cucumber.runtime.io.MultiLoader;
import cucumber.runtime.io.ResourceLoaderClassFinder;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 */
public class Main
{
	public static void main(String[] argv) throws Throwable
	{
		if(FeatureServerCheck.checkForFeatureServer(argv))
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
		cucumber.runtime.Runtime runtime = new Runtime(resourceLoader, classFinder, classLoader, runtimeOptions);
		GlueProcessor.processGlue(runtime);	// << Hook into Cucumber
		runtime.run();
		return runtime.exitStatus();
	}
}
