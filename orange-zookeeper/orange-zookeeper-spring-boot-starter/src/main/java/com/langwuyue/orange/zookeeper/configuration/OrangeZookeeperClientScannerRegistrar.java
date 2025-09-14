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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
class OrangeZookeeperClientScannerRegistrar implements ImportBeanDefinitionRegistrar {
	
	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		Set<String> newBasePackages = new HashSet<>();
		AnnotationAttributes annotationAttributes = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(OrangeZookeeperClientScan.class.getName()));
		if(annotationAttributes != null) {
			String[] basePackages = annotationAttributes.getStringArray("basePackages");
			if(basePackages == null || basePackages.length == 0) {
				return;
			}
			Collections.addAll(newBasePackages, basePackages);
		}
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(OrangeZookeeperClientScannerBeanDefinition.class);
	    builder.addPropertyValue("basePackages", newBasePackages.toArray(new String[newBasePackages.size()]));
	    builder.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
	    registry.registerBeanDefinition(importingClassMetadata.getClassName()+"#OrangeZookeeperClientScannerRegistrar",builder.getBeanDefinition());
	}
}
