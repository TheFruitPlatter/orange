/*
 * Copyright (c) 2025 Liang.Zhong. All rights reserved.
 *
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.langwuyue.orange.zookeeper.configuration;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.langwuyue.orange.zookeeper.annotations.OrangeZookeeperClient;
import com.langwuyue.orange.zookeeper.annotations.ZookeeperZNode;
import com.langwuyue.orange.zookeeper.context.OrangeZookeeperContext;
import com.langwuyue.orange.zookeeper.context.OrangeZookeeperContext.ZNode;
import com.langwuyue.orange.zookeeper.context.OrangeZookeeperContextBuilder;
import com.langwuyue.orange.zookeeper.executors.OrangeZookeeperExecutor;
import com.langwuyue.orange.zookeeper.mapping.OrangeZookeeperExecutorMapping;
import com.langwuyue.orange.zookeeper.util.OrangeStringTemlateUtils;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeZookeeperClientInvocationHandler implements InvocationHandler {
	
	private Class<?> operationOwner;
	
	private OrangeZookeeperExecutorMapping mapping;
	
	private ZookeeperZNode zNodeAnnotation;
	
	private OrangeZookeeperClient client;
	
	public OrangeZookeeperClientInvocationHandler(
		Class<?> operationOwner,
		OrangeZookeeperExecutorMapping mapping,
		ZookeeperZNode zNodeAnnotation,
		OrangeZookeeperClient client
	) {
		this.operationOwner = operationOwner;
		this.mapping = mapping;
		this.zNodeAnnotation = zNodeAnnotation;
		this.client = client;
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if(Object.class.equals(method.getDeclaringClass())) {
			return method.invoke(this, args);
		}else{
			try {
				OrangeZookeeperExecutor executor = this.mapping.getExecutor(method);
				Class<? extends OrangeZookeeperContext> contextClass = executor.getContextClass();
				Method actualMethod = this.mapping.getActualMethod(method);
				OrangeZookeeperContext context = new OrangeZookeeperContextBuilder()
														.contextClass(contextClass)
														.zNode(getZNode(method,args))
														.operationMethod(method)
														.operationOwner(this.operationOwner)
														.actualMethod(actualMethod)
														.args(args)
														.client(this.client)
														.build();
				return executor.execute(context);
			} catch(Exception e) {
				throw e;
			}
		}
	}
	
	protected ZNode getZNode(Method method,Object[] args) {
		String originPath = this.zNodeAnnotation.path();
		String path = OrangeStringTemlateUtils.getString(originPath, method, args);
		return new OrangeZookeeperContext.ZNode(
			originPath,
			path, 
			this.zNodeAnnotation.ttl(), 
			this.zNodeAnnotation.ttlUnit(),
			this.zNodeAnnotation.zNodeType()
		);
	}
}
