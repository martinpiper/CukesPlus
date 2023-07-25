package cucumber.runtime.java;

import TestGlue.Conditional;
import com.replicanet.cukesplus.IgnoreConditionalExecution;
import cucumber.runtime.StepDefinition;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ExtensionRuntimeAccess {
    public static boolean JavaStepDefinitionGetAllowRunStep(StepDefinition step) throws NoSuchFieldException {
        if (step instanceof cucumber.runtime.java.JavaStepDefinition) {
            Field f = null;
            JavaStepDefinition realStepDef = (JavaStepDefinition) step;
            f = realStepDef.getClass().getDeclaredField("method");
            f.setAccessible(true);
            try {
                Object baseMethodObject = f.get(realStepDef);
                if (null != baseMethodObject) {
                    Method realMethodObject = (Method) baseMethodObject;
                    IgnoreConditionalExecution desiredAnnotation = realMethodObject.getAnnotation(IgnoreConditionalExecution.class);
                    if (null != desiredAnnotation) {
                        return true;
                    }
                }
            } catch (Exception e) {}
            int conditionalIgnoredLevel = Conditional.getLevelToIgnore();
            if (conditionalIgnoredLevel > 0) {
                return false;
            }
        }
        return true;
    }
}
