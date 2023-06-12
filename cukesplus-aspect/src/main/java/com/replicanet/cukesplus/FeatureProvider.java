package com.replicanet.cukesplus;

public class FeatureProvider {

    static FeatureProvider last;

    public FeatureProvider() {
        this.last = this;
    }

    public String getFeature(String feature , String featureURI) {
        return feature;
    }

    public boolean getAllowRunStep(Object theObject) {return true; }

    public Object doProcessPathWithLines(Object theObject) {return theObject; }

    public static String getFeatureWithMacro(String feature , String featureURI) {
        return last.getFeature(feature , featureURI);
    }

    public static boolean allowRunStep(Object theObject) {
        return last.getAllowRunStep(theObject);
    }

    public static Object processPathWithLines(Object theObject) {
        return last.doProcessPathWithLines(theObject);
    }
}
