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
 * Abstract base class for generating unique identifiers for Redis executors based on annotations.
 * This class provides the core functionality for managing and processing Redis operation annotations,
 * generating unique IDs based on combinations of these annotations, and checking for the presence
 * of specific annotations in operation IDs.
 *
 * <p>The generator maintains a registry of supported annotation classes and their corresponding
 * indices, which are used to generate unique bit-masked identifiers. Each supported annotation
 * is assigned a unique position in the bit mask, allowing for efficient storage and lookup
 * of annotation combinations.
 *
 * <p>This class implements the basic annotation registration mechanism and ID generation
 * algorithm, while allowing subclasses to define additional supported annotations through
 * the {@link #registerSupportedAnnotationClasses(List)} method.
 *
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public abstract class OrangeRedisExecutorIdAbstractGenerator implements OrangeRedisExecutorIdGenerator {
	
	/**
	 * List of annotation classes supported by this generator.
	 * This list is populated during initialization by the registerSupportedAnnotationClasses method.
	 */
	private List<Class<? extends Annotation>> supportedAnnotationClasses;
	
	/**
	 * Map associating each supported annotation class with its unique index value.
	 * The index values are used in bit operations for generating and checking operation IDs.
	 * This map is built by the buildSupportedAnnotationClassesMap method during initialization.
	 */
	private Map<Class<? extends Annotation>,Integer> supportedAnnotationClassesIdMap;
	
	/**
	 * Protected constructor for initializing the abstract generator.
	 * This constructor initializes the collections for storing supported annotation classes
	 * and their corresponding indices, registers the default supported annotations, and
	 * builds the mapping between annotation classes and their indices.
	 */
	protected OrangeRedisExecutorIdAbstractGenerator() {
		this.supportedAnnotationClasses = new ArrayList<>(64);
		this.supportedAnnotationClassesIdMap = new LinkedHashMap<>(64);
		registerSupportedAnnotationClasses(supportedAnnotationClasses);
		buildSupportedAnnotationClassesMap();
	}
	
	/**
	 * Generates a unique identifier based on a list of annotation classes.
	 * This method creates a bit-masked identifier where each bit position corresponds
	 * to a specific annotation class. The resulting ID uniquely identifies the
	 * combination of annotations present in the provided list.
	 *
	 * <p>The generation algorithm uses bitwise XOR operations to set bits corresponding
	 * to each annotation's index in the supported annotations registry.
	 *
	 * @param supportedClasses the list of annotation classes for which to generate an ID
	 * @return a long value representing the unique identifier for the given annotation combination
	 * @throws OrangeRedisException if any annotation in the list is not supported by this generator
	 */
	@Override
	public long generate(List<Class<? extends Annotation>> supportedClasses) {
		long id = 0;
		for(Class<? extends Annotation> annotationClass : supportedClasses) {
			long index = getSupportedAnnotationIndex(annotationClass);
			id = id ^ (1 << index);
		}
		return id;
	}
	
	/**
	 * Registers the default annotation classes that this generator supports.
	 * This method adds the core Redis operation annotations to the list of supported
	 * annotation classes. Subclasses should override this method to add their specific
	 * annotations while calling super to include these default annotations.
	 *
	 * <p>The default supported annotations include:
	 * <ul>
	 *   <li>{@link SetExpiration} - For setting expiration time on Redis keys</li>
	 *   <li>{@link GetExpiration} - For retrieving expiration time of Redis keys</li>
	 *   <li>{@link Delete} - For deleting Redis keys</li>
	 *   <li>{@link TimeoutUnit} - For specifying time unit for timeout operations</li>
	 * </ul>
	 *
	 * @param supportedAnnotationClasses the list to which supported annotation classes
	 *                                  should be added
	 */
	protected void registerSupportedAnnotationClasses(List<Class<? extends Annotation>> supportedAnnotationClasses) {
		supportedAnnotationClasses.add(SetExpiration.class);
		supportedAnnotationClasses.add(GetExpiration.class);
		supportedAnnotationClasses.add(Delete.class);
		supportedAnnotationClasses.add(TimeoutUnit.class);
	}

	/**
	 * Retrieves the index assigned to a supported annotation class.
	 * This method looks up the index value associated with the given annotation class
	 * in the supported annotations registry.
	 *
	 * @param annotationClass the annotation class whose index is to be retrieved
	 * @return the index value assigned to the annotation class
	 * @throws OrangeRedisException if the annotation class is not supported by this generator
	 */
	private int getSupportedAnnotationIndex(Class<? extends Annotation> annotationClass) {
		Integer index = supportedAnnotationClassesIdMap.get(annotationClass);
		if(index == null) {
			throw new OrangeRedisException(String.format("The annotation @%s is not supported", annotationClass));
		}
		return index;
	}
	
	/**
	 * Builds the mapping between supported annotation classes and their corresponding indices.
	 * This method initializes the supportedAnnotationClassesIdMap with entries mapping each
	 * supported annotation class to its unique index value. The index values are used in
	 * bit operations for generating and checking operation IDs.
	 *
	 * <p>This method is called during initialization to prepare the generator for
	 * efficient ID generation and annotation checking operations.
	 */
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
	
	/**
	 * Checks if a given operation ID contains a specific annotation.
	 * This method determines whether the bit pattern corresponding to the specified
	 * annotation is present in the provided operation ID using bitwise operations.
	 *
	 * <p>The method performs a lookup of the annotation's index in the supported
	 * annotations registry and uses bitwise AND operation to check if all bits
	 * corresponding to the annotation are set in the operation ID.
	 *
	 * @param operationId the operation ID to check
	 * @param annotationClass the annotation class to check for
	 * @return true if the operation ID contains the specified annotation, false otherwise
	 */
	@Override
	public boolean contains(long operationId, Class<? extends Annotation> annotationClass) {
		Integer id = supportedAnnotationClassesIdMap.get(annotationClass);
		return id != null && ((operationId & id) == id);
	}
}