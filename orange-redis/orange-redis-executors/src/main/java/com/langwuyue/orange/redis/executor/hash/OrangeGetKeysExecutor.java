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
package com.langwuyue.orange.redis.executor.hash;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import com.langwuyue.orange.redis.annotation.hash.GetHashKeys;
import com.langwuyue.orange.redis.annotation.hash.HashKey;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisGetAbstractExecutor;
import com.langwuyue.orange.redis.executor.hash.context.OrangeHashContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisHashOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor for retrieving all field names (keys) from a Redis hash.
 *
 * <p>This executor provides functionality to get all field names stored in a Redis hash.
 * It supports returning the field names as a collection of the specified type, with proper
 * type conversion based on the return type's generic parameters.
 *
 * <p>This executor supports the following annotations:
 * <ul>
 *   <li>{@link GetHashKeys} - Marks a method as a hash keys retrieval operation</li>
 *   <li>{@link HashKey} - Used on return type fields to indicate which field should store the hash key values</li>
 * </ul>
 *
 * <p>The executor supports returning the hash keys as various collection types
 * (List, Set, etc.) and can map the keys to custom object fields marked with
 * {@link HashKey} annotation.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/hash">Orange Redis Hash Documentation</a>
 */
public class OrangeGetKeysExecutor extends OrangeRedisGetAbstractExecutor {
	
	private OrangeRedisHashOperations operations;

	/**
	 * Constructs a new hash keys executor with the required dependencies.
	 *
	 * @param operations the Redis hash operations component for performing key retrieval operations
	 * @param idGenerator the executor ID generator for creating unique identifiers
	 */
	public OrangeGetKeysExecutor(OrangeRedisHashOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Returns the list of annotation classes supported by this executor.
	 *
	 * @return a list containing only {@link GetHashKeys} annotation class
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetHashKeys.class);
	}

	/**
	 * Returns the context class used by this executor for handling hash keys operations.
	 *
	 * <p>This executor uses {@link OrangeHashContext} to store and manage:
	 * <ul>
	 *   <li>Redis key information</li>
	 *   <li>Key type information</li>
	 * </ul>
	 *
	 * @return the class object for {@link OrangeHashContext}
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeHashContext.class;
	}
	
	/**
	 * Finds the field in the return type that is annotated with {@link HashKey}.
	 * 
	 * <p>This method searches through all declared fields of the return type to find
	 * the field marked with {@link HashKey} annotation. This field will be used to
	 * store the hash key values when mapping to custom objects.
	 *
	 * @param type the return type to search for annotated fields
	 * @return the field annotated with {@link HashKey}, or null if none found
	 */
	@Override
	protected Field getValueField(Type type) {
		Class<?> returnType = getRawType(type);
		Field[] fields = returnType.getDeclaredFields();
		for(Field field : fields) {
			if(field.isAnnotationPresent(HashKey.class)) {
				return field;
			}
		}
		return null;
	}

	/**
	 * Performs the actual retrieval of hash keys from Redis.
	 *
	 * <p>This method retrieves all field names from the specified Redis hash and
	 * converts them to the appropriate return type. If a value field is specified
	 * (through {@link HashKey} annotation), the keys will be mapped to that field
	 * in the return type objects.
	 *
	 * @param context the Redis context containing the hash information
	 * @param valueField the field to map hash keys to (if using custom objects), or null
	 * @param returnArgumentType the expected return type for proper type conversion
	 * @return Collection a collection of hash keys in the specified return type
	 * @throws Exception if any error occurs during the operation
	 */
	@Override
	protected Collection doGet(OrangeRedisContext context, Field valueField, Type returnArgumentType) throws Exception {
		OrangeHashContext ctx = (OrangeHashContext) context;
		return this.operations.keys(ctx.getRedisKey().getValue(), ctx.getKeyType(), valueField == null ? returnArgumentType : valueField.getGenericType());
	}
}