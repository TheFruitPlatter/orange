package com.langwuyue.orange.zookeeper.mapping;

import java.lang.reflect.Method;

import com.langwuyue.orange.zookeeper.executors.OrangeZookeeperExecutor;

public interface OrangeZookeeperExecutorMapping {
	
	OrangeZookeeperExecutor getExecutor(Method method);
	
	void initExecutorRegistry();

	Method getActualMethod(Method method);

}
