package com.replicanet.cukesplus;

import cucumber.runtime.ClassFinder;
import cucumber.runtime.Runtime;
import cucumber.runtime.RuntimeOptions;
import cucumber.runtime.io.ResourceLoader;
import gherkin.I18n;
import gherkin.formatter.Reporter;
import gherkin.formatter.model.Step;

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

    int currentPosition = -1;
        @Override
    public void runStep(String featurePath, Step step, Reporter reporter, I18n i18n) {
        retireMacroSteps();
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
        retireMacroSteps();
    }

    private void retireMacroSteps() {
        currentPosition = -1;
        while (!macroSteps.isEmpty()) {
            currentPosition = 0;
            MacroStep macroStep = macroSteps.remove(0);
            super.runStep(macroStep.featurePath, macroStep.step, macroStep.reporter, macroStep.i18n);
        }
        currentPosition = -1;
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
