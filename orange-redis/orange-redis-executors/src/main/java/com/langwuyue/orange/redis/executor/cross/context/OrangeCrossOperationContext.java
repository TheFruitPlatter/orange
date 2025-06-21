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
package com.langwuyue.orange.redis.executor.cross.context;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.context.OrangeRedisContext;

/**
 * Base context class for Redis cross operations (operations involving multiple keys).
 * <p>
 * Extends {@link OrangeRedisContext} to provide common functionality for operations
 * that work across multiple Redis keys. Handles key management and result storage
 * configuration for cross operations.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeRedisContext Base Redis operation context
 */
public class OrangeCrossOperationContext extends OrangeRedisContext {

	/**
	 * List of Redis keys involved in the cross operation.
	 * <p>
	 * The first key is typically the reference key, while subsequent keys
	 * are comparison keys for the operation.
	 */
	private List<String> keys;
	
	/**
	 * Target field/property name where operation results should be stored.
	 * <p>
	 * If null, results won't be automatically stored. The value should match
	 * a field or setter method in the operation's return type.
	 */
	private String storeTo;
	
	/**
	 * Constructs a new context for cross operations.
	 * <p>
	 * Initializes the context with operation metadata and validates the keys.
	 * Ensures at least one key is provided for the operation.
	 *
	 * @param operationOwner The class containing the Redis operation method
	 * @param operationMethod The method annotated with Redis operation
	 * @param args The method arguments
	 * @param keys The Redis keys involved in the operation (must contain at least one key)
	 * @param storeTo The target field to store results (if any)
	 * @param valueType The type of values being operated on
	 */
	public OrangeCrossOperationContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args, 
		List<String> keys,
		String storeTo, 
		RedisValueTypeEnum valueType
	) {
		super(operationOwner, operationMethod, args, null,valueType);
		this.keys = keys;
		this.storeTo = storeTo;
	}
	
	/**
	 * Factory method to create a new cross operation context.
	 * <p>
	 * Creates and returns an appropriate context instance using reflection based on the
	 * provided context class. This is the preferred way to instantiate operation contexts.
	 *
	 * @param contextClass The specific context class to instantiate
	 * @param operationOwner The class containing the Redis operation method
	 * @param operationMethod The method annotated with Redis operation
	 * @param args The method arguments
	 * @param keys The Redis keys involved in the operation
	 * @param storeTo The target field to store results (if any)
	 * @param valueType The type of values being operated on
	 * @return A new operation context instance
	 * @throws Exception if context creation fails (reflection errors, etc.)
	 */
	public static OrangeRedisContext newInstance(
		Class<? extends OrangeRedisContext> contextClass,
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args,
		List<String> keys,
		String storeTo,
		RedisValueTypeEnum valueType
	) throws Exception {
		Constructor<? extends OrangeRedisContext> constructor = contextClass.getConstructor(
			Class.class,
			Method.class,
			Object[].class,
			List.class,
			String.class,
			RedisValueTypeEnum.class
		);
		return constructor.newInstance(operationOwner,operationMethod,args,keys,storeTo,valueType);
	}

	/**
	 * Gets all keys involved in the cross operation.
	 * <p>
	 * The returned list maintains the original key order, with the reference key
	 * at index 0 followed by comparison keys.
	 *
	 * @return Unmodifiable list of all operation keys
	 */
	public List<String> getKeys() {
		return keys;
	}
	
	/**
	 * Gets the reference key for the cross operation.
	 * <p>
	 * The reference key is always the first key in the keys list and serves as
	 * the primary key for the operation. Comparison operations are performed
	 * relative to this key.
	 *
	 * @return The reference key
	 */
	public String getReferenceKey() {
		return keys.get(0);
	}
	
	/**
	 * Gets the comparison keys for the cross operation.
	 * <p>
	 * Returns all keys except the reference key (first key) as a Set. These keys are used
	 * for comparison operations against the reference key. The Set implementation preserves
	 * insertion order while ensuring uniqueness.
	 *
	 * @return Set of comparison keys (empty if only one key exists)
	 */
	public Set<String> getComparisonKeys() {
		Set<String> comparisonKeys = new LinkedHashSet<>();
		for (int i = 1; i < keys.size(); i++) {
			comparisonKeys.add(keys.get(i));
		}
		return comparisonKeys;
	}

	/**
	 * Gets the target field/property name where operation results should be stored.
	 * <p>
	 * The returned value corresponds to a field or setter method in the operation's
	 * return type. If null, results won't be automatically stored.
	 *
	 * @return The target field name, or null if not specified
	 */
	public String getStoreTo() {
		return storeTo;
	}
}