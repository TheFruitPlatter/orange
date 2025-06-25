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
package com.langwuyue.orange.redis.configuration;

import java.util.Set;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;

import com.langwuyue.orange.redis.client.OrangeRedisClientFactoryMetaData;
import com.langwuyue.orange.redis.client.OrangeRedisClientFactoryMetaDataBuilder;

/**
 * A custom Spring bean definition scanner for Redis client interfaces.
 * 
 * <p>This scanner extends {@link ClassPathBeanDefinitionScanner} to scan for interfaces
 * annotated with Redis client annotations and register appropriate bean definitions.
 * It creates factory beans that will produce Redis client proxy instances at runtime.
 *
 * <p>The scanner performs the following operations:
 * <ul>
 *   <li>Scans specified base packages for candidate components</li>
 *   <li>Filters for interface definitions that are independent</li>
 *   <li>Creates bean definitions with appropriate constructor arguments</li>
 *   <li>Configures autowiring and initialization methods</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see ClassPathBeanDefinitionScanner
 * @see OrangeRedisConfiguration
 */
class OrangeRedisClientBeanDefinitionScanner extends ClassPathBeanDefinitionScanner {
	
	private OrangeRedisConfiguration orangeRedisConfiguration;
	
	/**
	 * Creates a new scanner instance with the specified registry and Redis configuration.
	 *
	 * @param registry the bean definition registry to use
	 * @param orangeRedisConfiguration the Redis configuration to apply to scanned clients
	 * @throws IllegalArgumentException if registry or configuration is null
	 */
	public OrangeRedisClientBeanDefinitionScanner(BeanDefinitionRegistry registry, OrangeRedisConfiguration orangeRedisConfiguration) {
		super(registry);
		this.orangeRedisConfiguration = orangeRedisConfiguration;
	}

	/**
	 * Scans the specified base packages for Redis client interfaces and registers bean definitions.
	 *
	 * <p>This method performs the following operations:
	 * <ol>
	 *   <li>Delegates to the parent class to scan for candidate components</li>
	 *   <li>For each found bean definition:
	 *     <ul>
	 *       <li>Loads the interface class using the scanner's class loader</li>
	 *       <li>Builds client factory metadata using {@link OrangeRedisClientFactoryMetaDataBuilder}</li>
	 *       <li>Configures the bean definition with:
	 *         <ul>
	 *           <li>Factory class from the metadata</li>
	 *           <li>Constructor arguments including interface class, client class and Redis configuration</li>
	 *           <li>Autowiring by type</li>
	 *           <li>Init method named "init"</li>
	 *         </ul>
	 *       </li>
	 *     </ul>
	 *   </li>
	 * </ol>
	 *
	 * @param basePackages the packages to scan for Redis client interfaces
	 * @return a set of bean definition holders for the registered clients
	 * @throws RuntimeException if any interface class cannot be loaded
	 * @see ClassPathBeanDefinitionScanner#doScan(String...)
	 * @see OrangeRedisClientFactoryMetaDataBuilder
	 * @see OrangeRedisClientFactoryMetaData
	 */
	@Override
	protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
		Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);
		if (!beanDefinitions.isEmpty()) {
			AbstractBeanDefinition definition;
			for (BeanDefinitionHolder holder : beanDefinitions) {
				definition = (AbstractBeanDefinition) holder.getBeanDefinition();
				String className = definition.getBeanClassName();
				Class clazz;
				try {
					clazz = OrangeRedisClientBeanDefinitionScanner.class.getClassLoader().loadClass(className);
				} catch (ClassNotFoundException e) {
					throw new RuntimeException(e);
				}
				OrangeRedisClientFactoryMetaDataBuilder builder = new OrangeRedisClientFactoryMetaDataBuilder(clazz,this.orangeRedisConfiguration);
				OrangeRedisClientFactoryMetaData clientFactoryMetaData = builder.build();
				definition.setBeanClass(clientFactoryMetaData.getFactoryClass());
				ConstructorArgumentValues values = definition.getConstructorArgumentValues();
				values.addGenericArgumentValue(clazz);
				values.addGenericArgumentValue(clientFactoryMetaData.getClientClass());
				values.addGenericArgumentValue(this.orangeRedisConfiguration);
				definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
				definition.setInitMethodName("init");
			}
		}
		return beanDefinitions;
	}
	
	/**
	 * Determines if a bean definition is a candidate component for Redis client registration.
	 *
	 * <p>This implementation checks that the candidate:
	 * <ul>
	 *   <li>Is an interface (not a class)</li>
	 *   <li>Is independent (not nested or annotated with other component annotations)</li>
	 * </ul>
	 *
	 * @param beanDefinition the annotated bean definition to check
	 * @return true if the bean definition represents a valid Redis client interface candidate
	 * @see AnnotatedBeanDefinition#getMetadata()
	 */
	@Override
	protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
		return beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent();
	}
	
	
}