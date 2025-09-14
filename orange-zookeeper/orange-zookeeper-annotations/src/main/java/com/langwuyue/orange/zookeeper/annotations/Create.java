package com.langwuyue.orange.zookeeper.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Create {

	boolean creatingParentsIfNeeded() default true;
	
	boolean creatingParentContainersIfNeeded() default false;
	
	boolean idempotent() default true;
	
	boolean withProtection() default false;
}
