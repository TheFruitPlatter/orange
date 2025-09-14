package com.langwuyue.orange.zookeeper.context;

import java.lang.reflect.Method;

import com.langwuyue.orange.zookeeper.annotation.OrangeContextFieldValue;
import com.langwuyue.orange.zookeeper.annotation.OrangeMethodAnnotationHandler;
import com.langwuyue.orange.zookeeper.annotations.Data;
import com.langwuyue.orange.zookeeper.annotations.OrangeZookeeperClient;
import com.langwuyue.orange.zookeeper.annotations.SetData;

public class OrangeCreationWithACLAndDataContext extends OrangeCreationWithACLContext {
	
	@OrangeContextFieldValue(binding = SetData.class, handler = OrangeMethodAnnotationHandler.class)
	private SetData setDataAnnotation;
	
	@OrangeContextFieldValue(binding = Data.class)
	private Object data;
	
	public OrangeCreationWithACLAndDataContext(
		ZNode zNode, 
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args,
		OrangeZookeeperClient client
	) {
		super(zNode, operationOwner, operationMethod, args, client);
	}

	public SetData getSetDataAnnotation() {
		return setDataAnnotation;
	}

	public Object getData() {
		return data;
	}
	
}
