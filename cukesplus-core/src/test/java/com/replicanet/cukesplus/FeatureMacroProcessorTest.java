package com.replicanet.cukesplus;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import sun.security.util.PendingException;

import java.io.File;

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
		assertThat(gotException , is(true));
	}

	@Test
	public void processFeatureFile1() throws Exception
	{
		FeatureMacroProcessor processor = new FeatureMacroProcessor();
		processor.processMacroFile("src/test/resources/macros/test4.macro");
		processor.processFeatureFile("src/test/resources/features/test1.macroFeature" , "target/test1.feature");

		String expected = FileUtils.readFileToString(new File("src/test/resources/features/test1.feature"));
		expected = expected.replaceAll("\\s","");
		String output = FileUtils.readFileToString(new File("target/test1.feature"));
		output = output.replaceAll("\\s","");
		assertThat(expected , is(equalTo(output)));
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
}
