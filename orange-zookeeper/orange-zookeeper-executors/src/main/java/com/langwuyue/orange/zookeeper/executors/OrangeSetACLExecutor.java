package com.langwuyue.orange.zookeeper.executors;

import java.lang.annotation.Annotation;
import java.util.List;

import com.langwuyue.orange.zookeeper.OrangeZookeeperOperations;
import com.langwuyue.orange.zookeeper.annotations.Perms;
import com.langwuyue.orange.zookeeper.annotations.SetACL;
import com.langwuyue.orange.zookeeper.context.OrangeSetACLContext;
import com.langwuyue.orange.zookeeper.context.OrangeZookeeperContext;
import com.langwuyue.orange.zookeeper.utils.OrangeCollectionUtils;

public class OrangeSetACLExecutor implements OrangeZookeeperExecutor{
	
	private OrangeZookeeperOperations operations;
	
	public OrangeSetACLExecutor(OrangeZookeeperOperations operations) {
		this.operations = operations;
	}

	@Override
	public Object execute(OrangeZookeeperContext context) throws Exception {
		return this.operations.setACL((OrangeSetACLContext)context);
	}

	@Override
	public List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(SetACL.class,Perms.class);
	}

	@Override
	public Class<? extends OrangeZookeeperContext> getContextClass() {
		return OrangeSetACLContext.class;
	}

	OrangeZookeeperOperations getOperations() {
		return operations;
	}
}
