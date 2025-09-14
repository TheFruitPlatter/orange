package com.langwuyue.orange.zookeeper.executors;

import java.lang.annotation.Annotation;
import java.util.List;

import com.langwuyue.orange.zookeeper.context.OrangeZookeeperContext;

public interface OrangeZookeeperExecutor {
	
	Object execute(OrangeZookeeperContext context) throws Exception;

	List<Class<? extends Annotation>> getSupportedAnnotationClasses();

	Class<? extends OrangeZookeeperContext> getContextClass();

}
