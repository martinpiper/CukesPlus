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

    public Object[] doProcessTheBasicStatementNew(Object realThis, Object[] args) {return args; }

    // Design note: "last" is referenced without a null check to ensure that all execution paths under test are going to be covered by FeatureProvider hooks.

    public static String getFeatureWithMacro(String feature , String featureURI) {
        return last.getFeature(feature , featureURI);
    }

    public static boolean allowRunStep(Object theObject) {
        return last.getAllowRunStep(theObject);
    }

    public static Object processPathWithLines(Object theObject) {
        return last.doProcessPathWithLines(theObject);
    }

    public static Object[] processTheBasicStatementNew(Object realThis, Object[] args) {
        return last.doProcessTheBasicStatementNew(realThis, args);
    }
}
