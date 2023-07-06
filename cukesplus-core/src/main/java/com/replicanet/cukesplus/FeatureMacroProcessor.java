package com.replicanet.cukesplus;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Parses macro files and feature macros to produce output parsed feature files that contain expanded macros
 */
public class FeatureMacroProcessor
{
	// MPi: TODO: Pull this list from the loaded Cucumber step keywords by iterating classes annotated with @StepDefAnnotation
	final List<String> stepKeywords = Arrays.asList("Given", "When", "Then", "And", "But", "*");
	int errors;
	Map<String, Macro> macroMap = new HashMap<String, Macro>();
	List<String> sortedDefinitions;

	private static void emitLineDebug(boolean firstIteration, String inputFile, BufferedWriter bw, int lineNumber, String currentIndent) throws IOException
	{
		if (firstIteration)
		{
			bw.write(currentIndent + "#> " + lineNumber + " : " + inputFile);
			bw.newLine();
		}
	}

	private void emitInfo(String message)
	{
		System.out.println("Info: " + message);
	}

	private void emitWarning(String message)
	{
		System.out.println("Warning: " + message);
	}

	private void emitError(String message)
	{
		System.out.println("Error: " + message);
		errors++;
	}

	public void processMacroSyntaxToGlue()
	{
		for (SortedMap.Entry<String, FeatureMacroProcessor.Macro> entry : macroMap.entrySet())
		{
			GlueProcessor.StepInformation theStep = new GlueProcessor.StepInformation();

			FeatureMacroProcessor.Macro macro = entry.getValue();

			if (null == macro)
			{
				continue;
			}
			if (null == macro.regexDefinition)
			{
				continue;
			}

			String safeName = makeSafeName(macro);
			String simpleName = makeSimpleName(macro);
			String theStepKey = makeStepKeyProlog(theStep, safeName, simpleName);
			String params = "";
			for (int i = 0 ; i < macro.namedParameters.size() ; i++)
			{
				params = appendParameter(theStep, params, macro.namedParameters.get(i) , macro.namedParametersTypes.get(i));
			}
			theStepKey += params + ") in file:" + macro.sourceFile;

			theStep.pattern = StringEscapeUtils.unescapeJava(macro.regexDefinition);
			theStep.declaredAnnotationType = macro.keyword;

			GlueProcessor.glueMap.put(theStepKey, theStep);
		}
	}

	private String makeSafeName(Macro macro)
	{
		String safeName = "";
		for (int i = 0; i < macro.regexDefinition.length(); i++)
		{
			char theChar = macro.regexDefinition.charAt(i);
			if (Character.isLetter(theChar))
			{
				safeName += theChar;
			}
			else
			{
				safeName += "_";
			}
		}
		return safeName;
	}

	private String makeSimpleName(Macro macro)
	{
		String simpleName = "";
		int pos2 = macro.sourceFile.lastIndexOf('.');
		if (-1 != pos2)
		{
			simpleName = macro.sourceFile.substring(0, pos2);
			pos2 = simpleName.lastIndexOf('/');
			if (-1 != pos2)
			{
				simpleName = simpleName.substring(pos2 + 1);
			}
			pos2 = simpleName.lastIndexOf('\\');
			if (-1 != pos2)
			{
				simpleName = simpleName.substring(pos2 + 1);
			}
		}
		return simpleName;
	}

	private String makeStepKeyProlog(GlueProcessor.StepInformation theStep, String safeName, String simpleName)
	{
		if (!simpleName.isEmpty())
		{
			simpleName = "." + simpleName;
		}
		simpleName = "Macro" + simpleName;
		theStep.simpleName = simpleName;

		return simpleName + "." + safeName + "(";
	}

	private String appendParameter(GlueProcessor.StepInformation theStep, String params, String param, String theType)
	{
		if (!params.isEmpty())
		{
			params += ",";
		}
		params += theType;

		if (null == theStep.parameterTypes)
		{
			theStep.parameterTypes = new ArrayList<String>();
		}

		if (null == theStep.parameterNames)
		{
			theStep.parameterNames = new ArrayList<String>();
		}

		theStep.parameterTypes.add(theType);
		if (param.startsWith("$"))
		{
			param = param.substring(1);
		}
		theStep.parameterNames.add(param);
		return params;
	}

