package com.replicanet.cukesplus;

public class FeatureProvider {

    static FeatureProvider last;

    public FeatureProvider() {
        this.last = this;
    }

    public String getFeature(String feature) {
        return feature;
    }

    public static String getFeatureWithMacro(String feature) {
        if (last == null) {
            return feature;
        }
        return last.getFeature(feature);
    }
}
