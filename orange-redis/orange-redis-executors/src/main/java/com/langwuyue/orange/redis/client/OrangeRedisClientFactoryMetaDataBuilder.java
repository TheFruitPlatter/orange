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
package com.langwuyue.orange.redis.client;

import java.lang.annotation.Annotation;

import com.langwuyue.orange.redis.registry.OrangeRedisClientRegistry;

/**
 * Builder class for creating {@link OrangeRedisClientFactoryMetaData} instances.
 * 
 * <p>This builder is responsible for constructing metadata objects that contain information
 * about Redis client classes and their corresponding factory classes. It analyzes the
 * annotations present on a client class to determine the appropriate factory class to use.
 * 
 * <p>The builder follows these steps to determine the factory class:
 * <ol>
 *   <li>Check if metadata for the client class already exists in the registry</li>
 *   <li>If not, examine the annotations on the client class</li>
 *   <li>For each annotation, ask the provider for a matching factory class</li>
 *   <li>If no factory is found via annotations, recursively check the interfaces implemented by the client class</li>
 *   <li>Once a factory class is found, create and register the metadata</li>
 * </ol>
 * 
 * <p>This approach allows for a flexible and extensible mechanism to associate client classes
 * with their factory classes based on annotations or interface hierarchies.
 * 
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeRedisClientFactoryMetaData
 * @see OrangeClientFactoryProvider
 * @see OrangeRedisClientRegistry
 */
public class OrangeRedisClientFactoryMetaDataBuilder {
	
	/**
	 * The client class for which to build metadata.
	 */
	private Class clientClass;
	
	/**
	 * The provider used to determine factory classes based on annotations.
	 */
	private OrangeClientFactoryProvider provider;
	
	/**
	 * Constructs a new builder for creating metadata for the specified client class.
	 * 
	 * <p>This constructor initializes a builder that will create metadata for the given
	 * client class using the specified provider to determine the appropriate factory class.
	 *
	 * @param clientClass the Redis client class for which to build metadata
	 * @param provider the provider used to determine factory classes based on annotations
	 */
	public OrangeRedisClientFactoryMetaDataBuilder(Class clientClass, OrangeClientFactoryProvider provider) {
		super();
		this.clientClass = clientClass;
		this.provider = provider;
	}

	/**
	 * Builds and returns the metadata for the client class.
	 * 
	 * <p>This method first checks if metadata for the client class already exists in the registry.
	 * If it does, that metadata is returned. Otherwise, new metadata is built by analyzing
	 * the client class's annotations and interface hierarchy.
	 * 
	 * <p>The newly built metadata is registered with the {@link OrangeRedisClientRegistry}
	 * before being returned.
	 *
	 * @return the metadata for the client class, or null if no factory class could be determined
	 * @see OrangeRedisClientRegistry#getClientFactoryMetaData(Class)
	 * @see OrangeRedisClientRegistry#register(Class, OrangeRedisClientFactoryMetaData)
	 */
	public OrangeRedisClientFactoryMetaData build() {
		OrangeRedisClientFactoryMetaData metaData = OrangeRedisClientRegistry.getClientFactoryMetaData(this.clientClass);
		if(metaData == null) {
			metaData = build(this.clientClass);
			OrangeRedisClientRegistry.register(this.clientClass, metaData);
		}
		return metaData;
	}
	
	/**
	 * Recursively builds metadata for the given class by examining its annotations and interfaces.
	 * 
	 * <p>This method follows these steps to determine the factory class:
	 * <ol>
	 *   <li>Examines all annotations on the given class</li>
	 *   <li>For each annotation, asks the provider for a matching factory class</li>
	 *   <li>If a factory class is found, creates and returns new metadata</li>
	 *   <li>If no factory is found via annotations, recursively checks all interfaces</li>
	 * </ol>
	 *
	 * @param clientClass the class to analyze for factory class determination
	 * @return metadata containing the client-factory class association, or null if no factory could be determined
	 */
	private OrangeRedisClientFactoryMetaData build(Class clientClass) {
		Annotation[] annotations = clientClass.getAnnotations();
		for(Annotation annotation : annotations) {
			Class<? extends Annotation> annotationClass = annotation.annotationType();
			Class factoryClass = this.provider.getFactoryClass(annotationClass);
			if(factoryClass != null) {
				return new OrangeRedisClientFactoryMetaData(clientClass,factoryClass);
			}
		}
		Class[] interfaces = clientClass.getInterfaces();
		for(Class interfaceClass : interfaces) {
			OrangeRedisClientFactoryMetaData clientFactoryMetaData = build(interfaceClass);
			if(clientFactoryMetaData != null) {
				return clientFactoryMetaData;
			}
		}
		return null;
	}

}