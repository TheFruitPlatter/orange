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
 * Executor for limited Redis GEO search within radius with member existence check and store results.
 * <p>
 * Extends the base radius search functionality with support for result count limitation.
 * Performs a search for geo points within a specified radius, verifies member existence,
 * stores the results to a target key, and supports limiting the number of results.
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeLimitedSearchInRadiusAndStoreByExistsMemberExecutor extends OrangeSearchInRadiusAndStoreByExistsMemberExecutor {
	

	/**
	 * Constructs a new executor for limited geo search in radius with member existence check.
	 *
	 * @param operations Redis geo operations implementation
	 * @param idGenerator Executor ID generator
	 */
	public OrangeLimitedSearchInRadiusAndStoreByExistsMemberExecutor(OrangeRedisGeoOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(operations,idGenerator);
	}

	/**
	 * Get the list of annotation classes supported by this executor.
	 * <p>
	 * Returns annotations specific to limited radius search with member existence verification.
	 *
	 * @return List of supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		List<Class<? extends Annotation>> supportedAnnotationClasses = super.getSupportedAnnotationClasses();
		supportedAnnotationClasses.add(Count.class);
		return supportedAnnotationClasses;
	}
	
}