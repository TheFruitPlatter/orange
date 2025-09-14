package com.langwuyue.orange.zookeeper.executors;

import java.lang.annotation.Annotation;
import java.util.List;

import com.langwuyue.orange.zookeeper.OrangeZookeeperOperations;
import com.langwuyue.orange.zookeeper.annotations.GetChildren;
import com.langwuyue.orange.zookeeper.annotations.GetStat;
import com.langwuyue.orange.zookeeper.context.OrangeZookeeperContext;
import com.langwuyue.orange.zookeeper.utils.OrangeCollectionUtils;

public class OrangeGetChildrenAndStatExecutor implements OrangeZookeeperExecutor{
	
	private OrangeZookeeperOperations operations;
	
	public OrangeGetChildrenAndStatExecutor(OrangeZookeeperOperations operations) {
		this.operations = operations;
	}

	@Override
	public Object execute(OrangeZookeeperContext context) throws Exception {
		return this.operations.getChildrenAndStat(context);
	}

	@Override
	public List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetChildren.class,GetStat.class);
	}

	@Override
	public Class<? extends OrangeZookeeperContext> getContextClass() {
		return OrangeZookeeperContext.class;
	}

	OrangeZookeeperOperations getOperations() {
		return operations;
	}
}
