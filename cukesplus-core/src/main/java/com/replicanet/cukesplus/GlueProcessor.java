package com.replicanet.cukesplus;

import cucumber.api.StepDefinitionReporter;
import cucumber.api.java.en.*;
import cucumber.runtime.Glue;
import cucumber.runtime.ParameterInfo;
import cucumber.runtime.Runtime;
import cucumber.runtime.StepDefinition;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import static cucumber.runtime.java.ExtractJavaStepDefinitionMembers.extractMethod;
import static cucumber.runtime.java.ExtractJavaStepDefinitionMembers.extractParameterInfos;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 *
 */
public class GlueProcessor
{
	private static class StepInformation
	{
		String pattern;
		Method method;
		List<ParameterInfo> parameterInfos;
		List<String> parameterNames;
	}

	public static List<String> getParameterNames(Method method)
	{
		Parameter[] parameters = method.getParameters();
		List<String> parameterNames = new ArrayList<>();

		for (Parameter parameter : parameters)
		{
			if(!parameter.isNamePresent())
			{
//				System.out.println("**** Could not get names");
				return null;
			}

			String parameterName = parameter.getName();
			System.out.println("**** Got name" + parameterName);
			parameterNames.add(parameterName);
		}

		return parameterNames;
	}

	public static void processGlue(Runtime runtime)
	{
		Glue glue = runtime.getGlue();

		final TreeMap<String, StepInformation> glueMap = new TreeMap<String,StepInformation>();
		glue.reportStepDefinitions( new StepDefinitionReporter()
		{
			@Override
			public void stepDefinition(StepDefinition stepDefinition)
			{
				// This will probably need access to the Java Method and its annotation
				StepInformation stepInformation = new StepInformation();
				stepInformation.pattern = stepDefinition.getPattern();
				stepInformation.method = extractMethod(stepDefinition);
				stepInformation.parameterInfos = extractParameterInfos(stepDefinition);
				try
				{
					stepInformation.parameterNames = getParameterNames(stepInformation.method);
				}
				catch (Exception e)
				{
					// Unable to get any parameter names, don't worry about it.
				}

				glueMap.put(stepDefinition.getLocation(true), stepInformation);
			}
		});

		final StringBuilder outputSimple = new StringBuilder();
		final StringBuilder outputComplex = new StringBuilder();
		for(SortedMap.Entry<String,StepInformation> entry : glueMap.entrySet())
		{
			String theRegex = entry.getValue().pattern;
			// Tidy the regex so it can be used in auto complete matchers
			// http://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html
			// Remove any string start or end constructs
			theRegex = theRegex.trim();
			theRegex = StringUtils.strip(theRegex,"^$");
			if (theRegex.length() == 0)
			{
				continue;
			}

			// Look for any regex commands that are not part of capture groups and resolve them to suitable text

			// Trim any capture groups
			int lastPos = 0;
			int pos = 0;
			String tidiedRegex = "";
			String plainRegex = "";
			int captureGroupStart = -1;
			int captureGroupEnd = -1;
			int captureGroup = 0;
			int outputGroup = 1;	// Starts at one for the text mate groups
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

					if (captureGroupStart >= 1 && theRegex.charAt(captureGroupStart-1) == '\\')
					{
						captureGroupStart = -1;
					}
					continue;
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
						captureGroupEnd = -1;
						continue;
					}
				}

				// At this point captureGroupStart and captureGroupEnd should be a valid capture group
				assertThat(captureGroupStart , is(not(equalTo(-1))));
				assertThat(captureGroupEnd , is(not(equalTo(-1))));
				assertThat(captureGroupEnd , is(greaterThan(captureGroupStart)));

