package com.langwuyue.orange.zookeeper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public interface OrangeZookeeperBackgroundCallback {

	void processResult(BackgroundEvent event) throws Exception;

	Type getDataType();

	Class<?> getTargetClass();

	Class<? extends Annotation> getOperationAnnotationClass();
}
