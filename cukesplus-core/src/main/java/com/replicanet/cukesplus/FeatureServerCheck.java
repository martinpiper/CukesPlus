package com.replicanet.cukesplus;

import com.replicanet.ACEServer;
import com.replicanet.ACEServerCallback;
import gherkin.deps.com.google.gson.JsonArray;
import gherkin.deps.com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
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

	public static boolean checkForFeatureServer(final String[] argv) throws IOException
	{
		if (System.getProperty("com.replicanet.cukesplus.server.featureEditor") == null)
		{
			return false;
		}

		buildFileList(argv);

		ACEServer.addCallback(new ACEServerCallback()
		{
			public InputStream beforeGet(String uri)
			{
				if (!uri.contains(".feature.debug.json"))
				{
					return null;
				}
				// An example of what to do to return runtime generated data
				File dir = new File("target");
				FileFilter fileFilter = new WildcardFileFilter("report*.json");
				File[] files = dir.listFiles(fileFilter);

				for (File file : files)
				{
					try
					{
						BufferedReader br = new BufferedReader(new FileReader(file));

						JsonParser parser = new JsonParser();
						JsonArray rootObjs = parser.parse(br).getAsJsonArray();
						br.close();

						int i;
						for (i = 0; i < rootObjs.size(); i++)
						{
							String toRet = "{\n" +
									"\"lines\" : [\n";
							boolean outputAny = false;
							String reportUri = rootObjs.get(i).getAsJsonObject().get("uri").getAsString();
							// We found a match in the report for the file we are interested in seeing debug information for
							if (uri.contains(reportUri.replace("\\" , "/")))
							{
								JsonArray elementObjs = rootObjs.get(i).getAsJsonObject().get("elements").getAsJsonArray();
								int j;
								for (j = 0; j < elementObjs.size() ; j++)
								{
									JsonArray stepsObjs = elementObjs.get(j).getAsJsonObject().get("steps").getAsJsonArray();
									int k;
									for (k = 0 ; k < stepsObjs.size() ; k++)
									{
										if (outputAny)
										{
											toRet += " ,\n";
										}
										outputAny = true;
										String status = stepsObjs.get(k).getAsJsonObject().get("result").getAsJsonObject().get("status").getAsString();
										int line = stepsObjs.get(k).getAsJsonObject().get("line").getAsInt();
										line--;
										// MPi: TODO: Optimise contiguous ranges of lines
										toRet += "\t{ \"type\" : \"";
										toRet += status + "_step_line";
										toRet += "\" ,   \"from\" : "+line+" ,    \"to\" : "+line+"}";
									}
								}
							}
							toRet += "\n\t]\n" +
									"}\n";
							if (outputAny)
							{
								return new ByteArrayInputStream(toRet.getBytes(StandardCharsets.UTF_8));
							}
						}
					}
					catch (FileNotFoundException e)
					{
						e.printStackTrace();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}

				}

				return null;
			}

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
