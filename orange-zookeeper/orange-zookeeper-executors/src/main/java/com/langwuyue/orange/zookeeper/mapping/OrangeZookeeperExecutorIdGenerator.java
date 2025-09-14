package com.langwuyue.orange.zookeeper.mapping;

import java.lang.annotation.Annotation;
import java.util.List;

public interface OrangeZookeeperExecutorIdGenerator {
	
	long generate(List<Class<? extends Annotation>> annotationClasses);
	
	void initExecutorIdGenerator();

}
