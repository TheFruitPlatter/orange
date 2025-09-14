package com.langwuyue.orange.zookeeper.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Delete {

	boolean deletingChildrenIfNeeded() default false;
	
	boolean guaranteed() default true;
	
	boolean quietly() default false;
}
