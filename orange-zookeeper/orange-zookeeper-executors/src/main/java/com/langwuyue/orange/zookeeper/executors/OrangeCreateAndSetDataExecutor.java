package com.langwuyue.orange.zookeeper.executors;

import java.lang.annotation.Annotation;
import java.util.List;

import com.langwuyue.orange.zookeeper.OrangeZookeeperOperations;
import com.langwuyue.orange.zookeeper.annotations.Data;
import com.langwuyue.orange.zookeeper.annotations.SetData;
import com.langwuyue.orange.zookeeper.context.OrangeCreationAndSetDataContext;
import com.langwuyue.orange.zookeeper.context.OrangeZookeeperContext;

public class OrangeCreateAndSetDataExecutor extends OrangeCreateExecutor {
	
	public OrangeCreateAndSetDataExecutor(OrangeZookeeperOperations operations) {
		super(operations);
	}

	@Override
	public Object execute(OrangeZookeeperContext context) throws Exception {
		return this.getOperations().createAndSetData((OrangeCreationAndSetDataContext)context);
	}

	@Override
	public List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		List<Class<? extends Annotation>> annotationClasses = super.getSupportedAnnotationClasses();
		annotationClasses.add(SetData.class);
		annotationClasses.add(Data.class);
		return annotationClasses;
	}

	@Override
	public Class<? extends OrangeZookeeperContext> getContextClass() {
		return OrangeCreationAndSetDataContext.class;
	}

}
