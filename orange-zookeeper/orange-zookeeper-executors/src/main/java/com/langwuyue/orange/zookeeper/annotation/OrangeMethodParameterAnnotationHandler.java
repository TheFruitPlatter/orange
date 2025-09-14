package com.langwuyue.orange.zookeeper.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import com.langwuyue.orange.zookeeper.context.OrangeZookeeperContext;
import com.langwuyue.orange.zookeeper.utils.OrangeReflectionUtils;

public class OrangeMethodParameterAnnotationHandler implements OrangeContextFieldValueHandler {

	@Override
	public void handle(
		OrangeZookeeperContext context, 
		Field field, 
		Annotation parameterAnnotation, 
		Object[] args, 
		int parameterIndex, 
		int parameterCount
	) {
		OrangeReflectionUtils.setFieldValue(field, context, parameterAnnotation);
	}
	
}
