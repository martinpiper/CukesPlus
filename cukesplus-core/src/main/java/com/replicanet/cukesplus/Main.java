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
	public static void getDirectoryContents(Set<String> filesList , File dir)
	{
		File[] files = dir.listFiles();
		for (File file : files)
		{
			if (file.isDirectory())
			{
				getDirectoryContents(filesList, file);
			}
			else
			{
				addSafePath(filesList, file.getPath());
			}
		}
	}

	private static void addSafePath(Set<String> filesList , String path)
	{
		if (path.startsWith("./") || path.startsWith(".\\"))
		{
			path = path.substring(2);
		}
		if (path.endsWith(".feature"))
		{
			filesList.add(path);
		}
	}

	public static void main(String[] argv) throws Throwable
	{
		if (System.getProperty("com.replicanet.cukesplus.server.featureEditor") != null)
		{
			Set<String> filesList = new TreeSet<>();
			for (String arg : argv)
			{
				File dir = new File(".");
				File potential = new File(dir , arg);
				if (potential.exists() && potential.isFile())
				{
					addSafePath(filesList, potential.getPath());
					continue;
				}
				FileFilter fileFilter = new WildcardFileFilter(arg);
				File[] files = dir.listFiles(fileFilter);
				for (int i = 0; i < files.length; i++)
				{
					getDirectoryContents(filesList,files[i]);
				}
			}
			String htmlFileList = "<html><body bgcolor=\"#E6E6FA\"><table>";
			for (String path : filesList)
			{
				path = path.replace("\\" , "/");
				htmlFileList += "<tr><td><a href = \"demo/autocompletion.html?filename=" + path + "\" target='_parent'>" + path + "</a></td></tr>";
			}
			htmlFileList += "</table></body></html>";
			FileUtils.writeStringToFile(new File("target", "fileList.html"), htmlFileList);
			// http://127.0.0.1:8001/ace-builds-master/demo/autocompletion.html?filename=features/test.feature
			ACEServer.startServer(new InetSocketAddress(8001));
//			System.out.println("http://127.0.0.1:8001/ace-builds-master/fileList.html");
			System.out.println("http://127.0.0.1:8001/ace-builds-master/demo/autocompletion.html");
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
