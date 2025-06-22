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
import java.util.List;

import com.langwuyue.orange.redis.annotation.Count;
import com.langwuyue.orange.redis.annotation.GetMembers;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.cross.CrossOperationKeys;
import com.langwuyue.orange.redis.annotation.cross.StoreTo;
import com.langwuyue.orange.redis.annotation.geo.Distance;
import com.langwuyue.orange.redis.annotation.geo.Height;
import com.langwuyue.orange.redis.annotation.geo.Latitude;
import com.langwuyue.orange.redis.annotation.geo.Longitude;
import com.langwuyue.orange.redis.annotation.geo.SearchArgs;
import com.langwuyue.orange.redis.annotation.geo.Width;

/**
 * Executor ID generator for Redis Geo operations that work across multiple keys.
 * 
 * <p>This class is responsible for generating executor IDs for Redis Geo operations
 * that involve cross-key functionality, such as storing geo search results to another key
 * or performing operations across multiple geo indexes.
 * 
 * <p>It registers all the annotation classes that are relevant for geo cross-key operations,
 * including annotations for specifying geo coordinates, search parameters, and cross-operation
 * key definitions.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisGeoCrossKeyExecutorIdGenerator extends OrangeRedisExecutorIdAbstractGenerator {

	/**
	 * Registers all annotation classes supported by this executor ID generator.
	 * 
	 * @param supportedAnnotationClasses the list to which supported annotation classes will be added
	 */
	@Override
	protected void registerSupportedAnnotationClasses(List<Class<? extends Annotation>> supportedAnnotationClasses) {
		supportedAnnotationClasses.add(GetMembers.class);
		supportedAnnotationClasses.add(SearchArgs.class);
		supportedAnnotationClasses.add(StoreTo.class);
		supportedAnnotationClasses.add(CrossOperationKeys.class);
		supportedAnnotationClasses.add(RedisValue.class);
		supportedAnnotationClasses.add(Width.class);
		supportedAnnotationClasses.add(Height.class);
		supportedAnnotationClasses.add(Longitude.class);
		supportedAnnotationClasses.add(Latitude.class);
		supportedAnnotationClasses.add(Distance.class);
		supportedAnnotationClasses.add(Count.class);

	}
}