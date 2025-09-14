package com.langwuyue.orange.zookeeper.context;

import java.lang.reflect.Method;

import com.langwuyue.orange.zookeeper.annotation.OrangeContextFieldValue;
import com.langwuyue.orange.zookeeper.annotation.OrangeMethodAnnotationHandler;
import com.langwuyue.orange.zookeeper.annotations.Create;
import com.langwuyue.orange.zookeeper.annotations.OrangeZookeeperClient;

public class OrangeCreationContext extends OrangeZookeeperContext {
	
	@OrangeContextFieldValue(binding = Create.class, handler = OrangeMethodAnnotationHandler.class)
	private Create createAnnotation;
	
	public OrangeCreationContext(ZNode zNode, Class<?> operationOwner, Method operationMethod, Object[] args,
			OrangeZookeeperClient client) {
		super(zNode, operationOwner, operationMethod, args, client);
	}

	public Create getCreateAnnotation() {
		return createAnnotation;
	}
}
