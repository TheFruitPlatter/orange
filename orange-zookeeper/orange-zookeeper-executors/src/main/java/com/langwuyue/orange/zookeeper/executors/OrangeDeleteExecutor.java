package com.langwuyue.orange.zookeeper.executors;

import java.lang.annotation.Annotation;
import java.util.List;

import com.langwuyue.orange.zookeeper.OrangeZookeeperOperations;
import com.langwuyue.orange.zookeeper.annotations.Delete;
import com.langwuyue.orange.zookeeper.context.OrangeDeleteContext;
import com.langwuyue.orange.zookeeper.context.OrangeZookeeperContext;
import com.langwuyue.orange.zookeeper.utils.OrangeCollectionUtils;

public class OrangeDeleteExecutor implements OrangeZookeeperExecutor {
	
	private OrangeZookeeperOperations operations;
	
	public OrangeDeleteExecutor(OrangeZookeeperOperations operations) {
		this.operations = operations;
	}

	@Override
	public Object execute(OrangeZookeeperContext context) throws Exception {
		this.operations.delete((OrangeDeleteContext)context);
		return null;
	}

	@Override
	public List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(Delete.class);
	}

	@Override
	public Class<? extends OrangeZookeeperContext> getContextClass() {
		return OrangeDeleteContext.class;
	}

	OrangeZookeeperOperations getOperations() {
		return operations;
	}
}
