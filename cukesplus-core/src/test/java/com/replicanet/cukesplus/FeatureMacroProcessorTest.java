package com.replicanet.cukesplus;

import cucumber.api.PendingException;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Created by Martin on 13/05/2017.
 */
public class FeatureMacroProcessorTest
{
//	@Test
	public void processMacroSyntaxToGlue() throws Exception
	{
		throw new PendingException();
	}

	@Test
	public void processMacroFileTest1() throws Exception
	{
		FeatureMacroProcessor processor = new FeatureMacroProcessor();
		processor.processMacroFile("src/test/resources/macros/test1.macro");
		assertThat(processor.macroMap.size() , is(equalTo(1)));
		assertThat(processor.macroMap , hasKey("^this is a step definition$"));
	}

	@Test
	public void processMacroFileTest2() throws Exception
	{
		FeatureMacroProcessor processor = new FeatureMacroProcessor();
		processor.processMacroFile("src/test/resources/macros/test2.macro");
		assertThat(processor.macroMap.size() , is(equalTo(2)));
		assertThat(processor.macroMap , hasKey("^this is a step definition$"));
		assertThat(processor.macroMap , hasKey("^this is a step definition with a simple parameter for (.*) in the middle$"));
		processor.processMacroSyntaxToGlue();
	}

	@Test
	public void processMacroFileTest3() throws Exception
	{
		FeatureMacroProcessor processor = new FeatureMacroProcessor();
		processor.processMacroFile("src/test/resources/macros/test3.macro");
		assertThat(processor.macroMap.size() , is(equalTo(1)));
		assertThat(processor.macroMap , hasKey("^this is a step definition with a simple parameter for (.*) in the middle$"));
		FeatureMacroProcessor.Macro macro = processor.macroMap.get("^this is a step definition with a simple parameter for (.*) in the middle$");
		assertThat(macro.stepLines.size() , is(equalTo(3)));
		assertThat(macro.stepLines , contains("Then it does this step" , "And this step" , "* this is a step with parameter $1"));
		processor.processMacroSyntaxToGlue();
	}

	@Test
	public void processMacroFileTest4() throws Exception
	{
		FeatureMacroProcessor processor = new FeatureMacroProcessor();
		processor.processMacroFile("src/test/resources/macros/test4.macro");
		assertThat(processor.macroMap.size() , is(equalTo(4)));
		processor.processMacroSyntaxToGlue();
	}

	@Test
	public void processMacroFileTest5() throws Exception
	{
		boolean gotException = false;
		FeatureMacroProcessor processor = new FeatureMacroProcessor();
		try
		{
			processor.processMacroFile("src/test/resources/macros/test5.macro");
		}
		catch (RuntimeException e)
		{
			gotException = true;
		}
		assertThat(gotException , is(false));
	}

	@Test
	public void processMacroFileTest6() throws Exception
	{
		// https://github.com/martinpiper/BDD6502/issues/16
		FeatureMacroProcessor processor = new FeatureMacroProcessor();
		processor.processMacroFile("src/test/resources/macros/test6.macro");
		assertThat(processor.macroMap.values().iterator().next().stepLines.get(2) , containsString("<<<"));
	}
	@Test
	public void processFeatureFile1() throws Exception
	{
		FeatureMacroProcessor processor = new FeatureMacroProcessor();
		processor.processMacroFile("src/test/resources/macros/test4.macro");
		processor.processFeatureFile("src/test/resources/features/test1.macroFeature" , "target/test1.feature");

		ensureFilesAreTheSame("target/test1.feature" , "src/test/resources/features/test1.feature");
	}

	private static void ensureFilesAreTheSame(String output, String expected) throws IOException {
		String expectedText = FileUtils.readFileToString(new File(expected));
		expectedText = expectedText.replaceAll("\\s","");
		String outputText = FileUtils.readFileToString(new File(output));
		outputText = outputText.replaceAll("\\s","");
		assertThat(expectedText , is(equalTo(outputText)));
	}

	@Test
	public void processFeatureFile2() throws Exception
	{
		FeatureMacroProcessor processor = new FeatureMacroProcessor();

		boolean gotException = false;
		try
		{
			processor.processFeatureFile("src/test/resources/features/test2.macroFeature" , "target/test2.feature");
		}
		catch (RuntimeException e)
		{
			gotException = true;
		}
		assertThat(gotException , is(true));
	}

	@Test
	public void processFeatureFile6() throws Exception
	{
		// https://github.com/martinpiper/BDD6502/issues/16
		FeatureMacroProcessor processor = new FeatureMacroProcessor();

		System.setProperty("test.insert.rows", "2");
		processor.processMacroFile("src/test/resources/macros/test6.macro");
		processor.processFeatureFile("src/test/resources/features/test6.macroFeature" , "target/test6.feature");

		ensureFilesAreTheSame("target/test6.feature" , "src/test/resources/features/test6.feature");
	}
}
