package com.langwuyue.orange.zookeeper.context;

import java.lang.reflect.Method;

import com.langwuyue.orange.zookeeper.ACL;
import com.langwuyue.orange.zookeeper.annotation.OrangeContextFieldValue;
import com.langwuyue.orange.zookeeper.annotation.OrangeMethodAnnotationHandler;
import com.langwuyue.orange.zookeeper.annotations.OrangeZookeeperClient;
import com.langwuyue.orange.zookeeper.annotations.Perms;
import com.langwuyue.orange.zookeeper.annotations.SetACL;

public class OrangeSetACLContext extends OrangeZookeeperContext {
	
	@OrangeContextFieldValue(binding = SetACL.class, handler = OrangeMethodAnnotationHandler.class)
	private SetACL setACLAnnotation;
	
	@OrangeContextFieldValue(binding = Perms.class)
	private ACL[] perms;
	
	public OrangeSetACLContext(
		ZNode zNode, 
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args,
		OrangeZookeeperClient client
	) {
		super(zNode, operationOwner, operationMethod, args, client);
	}

	public SetACL getSetACLAnnotation() {
		return setACLAnnotation;
	}

	public ACL[] getPerms() {
		return perms;
	}
	
}
