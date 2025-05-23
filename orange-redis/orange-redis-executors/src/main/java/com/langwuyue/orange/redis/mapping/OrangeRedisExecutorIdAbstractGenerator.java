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
package com.langwuyue.orange.redis.mapping;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.annotation.TimeoutUnit;
import com.langwuyue.orange.redis.annotation.global.Delete;
import com.langwuyue.orange.redis.annotation.global.GetExpiration;
import com.langwuyue.orange.redis.annotation.global.SetExpiration;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public abstract class OrangeRedisExecutorIdAbstractGenerator implements OrangeRedisExecutorIdGenerator {
	
	private List<Class<? extends Annotation>> supportedAnnotationClasses;
	
	private Map<Class<? extends Annotation>,Integer> supportedAnnotationClassesIdMap;
	
	protected OrangeRedisExecutorIdAbstractGenerator() {
		this.supportedAnnotationClasses = new ArrayList<>(64);
		this.supportedAnnotationClassesIdMap = new LinkedHashMap<>(64);
		registerSupportedAnnotationClasses(supportedAnnotationClasses);
		buildSupportedAnnotationClassesMap();
	}
	
	@Override
	public long generate(List<Class<? extends Annotation>> supportedClasses) {
		long id = 0;
		for(Class<? extends Annotation> annotationClass : supportedClasses) {
			long index = getSupportedAnnotationIndex(annotationClass);
			id = id ^ (1 << index);
		}
		return id;
	}
	
	protected void registerSupportedAnnotationClasses(List<Class<? extends Annotation>> supportedAnnotationClasses) {
		supportedAnnotationClasses.add(SetExpiration.class);
		supportedAnnotationClasses.add(GetExpiration.class);
		supportedAnnotationClasses.add(Delete.class);
		supportedAnnotationClasses.add(TimeoutUnit.class);
	}

	private int getSupportedAnnotationIndex(Class<? extends Annotation> annotationClass) {
		Integer index = supportedAnnotationClassesIdMap.get(annotationClass);
		if(index == null) {
			throw new OrangeRedisException(String.format("The annotation @%s is not supported", annotationClass));
		}
		return index;
	}
	
	private void buildSupportedAnnotationClassesMap() {
		int size = supportedAnnotationClasses.size();
		for(int i = 0; i < size; i++) {
			Class<? extends Annotation> annotationClass = supportedAnnotationClasses.get(i);
			Integer id = supportedAnnotationClassesIdMap.get(annotationClass);
			if(id != null) {
				throw new OrangeRedisException(String.format("Duplicate annotation class detected. Duplicate indices: %s, %s", id,i));
			}
			supportedAnnotationClassesIdMap.put(annotationClass, i);
		}
	}
	
	@Override
	public boolean contains(long operationId, Class<? extends Annotation> annotationClass) {
		Integer id = supportedAnnotationClassesIdMap.get(annotationClass);
		return id != null && ((operationId & id) == id);
	}
}
