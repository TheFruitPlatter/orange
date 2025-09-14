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

import java.lang.reflect.Proxy;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.apache.curator.ensemble.fixed.FixedEnsembleProvider;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.imps.GzipCompressionProvider;
import org.apache.curator.retry.BoundedExponentialBackoffRetry;
import org.apache.curator.utils.DefaultZookeeperFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.langwuyue.orange.zookeeper.OrangeStringSerializer;
import com.langwuyue.orange.zookeeper.OrangeZookeeperBackgroundCallback;
import com.langwuyue.orange.zookeeper.OrangeZookeeperDataSerializer;
import com.langwuyue.orange.zookeeper.OrangeZookeeperException;
import com.langwuyue.orange.zookeeper.ZNodeTypeEnum;
import com.langwuyue.orange.zookeeper.annotations.OrangeZookeeperClient;
import com.langwuyue.orange.zookeeper.annotations.ZookeeperZNode;
import com.langwuyue.orange.zookeeper.enums.OrangeDefaultZookeeperOperations;
import com.langwuyue.orange.zookeeper.mapping.OrangeDefaultZookeeperExecutorMapping;
import com.langwuyue.orange.zookeeper.mapping.OrangeZookeeperExecutorMapping;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeZookeeperClientFactoryBean implements FactoryBean<Object>, ApplicationContextAware {
	
	private Class<?> operationOwner;
	
	private Class<?> operationOriginOwner;
	
	private ApplicationContext applicationContext;
	
	private OrangeZookeeperClient client;
	
	private static CuratorFramework curatorFramework;
	
	private OrangeZookeeperExecutorMapping mapping;
	
	private ZookeeperZNode znode;
	
	private OrangeZookeeperProperties properties;
	
	private static OrangeZookeeperDataSerializer serializer;
	
	protected OrangeZookeeperClientFactoryBean(Class<?> operationOwner) {
		this.operationOwner = operationOwner;
	}
	
	public void init() {
		// Register listener for handling callback
		OrangeZookeeperClientFactoryBean.curatorFramework.getCuratorListenable().addListener(
			new OrangeDefaultCuratorListener(
				OrangeZookeeperClientFactoryBean.serializer,
				getCallbacks()
			)
		);
		// Get ZookeeperZNode annotation
		this.znode = this.operationOwner.getAnnotation(ZookeeperZNode.class);
		if(znode == null) {
			throw new OrangeZookeeperException(String.format(
				"Illegal client: Annotation %s not found on this type. Current operation owner type is %s", 
				ZookeeperZNode.class, 
				this.operationOwner
			));
		}
		if((znode.zNodeType() == ZNodeTypeEnum.PERSISTENT_WITH_TTL || znode.zNodeType() == ZNodeTypeEnum.PERSISTENT_SEQUENTIAL_WITH_TTL)
				&& znode.ttl() <= 0) {
			throw new OrangeZookeeperException(String.format(
				"Illegal client: The ttl of annotation %s must greate than zero when znodeType is %s%n. Operation owner type is %s", 
				ZookeeperZNode.class, 
				znode.zNodeType().name(),
				this.operationOwner
			));
		}
		
		// Get the origin operation owner, which may differ from the operation owner.
		// The operation owner may inherit from an operation template, 
		// in which case the template is considered the origin operation owner.
		this.operationOriginOwner = getOperationOriginOwner(this.operationOwner);
		if(this.operationOriginOwner == null) {
			// This won't happen, just in case
			throw new OrangeZookeeperException(String.format(
				"Illegal client: Annotation %s not found on this type. Current operation owner type is %s", 
				OrangeZookeeperClient.class, 
				this.operationOwner
			));
		}
		
		// The CuratorFramework should be started only once.  
		if(OrangeZookeeperClientFactoryBean.curatorFramework != null 
				&& OrangeZookeeperClientFactoryBean.curatorFramework.getState() != CuratorFrameworkState.STARTED) {
			OrangeZookeeperClientFactoryBean.curatorFramework.start();
		}
		
		// Build executor mapping
		this.mapping = new OrangeDefaultZookeeperExecutorMapping(
				operationOwner,
				new OrangeDefaultZookeeperOperations(
					OrangeZookeeperClientFactoryBean.curatorFramework,
					OrangeZookeeperClientFactoryBean.serializer
				)
		);
		this.mapping.initExecutorRegistry();
	}
	
	private List<OrangeZookeeperBackgroundCallback> getCallbacks(){
		Map<String, OrangeZookeeperBackgroundCallback> beanMap = this.applicationContext.getBeansOfType(OrangeZookeeperBackgroundCallback.class);
		if(beanMap == null || beanMap.isEmpty()) {
			return new ArrayList<>();
		}
		List<OrangeZookeeperBackgroundCallback> listeners = beanMap.values().stream().collect(Collectors.toList());
		AnnotationAwareOrderComparator.sort(listeners);
		return listeners;
	}
	
	private Class<?> getOperationOriginOwner(Class<?> operationOwner) {
		OrangeZookeeperClient client = operationOwner.getAnnotation(OrangeZookeeperClient.class);
		if(client != null) {
			this.client = client;
			return operationOwner;
		}
		for(Class superInterface:operationOwner.getInterfaces()) {
			Class<?> operationOriginOwner = getOperationOriginOwner(superInterface);
			if(operationOriginOwner != null) {
				return operationOriginOwner;
			}
		}
		return null;
		
	}

	@Override
	public Object getObject() throws Exception {
		return Proxy.newProxyInstance(
			this.operationOwner.getClassLoader(), 
			new Class[] {this.operationOwner}, 
			new OrangeZookeeperClientInvocationHandler(this.operationOwner,this.mapping,this.znode,this.client)
		);
	}

	@Override
	public Class<?> getObjectType() {
		return this.operationOwner;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
		if(OrangeZookeeperClientFactoryBean.curatorFramework == null 
				|| OrangeZookeeperClientFactoryBean.curatorFramework.getState() != CuratorFrameworkState.LATENT 
				|| OrangeZookeeperClientFactoryBean.curatorFramework.getState() != CuratorFrameworkState.STOPPED) {
			this.properties = this.applicationContext.getBean(OrangeZookeeperProperties.class);
			OrangeZookeeperClientFactoryBean.serializer = new OrangeZookeeperDataSerializer(
				getDefaultObjectMapper(),
				new OrangeStringSerializer(this.properties.getCharset())
			);
			OrangeZookeeperClientFactoryBean.curatorFramework = newCuratorFramework(this.properties);
		}
	}

	Class<?> getOperationOwner() {
		return this.operationOwner;
	}
	
	private CuratorFramework newCuratorFramework(OrangeZookeeperProperties properties) {
		CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder();
		builder.ensembleProvider(new FixedEnsembleProvider(properties.getConnectionString()));
		builder.retryPolicy(new BoundedExponentialBackoffRetry(1000,1000,3));
		builder.compressionProvider(new GzipCompressionProvider());
		builder.zookeeperFactory(new DefaultZookeeperFactory());
		builder.namespace(properties.getNamespace());
		builder.sessionTimeoutMs((int)properties.getSessionTimeout().toMillis());
		builder.connectionTimeoutMs((int)properties.getConnectionTimeout().toMillis());
		builder.maxCloseWaitMs((int)properties.getCloseTimeout().toMillis());
		builder.defaultData(properties.getDefaultData().getBytes(properties.getCharset()));
		builder.canBeReadOnly(properties.isCanBeReadOnly());
		if (!properties.isUseContainerParentsIfAvailable()) {
			builder.dontUseContainerParents();
		}else {
			builder.useContainerParentsIfAvailable();
		}
		return builder.build();
	}
	
	private ObjectMapper getDefaultObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        objectMapper.setDateFormat(dateFormat);
        objectMapper.setTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()));
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.disable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS,true);
        return objectMapper;
	}
}
