package com.replicanet.cukesplus;

import cucumber.api.StepDefinitionReporter;
import cucumber.runtime.Glue;
import cucumber.runtime.ParameterInfo;
import cucumber.runtime.Runtime;
import cucumber.runtime.StepDefinition;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
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
				// TODO: Going to need to resolve what kind of keyword is used: Given, When, Then, And
				// This will probably need access to the Java Method and its annotation
				StepInformation stepInformation = new StepInformation();
				stepInformation.pattern = stepDefinition.getPattern();
				stepInformation.method = extractMethod(stepDefinition);
				stepInformation.parameterInfos = extractParameterInfos(stepDefinition);

				glueMap.put(stepDefinition.getLocation(true), stepInformation);
			}
		});

		final StringBuilder output = new StringBuilder();
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

			// TODO: Look for any regex commands that are not part of capture groups and resolve them to suitable text

			// TODO: Remove any escapes like '\(' should be '(' and '\\' should be '\'

			// Trim any capture groups
			int lastPos = 0;
			int pos = 0;
			String tidiedRegex = "";
			int captureGroupStart = -1;
			int captureGroupEnd = -1;
			int captureGroup = 0;
			int outputGroup = 1;	// Starts at one for the text mate groups

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
				// Include the parameter type with textmate style "${1:number} groups
				List<ParameterInfo> parameterInfos = entry.getValue().parameterInfos;
				if ( null != parameterInfos && captureGroup < parameterInfos.size())
				{
					// Use the type name without any dots
					String typeName = parameterInfos.get(captureGroup).getType().toString();
					int pos2 = typeName.lastIndexOf('.');
					if ( pos2 != -1)
					{
						typeName = typeName.substring(pos2+1);
					}
					tidiedRegex += "${" + outputGroup + ":" + typeName + "}";
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
			}

			System.out.println(tidiedRegex);
		}
		try
		{
			FileUtils.writeStringToFile(new File("target" , "gherkin-steps.js"), output.toString());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