	public int processMacroFile(String filepath) throws Exception
	{
		sortedDefinitions = null;    // Invalidate the sorted definitions cache
		errors = 0;
		int parsed = 0;
		boolean insideTextBlock = false;

		emitInfo("Processing " + filepath);

		BufferedReader br = new BufferedReader(new FileReader(filepath));
		String line;
		Macro macro = null;
		int lineNumber = 0;
		String textBlockLeading = null;
		while ((line = br.readLine()) != null)
		{
			lineNumber++;
			String trimmed = line.trim();
			if (trimmed.isEmpty())
			{
				continue;
			}

			// Reject lines starting with '#'
			if (trimmed.charAt(0) == '#')
			{
				continue;
			}

			int pos = line.indexOf(trimmed);
			String indent = line.substring(0, pos);

			if (!insideTextBlock && trimmed.charAt(0) == '$')
			{
				if (null == macro || !macro.stepLines.isEmpty())
				{
					emitError("Syntax error parsing " + filepath + " line " + lineNumber + " the parameter names '" + trimmed + "' was not expected at this time. They should be specififed after a step definition and before any step lines.");
					continue;
				}

				// It is a variable name definition for the current macro
				String[] tokens = trimmed.split("\\s+");
				for (String token : tokens)
				{
					if (token.charAt(0) != '$')
					{
						emitError("Syntax error parsing " + filepath + " line " + lineNumber + " the parameter name '" + token + "' does not start with $");
						continue;
					}
					if (token.indexOf('.') == -1)
					{
						macro.namedParametersTypes.add("String");
						macro.namedParameters.add(token);
					}
					else
					{
						String[] split = token.split("\\.");
						macro.namedParametersTypes.add(split[0]);
						macro.namedParameters.add("$" + split[1]);
					}
				}
				continue;
			}

			// If it's a new macro definition
			if (indent.isEmpty())
			{
				if (null != macro)
				{
					macroMap.put(macro.regexDefinition, macro);
					parsed++;
				}
				macro = new Macro();
				macro.sourceFile = filepath;
				// MPi: TODO: Detect and transform the reduced syntax regex to the full regex if needed
				if (trimmed.charAt(0) != '@')
				{
					// It might be a simplified regex, so expand it
					String[] tokens = trimmed.split("\\s+", 2);
					if (tokens.length <= 1)
					{
						emitError("Syntax error parsing " + filepath + " line " + lineNumber + " the step definition line does not contain a step keyword followed by the defintion.");
						continue;
					}
					if (!stepKeywords.contains(tokens[0]))
					{
						emitError("Syntax error parsing " + filepath + " line " + lineNumber + " the step keyword '" + tokens[0] + "' was not recognised.");
						continue;
					}

					if (tokens[0].equals("*"))
					{
						tokens[0] = "And";
					}

					macro.keyword = tokens[0];

					String replacement = "@" + tokens[0] + "(\"^";
					String fragment = getFragment(tokens);

					fragment = StringEscapeUtils.escapeJava(fragment);
					replacement += fragment;

					replacement += "$\")";

					trimmed = replacement;
				}

				// Now we have a trimmed string in like the standard java step definition...
				// @When("^I process the feature file \"([^\"]*)\" and the macro file \"([^\"]*)\"$")
				macro.keyword = trimmed.split("[@(]")[1];
				trimmed = trimRegex(trimmed);

				macro.regexDefinition = trimmed;
				macro.regexDefinitionLine = lineNumber;

				continue;
			}

			if (null == macro || null == macro.regexDefinition || macro.regexDefinition.isEmpty())
			{
				emitError("Syntax error parsing " + filepath + " line " + lineNumber + " the step lines '" + trimmed + "' was not expected at this time because a step definition has not been set yet.");
				continue;
			}

			String[] tokens = trimmed.split("\\s+", 2);

			if (insideTextBlock)
			{
				if (null != textBlockLeading && line.indexOf(textBlockLeading) == 0)
				{
					trimmed = line.substring(textBlockLeading.length());
				}
			}
			else
			{
				textBlockLeading = null;
			}

			if (tokens[0].startsWith("\"\"\""))
			{
				if (null == textBlockLeading)
				{
					textBlockLeading = line.substring(0, line.indexOf(tokens[0]));
				}
				// Toggle the text block status with each line starting with """
				insideTextBlock = !insideTextBlock;

				macro.stepLines.add("  " + tokens[0]);
				// Flag that the line number should not be output in the debug information
				macro.stepLineNumbers.add(-1);
			}
			else if (insideTextBlock || tokens[0].startsWith("|"))
			{
				macro.stepLines.add("  " + trimmed);    // Indent here for a table
				// Flag that the line number should not be output in the debug information
				macro.stepLineNumbers.add(-1);
			}
			else if (stepKeywords.contains(tokens[0]))
			{
				macro.stepLines.add(trimmed);
				macro.stepLineNumbers.add(lineNumber);
			}
			else
			{
				macro.stepLines.add("* " + trimmed);
				macro.stepLineNumbers.add(lineNumber);
			}
		}

		if (null != macro)
		{
			macroMap.put(macro.regexDefinition, macro);
			parsed++;
		}

		if (errors > 0)
		{
			throw new RuntimeException("Got errors!");
		}

		return errors;
	}

