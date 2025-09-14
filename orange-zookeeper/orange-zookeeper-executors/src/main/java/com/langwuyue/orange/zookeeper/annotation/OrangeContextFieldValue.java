package com.langwuyue.orange.zookeeper.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OrangeContextFieldValue {

	Class<? extends Annotation> binding();
	
	Class<? extends OrangeContextFieldValueHandler> handler() default OrangeMethodParameterValueHandler.class;
}
