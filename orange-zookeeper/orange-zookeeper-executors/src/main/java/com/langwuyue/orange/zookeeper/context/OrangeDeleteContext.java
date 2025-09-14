package com.langwuyue.orange.zookeeper.context;

import java.lang.reflect.Method;

import com.langwuyue.orange.zookeeper.annotation.OrangeContextFieldValue;
import com.langwuyue.orange.zookeeper.annotation.OrangeMethodAnnotationHandler;
import com.langwuyue.orange.zookeeper.annotations.Delete;
import com.langwuyue.orange.zookeeper.annotations.OrangeZookeeperClient;

public class OrangeDeleteContext extends OrangeZookeeperContext {
	
	@OrangeContextFieldValue(binding = Delete.class, handler = OrangeMethodAnnotationHandler.class)
	private Delete deleteAnnotation;
	
	
	public OrangeDeleteContext(
		ZNode zNode, 
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args,
		OrangeZookeeperClient client
	) {
		super(zNode, operationOwner, operationMethod, args, client);
	}


	public Delete getDeleteAnnotation() {
		return deleteAnnotation;
	}
}
