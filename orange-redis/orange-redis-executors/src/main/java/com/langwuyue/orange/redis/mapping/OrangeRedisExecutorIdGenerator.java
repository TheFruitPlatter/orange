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

/**
 * Interface for generating and validating operation IDs for Redis executors.
 * 
 * <p>This interface defines methods for generating unique operation IDs based on
 * a list of annotation classes and for checking if a given operation ID contains
 * a specific annotation. The implementation of this interface is responsible for
 * mapping annotations to bit positions in the generated ID.
 *
 * <p>Operation IDs are used to efficiently identify and categorize Redis operations
 * based on their associated annotations. Each operation ID is a 64-bit long value
 * where each bit represents the presence or absence of a specific annotation.
 *
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public interface OrangeRedisExecutorIdGenerator {
	
	/**
	 * Generates an operation ID based on a list of annotation classes.
	 * 
	 * <p>This method creates a unique identifier by combining bit representations
	 * of the provided annotation classes. Each annotation class is mapped to a specific
	 * bit position, and the resulting ID has bits set for all the provided annotations.
	 *
	 * @param supportedClasses a list of annotation classes to include in the operation ID
	 * @return a long value representing the generated operation ID
	 */
	long generate(List<Class<? extends Annotation>> supportedClasses);
	
	/**
	 * Checks if a given operation ID contains a specific annotation.
	 * 
	 * <p>This method determines whether the bit pattern corresponding to the specified
	 * annotation is present in the provided operation ID.
	 *
	 * @param operationId the operation ID to check
	 * @param annotationClass the annotation class to check for
	 * @return true if the operation ID contains the specified annotation, false otherwise
	 */
	boolean contains(long operationId, Class<? extends Annotation> annotationClass);

}