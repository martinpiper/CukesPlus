package com.replicanet.cukesplus;

import cucumber.api.StepDefinitionReporter;
import cucumber.api.java.en.*;
import cucumber.runtime.Glue;
import cucumber.runtime.ParameterInfo;
import cucumber.runtime.Runtime;
import cucumber.runtime.StepDefinition;
import cucumber.runtime.java.MacroStepDefinition;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.regex.Pattern;

import static cucumber.runtime.java.ExtractJavaStepDefinitionMembers.extractMethod;
import static cucumber.runtime.java.ExtractJavaStepDefinitionMembers.extractParameterInfos;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Processes glue and outputs HTML reports for any syntax discovered
 */
public class GlueProcessor
{
	public static class StepInformation
	{
		public String pattern;
		public String declaredAnnotationType;
		public String simpleName;
		public List<String> parameterTypes;
		public List<String> parameterNames;
	}

	static class Section
	{
		String text;
		String complexText;
		boolean isParameter;

		public Section(String text)
		{
			this.text = text;
			this.isParameter = false;
		}

		public Section(String text, String complexText)
		{
			this.text = text;
			this.complexText = complexText;
			this.isParameter = true;
		}
	}

	public static TreeMap<String, StepInformation> glueMap = new TreeMap<String, StepInformation>();

	public static List<String> getParameterNames(Method method)
	{
		Parameter[] parameters = method.getParameters();
		List<String> parameterNames = new ArrayList<>();

		for (Parameter parameter : parameters)
		{
			if (!parameter.isNamePresent())
			{
//				System.out.println("**** Could not get names");
				return null;
			}

			String parameterName = parameter.getName();
//			System.out.println("**** Got name" + parameterName);
			parameterNames.add(parameterName);
		}

		return parameterNames;
	}

