package com.langwuyue.orange.zookeeper.executors;

import java.lang.annotation.Annotation;
import java.util.List;

import com.langwuyue.orange.zookeeper.OrangeZookeeperOperations;
import com.langwuyue.orange.zookeeper.annotations.Version;
import com.langwuyue.orange.zookeeper.context.OrangeSetDataWitVersionContext;
import com.langwuyue.orange.zookeeper.context.OrangeZookeeperContext;

public class OrangeSetDataWithVersionExecutor extends OrangeSetDataExecutor{
	
	public OrangeSetDataWithVersionExecutor(OrangeZookeeperOperations operations) {
		super(operations);
	}

	@Override
	public Object execute(OrangeZookeeperContext context) throws Exception {
		return this.getOperations().setData((OrangeSetDataWitVersionContext)context);
	}

	@Override
	public List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		List<Class<? extends Annotation>> annotationClasses = super.getSupportedAnnotationClasses();
		annotationClasses.add(Version.class);
		return annotationClasses;
	}

	@Override
	public Class<? extends OrangeZookeeperContext> getContextClass() {
		return OrangeSetDataWitVersionContext.class;
	}

}
