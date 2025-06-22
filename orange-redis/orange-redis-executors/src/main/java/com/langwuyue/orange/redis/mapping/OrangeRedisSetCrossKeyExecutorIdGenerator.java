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

import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.cross.CrossOperationKeys;
import com.langwuyue.orange.redis.annotation.cross.Difference;
import com.langwuyue.orange.redis.annotation.cross.Intersect;
import com.langwuyue.orange.redis.annotation.cross.Move;
import com.langwuyue.orange.redis.annotation.cross.StoreTo;
import com.langwuyue.orange.redis.annotation.cross.Union;

/**
 * A specialized executor ID generator for Redis Set cross-key operations.
 * 
 * <p>This class extends {@link OrangeRedisExecutorIdAbstractGenerator} to provide
 * specific support for generating executor IDs for Redis Set operations that work
 * across multiple keys. It registers annotations related to set operations like
 * union, intersection, difference, and other cross-key operations.
 * 
 * <p>The generator supports the following cross-key set operations:
 * <ul>
 *   <li>Difference - Set difference operations between multiple sets</li>
 *   <li>Union - Set union operations combining multiple sets</li>
 *   <li>Intersect - Set intersection operations between multiple sets</li>
 *   <li>Move - Moving elements between sets</li>
 *   <li>StoreTo - Storing operation results to a destination key</li>
 * </ul>
 * 
 * <p>This generator works in conjunction with the corresponding executors to
 * properly route cross-key set operations to the appropriate implementation.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisSetCrossKeyExecutorIdGenerator extends OrangeRedisExecutorIdAbstractGenerator {

	/**
	 * Registers all annotation classes supported by this executor ID generator.
	 * 
	 * <p>When methods are annotated with these annotations, this generator will
	 * be able to create appropriate executor IDs for routing the operations to
	 * the correct executor implementation.
	 *
	 * @param supportedAnnotationClasses the list to which supported annotation classes will be added
	 */
	@Override
	protected void registerSupportedAnnotationClasses(List<Class<? extends Annotation>> supportedAnnotationClasses) {
		supportedAnnotationClasses.add(Difference.class);
		supportedAnnotationClasses.add(CrossOperationKeys.class);
		supportedAnnotationClasses.add(Union.class);
		supportedAnnotationClasses.add(StoreTo.class);
		supportedAnnotationClasses.add(Intersect.class);
		supportedAnnotationClasses.add(Move.class);
		supportedAnnotationClasses.add(RedisValue.class);
	}
}