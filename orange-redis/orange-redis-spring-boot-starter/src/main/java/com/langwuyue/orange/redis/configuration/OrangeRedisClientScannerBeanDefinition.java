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

import java.lang.annotation.Annotation;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.core.type.filter.AnnotationTypeFilter;


/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
class OrangeRedisClientScannerBeanDefinition implements BeanDefinitionRegistryPostProcessor{

	private String[] basePackages;
	
	private OrangeRedisConfiguration orangeRedisConfiguration;
	
	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		OrangeRedisClientBeanDefinitionScanner scanner = new OrangeRedisClientBeanDefinitionScanner(registry,this.orangeRedisConfiguration);
		for(Class<? extends Annotation> annotationClass : orangeRedisConfiguration.getRedisClientAnnotationClasses()) {
			scanner.addIncludeFilter(new AnnotationTypeFilter(annotationClass,true,true));
		}
		scanner.scan(basePackages);
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		
	}

	public String[] getBasePackages() {
		return basePackages;
	}

	public void setBasePackages(String[] basePackages) {
		this.basePackages = basePackages;
	}

	public OrangeRedisConfiguration getOrangeRedisConfiguration() {
		return orangeRedisConfiguration;
	}

	public void setOrangeRedisConfiguration(OrangeRedisConfiguration orangeRedisConfiguration) {
		this.orangeRedisConfiguration = orangeRedisConfiguration;
	}
}