				String theCaptureGroup = theRegex.substring(captureGroupStart , captureGroupEnd+1);
				tidiedRegex += theRegex.substring(lastPos , captureGroupStart);
				plainRegex += theRegex.substring(lastPos , captureGroupStart);
				// Include the parameter type with textmate style "${1:number} groups
				List<ParameterInfo> parameterInfos = entry.getValue().parameterInfos;
				// For parameter names to work then the compiler options in the pom.xml need to be:
				/*
					<compilerArgs>
						<arg>-parameters</arg>
					</compilerArgs>
				*/
				// MPi: TODO: Alternatively parameter name hints could be extracted from a new method annotation that the framework understands
				List<String> parameterNames = entry.getValue().parameterNames;
				if ( null != parameterInfos && captureGroup < parameterInfos.size())
				{
					// Use the type name without any dots
					String typeName = parameterInfos.get(captureGroup).getType().toString();

					int pos2 = typeName.lastIndexOf('.');
					if ( pos2 != -1)
					{
						typeName = typeName.substring(pos2+1);
					}

					String simpleName = typeName;

					if ( null != parameterNames && captureGroup < parameterNames.size())
					{
						simpleName = parameterNames.get(captureGroup);
						typeName += " " + simpleName;
					}

					tidiedRegex += "${" + outputGroup + ":" + typeName + "}";
					plainRegex += simpleName;
					isComplexRegex = true;
				}
				else
				{
					// MPi: TODO: Deduce a suitable identifier from the regex in the capture group
					tidiedRegex += "*";
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
				tidiedRegex += theRegex.substring(lastPos);
				plainRegex += theRegex.substring(lastPos);
			}

			Method method = entry.getValue().method;
			if (null != method)
			{
				// Resolve what kind of keyword is used: Given, When, Then, And
 				if (null != method.getDeclaredAnnotation(Given.class))
				{
					tidiedRegex = "Given " + tidiedRegex;
					plainRegex  = "Given " + plainRegex;
				}
				else if (null != method.getDeclaredAnnotation(When.class))
				{
					tidiedRegex = "When " + tidiedRegex;
					plainRegex = "When " + plainRegex;
				}
				else if (null != method.getDeclaredAnnotation(Then.class))
				{
					tidiedRegex = "Then " + tidiedRegex;
					plainRegex = "Then " + plainRegex;
				}
				else if (null != method.getDeclaredAnnotation(And.class))
				{
					tidiedRegex = "And " + tidiedRegex;
					plainRegex = "And " + plainRegex;
				}
				else if (null != method.getDeclaredAnnotation(But.class))
				{
					tidiedRegex = "But " + tidiedRegex;
					plainRegex = "But " + plainRegex;
				}
				else
				{
					tidiedRegex = "* " + tidiedRegex;
					plainRegex = "* " + plainRegex;
				}
			}
			else
			{
				// No method, so fallback to the generic * step format
				tidiedRegex = "* " + tidiedRegex;
				plainRegex = "* " + plainRegex;
			}

			// JavaScript will handle any escapes like '\(' which should be '(' and '\\' which should be '\'
			// MPi: TODO: Handle escapes for HTML syntax reports
			// MPi: TODO: HTML Syntax reports should be sorted and grouped by functionality and sorted within the group

			// For the ACEServer feature file editor add to complexPotentials if complex named capture groups are present, otherwise add to simplePotentials.
			// The feature editor will prompt for and display complex snippets with named parameters differently.
			System.out.println(plainRegex);
			System.out.println(tidiedRegex);
			if (isComplexRegex)
			{
				outputComplex.append("snippet " + plainRegex + "\\n\\\n");
				outputComplex.append("\t" + tidiedRegex + "\\n\\\n");
			}
			else
			{
				if(outputSimple.length() > 0)
				{
					outputSimple.append(",\n");
				}
				outputSimple.append("\"" + tidiedRegex + "\"");
			}
		}
		try
		{
			String output = "var simplePotentials = [\n" + outputSimple + "\n];\n";
			output += "var complexPotentials = \"\\\n" + outputComplex + "\";\n";
			FileUtils.writeStringToFile(new File("target" , "gherkin-steps.js"), output.toString());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
