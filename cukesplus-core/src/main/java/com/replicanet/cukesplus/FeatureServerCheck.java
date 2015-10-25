package com.replicanet.cukesplus;

import com.replicanet.ACEServer;
import com.replicanet.ACEServerCallback;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Set;
import java.util.TreeSet;

/**
 * Checks to see if the feature file server needs to be started and calculates what file list to start it with
 */
public class FeatureServerCheck
{
	public static void getDirectoryContents(Set<String> filesList , File dir)
	{
		File[] files = dir.listFiles();
		if (null == files)
		{
			return;
		}
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

	public static void buildFileList(String[] argv)
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
			if (potential.exists() && potential.isDirectory())
			{
				getDirectoryContents(filesList, potential);
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
		try
		{
			FileUtils.writeStringToFile(new File("target", "fileList.html"), htmlFileList);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static boolean checkForFeatureServer(String[] argv) throws IOException
	{
		if (System.getProperty("com.replicanet.cukesplus.server.featureEditor") == null)
		{
			return false;
		}

		buildFileList(argv);

		ACEServer.addCallback(new ACEServerCallback()
		{
			@Override
			public void afterGet(String s)
			{
			}

			@Override
			public void afterPut(String s)
			{
				buildFileList(argv);
			}
		});
		// http://127.0.0.1:8001/ace-builds-master/demo/autocompletion.html?filename=features/test.feature
		ACEServer.startServer(new InetSocketAddress(8001));
//		System.out.println("http://127.0.0.1:8001/ace-builds-master/fileList.html");
		System.out.println("http://127.0.0.1:8001/ace-builds-master/demo/autocompletion.html");
		return true;
	}
}
