package com.langwuyue.orange.zookeeper.executors;

import java.lang.annotation.Annotation;
import java.util.List;

import com.langwuyue.orange.zookeeper.OrangeZookeeperOperations;
import com.langwuyue.orange.zookeeper.annotations.GetACL;
import com.langwuyue.orange.zookeeper.annotations.GetStat;
import com.langwuyue.orange.zookeeper.context.OrangeZookeeperContext;
import com.langwuyue.orange.zookeeper.utils.OrangeCollectionUtils;

public class OrangeGetACLAndStatExecutor implements OrangeZookeeperExecutor{
	
	private OrangeZookeeperOperations operations;
	
	public OrangeGetACLAndStatExecutor(OrangeZookeeperOperations operations) {
		this.operations = operations;
	}

	@Override
	public Object execute(OrangeZookeeperContext context) throws Exception {
		return this.operations.getACLAndStat(context);
	}

	@Override
	public List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetACL.class,GetStat.class);
	}

	@Override
	public Class<? extends OrangeZookeeperContext> getContextClass() {
		return OrangeZookeeperContext.class;
	}

	OrangeZookeeperOperations getOperations() {
		return operations;
	}
}
