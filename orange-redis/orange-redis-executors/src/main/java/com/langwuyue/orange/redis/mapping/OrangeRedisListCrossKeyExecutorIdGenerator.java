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

import com.langwuyue.orange.redis.annotation.Timeout;
import com.langwuyue.orange.redis.annotation.TimeoutUnit;
import com.langwuyue.orange.redis.annotation.TimeoutValue;
import com.langwuyue.orange.redis.annotation.cross.CrossOperationKeys;
import com.langwuyue.orange.redis.annotation.cross.ListMoveDirection;
import com.langwuyue.orange.redis.annotation.cross.Move;
import com.langwuyue.orange.redis.annotation.cross.StoreTo;

/**
 * A specialized executor ID generator for Redis List cross-key operations.
 * 
 * <p>This class extends {@link OrangeRedisExecutorIdAbstractGenerator} to provide
 * support for generating unique identifiers for executors that handle Redis List
 * operations across multiple keys. It registers various annotations related to
 * cross-key list operations such as moving elements between lists, storing operation
 * results to different keys, and specifying timeout parameters.
 * 
 * <p>The generator supports the following annotation types:
 * <ul>
 *   <li>{@link CrossOperationKeys} - For specifying multiple keys in cross-key operations</li>
 *   <li>{@link StoreTo} - For designating a destination key for operation results</li>
 *   <li>{@link Move} - For moving elements between lists</li>
 *   <li>{@link ListMoveDirection} - For specifying the direction of list element movement</li>
 *   <li>{@link Timeout}, {@link TimeoutUnit}, {@link TimeoutValue} - For timeout configurations</li>
 * </ul>
 * 
 * <p>These annotations are used in the ID generation process to create unique identifiers
 * that reflect the specific cross-key list operation being executed.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisListCrossKeyExecutorIdGenerator extends OrangeRedisExecutorIdAbstractGenerator {

	/**
	 * Registers all annotation classes supported by this executor ID generator.
	 * 
	 * <p>This method extends the base implementation by registering annotations specific
	 * to Redis List cross-key operations. The registered annotations are used to generate
	 * unique executor IDs based on the operation characteristics.
	 *
	 * @param supportedAnnotationClasses the list to which supported annotation classes will be added
	 */
	@Override
	protected void registerSupportedAnnotationClasses(List<Class<? extends Annotation>> supportedAnnotationClasses) {
		supportedAnnotationClasses.add(CrossOperationKeys.class);
		supportedAnnotationClasses.add(StoreTo.class);
		supportedAnnotationClasses.add(Move.class);
		supportedAnnotationClasses.add(ListMoveDirection.class);
		supportedAnnotationClasses.add(Timeout.class);
		supportedAnnotationClasses.add(TimeoutUnit.class);
		supportedAnnotationClasses.add(TimeoutValue.class);
	}
}