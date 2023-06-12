package com.replicanet.cukesplus;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Aspect
public class AspectE {
//    @Pointcut("execution(@EntryPoint * gherkin.lexer.I18nLexer.scan(..))")
//    public void defineEntryPoint() {
//    }

//    @Before("defineEntryPoint()")
//    @Before("execution(* gherkin.lexer.I18nLexer.scan(..))")
//    @Around("execution(* gherkin.lexer.I18nLexer.scan(java.lang.String))")
    @Around("execution(* gherkin.parser.Parser.parse(java.lang.String,java.lang.String,..))")
    public void theLexerScan(ProceedingJoinPoint pjp) throws Throwable {
        Object[] args = pjp.getArgs();
        String originalFeature = (String) args[0];
        String featureURI = (String) args[1];
//        System.out.println("****** HERE original!!! ********\r\n" + originalFeature);
        String newFeature = FeatureProvider.getFeatureWithMacro(originalFeature , featureURI);
//        System.out.println("****** HERE updated!!! ********\r\n" + newFeature);
        args[0] = newFeature;
//        MethodSignature signature = (MethodSignature) pjp.getSignature();
//        String methodName = signature.getMethod().getName();
//        Class<?>[] parameterTypes = signature.getMethod().getParameterTypes();
        pjp.proceed(args);
    }

    @Around("execution(* cucumber.runtime.StepDefinitionMatch.runStep(gherkin.I18n))")
    public void theRunStep(ProceedingJoinPoint pjp) throws Throwable {
//        System.out.println("****** HERE theRunStep!!! ********\r\n");
        Object[] args = pjp.getArgs();
        boolean allow = FeatureProvider.allowRunStep(pjp.getThis());
        if (!allow) {
            return;
        }
//        System.out.println("****** HERE theRunStep!!! proceed ********\r\n");

        pjp.proceed(args);
    }

    @AfterReturning("initialization(cucumber.runtime.model.PathWithLines.new(java.lang.String))")
    public void thePathWithLines(JoinPoint jp) throws Throwable {
        Object[] args = jp.getArgs();
        Object object = jp.getThis();

        FeatureProvider.processPathWithLines(object);
    }
}
