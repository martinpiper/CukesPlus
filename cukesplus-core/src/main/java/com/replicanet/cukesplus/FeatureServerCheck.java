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
import java.util.*;

/**
 * Checks to see if the feature file server needs to be started and calculates what file list to start it with
 */
public class FeatureServerCheck
{
	public static final String RECORD_FILE = ".record.file";
	public static final String RUN_FILE = ".run.file";
	public static final String FEATURE_DEBUG_JSON = ".feature.debug.json";
	public static final String RUN_SUITE = ".run.suite";
	public static final String CLEAR_RESULTS = ".clear.results";
	public static final String SERVER_FEATURE_EDITOR = "com.replicanet.cukesplus.server.featureEditor";
	static volatile boolean doingRun = false;

	static FeatureMacroProcessor featureMacroProcessor = new FeatureMacroProcessor();

	public static FeatureMacroProcessor getFeatureMacroProcessor() {
		return featureMacroProcessor;
	}

	public static void getDirectoryContents(Set<String> filesList, File dir)
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

	private static void addSafePath(Set<String> filesList, String path)
	{
		if (path.startsWith("./") || path.startsWith(".\\"))
		{
			path = path.substring(2);
		}
		if (path.toLowerCase().endsWith(".feature") || path.toLowerCase().endsWith(".macrofeature") || path.toLowerCase().endsWith(".macro"))
		{
			filesList.add(path);
		}

		if (path.toLowerCase().endsWith(".macro")) {
			try {
				featureMacroProcessor.processMacroFile(path);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static Set<String> buildFileList(String[] argv) {
		Set<String> filesList = new TreeSet<>();
		for (String arg : argv) {
			File dir = new File(".");
			File potential = new File(dir, arg);
			if (potential.exists() && potential.isFile()) {
				addSafePath(filesList, potential.getPath());
				continue;
			}
			if (potential.exists() && potential.isDirectory()) {
				getDirectoryContents(filesList, potential);
				continue;
			}
			FileFilter fileFilter = new WildcardFileFilter(arg);
			File[] files = dir.listFiles(fileFilter);
			for (int i = 0; i < files.length; i++) {
				getDirectoryContents(filesList, files[i]);
			}
		}
		return filesList;
	}

	public static void writeFileList(Set<String> filesList) {
		String htmlFileList = "<html><body bgcolor=\"#E6E6FA\"><table>";
		for (String path : filesList)
		{
			path = path.replace("\\", "/");
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

	public static boolean checkForFeatureServer(final Class theClass, final String[] argv) throws IOException
	{
		if (System.getProperty(SERVER_FEATURE_EDITOR) == null)
		{
			buildFileList(argv);

			if (featureMacroProcessor.errors > 0) {
				System.out.println("Processing macros errors: " + featureMacroProcessor.errors);

				System.exit(-1);
			}
			return false;
		}

		writeFileList(buildFileList(argv));

		System.clearProperty(SERVER_FEATURE_EDITOR);

		ACEServer.addCallback(new ACEServerCallback()
		{
			final InputStream emptyReply = new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8));

			public InputStream beforeGet(String uri)
			{
				if (uri.endsWith(FEATURE_DEBUG_JSON))
				{
					return handleFeatureDebugJson(uri);
				}
				else if (uri.endsWith(RECORD_FILE))
				{
					return handleRecordFile(uri);
				}
				else if (uri.endsWith(RUN_FILE))
				{
					return handleRunFile(uri);
				}
				else if (uri.endsWith(RUN_SUITE))
				{
					return handleRunSuite(argv);
				}
				else if (uri.endsWith(CLEAR_RESULTS))
				{
					return handleClearResults();
				}
				return null;
			}

			private InputStream handleRecordFile(String uri)
			{
				FileUtils.deleteQuietly(new File("target/events.txt"));

				System.setProperty("com.replicanet.cukesplus.recording.selenium", "");

				int pos = uri.lastIndexOf(RECORD_FILE);
				String realFile = uri.substring(0, pos);
				realFile += RUN_FILE;

				InputStream ret = handleRunFile(realFile);

				System.clearProperty("com.replicanet.cukesplus.recording.selenium");
				return ret;
			}

			private InputStream handleRunFile(String uri)
			{
				int pos = uri.lastIndexOf(RUN_FILE);
				String realFile = uri.substring(0, pos);

				// Remove all passed in feature files then add the single file we want to run
				List<String> trimmedArgv = new LinkedList<String>();
				boolean addNext = false;
				boolean skipNext = false;
				for (String arg : argv)
				{
					if (skipNext)
					{
						skipNext = false;
						continue;
					}

					if (addNext)
					{
						addNext = false;
						trimmedArgv.add(arg);
						continue;
					}

					// If running line number filters then remove any tags, since tags and line number filters produce an error from Cucumber
					if (arg.equals("--tags"))
					{
						skipNext = true;
						continue;
					}

					if (arg.startsWith("--"))
					{
						trimmedArgv.add(arg);
						addNext = true;    // Parameters starting with "--" have a second parameter that we always want to include
						continue;
					}

					if (arg.startsWith("-") || arg.startsWith("/"))
					{
						trimmedArgv.add(arg);
						continue;
					}

					if (arg.endsWith(".feature") || arg.endsWith(".macroFeature"))
					{
						continue;
					}

					File file = new File(arg);
					if (file.exists() && file.isDirectory())
					{
						continue;
					}

					trimmedArgv.add(arg);
				}

				trimmedArgv.add(realFile);
				return handleRunSuite(trimmedArgv.toArray(new String[trimmedArgv.size()]));
			}

			private String makeSafeArg(String arg)
			{
				arg = arg.replace("\"" , "");
//				if(arg.contains(" "))
//				{
//					return "\"" + arg + "\"";
//				}
				return arg;
			}

			private InputStream handleRunSuite(String[] thisArgv)
			{
				if (doingRun)
				{
					System.out.println("Test run in progress, run request ignored");
					return emptyReply;
				}

				doingRun = true;

				List<String> newArgs = new LinkedList<>();
				newArgs.add("java");

				// Make sure the properties that we want are passed to the new process
				Properties propToEnum = System.getProperties();
				Enumeration e = propToEnum.propertyNames();
				while (e.hasMoreElements())
				{
					String key = (String) e.nextElement();
					String r = String.format("-D%s=%s", key, propToEnum.getProperty(key));
					newArgs.add(makeSafeArg(r));
				}

/*					String path = FeatureServerCheck.class.getProtectionDomain().getCodeSource().getLocation().getPath();
				File f = new File(path);
				path = f.getCanonicalPath();
				System.out.println("Current path: " + path);
				if (path.endsWith(".jar"))
				{
					newArgs.add("-jar");
					newArgs.add(path);
				}
				else
				{
					newArgs.add("-cp");
					newArgs.add(path);
					newArgs.add(Main.class.getCanonicalName());
				}
*/

				System.out.println("Current path: " + System.getProperty("java.class.path"));
				newArgs.add("-cp");
				newArgs.add(System.getProperty("java.class.path"));
				newArgs.add(theClass.getCanonicalName());

				for (String arg : thisArgv)
				{
					newArgs.add(makeSafeArg(arg));
				}

				System.out.println("The args =" + newArgs.toString());

				new Thread(new Runnable()
				{
					@Override
					public void run()
					{
						try
						{

							System.out.println("FeatureServer starting run...");
							ProcessBuilder builder = new ProcessBuilder(newArgs.toArray(new String[newArgs.size()]));
							builder.redirectErrorStream(true);
							Process process = builder.start();
//					process.getOutputStream().close();

							// Consume and print output until the process finishes
							try
							{
								InputStream input = process.getInputStream();
								BufferedReader br = null;
								try
								{
									br = new BufferedReader(new InputStreamReader(input));
									String line = null;
									while ((line = br.readLine()) != null)
									{
										System.out.println(line);
									}
								}
								finally
								{
									br.close();
								}
							}
							catch (Exception e2)
							{
							}

							int exitstatus = process.exitValue();
							System.out.println("FeatureServer run exitstatus = " + exitstatus);
							process = null;
							builder = null;

						}
						catch (IOException e)
						{
							System.out.println("FeatureServer run");
							e.printStackTrace();
						}
						doingRun = false;
					}
				}).start();

				// It's the end of the run, tidy up any memory we have accumulated this run
				System.gc();


				return emptyReply;
			}

			private InputStream handleClearResults()
			{
				File dir = new File("target");
				FileFilter fileFilter = new WildcardFileFilter("report*.json");
				File[] files = dir.listFiles(fileFilter);

				for (File file : files)
				{
					FileUtils.deleteQuietly(file);
				}
				return emptyReply;
			}

			private InputStream handleFeatureDebugJson(String uri)
			{
				List<String> originalFeature = null;

				// Look for event hints first
				try
				{
					File events = new File("target/events.txt");
					String eventsString = FileUtils.readFileToString(events);
					String toRet = "{\n" +
							"\"toAppend\" : [\n";
					toRet += eventsString;
					toRet += "]\n}\n";

					FileUtils.deleteQuietly(events);
					return new ByteArrayInputStream(toRet.getBytes(StandardCharsets.UTF_8));
				}
				catch (Exception e)
				{
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
							originalFeature = null;
							String reportUri = rootObjs.get(i).getAsJsonObject().get("uri").getAsString();

							// Look for match in the report for the file we are interested in seeing debug information for
							if (!uri.contains(reportUri.replace("\\", "/")))
							{
								continue;
							}

							if (originalFeature == null) {
								try {
									originalFeature = FileUtils.readLines(new File(ExtensionRuntime.makeSafeName(reportUri)));
								} catch (Exception e) {
								}
							}

							Map<Integer, String> stateByLine = new TreeMap<>();
							JsonArray elementObjs = rootObjs.get(i).getAsJsonObject().get("elements").getAsJsonArray();
							int j;
							for (j = 0; j < elementObjs.size(); j++)
							{
								String keyword = elementObjs.get(j).getAsJsonObject().get("keyword").getAsString();
								JsonArray stepsObjs = elementObjs.get(j).getAsJsonObject().get("steps").getAsJsonArray();
								int k;
								String lastStatus = "skipped";
								boolean first = true;
								for (k = 0; k < stepsObjs.size(); k++)
								{
									String status = stepsObjs.get(k).getAsJsonObject().get("result").getAsJsonObject().get("status").getAsString();
									if (first)
									{
										first = false;
										lastStatus = status;
									}
									else if (!status.equals("skipped"))
									{
										// Only use the last good status from the steps, not skipped status from the steps
										lastStatus = status;
									}
									int line = stepsObjs.get(k).getAsJsonObject().get("line").getAsInt();
									// Remap the line from any processed feature back to the original line...
									if (originalFeature != null && line < originalFeature.size()) {
										int debugLine = line - 1;
										while (debugLine > 0) {
											String debugLineContents = originalFeature.get(debugLine).trim();
											if (debugLineContents.startsWith("#> ")) {
												String[] splits = debugLineContents.split(" ", 3);
												int replacementLine = -1;
												try {
													replacementLine = Integer.parseInt(splits[1]);
												} catch (Exception e) {
												}
												if (replacementLine >= 0) {
													line = replacementLine;
												}
												break;
											}
											debugLine--;
										}
									}
									String anyOtherStates = stateByLine.get(line);
									if (null != anyOtherStates && (anyOtherStates.equals("failed") || anyOtherStates.equals("pending")))
									{
										// Preserve any previous state that shows an interesting error for this line
										continue;
									}
									stateByLine.put(line, status);
								}

								// Emit information for the scenario or the examples table line for the scenario outline
								int line = elementObjs.get(j).getAsJsonObject().get("line").getAsInt();
								stateByLine.put(line, lastStatus);
							}

							if (!stateByLine.isEmpty())
							{
								String toRet = "{\n" +
										"\"lines\" : [\n";

								// MPi: TODO: Optimise contiguous ranges of lines
								boolean first = true;
								for (Map.Entry<Integer, String> entry : stateByLine.entrySet())
								{
									if (!first)
									{
										toRet += " ,\n";
									}
									first = false;
									toRet += "\t{ \"type\" : \"";
									toRet += entry.getValue() + "_step_line";
									toRet += "\" ,   \"from\" : " + entry.getKey() + " ,    \"to\" : " + entry.getKey() + "}";
								}

								toRet += "]\n}\n";

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

				return emptyReply;
			}

			@Override
			public void afterGet(String s)
			{
			}

			@Override
			public void afterPut(String s)
			{
				writeFileList(buildFileList(argv));
			}
		});
		// http://127.0.0.1:8001/ace-builds-master/demo/autocompletion.html?filename=features/test.feature
		ACEServer.startServer(new InetSocketAddress(8001));
//		System.out.println("http://127.0.0.1:8001/ace-builds-master/fileList.html");
		System.out.println("http://127.0.0.1:8001/ace-builds-master/demo/autocompletion.html");
		return true;
	}
}
