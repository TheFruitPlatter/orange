package com.langwuyue.orange.zookeeper.executors;

import java.lang.annotation.Annotation;
import java.util.List;

import com.langwuyue.orange.zookeeper.OrangeZookeeperOperations;
import com.langwuyue.orange.zookeeper.annotations.Version;
import com.langwuyue.orange.zookeeper.context.OrangeDeleteWithVersionContext;
import com.langwuyue.orange.zookeeper.context.OrangeZookeeperContext;

public class OrangeDeleteWithVersionExecutor extends OrangeDeleteExecutor {
	
	public OrangeDeleteWithVersionExecutor(OrangeZookeeperOperations operations) {
		super(operations);
	}

	@Override
	public Object execute(OrangeZookeeperContext context) throws Exception {
		this.getOperations().delete((OrangeDeleteWithVersionContext)context);
		return null;
	}

	@Override
	public List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		List<Class<? extends Annotation>> annotationClasses = super.getSupportedAnnotationClasses();
		annotationClasses.add(Version.class);
		return annotationClasses;
	}

	@Override
	public Class<? extends OrangeZookeeperContext> getContextClass() {
		return OrangeDeleteWithVersionContext.class;
	}
}
