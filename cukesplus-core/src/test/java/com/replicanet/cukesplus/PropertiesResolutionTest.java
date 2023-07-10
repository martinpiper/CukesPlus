package com.replicanet.cukesplus;

import cucumber.api.Scenario;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;
import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(JUnit4.class)
public class PropertiesResolutionTest extends TestCase {

    Scenario scenario;
    String gotScenarioText;
    @Before
    public void before() {
        gotScenarioText = "";
        // Hook into Scenario
        scenario = new Scenario() {
            @Override
            public Collection<String> getSourceTagNames() {
                return null;
            }

            @Override
            public String getStatus() {
                return null;
            }

            @Override
            public boolean isFailed() {
                return false;
            }

            @Override
            public void embed(byte[] bytes, String s) {

            }

            @Override
            public void write(String s) {
                gotScenarioText += s;
            }

            @Override
            public String getName() {
                return null;
            }

            @Override
            public String getId() {
                return null;
            }
        };
    }

    @Test
    public void testResolveInputWithoutResolution() throws ParseException {
        System.getProperties().setProperty("test.input", "432");
        String result = PropertiesResolution.resolveInput(scenario, "123");
        assertThat(result,comparesEqualTo("123"));
        assertThat(gotScenarioText,comparesEqualTo(""));
    }

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void testResolveInputWithMismatchedBraces() throws ParseException {
        System.getProperties().setProperty("test.input", "432");
        exceptionRule.expect(java.lang.IllegalArgumentException.class);
        exceptionRule.expectMessage("Opening ${ was not matched with a closing } at 0");
        String result = PropertiesResolution.resolveInput(scenario, "${test.input");
        assertThat(gotScenarioText,comparesEqualTo(""));
    }

    @Test
    public void testResolveInputWithSimpleResolution() throws ParseException {
        System.getProperties().setProperty("test.input", "432");
        String result = PropertiesResolution.resolveInput(scenario, "${test.input}");
        assertThat(result,comparesEqualTo("432"));
        assertThat(gotScenarioText,comparesEqualTo("Resolved property 'test.input' to value '432'"));
    }

    @Test
    public void testResolveInputWithResolutionPrefix() throws ParseException {
        System.getProperties().setProperty("test.input", "432");
        String result = PropertiesResolution.resolveInput(scenario, "abc${test.input}");
        assertThat(result,comparesEqualTo("abc432"));
        assertThat(gotScenarioText,comparesEqualTo("Resolved property 'test.input' to value '432'"));
    }

    @Test
    public void testResolveInputWithResolutionSuffix() throws ParseException {
        System.getProperties().setProperty("test.input", "432");
        String result = PropertiesResolution.resolveInput(scenario, "${test.input}xyz");
        assertThat(result,comparesEqualTo("432xyz"));
        assertThat(gotScenarioText,comparesEqualTo("Resolved property 'test.input' to value '432'"));
    }

    @Test
    public void testResolveInputWithResolutionPrefixSuffix() throws ParseException {
        System.getProperties().setProperty("test.input", "432");
        String result = PropertiesResolution.resolveInput(scenario, "abc${test.input}xyz");
        assertThat(result,comparesEqualTo("abc432xyz"));
        assertThat(gotScenarioText,comparesEqualTo("Resolved property 'test.input' to value '432'"));
    }

    @Test
    public void testResolveInputWithTwoResolutionPrefixSuffix() throws ParseException {
        System.getProperties().setProperty("test.input1", "432");
        System.getProperties().setProperty("test.input2", "987");
        String result = PropertiesResolution.resolveInput(scenario, "abc${test.input1}xyz${test.input2}qed");
        assertThat(result,comparesEqualTo("abc432xyz987qed"));
        assertThat(gotScenarioText,comparesEqualTo("Resolved property 'test.input2' to value '987'Resolved property 'test.input1' to value '432'"));
    }

    @Test
    public void testResolveInputWithTwoResolutionPrefixSuffixRecursive() throws ParseException {
        System.getProperties().setProperty("test.input1", "432");
        System.getProperties().setProperty("test.input2", "987");
        System.getProperties().setProperty("test.input3", "2");
        String result = PropertiesResolution.resolveInput(scenario, "abc${test.input1}xyz${test.input${test.input3}}qed");
        assertThat(result,comparesEqualTo("abc432xyz987qed"));
        assertThat(gotScenarioText,comparesEqualTo("Resolved property 'test.input3' to value '2'Resolved property 'test.input2' to value '987'Resolved property 'test.input1' to value '432'"));
    }

    @Test
    public void testResolveInputWithTwoResolutionPrefixSuffixRecursiveWithProperty() throws ParseException {
        System.getProperties().setProperty("test.input1", "432");
        System.getProperties().setProperty("test.input2", "987");
        System.getProperties().setProperty("test.input3", "${test.input2}");
        String result = PropertiesResolution.resolveInput(scenario, "abc${test.input1}xyz${test.input3}qed");
        assertThat(result,comparesEqualTo("abc432xyz987qed"));
        assertThat(gotScenarioText,comparesEqualTo("Resolved property 'test.input3' to value '${test.input2}'Resolved property 'test.input2' to value '987'Resolved property 'test.input1' to value '432'"));
    }
}
