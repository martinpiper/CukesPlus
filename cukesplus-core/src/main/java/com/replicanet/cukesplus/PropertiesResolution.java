package com.replicanet.cukesplus;

import cucumber.api.Scenario;

import java.text.ParseException;

public class PropertiesResolution {

    public static void writeDebug(Scenario scenario, String message) {
        if (null != scenario) {
            scenario.write(message);
        }
//        System.out.println(message);
    }

    public static String resolveInput(String input) throws ParseException {
        return resolveInput(null,input);
    }

    public static String resolveInput(Scenario scenario, String input) throws ParseException {
        int lastOpeningPos;
        String working = input;
        while ( (lastOpeningPos = working.lastIndexOf("${")) != -1) {
            int closingPos = working.indexOf('}', lastOpeningPos);
            if (closingPos == -1) {
                throw new IllegalArgumentException("Opening ${ was not matched with a closing } at " + lastOpeningPos);
            }

            String propertyToFind = working.substring(lastOpeningPos+2, closingPos);
            String toReplaceWith = System.getProperty(propertyToFind, "");

            writeDebug(scenario,"Resolved property '" + propertyToFind + "' to value '" + toReplaceWith + "'");

            String newInput = working.substring(0,lastOpeningPos) + toReplaceWith + working.substring(closingPos+1);
            working = newInput;
        }
        return working;
    }
}
