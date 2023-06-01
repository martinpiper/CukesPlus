package com.replicanet.cukesplus;

import cucumber.runtime.ClassFinder;
import cucumber.runtime.Runtime;
import cucumber.runtime.RuntimeOptions;
import cucumber.runtime.io.ResourceLoader;
import gherkin.I18n;
import gherkin.formatter.Reporter;
import gherkin.formatter.model.Step;

public class ExtensionRuntime extends Runtime {

    public static String featurePath;
    public static Step step;
    public static Reporter reporter;
    public static I18n i18n;
    public static ExtensionRuntime runtime;

    public ExtensionRuntime(ResourceLoader resourceLoader, ClassFinder classFinder, ClassLoader classLoader, RuntimeOptions runtimeOptions) {
        super(resourceLoader,classFinder,classLoader,runtimeOptions);
    }

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

    public void runMacroStep(String featurePath, Step step, Reporter reporter, I18n i18n) {
        // At this point, because we are exposing global static variables, then queue any threads that might happen at this point
        synchronized (ExtensionRuntime.class){
            super.runStep(featurePath, step, reporter, i18n);
        }
    }
}
