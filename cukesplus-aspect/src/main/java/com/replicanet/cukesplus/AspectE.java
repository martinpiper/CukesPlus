package com.replicanet.cukesplus;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Aspect
public class AspectE {
//    @Pointcut("execution(@EntryPoint * gherkin.lexer.I18nLexer.scan(..))")
//    public void defineEntryPoint() {
//    }

//    @Before("defineEntryPoint()")
//    @Before("execution(* gherkin.lexer.I18nLexer.scan(..))")
    @Before("execution(* gherkin.lexer.I18nLexer.scan(java.lang.String))")
    public void theLexerScan(JoinPoint pjp) throws Throwable {
        Object[] args = pjp.getArgs();
        String newFeature = "@testing\r\n" + (String) args[0];
        System.out.println("****** HERE!!! ********\r\n" + newFeature);
        args[0] = newFeature;
//        MethodSignature signature = (MethodSignature) pjp.getSignature();
//        String methodName = signature.getMethod().getName();
//        Class<?>[] parameterTypes = signature.getMethod().getParameterTypes();
//        pjp.proceed(args);
    }
}