	public static void processGlue(Runtime runtime)
	{
		FeatureServerCheck.getFeatureMacroProcessor().processMacroSyntaxToGlue();
		for (Map.Entry<String , StepInformation> entry : glueMap.entrySet()) {
			StepInformation stepInfo = entry.getValue();
			runtime.getGlue().addStepDefinition(new MacroStepDefinition(entry.getKey() , Pattern.compile(stepInfo.pattern), 0));
		}

		Glue glue = runtime.getGlue();

		glue.reportStepDefinitions(new StepDefinitionReporter()
		{
			@Override
			public void stepDefinition(StepDefinition stepDefinition)
			{
				// This will probably need access to the Java Method and its annotation
				StepInformation stepInformation = new StepInformation();
				stepInformation.pattern = stepDefinition.getPattern();
				Method method = extractMethod(stepDefinition);
				if (method == null) {
					return;
				}
				stepInformation.simpleName = method.getDeclaringClass().getSimpleName();

				if (null != method.getDeclaredAnnotation(Given.class))
				{
					stepInformation.declaredAnnotationType = "Given";
				}
				else if (null != method.getDeclaredAnnotation(When.class))
				{
					stepInformation.declaredAnnotationType = "When";
				}
				else if (null != method.getDeclaredAnnotation(Then.class))
				{
					stepInformation.declaredAnnotationType = "Then";
				}
				else if (null != method.getDeclaredAnnotation(And.class))
				{
					stepInformation.declaredAnnotationType = "And";
				}
				else if (null != method.getDeclaredAnnotation(But.class))
				{
					stepInformation.declaredAnnotationType = "But";
				}
				else
				{
					stepInformation.declaredAnnotationType = "*";
				}

				List<ParameterInfo> infos = extractParameterInfos(stepDefinition);

				if (null != infos && infos.size() > 0)
				{
					List<String> types = new ArrayList<String>(infos.size());
					for (ParameterInfo info : infos)
					{
						types.add(info.getType().toString());
					}
					stepInformation.parameterTypes = types;
				}

				try
				{
					stepInformation.parameterNames = getParameterNames(method);
				}
				catch (Exception e)
				{
					// Unable to get any parameter names, don't worry about it.
				}

				String theKey = stepDefinition.getLocation(true);

				glueMap.put(theKey, stepInformation);
			}
		});

		final TreeMap<String, String> stepByHTML = new TreeMap<String, String>();
		final TreeMap<String, String> stepByHTMLWithoutKeyword = new TreeMap<String, String>();
		final TreeMap<String, String> stepByHTMLByClassName = new TreeMap<String, String>();

		final StringBuilder outputSimple = new StringBuilder();
		final StringBuilder outputComplex = new StringBuilder();
		for (SortedMap.Entry<String, StepInformation> entry : glueMap.entrySet())
		{
			String theRegex = entry.getValue().pattern;
			// Tidy the regex so it can be used in auto complete matchers
			// http://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html
			// Remove any string start or end constructs
			theRegex = theRegex.trim();
			theRegex = StringUtils.strip(theRegex, "^$");
			if (theRegex.length() == 0)
			{
				continue;
			}

			// Look for any regex commands that are not part of capture groups and resolve them to suitable text

			// Trim any capture groups
			int lastPos = 0;
			int pos = 0;
			final ArrayList<Section> regexSections = new ArrayList<>();
			int captureGroupStart = -1;
			int captureGroupEnd = -1;
			int captureGroup = 0;
			int outputGroup = 1;    // Starts at one for the text mate groups
			boolean isComplexRegex = false;

			while (pos < theRegex.length())
			{
				// Find the first non-escaped '(' character
				if (captureGroupStart == -1)
				{
					captureGroupStart = theRegex.indexOf('(', pos);
					if (captureGroupStart == -1)
					{
						break;
					}

					pos = captureGroupStart + 1;

					if (captureGroupStart >= 1 && theRegex.charAt(captureGroupStart - 1) == '\\')
					{
						theRegex = theRegex.substring(0, pos - 2) + theRegex.substring(pos - 1);
						captureGroupStart = -1;
						pos--;
						continue;
					}
				}

				if (captureGroupEnd == -1)
				{
					captureGroupEnd = theRegex.indexOf(')', pos);
					if (captureGroupEnd == -1)
					{
						break;
					}

					pos = captureGroupEnd + 1;
					if (captureGroupEnd >= 1 && theRegex.charAt(captureGroupEnd - 1) == '\\')
					{
						theRegex = theRegex.substring(0, pos - 2) + theRegex.substring(pos - 1);
						captureGroupEnd = -1;
						pos--;
						continue;
					}
				}

				// At this point captureGroupStart and captureGroupEnd should be a valid capture group
				assertThat(captureGroupStart, is(not(equalTo(-1))));
				assertThat(captureGroupEnd, is(not(equalTo(-1))));
				assertThat(captureGroupEnd, is(greaterThan(captureGroupStart)));

				String theCaptureGroup = theRegex.substring(captureGroupStart, captureGroupEnd + 1);
				String section = theRegex.substring(lastPos, captureGroupStart);
				regexSections.add(new Section(section));
				// Include the parameter type with textmate style "${1:number} groups
				List<String> parameterInfos = entry.getValue().parameterTypes;
				// For parameter names to work then the compiler options in the pom.xml need to be:
				/*
					<compilerArgs>
						<arg>-parameters</arg>
					</compilerArgs>
				*/
				// MPi: TODO: Alternatively parameter name hints could be extracted from a new method annotation that the framework understands
				List<String> parameterNames = entry.getValue().parameterNames;
				if (null != parameterInfos && captureGroup < parameterInfos.size())
				{
					// Use the type name without any dots
					String typeName = parameterInfos.get(captureGroup);

					int pos2 = typeName.lastIndexOf('.');
					if (pos2 != -1)
					{
						typeName = typeName.substring(pos2 + 1);
					}

					String simpleName = typeName;

					// If the variable name exists then use that instead
					if (null != parameterNames && captureGroup < parameterNames.size())
					{
						simpleName = parameterNames.get(captureGroup);
						typeName += " " + simpleName;
					}

					String complexParameter = "${" + outputGroup + ":" + typeName + "}";
					outputGroup++;
					regexSections.add(new Section(simpleName, complexParameter));
					isComplexRegex = true;
				}
				else
				{
					// MPi: TODO: Deduce a suitable identifier from the regex in the capture group
					regexSections.add(new Section("*", "*"));
				}
				pos = captureGroupEnd + 1;
				lastPos = pos;

				// Start searching for the next one
				captureGroupStart = -1;
				captureGroupEnd = -1;
				captureGroup++;
			}
			// Add anything left in the original regex
			if (lastPos < theRegex.length())
			{
				// Handle any remaining "\)" sequences
				pos = lastPos;
				while ((pos = theRegex.indexOf(')', pos)) != -1)
				{
					if (pos >= 1 && theRegex.charAt(pos - 1) == '\\')
					{
						theRegex = theRegex.substring(0, pos - 1) + theRegex.substring(pos);
					}
				}

				String section = theRegex.substring(lastPos);
				regexSections.add(new Section(section));
			}

			if (null != entry.getValue().declaredAnnotationType)
			{
				regexSections.add(0, new Section(entry.getValue().declaredAnnotationType + " "));
			}
			else
			{
				// No method, so fallback to the generic * step format
				regexSections.add(0, new Section("* "));
			}

			// JavaScript will handle any escapes like '\(' which should be '(' and '\\' which should be '\'
			String tidiedRegex = "";
			String plainRegex = "";
			String withoutKeyword = "";

			// Handle escapes for HTML syntax reports
			// MPi: TODO: HTML Syntax reports should be sorted and grouped by functionality and sorted within the group
			String htmlSection = "<i>";
			boolean first = true;
			for (Section section : regexSections)
			{
				if (section.isParameter)
				{
					htmlSection += "<b>";
					htmlSection += StringEscapeUtils.escapeHtml4(section.text);
					htmlSection += "</b>";
					tidiedRegex += section.complexText;
				}
				else
				{
					htmlSection += StringEscapeUtils.escapeHtml4(section.text);
					tidiedRegex += section.text;
				}
				plainRegex += section.text;

				if (first)
				{
					first = false;
					htmlSection += "</i>";
				}
				else
				{
					withoutKeyword += section.text;
				}
			}

			stepByHTML.put(plainRegex, htmlSection);
			stepByHTMLWithoutKeyword.put(withoutKeyword, htmlSection);
			if (null != entry.getValue().simpleName)
			{
				stepByHTMLByClassName.put(entry.getValue().simpleName + " " + withoutKeyword, htmlSection);
			}

			// For the ACEServer feature file editor add to complexPotentials if complex named capture groups are present, otherwise add to simplePotentials.
			// The feature editor will prompt for and display complex snippets with named parameters differently.
			appendHint(outputSimple, outputComplex, isComplexRegex, tidiedRegex, plainRegex);
			tidiedRegex = tidiedRegex.split(" ", 2)[1];
			plainRegex = plainRegex.split(" ", 2)[1];
			// Append hints without starting step keyword, for better matching
			appendHint(outputSimple, outputComplex, isComplexRegex, tidiedRegex, plainRegex);
		}
		try
		{
			String output = "var simplePotentials = [\n" + outputSimple + "\n];\n";
			output += "var complexPotentials = \"\\\n" + outputComplex + "\";\n";
			FileUtils.writeStringToFile(new File("target", "gherkin-steps.js"), output);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		try
		{
			StringBuilder htmlOut = new StringBuilder();
			htmlOut.append("<html><body>");
			htmlOut.append("<h1>Table of contents</h1>" +
					"<ul>" +
					"<li><a href=\"#full\">Syntax sorted by full step line</a></li>" +
					"<li><a href=\"#without\">Sorted without keyword</a></li>" +
					"<li><a href=\"#grouped\">Grouped by functionality sorted without keyword</a></li>" +
					"<ul>");
			String previousGlue = "";
			for (SortedMap.Entry<String, String> entry : stepByHTMLByClassName.entrySet())
			{
				String thisGlue = entry.getKey().split(" ", 2)[0];
				if (!thisGlue.equals(previousGlue))
				{
					htmlOut.append("<li><a href=\"#grouped").append(thisGlue).append("\">").append(thisGlue).append("</a></li>");
					previousGlue = thisGlue;
				}
			}

			htmlOut.append("</ul>");
			htmlOut.append("</ul>");

			htmlOut.append("<h1 id=\"full\">Syntax sorted by full step line</h1>");
			htmlOut.append("<ul>");
			for (SortedMap.Entry<String, String> entry : stepByHTML.entrySet())
			{
				htmlOut.append("<li>").append(entry.getValue()).append("</li>");
			}
			htmlOut.append("</ul>");

			htmlOut.append("<h1 id=\"without\">Sorted without keyword</h1>");
			htmlOut.append("<ul>");
			for (SortedMap.Entry<String, String> entry : stepByHTMLWithoutKeyword.entrySet())
			{
				htmlOut.append("<li>").append(entry.getValue()).append("</li>");
			}
			htmlOut.append("</ul>");

			htmlOut.append("<h1 id=\"grouped\">Grouped by functionality sorted without keyword</h1>");
			previousGlue = "";
			for (SortedMap.Entry<String, String> entry : stepByHTMLByClassName.entrySet())
			{
				String thisGlue = entry.getKey().split(" ", 2)[0];
				if (!thisGlue.equals(previousGlue))
				{
					if (!previousGlue.isEmpty())
					{
						htmlOut.append("</ul>");
					}
					htmlOut.append("<h2 id=\"grouped").append(thisGlue).append("\">").append(thisGlue).append("</h2>");
					htmlOut.append("<ul>");
					previousGlue = thisGlue;
				}
				// MPi: TODO: Create a sub-list for each new class name in the key
				htmlOut.append("<li>").append(entry.getValue()).append("</li>");
			}
			htmlOut.append("</ul>");

			htmlOut.append("</body></html>");
			FileUtils.writeStringToFile(new File("target", "syntax.html"), htmlOut.toString());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private static void appendHint(StringBuilder outputSimple, StringBuilder outputComplex, boolean isComplexRegex, String tidiedRegex, String plainRegex)
	{
		if (isComplexRegex)
		{
			outputComplex.append("snippet " + StringEscapeUtils.escapeJava(plainRegex) + "\\n\\\n");
			outputComplex.append("\t" + StringEscapeUtils.escapeJava(tidiedRegex) + "\\n\\\n");
		}
		else
		{
			if (outputSimple.length() > 0)
			{
				outputSimple.append(",\n");
			}
			outputSimple.append("\"" + StringEscapeUtils.escapeJava(tidiedRegex) + "\"");
		}
	}
}