	private String trimRegex(String trimmed)
	{
		int pos;// Trim out the regex and store it
		pos = trimmed.indexOf('"');
		int pos2 = trimmed.lastIndexOf('"');
		trimmed = trimmed.substring(pos + 1, pos2);
		trimmed = StringEscapeUtils.unescapeJava(trimmed);
		return trimmed;
	}

	private String getFragment(String[] tokens)
	{
		int pos;
		String fragment = "";
		int lastPos = 0;
		// Escape the string, since it's unescaped plain text
		tokens[1] = StringEscapeUtils.escapeJava(tokens[1]);
		while ((pos = tokens[1].indexOf("*", lastPos)) != -1)
		{
			fragment += tokens[1].substring(lastPos, pos);
			lastPos = pos + 1;

			if (pos < tokens[1].length() - 1)
			{
				if (tokens[1].charAt(pos + 1) == '*')
				{
					// Double * is treated as a single literal *
					fragment += "\\*";
					lastPos++;    // Skip the second *
					continue;
				}
			}

			// Standard "*" to "(.*)" regex transformation
			fragment += "(.*)";
		}

		// Add anything else not included
		fragment += tokens[1].substring(lastPos, tokens[1].length());
		return fragment;
	}

	public String processFeatureText(String text, String featureURI) throws IOException, ParseException {
		FileUtils.writeStringToFile(new File("target/t.macroFeature"), text);
		int lineCount = processFeatureFile("target/t.macroFeature" , "target/t.feature", featureURI);
		if (lineCount <= 0) {
			return text;
		}
		String processed = FileUtils.readFileToString(new File("target/t.feature"));
		return processed;
	}

	public int processFeatureFile(String inputFile, String outputFile) throws IOException, ParseException {
		return processFeatureFile(inputFile , outputFile , inputFile);
	}
	public int processFeatureFile(String inputFile, String outputFile, String featureURI) throws IOException, ParseException
	{
		String tempOutput = outputFile + ".temp";

		int realLineCount = 0;
		final int maxProcessingDepth = 32;    // The maximum number of times it will process the same file
		int processingDepth = 0;

		int thisLineCount;
		boolean firstIteration = true;
		int depth = 1;
		// Keep on processing until there are no more updated lines or until the processing depth is reached
		do
		{
			processingDepth++;
			FileUtils.deleteQuietly(new File(outputFile));
			thisLineCount = internalProcessFeatureFile(firstIteration, inputFile, outputFile, featureURI, depth);
			if (thisLineCount > 0 && processingDepth < maxProcessingDepth)
			{
				inputFile = flipFile(outputFile, tempOutput);
			}
			realLineCount += thisLineCount;
			emitInfo("Feature macro processing '" + featureURI + "' depth " + processingDepth + " new lines " + thisLineCount + " total lines " + realLineCount);
			firstIteration = false;
			depth++;
		} while (thisLineCount > 0 && processingDepth < maxProcessingDepth);

		if (processingDepth == maxProcessingDepth)
		{
			// MPi: TODO: Flag potentially recursive macros by examining the output and flagging these problems earlier
			emitWarning("Macro feature file '" + featureURI + "' reached the maximum processing depth. There might be a recursive macro.");
		}

		FileUtils.deleteQuietly(new File(tempOutput));
		if (realLineCount == 0)
		{
			// No meaningful output so don't keep the files
			FileUtils.deleteQuietly(new File(outputFile));
		}

		return realLineCount;
	}

