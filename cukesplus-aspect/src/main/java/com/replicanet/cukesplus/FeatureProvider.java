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

    public static String getFeatureWithMacro(String feature , String featureURI) {
        if (last == null) {
            return feature;
        }
        return last.getFeature(feature , featureURI);
    }

    public static boolean allowRunStep(Object theObject) {
        if (last == null) {
            return true;
        }
        return last.getAllowRunStep(theObject);
    }
}
