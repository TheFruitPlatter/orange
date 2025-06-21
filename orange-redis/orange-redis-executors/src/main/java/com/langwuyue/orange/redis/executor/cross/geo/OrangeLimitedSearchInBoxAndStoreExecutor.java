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
package com.langwuyue.orange.redis.executor.cross.geo;

import java.lang.annotation.Annotation;
import java.util.List;

import com.langwuyue.orange.redis.annotation.Count;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisGeoOperations;

/**
 * Executor for limited GEO search within a rectangular bounding box and store results.
 * 
 * <p>Extends {@link OrangeSearchInBoxAndStoreExecutor} to add support for limiting
 * the number of results returned from the search operation. This executor handles:
 * <ul>
 *   <li>Searching for members within a specified rectangular area</li>
 *   <li>Storing the results in a destination key</li>
 *   <li>Limiting the number of results via {@link Count} annotation</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeLimitedSearchInBoxAndStoreExecutor extends OrangeSearchInBoxAndStoreExecutor {
	

	/**
	 * Creates a new OrangeLimitedSearchInBoxAndStoreExecutor instance.
	 * 
	 * @param operations the Redis GEO operations implementation
	 * @param idGenerator the executor ID generator
	 */
	public OrangeLimitedSearchInBoxAndStoreExecutor(OrangeRedisGeoOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(operations,idGenerator);
	}

	/**
	 * Gets the list of supported annotation classes for this executor.
	 * <p>Extends the parent class's supported annotations by adding {@link Count}
	 * annotation to support limiting the number of search results.
	 * 
	 * @return list of annotation classes including:
	 * <ul>
	 *   <li>{@link Count} - for limiting result count</li>
	 *   <li>All annotations from parent class</li>
	 * </ul>
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		List<Class<? extends Annotation>> supportedAnnotationClasses = super.getSupportedAnnotationClasses();
		supportedAnnotationClasses.add(Count.class);
		return supportedAnnotationClasses;
	}
}