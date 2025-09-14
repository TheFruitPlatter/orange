package com.langwuyue.orange.zookeeper.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;

import com.langwuyue.orange.zookeeper.context.OrangeZookeeperContext;
import com.langwuyue.orange.zookeeper.utils.OrangeReflectionUtils;

public interface OrangeContextFieldValueHandler {

	default void handle(
		OrangeZookeeperContext context, 
		Field field, 
		Annotation methodAnnotation
	) {
		OrangeReflectionUtils.setFieldValue(field, context, methodAnnotation);
	}

	default void handle(
		OrangeZookeeperContext context, 
		Field field, 
		Annotation parameterAnnotation, 
		Object[] args, 
		int parameterIndex, 
		int parameterCount
	) {
		Object value;
		int len = args.length;
		if(len > parameterCount && parameterIndex == parameterCount - 1) {
			value = Arrays.copyOfRange(args, parameterIndex, len);
		}else {
			value = args[parameterIndex];
		}
		OrangeReflectionUtils.setFieldValue(field, context, value);
	}
}
