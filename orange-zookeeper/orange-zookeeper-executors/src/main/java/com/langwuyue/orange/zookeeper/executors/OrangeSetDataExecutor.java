package com.langwuyue.orange.zookeeper.executors;

import java.lang.annotation.Annotation;
import java.util.List;

import com.langwuyue.orange.zookeeper.OrangeZookeeperOperations;
import com.langwuyue.orange.zookeeper.annotations.Data;
import com.langwuyue.orange.zookeeper.annotations.SetData;
import com.langwuyue.orange.zookeeper.context.OrangeSetDataContext;
import com.langwuyue.orange.zookeeper.context.OrangeZookeeperContext;
import com.langwuyue.orange.zookeeper.utils.OrangeCollectionUtils;

public class OrangeSetDataExecutor implements OrangeZookeeperExecutor{
	
	private OrangeZookeeperOperations operations;
	
	public OrangeSetDataExecutor(OrangeZookeeperOperations operations) {
		this.operations = operations;
	}

	@Override
	public Object execute(OrangeZookeeperContext context) throws Exception {
		return this.operations.setData((OrangeSetDataContext)context);
	}

	@Override
	public List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(SetData.class,Data.class);
	}

	@Override
	public Class<? extends OrangeZookeeperContext> getContextClass() {
		return OrangeSetDataContext.class;
	}

	OrangeZookeeperOperations getOperations() {
		return operations;
	}
}
