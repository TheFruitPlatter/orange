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
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisClientFactoryMetaDataBuilder {
	
	private Class clientClass;
	
	private OrangeClientFactoryProvider provider;
	
	public OrangeRedisClientFactoryMetaDataBuilder(Class clientClass, OrangeClientFactoryProvider provider) {
		super();
		this.clientClass = clientClass;
		this.provider = provider;
	}

	public OrangeRedisClientFactoryMetaData build() {
		OrangeRedisClientFactoryMetaData metaData = OrangeRedisClientRegistry.getClientFactoryMetaData(this.clientClass);
		if(metaData == null) {
			metaData = build(this.clientClass);
			OrangeRedisClientRegistry.register(this.clientClass, metaData);
		}
		return metaData;
	}
	
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
