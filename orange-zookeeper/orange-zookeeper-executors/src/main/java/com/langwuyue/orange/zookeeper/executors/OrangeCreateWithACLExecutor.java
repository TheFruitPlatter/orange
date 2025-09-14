package com.langwuyue.orange.zookeeper.executors;

import java.lang.annotation.Annotation;
import java.util.List;

import com.langwuyue.orange.zookeeper.OrangeZookeeperOperations;
import com.langwuyue.orange.zookeeper.annotations.Perms;
import com.langwuyue.orange.zookeeper.annotations.SetACL;
import com.langwuyue.orange.zookeeper.context.OrangeCreationWithACLContext;
import com.langwuyue.orange.zookeeper.context.OrangeZookeeperContext;

public class OrangeCreateWithACLExecutor extends OrangeCreateExecutor {
	
	public OrangeCreateWithACLExecutor(OrangeZookeeperOperations operations) {
		super(operations);
	}

	@Override
	public Object execute(OrangeZookeeperContext context) throws Exception {
		return this.getOperations().createWithACL((OrangeCreationWithACLContext)context);
	}

	@Override
	public List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		List<Class<? extends Annotation>> annotationClasses = super.getSupportedAnnotationClasses();
		annotationClasses.add(SetACL.class);
		annotationClasses.add(Perms.class);
		return annotationClasses;
	}

	@Override
	public Class<? extends OrangeZookeeperContext> getContextClass() {
		return OrangeCreationWithACLContext.class;
	}
}
