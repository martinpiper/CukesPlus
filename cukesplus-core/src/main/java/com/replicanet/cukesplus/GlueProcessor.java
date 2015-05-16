package com.replicanet.cukesplus;

import cucumber.api.StepDefinitionReporter;
import cucumber.runtime.Glue;
import cucumber.runtime.Runtime;
import cucumber.runtime.StepDefinition;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 *
 */
public class GlueProcessor
{
	public static void processGlue(Runtime runtime)
	{
		Glue glue = runtime.getGlue();

		final SortedMap<String,String> glueMap = new TreeMap<String,String>();
		glue.reportStepDefinitions( new StepDefinitionReporter()
		{
			@Override
			public void stepDefinition(StepDefinition stepDefinition)
			{
				glueMap.put(stepDefinition.getLocation(true), stepDefinition.getPattern());
			}
		});

		final StringBuilder output = new StringBuilder();
		for(SortedMap.Entry<String,String> entry : glueMap.entrySet())
		{
			output.append(entry.getKey() + "\n");
			output.append(entry.getValue() + "\n");
		}
		try
		{
			FileUtils.writeStringToFile(new File("target" , "glue.txt"), output.toString());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
