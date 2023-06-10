package com.replicanet.cukesplus;

import cucumber.runtime.*;
import cucumber.runtime.Runtime;
import cucumber.runtime.io.ResourceLoader;
import cucumber.runtime.java.MacroStepDefinition;
import gherkin.I18n;
import gherkin.formatter.Reporter;
import gherkin.formatter.model.Step;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.annotation.Aspect;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ExtensionRuntime extends Runtime {

    public static String featurePath;
    public static Step step;
    public static Reporter reporter;
    public static I18n i18n;
    public static ExtensionRuntime runtime;

    public ExtensionRuntime(ResourceLoader resourceLoader, ClassFinder classFinder, ClassLoader classLoader, RuntimeOptions runtimeOptions) {
        super(resourceLoader,classFinder,classLoader,runtimeOptions);

        macroSteps = new LinkedList<MacroStep>();
    }

    HashMap<String , String> featureURIToOriginalFeature = new HashMap<>();
    HashMap<String , String> featureURIToProcessedFeature = new HashMap<>();

    public static String makeSafeName(String name) {
        String ret = "target/tm.";


        for (int i = 0; i < name.length(); i++)
        {
            char theChar = name.charAt(i);
            if (Character.isLetterOrDigit(theChar)) {
                ret += theChar;
            } else {
                ret += "_";
            }
        }

        return ret;
    }
    class ExtensionFeatureProvider extends FeatureProvider {

        @Override
        public String getFeature(String feature , String featureURI) {
            try {
                featureURIToOriginalFeature.put(featureURI , feature);
                feature = FeatureServerCheck.getFeatureMacroProcessor().processFeatureText(feature, featureURI);
                feature = feature.replace("##__#__## " , "");
//                feature = feature.replace("target/t.macroFeature", featureURI);
                featureURIToProcessedFeature.put(featureURI , feature);
                FileUtils.writeStringToFile(new File(makeSafeName(featureURI)), feature);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return feature;
        }

        @Override
        public boolean getAllowRunStep(Object theObject) {
            StepDefinitionMatch realObject = (StepDefinitionMatch) theObject;

            // Test to see if we should just allow this step to proceed without executing anything
            try {
                Field f = null;
                f = realObject.getClass().getDeclaredField("stepDefinition");
                f.setAccessible(true);
                StepDefinition step = (StepDefinition) f.get(realObject);
                if (step instanceof MacroStepDefinition) {
                    return false;
                }
            } catch (Exception e) {

            }

            return true;
        }

    }

    ExtensionFeatureProvider extensionFeatureProvider = new ExtensionFeatureProvider();
    int currentPosition = -1;
        @Override
    public void runStep(String featurePath, Step step, Reporter reporter, I18n i18n) {
        // At this point, because we are exposing global static variables, then queue any threads that might happen at this point
        synchronized (ExtensionRuntime.class){
            // Hacky expose of these
            this.runtime = this;
            this.featurePath = featurePath;
            this.step = step;
            this.reporter = reporter;
            this.i18n = i18n;
            super.runStep(featurePath, step, reporter, i18n);
        }
    }

    class MacroStep {
        String featurePath;
        Step step;
        Reporter reporter;
        I18n i18n;
    }
    List<MacroStep> macroSteps;

    public void addMacroStep(String featurePath, Step step, Reporter reporter, I18n i18n) {
        // At this point, because we are exposing global static variables, then queue any threads that might happen at this point
        synchronized (ExtensionRuntime.class){
            MacroStep macroStep = new MacroStep();
            macroStep.featurePath = featurePath;
            macroStep.step = step;
            macroStep.reporter = reporter;
            macroStep.i18n = i18n;
            // Handle multiple additions or recursive additions
            if (currentPosition < 0 || macroSteps.size()-1 <= currentPosition) {
                macroSteps.add(macroStep);
            } else {
                macroSteps.add(currentPosition,macroStep);
            }
            currentPosition++;
        }
    }
}
