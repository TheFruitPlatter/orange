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
 * @author Liang.Zhong
 * @since 1.0.0
 */
class OrangeRedisClientBeanDefinitionScanner extends ClassPathBeanDefinitionScanner {
	
	private OrangeRedisConfiguration orangeRedisConfiguration;
	
	public OrangeRedisClientBeanDefinitionScanner(BeanDefinitionRegistry registry,OrangeRedisConfiguration orangeRedisConfiguration) {
		super(registry);
		this.orangeRedisConfiguration = orangeRedisConfiguration;
	}

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
	
	@Override
	protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
		return beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent();
	}
	
	
}