	private String flipFile(String outputFile, String tempOutput) throws IOException
	{
		String inputFile;
		FileUtils.deleteQuietly(new File(tempOutput));
		Files.move(Paths.get(outputFile), Paths.get(tempOutput));
		inputFile = tempOutput;
		return inputFile;
	}

	private int internalProcessFeatureFile(boolean firstIteration, String inputFile, String outputFile, String featureURI, int depth) throws IOException, ParseException
	{
		errors = 0;
		if (null == sortedDefinitions)
		{
			String[] strings = macroMap.keySet().toArray(new String[macroMap.size()]);
			sortedDefinitions = Arrays.asList(strings);
			sortedDefinitions.sort(new reverseLenComp());
		}

		int linesChanged = 0;

		emitInfo("Processing " + featureURI);
		BufferedReader br = new BufferedReader(new FileReader(inputFile));
		BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
		String line;
		int lineNumber = 0;
		boolean inScenario = false;
		boolean insideTextBlock = false;

		while ((line = br.readLine()) != null)
		{
			lineNumber++;
			String trimmed = line.trim();
			// Output these lines without translation
			if (trimmed.isEmpty() || trimmed.startsWith("@") || trimmed.startsWith("#") || trimmed.startsWith("\"\"\"") || insideTextBlock)
			{
				bw.write(line);
				bw.newLine();

				if (trimmed.startsWith("\"\"\""))
				{
					insideTextBlock = !insideTextBlock;
				}
				continue;
			}

			String[] tokens = trimmed.split("\\s+", 2);
			String currentIndent = "    ";
			int indentPos = line.indexOf(tokens[0]);
			if (indentPos != -1)
			{
				currentIndent = line.substring(0, indentPos);
			}

			String workLower = trimmed.toLowerCase();

			if (workLower.startsWith("background:") || workLower.startsWith("scenario:") || workLower.startsWith("scenario outline:"))
			{
				inScenario = true;
				bw.write(line);
				bw.newLine();
				continue;
			}
			if (!inScenario)
			{
				bw.write(line);
				bw.newLine();
				continue;
			}
			if (workLower.startsWith("examples:"))
			{
				emitLineDebug(firstIteration, featureURI, bw, lineNumber, currentIndent);
				inScenario = false;
				bw.write(line);
				bw.newLine();
				continue;
			}

			if (!insideTextBlock && trimmed.startsWith("\"\"\""))
			{
				insideTextBlock = true;
			}

			if (!trimmed.startsWith("|") && !insideTextBlock && !stepKeywords.contains(tokens[0]))
			{
				emitError("Syntax error parsing " + featureURI + " line " + lineNumber + " the line '" + trimmed + "' does not resemble a step line but I'm expecting a step line");
				continue;
			}

			// Now we should be processing step lines
			ArrayList<String> matchedLines = new ArrayList<>();
			if (tokens.length > 1)
			{
				for (String regex : sortedDefinitions)
				{
					// MPi: TODO: Cache the Pattern compilation result
					Pattern p = Pattern.compile(regex);
					Matcher m = p.matcher(tokens[1]);
					if (m.matches())
					{
						Macro macro = macroMap.get(regex);
						String lineDebug = macro.regexDefinitionLine + " : " + macro.sourceFile;
						matchedLines.add(lineDebug);
						// Only process the first match
						if (matchedLines.size() == 1)
						{
							emitLineDebug(firstIteration, featureURI, bw, lineNumber, currentIndent);

							writeLineWithDebug(bw, "##__#__## " + trimmed, currentIndent, lineDebug);

							ArrayList<String> params = getParams(m);

							int lineIndex = -1;
							for (String step : macro.stepLines)
							{
								lineIndex++;

								// Only write line number debug information when the line number is valid
								// This avoids problems with non-step lines (tables and text blocks) getting comments inserted into them.
								if (macro.stepLineNumbers.get(lineIndex) >= 0)
								{
									writeLineDebugComment(bw, currentIndent, macro, lineIndex, depth);
								}

								String newStep = currentIndent;
								int pos;
								int lastPos = 0;
								while ((pos = step.indexOf('$', lastPos)) != -1)
								{
									String paramToken = step.substring(pos);

									if (paramToken.startsWith("$$"))
									{
										newStep += step.substring(lastPos, pos);
										newStep += "$";
										lastPos = pos + 2;
										continue;
									}

									// Macro parameters can only contain dollar at the start plus letters and numbers. No punctuation etc
									String paramTokens[] = paramToken.split("[^a-zA-Z0-9]", 3);

									paramTokens[1] = "$" + paramTokens[1];

									newStep += step.substring(lastPos, pos);


									// Look for the numbered token position
									int tokPos = getTokPos(macro, paramTokens[1]);

									// Replace the token with suitable text
									if (tokPos == -1)
									{
										newStep += "<<Not found " + paramTokens[1] + ">>";
										emitError("Parameter '" + paramTokens[1] + "' not found error parsing " + featureURI + " line " + lineNumber + " macro file " + macro.sourceFile + " line " + macro.stepLineNumbers.get(lineIndex) + " the line '" + trimmed + "'");
									}
									else
									{
										newStep += params.get(tokPos - 1);
									}

									lastPos = pos + paramTokens[1].length();
								}

								newStep += step.substring(lastPos);
								bw.write(newStep);
								bw.newLine();
								linesChanged++;
							}
						}
					}
				}
			}

			if (matchedLines.size() > 1)
			{
				emitInfo("The macro line '" + trimmed + "' was matched " + Arrays.toString(matchedLines.toArray()));
			}
			else if (matchedLines.isEmpty())
			{
				if (!trimmed.startsWith("|"))
				{
					emitLineDebug(firstIteration, featureURI, bw, lineNumber, currentIndent);
				}
				bw.write(line);
				bw.newLine();
			}
		}

		br.close();
		bw.flush();
		bw.close();

		if (errors > 0)
		{
			throw new RuntimeException("Got errors");
		}

		return linesChanged;
	}

