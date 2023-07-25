package com.replicanet.cukesplus;

import TestGlue.Conditional;
import cucumber.runtime.*;
import cucumber.runtime.Runtime;
import cucumber.runtime.io.ResourceLoader;
import cucumber.runtime.java.ExtensionRuntimeAccess;
import cucumber.runtime.java.Java8StepDefinition;
import cucumber.runtime.java.MacroStepDefinition;
import cucumber.runtime.model.PathWithLines;
import gherkin.I18n;
import gherkin.formatter.Reporter;
import gherkin.formatter.model.Comment;
import gherkin.formatter.model.Step;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.annotation.Aspect;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.ArrayList;
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
                Object baseStepObject = f.get(realObject);
                if (null != baseStepObject) {
                    StepDefinition step = (StepDefinition) baseStepObject;
                    if (step instanceof MacroStepDefinition) {
                        return false;
                    }
                    boolean state = ExtensionRuntimeAccess.JavaStepDefinitionGetAllowRunStep(step);
                    return state;
                }
            } catch (Exception e) {

            }

            return true;
        }

        @Override
        public Object doProcessPathWithLines(Object theObject) {
            PathWithLines realObject = (PathWithLines) theObject;
            if (realObject.lines != null && realObject.lines.size() > 0) {
                try {
                    System.out.println("Info: Processing line filter for file: " + realObject.path);
                    String theFeature = FileUtils.readFileToString(new File(realObject.path));
                    // Now remap using the macros
                    theFeature = getFeature(theFeature , realObject.path);
                    if (theFeature.contains("#> ")) {
                        System.out.println("Info: Detected macro expansion(s)");
                        String[] featureLines = theFeature.split("\\R");
                        ArrayList<Long> replacementList = new ArrayList<Long>();
                        for (Long index : realObject.lines) {
                            try {
                                // Increase replacementLine until currentLine >= index
                                int replacementLine = 0;
                                int currentLine = 0;
                                boolean autoIncrement = true;
                                for (String theLine : featureLines) {
                                    theLine = theLine.trim();
                                    if (theLine.startsWith("#> ")) {
                                        String theLineSplits[] = theLine.split(" ", 3);
                                        currentLine = Integer.parseInt(theLineSplits[1]);
                                        autoIncrement = false;
                                    }
                                    if (autoIncrement) {
                                        currentLine++;
                                    }
                                    replacementLine++;
                                    if (currentLine >= index) {
                                        break;
                                    }
                                }

                                System.out.println("Info: " + index + " remaps to " + replacementLine);
                                replacementList.add(new Long(replacementLine));
                            } catch (Exception e) {
                                // Any problem, just add the old index
                                System.out.println("Info: No remap for " + index);
                                replacementList.add(new Long(index));
                            }
                        }

                        Field field = PathWithLines.class.getDeclaredField("lines");
                        field.setAccessible(true);
                        field.set(realObject, replacementList);
                    }
                } catch (Exception e) {
                    int i = 0;
                }
            }
            return realObject;
        }

        @Override
        public Object[] doProcessTheBasicStatementNew(Object realThis, Object[] args) {
            try {
                if (realThis instanceof Step && args.length >= 4) {
                    if (args[0] instanceof ArrayList && args[1] instanceof String) {
                        ArrayList comments = (ArrayList) args[0];
                        String prefix = "";
                        for (int i = 0; i < comments.size(); i++) {
                            Object comment = comments.get(i);
                            if (comment instanceof Comment) {
                                Comment realComment = (Comment) comment;
                                String commentText = realComment.getValue();
                                if (commentText.startsWith("#>>> ")) {
                                    String[] splits = commentText.split(",", 3);
                                    if (splits.length >= 2) {
                                        int depth = Integer.parseInt(splits[1].trim());
                                        while (depth > 0) {
                                            // Add some non-breaking spaces for report indentation...
                                            prefix += "\u00A0\u00A0";
                                            depth--;
                                        }
                                    }
                                }
                            }
                        }
                        String newKeyword = prefix + (String) args[1];
                        args[1] = newKeyword;
                    }
                }
            } catch (Exception e) {}
            return args;
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
}
