package com.langwuyue.orange.zookeeper.context;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.langwuyue.orange.zookeeper.annotation.OrangeContextFieldValue;
import com.langwuyue.orange.zookeeper.annotation.OrangeContextFieldValueHandler;
import com.langwuyue.orange.zookeeper.annotations.OrangeZookeeperClient;
import com.langwuyue.orange.zookeeper.context.OrangeZookeeperContext.ZNode;


public class OrangeZookeeperContextBuilder {
	
	private static final Map<Class<? extends OrangeContextFieldValueHandler>, OrangeContextFieldValueHandler> CONTEXT_FIELD_VALUE_HANDLER_MAP = new ConcurrentHashMap<>();
	
	private Class<? extends OrangeZookeeperContext> contextClass;
	
	private Class<?> operationOwner;
	
	private Method operationMethod;
	
	private Method actualMethod;
	
	private Object[] args;
	
	private ZNode zNode;
	
	private OrangeZookeeperClient client;
	
	public OrangeZookeeperContextBuilder contextClass(Class<? extends OrangeZookeeperContext> contextClass) {
		this.contextClass = contextClass;
		return this;
	}
	public OrangeZookeeperContextBuilder operationOwner(Class<?> operationOwner) {
		this.operationOwner = operationOwner;
		return this;
	}
	public OrangeZookeeperContextBuilder operationMethod(Method operationMethod) {
		this.operationMethod = operationMethod;
		return this;
	}
	public OrangeZookeeperContextBuilder actualMethod(Method actualMethod) {
		this.actualMethod = actualMethod;
		return this;
	}
	public OrangeZookeeperContextBuilder args(Object[] args) {
		this.args = args;
		return this;
	}
	public OrangeZookeeperContextBuilder zNode(ZNode zNode) {
		this.zNode = zNode;
		return this;
	}
	public OrangeZookeeperContextBuilder client(OrangeZookeeperClient client) {
		this.client = client;
		return this;
	}
	public OrangeZookeeperContext build() throws Exception {
		OrangeZookeeperContext context = newContext();
		handleFieldValue(context);
		return context;
	}
	public void handleFieldValue(OrangeZookeeperContext context) {
		Field[] fields = this.contextClass.getDeclaredFields();
		for(int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			OrangeContextFieldValue contextFieldValue = field.getAnnotation(OrangeContextFieldValue.class);
			if(contextFieldValue == null || handleMethodAnnotation(context,field,contextFieldValue)) {
				continue;
			}
			handleMethodParameter(context,field,contextFieldValue);
		}
	}
	
	boolean handleMethodAnnotation(OrangeZookeeperContext context, Field field, OrangeContextFieldValue contextFieldValue) {
		Class<? extends OrangeContextFieldValueHandler> handlerClass = contextFieldValue.handler();
		OrangeContextFieldValueHandler handler = CONTEXT_FIELD_VALUE_HANDLER_MAP.get(handlerClass);
		Class<? extends Annotation> annotationClass = contextFieldValue.binding();
		Annotation[] methodAnnotations = this.actualMethod.getAnnotations();
		for(Annotation annotation : methodAnnotations) {
			if(annotation.annotationType() == annotationClass) {
				handler.handle(context, field, annotation);
				return true;
			}
		}
		return false;
	}
	
	boolean handleMethodParameter(OrangeZookeeperContext context, Field field, OrangeContextFieldValue contextFieldValue) {
		Class<? extends OrangeContextFieldValueHandler> handlerClass = contextFieldValue.handler();
		OrangeContextFieldValueHandler handler = CONTEXT_FIELD_VALUE_HANDLER_MAP.get(handlerClass);
		Class<? extends Annotation> annotationClass = contextFieldValue.binding();
		Annotation[][] parametersAnnotations = this.actualMethod.getParameterAnnotations();
		int count = parametersAnnotations.length;
		boolean handled = false;
		for(int j = 0; j < count; j++) {
			Annotation[] parameterAnnotations = parametersAnnotations[j];
			if(parameterAnnotations == null || parameterAnnotations.length == 0) {
				continue;
			}
			for(Annotation parameterAnnotation : parameterAnnotations) {
				if(parameterAnnotation.annotationType() == annotationClass) {
					handler.handle(context, field, parameterAnnotation, args, j, count);
					handled = true;
				}
			}
		}
		return handled;
	}
	
	protected OrangeZookeeperContext newContext() throws Exception {
		return OrangeZookeeperContext.newInstance(
			this.contextClass, 
			this.zNode, 
			this.operationOwner, 
			this.operationMethod, 
			this.args, 
			this.client
		);
	}
}
