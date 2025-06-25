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
 * A bean definition registry post processor that scans for Redis client interfaces
 * and registers them as Spring beans.
 * 
 * <p>This class is responsible for scanning specified base packages for interfaces
 * annotated with Redis client annotations (configured in {@link OrangeRedisConfiguration})
 * and registering them with the Spring container. It works in conjunction with
 * {@link OrangeRedisClientBeanDefinitionScanner} to perform the actual scanning
 * and registration process.
 * 
 * <p>The scanning process occurs during the Spring container initialization phase,
 * specifically during the bean definition registration phase. This ensures that all
 * Redis client interfaces are properly registered before any beans are instantiated.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see BeanDefinitionRegistryPostProcessor
 * @see OrangeRedisClientBeanDefinitionScanner
 * @see OrangeRedisConfiguration
 */
class OrangeRedisClientScannerBeanDefinition implements BeanDefinitionRegistryPostProcessor{

	/**
	 * The base packages to scan for Redis client interfaces.
	 * 
	 * <p>These packages and their sub-packages will be scanned for interfaces
	 * annotated with Redis client annotations as configured in {@link OrangeRedisConfiguration}.
	 */
	private String[] basePackages;
	
	/**
	 * The Redis configuration that contains settings for Redis client scanning.
	 * 
	 * <p>This configuration provides the annotation classes to look for when scanning
	 * for Redis client interfaces, as well as other Redis-related configuration options.
	 */
	private OrangeRedisConfiguration orangeRedisConfiguration;
	
	/**
	 * Processes the bean definition registry after it has been standard initialized.
	 * This method scans the configured base packages for Redis client interfaces and
	 * registers them with the Spring container.
	 * 
	 * <p>The method creates a {@link OrangeRedisClientBeanDefinitionScanner} and configures it
	 * with the annotation filters from {@link OrangeRedisConfiguration}. It then scans the
	 * specified base packages for interfaces matching these annotations.
	 *
	 * @param registry the bean definition registry to post-process
	 * @throws BeansException if an error occurs during processing
	 */
	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		OrangeRedisClientBeanDefinitionScanner scanner = new OrangeRedisClientBeanDefinitionScanner(registry,this.orangeRedisConfiguration);
		for(Class<? extends Annotation> annotationClass : orangeRedisConfiguration.getRedisClientAnnotationClasses()) {
			scanner.addIncludeFilter(new AnnotationTypeFilter(annotationClass,true,true));
		}
		scanner.scan(basePackages);
	}

	/**
	 * Post-processes the bean factory after its standard initialization.
	 * 
	 * <p>This implementation is empty as all the necessary processing is done in
	 * {@link #postProcessBeanDefinitionRegistry}. This method is required by the
	 * {@link BeanDefinitionRegistryPostProcessor} interface but not used in this
	 * implementation.
	 *
	 * @param beanFactory the bean factory to post-process
	 * @throws BeansException if an error occurs during processing
	 */
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		// No additional processing needed here
	}

	/**
	 * Gets the base packages to scan for Redis client interfaces.
	 *
	 * @return an array of package names to scan
	 */
	public String[] getBasePackages() {
		return basePackages;
	}

	/**
	 * Sets the base packages to scan for Redis client interfaces.
	 * 
	 * <p>These packages and their sub-packages will be scanned for interfaces
	 * that are annotated with Redis client annotations.
	 *
	 * @param basePackages an array of package names to scan
	 */
	public void setBasePackages(String[] basePackages) {
		this.basePackages = basePackages;
	}

	/**
	 * Gets the Redis configuration containing scanning settings and other Redis-related options.
	 *
	 * @return the Redis configuration object
	 */
	public OrangeRedisConfiguration getOrangeRedisConfiguration() {
		return orangeRedisConfiguration;
	}

	/**
	 * Sets the Redis configuration containing scanning settings and other Redis-related options.
	 * 
	 * <p>This configuration provides the annotation classes to look for when scanning
	 * for Redis client interfaces and other Redis-specific configuration settings.
	 *
	 * @param orangeRedisConfiguration the Redis configuration object to use
	 */
	public void setOrangeRedisConfiguration(OrangeRedisConfiguration orangeRedisConfiguration) {
		this.orangeRedisConfiguration = orangeRedisConfiguration;
	}
}