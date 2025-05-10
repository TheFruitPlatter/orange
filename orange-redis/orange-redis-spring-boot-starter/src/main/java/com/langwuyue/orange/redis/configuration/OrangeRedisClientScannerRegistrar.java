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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import com.langwuyue.orange.redis.annotation.cross.OrangeRedisGeoCrossKeyClient;
import com.langwuyue.orange.redis.annotation.cross.OrangeRedisListCrossKeyClient;
import com.langwuyue.orange.redis.annotation.cross.OrangeRedisSetCrossKeyClient;
import com.langwuyue.orange.redis.annotation.cross.OrangeRedisZSetCrossKeyClient;
import com.langwuyue.orange.redis.annotation.geo.OrangeRedisGeoClient;
import com.langwuyue.orange.redis.annotation.hash.OrangeRedisHashClient;
import com.langwuyue.orange.redis.annotation.list.OrangeRedisListClient;
import com.langwuyue.orange.redis.annotation.multiplelocks.OrangeRedisMultipleLocksClient;
import com.langwuyue.orange.redis.annotation.script.OrangeRedisScriptClient;
import com.langwuyue.orange.redis.annotation.set.OrangeRedisSetClient;
import com.langwuyue.orange.redis.annotation.transaction.OrangeRedisTransactionClient;
import com.langwuyue.orange.redis.annotation.value.OrangeRedisValueClient;
import com.langwuyue.orange.redis.annotation.zset.OrangeRedisZSetClient;
import com.langwuyue.orange.redis.context.builder.OrangeArgAndAnnotationToMapHandler;
import com.langwuyue.orange.redis.context.builder.OrangeMethodAnnotationHandler;
import com.langwuyue.orange.redis.context.builder.OrangeOperationArgAnnotationHandler;
import com.langwuyue.orange.redis.context.builder.OrangeOperationArgMultipleHandler;
import com.langwuyue.orange.redis.context.builder.OrangeOperationArgSimpleHandler;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
class OrangeRedisClientScannerRegistrar implements ImportBeanDefinitionRegistrar {
	
	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		Set<String> newBasePackages = new HashSet<>();
		AnnotationAttributes annotationAttributes = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(OrangeRedisClientScan.class.getName()));
		if(annotationAttributes != null) {
			String[] basePackages = annotationAttributes.getStringArray("basePackages");
			if(basePackages == null || basePackages.length == 0) {
				return;
			}
			Collections.addAll(newBasePackages, basePackages);
		}
		OrangeRedisConfiguration configuration = new OrangeRedisConfiguration();
		configuration.config(
			OrangeRedisValueClient.class, 
			OrangeRedisValueClientFactoryBean.class
		);
		configuration.config(
			OrangeRedisZSetClient.class, 
			OrangeRedisZSetClientFactoryBean.class
		);
		configuration.config(
			OrangeRedisSetClient.class, 
			OrangeRedisSetClientFactoryBean.class
		);
		configuration.config(
			OrangeRedisListClient.class, 
			OrangeRedisListClientFactoryBean.class
		);
		configuration.config(
			OrangeRedisHashClient.class, 
			OrangeRedisHashClientFactoryBean.class
		);
		configuration.config(
			OrangeRedisGeoClient.class, 
			OrangeRedisGeoClientFactoryBean.class
		);
		
		configuration.config(
			OrangeRedisZSetCrossKeyClient.class, 
			OrangeRedisZSetCrossKeyClientFactoryBean.class
		);
		
		configuration.config(
			OrangeRedisSetCrossKeyClient.class, 
			OrangeRedisSetCrossKeyClientFactoryBean.class
		);
		
		configuration.config(
			OrangeRedisListCrossKeyClient.class, 
			OrangeRedisListCrossKeyClientFactoryBean.class
		);
		configuration.config(
			OrangeRedisGeoCrossKeyClient.class, 
			OrangeRedisGeoCrossKeyClientFactoryBean.class
		);
		configuration.config(
			OrangeRedisTransactionClient.class, 
			OrangeRedisTransactionClientFactoryBean.class
		);
		configuration.config(
			OrangeRedisMultipleLocksClient.class, 
			OrangeRedisMultipleLocksClientFactoryBean.class
		);
		configuration.config(
			OrangeRedisScriptClient.class, 
			OrangeRedisScriptClientFactoryBean.class
		);
		
		configuration.config(new OrangeOperationArgSimpleHandler());
		configuration.config(new OrangeOperationArgMultipleHandler());
		configuration.config(new OrangeMethodAnnotationHandler());
		configuration.config(new OrangeOperationArgAnnotationHandler());
		configuration.config(new OrangeArgAndAnnotationToMapHandler());
		
	    BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(OrangeRedisClientScannerBeanDefinition.class);
	    builder.addPropertyValue("basePackages", newBasePackages.toArray(new String[newBasePackages.size()]));
	    builder.addPropertyValue("orangeRedisConfiguration", configuration);
	    builder.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
	    registry.registerBeanDefinition(importingClassMetadata.getClassName()+"#OrangeRedisClientScannerRegistrar",builder.getBeanDefinition());

	}
}
