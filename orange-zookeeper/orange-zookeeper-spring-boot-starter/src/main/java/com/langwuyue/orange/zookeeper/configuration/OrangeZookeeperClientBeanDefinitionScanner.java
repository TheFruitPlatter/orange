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

import java.util.Set;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.util.ClassUtils;


/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
class OrangeZookeeperClientBeanDefinitionScanner extends ClassPathBeanDefinitionScanner {
	
	public OrangeZookeeperClientBeanDefinitionScanner(BeanDefinitionRegistry registry) {
		super(registry);
	}

	@Override
	protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
		Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);
		if (beanDefinitions.isEmpty()) {
			return beanDefinitions;
		}
		AbstractBeanDefinition definition;
		for (BeanDefinitionHolder holder : beanDefinitions) {
			definition = (AbstractBeanDefinition) holder.getBeanDefinition();
			Class clazz;
			try {
				clazz = definition.resolveBeanClass(ClassUtils.getDefaultClassLoader());
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
			definition.setBeanClass(OrangeZookeeperClientFactoryBean.class);
			ConstructorArgumentValues values = definition.getConstructorArgumentValues();
			values.addGenericArgumentValue(clazz);
			definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
			definition.setInitMethodName("init");
		}
		return beanDefinitions;
	}
	
	@Override
	protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
		return beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent();
	}
}
