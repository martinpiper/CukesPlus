package com.replicanet.cukesplus;

import cucumber.runtime.ClassFinder;
import cucumber.runtime.Runtime;
import cucumber.runtime.RuntimeOptions;
import cucumber.runtime.io.MultiLoader;
import cucumber.runtime.io.ResourceLoader;
import cucumber.runtime.io.ResourceLoaderClassFinder;
import gherkin.I18n;
import gherkin.formatter.Reporter;
import gherkin.formatter.model.Step;

public class MyRuntime extends Runtime {

    public static String featurePath;
    public static Step step;
    public static Reporter reporter;
    public static I18n i18n;
    public static MyRuntime runtime;

    public MyRuntime(ResourceLoader resourceLoader, ClassFinder classFinder, ClassLoader classLoader, RuntimeOptions runtimeOptions) {
        super(resourceLoader,classFinder,classLoader,runtimeOptions);
    }

        @Override
    public void runStep(String featurePath, Step step, Reporter reporter, I18n i18n) {
        // Hacky expose of these
        this.runtime = this;
        this.featurePath = featurePath;
        this.step = step;
        this.reporter = reporter;
        this.i18n = i18n;
        super.runStep(featurePath, step, reporter, i18n);
    }
}
