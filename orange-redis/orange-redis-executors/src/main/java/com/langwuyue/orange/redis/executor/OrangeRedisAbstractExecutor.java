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
package com.langwuyue.orange.redis.executor;

import java.lang.annotation.Annotation;
import java.util.List;

import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;

/**
 * Abstract base implementation of {@link OrangeRedisExecutor} that provides common functionality
 * for Redis operation executors.
 * 
 * <p>This class handles the executor ID generation and management, allowing concrete implementations
 * to focus on their specific Redis operation logic. The ID generation is based on the supported
 * annotations of the executor.
 * 
 * <p>Key features:
 * <ul>
 *   <li>Automatic executor ID generation based on supported annotations</li>
 *   <li>Common base implementation for Redis executors</li>
 *   <li>Abstract template pattern for annotation support</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeRedisExecutor
 * @see OrangeRedisExecutorIdGenerator
 */
public abstract class OrangeRedisAbstractExecutor implements OrangeRedisExecutor {
	
	/**
	 * The unique identifier for this executor.
	 * This ID is generated based on the supported annotation classes
	 * and is used for executor registration and lookup.
	 */
	private long id;
	
	/**
	 * Creates a new instance of OrangeRedisAbstractExecutor.
	 * 
	 * <p>This constructor initializes the executor with a unique ID generated
	 * based on the supported annotation classes. The ID generation is delegated
	 * to the provided {@link OrangeRedisExecutorIdGenerator}.
	 * 
	 * <p>The ID generation process typically involves:
	 * <ol>
	 *   <li>Getting the list of supported annotation classes from the concrete implementation</li>
	 *   <li>Using these classes to generate a unique ID (e.g., using hash functions)</li>
	 *   <li>Storing the generated ID for later use</li>
	 * </ol>
	 *
	 * @param idGenerator the generator responsible for creating unique executor IDs
	 *        based on annotation classes
	 */
	public OrangeRedisAbstractExecutor(OrangeRedisExecutorIdGenerator idGenerator) {
		this.id = idGenerator.generate(getSupportedAnnotationClasses());
	}

	/**
	 * Returns the list of annotation classes supported by this executor.
	 * 
	 * <p>This method is used during ID generation to create a unique identifier
	 * for the executor based on the annotations it supports. Each concrete
	 * implementation must provide the list of annotation classes it can handle.
	 * 
	 * @return a non-null list of annotation classes supported by this executor
	 */
	protected abstract List<Class<? extends Annotation>> getSupportedAnnotationClasses();

	/**
	 * {@inheritDoc}
	 * 
	 * <p>This implementation returns the ID that was generated during
	 * executor initialization based on the supported annotation classes.
	 *
	 * @return the unique identifier for this executor
	 */
	@Override
	public long getId() {
		return id;
	}
}