	private int getTokPos(Macro macro, String paramToken)
	{
		int tokPos = -1;
		try
		{
			String potentialNumber = paramToken.substring(1);
			//									potentialNumber = potentialNumber.replaceAll("[^0-9]", "").trim();
			tokPos = Integer.parseInt(potentialNumber);
		}
		catch (Exception e)
		{
			tokPos = -1;
		}

		// Look for a named token position if it hasn't been found yet
		if (tokPos == -1)
		{
			tokPos = macro.namedParameters.indexOf(paramToken);
			if (tokPos != -1)
			{
				tokPos++;
			}
		}
		return tokPos;
	}

	private void writeLineDebugComment(BufferedWriter bw, String currentIndent, Macro macro, int lineIndex, int level) throws IOException
	{
		bw.write(currentIndent + "#>>> " + macro.stepLineNumbers.get(lineIndex) + " , " + level);
		bw.newLine();
	}

	private ArrayList<String> getParams(Matcher m)
	{
		ArrayList<String> params = new ArrayList<>();
		int i;
		for (i = 1; i <= m.groupCount(); i++)
		{
			params.add(m.group(i));
		}
		return params;
	}

	private void writeLineWithDebug(BufferedWriter bw, String trimmed, String currentIndent, String lineDebug) throws IOException
	{
		// Write out the old line with a comment
		bw.write(currentIndent + trimmed);
		bw.newLine();

		bw.write(currentIndent + "#>> " + lineDebug);
		bw.newLine();
	}

	public static class Macro
	{
		String sourceFile;
		String regexDefinition;
		Integer regexDefinitionLine;
		String keyword;
		List<String> namedParameters = new ArrayList<>();
		List<String> namedParametersTypes = new ArrayList<>();
		List<String> stepLines = new ArrayList<>();
		List<Integer> stepLineNumbers = new ArrayList<>();
	}

	static class reverseLenComp implements Comparator<String>
	{
		public int compare(String o1, String o2)
		{
			return Integer.compare(o2.length(), o1.length());
		}
	}
}
