package com.langwuyue.orange.zookeeper.mapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.langwuyue.orange.zookeeper.OrangeZookeeperException;
import com.langwuyue.orange.zookeeper.OrangeZookeeperOperations;
import com.langwuyue.orange.zookeeper.annotations.PathVariable;
import com.langwuyue.orange.zookeeper.executors.OrangeCreateExecutor;
import com.langwuyue.orange.zookeeper.executors.OrangeZookeeperExecutor;
import com.langwuyue.orange.zookeeper.template.JSONOperationsTemplate;

public class OrangeDefaultZookeeperExecutorMapping implements OrangeZookeeperExecutorMapping {
	
	private Class<?> operationOwner;
	
	private OrangeZookeeperOperations operations;
	
	private Map<Method,OrangeZookeeperExecutor> methodExecutorMap;
	
	private Map<Method,Method> extendMethodMap;
	
	private Map<Long,OrangeZookeeperExecutor> executorRegistry;
	
	private OrangeZookeeperExecutorIdGenerator generator;
	
	public OrangeDefaultZookeeperExecutorMapping(Class<?> operationOwner, OrangeZookeeperOperations operations) {
		this.operationOwner = operationOwner;
		this.operations = operations;
		this.executorRegistry = new HashMap<>();
		this.extendMethodMap = new HashMap<>();
		this.methodExecutorMap = new HashMap<>();
		this.generator = new OrangeDefaultZookeeperExecutorIdGenerator();
	}
	
	@Override
	public void initExecutorRegistry() {
		this.generator.initExecutorIdGenerator();
		List<OrangeZookeeperExecutor> executors = new ArrayList<>();
		executors.add(new OrangeCreateExecutor(this.operations));
		for(OrangeZookeeperExecutor executor : executors) {
			this.executorRegistry.put(generator.generate(executor.getSupportedAnnotationClasses()), executor);
		}
		scanMethods();
	}
	
	private void scanMethods() {
		Method[] methods = this.operationOwner.getMethods();
		for(Method method : methods) {
			OrangeZookeeperExecutor executor = this.methodExecutorMap.get(method);
			if(executor != null) {
				continue;
			}
			Annotation[] annotations = method.getAnnotations();
			if(annotations == null || annotations.length == 0) {
				scanParentInterfaces(method);
				return;
			}
			long id = this.generator.generate(getAnnotationClasses(annotations,method));
			executor = this.executorRegistry.get(id);
			if(executor == null) {
				throw new OrangeZookeeperException(String.format("Operation not support! Please compare with operation template %s", getTemplateClass()));
			}
			this.methodExecutorMap.put(method, executor);
			this.extendMethodMap.put(method, method);
		}
	}
	
	protected OrangeZookeeperExecutor scanParentInterfaces(Method method) {
		Class[] interfaces = method.getDeclaringClass().getInterfaces();
		OrangeZookeeperExecutor executor = null;
		for(Class interfaceClass : interfaces) {
			try {
				Method superMethod = interfaceClass.getMethod(method.getName(), method.getParameterTypes());
				executor = getExecutor(superMethod);
				if(executor == null) {
					continue;
				}
				this.methodExecutorMap.put(method, executor);
				this.extendMethodMap.put(method, superMethod);
				return executor;
			}catch (Exception e) {
				if(!(e instanceof NoSuchMethodException)) {
					continue;
				}
				Method[] methods = method.getDeclaringClass().getDeclaredMethods();
				for(Method m : methods) {
					if(!m.isBridge()) {
						continue;
					}
					if(m.equals(method)) {
						continue;
					}
					if(!m.getName().equals(method.getName())) {
						continue;
					}
					if(m.getParameterCount() != method.getParameterCount()) {
						continue;
					}
					Class[] mTypes = m.getParameterTypes();
					Class[] methodTypes = method.getParameterTypes();
					boolean isOverride = true;
					for (int i = 0; i < mTypes.length; i++) {
						if(!mTypes[i].isAssignableFrom(methodTypes[i])) {
							isOverride = false;
							break;
						}
					}
					if(isOverride) {
						executor = getExecutor(m);
						if(executor == null) {
							continue;
						}
						this.methodExecutorMap.put(method, executor);
						this.extendMethodMap.put(method, m);
						return executor;
					}
				}
			}
		}		
		throw new OrangeZookeeperException(String.format("Operation not support! Please compare with operation template %s", getTemplateClass()));
	}
	
	protected List<Class<? extends Annotation>> getAnnotationClasses(Annotation[] annotations,Method method) {
		List<Class<? extends Annotation>> annotationClasses = new ArrayList<>();
		for(Annotation annotation : annotations) {
			annotationClasses.add(annotation.annotationType());
		}
		Annotation[][] parametersAnnotations = method.getParameterAnnotations();
		if(parametersAnnotations != null && parametersAnnotations.length != 0) {
			for(Annotation[] parameterAnnotations : parametersAnnotations) {
				for(Annotation parameterAnnotation : parameterAnnotations) {
					if(parameterAnnotation.annotationType() == PathVariable.class) {
						continue;
					}
					annotationClasses.add(parameterAnnotation.annotationType());
				}
			}
		}
		return annotationClasses;
	}

	private Class<?> getTemplateClass() {
		return JSONOperationsTemplate.class;
	}

	@Override
	public OrangeZookeeperExecutor getExecutor(Method method) {
		return this.methodExecutorMap.get(method);
	}

	@Override
	public Method getActualMethod(Method method) {
		return this.extendMethodMap.get(method);
	}
}
