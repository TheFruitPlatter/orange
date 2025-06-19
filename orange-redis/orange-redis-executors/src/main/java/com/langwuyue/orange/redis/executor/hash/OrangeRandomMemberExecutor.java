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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.langwuyue.orange.redis.annotation.Random;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.hash.context.OrangeHashContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisHashOperations;
import com.langwuyue.orange.redis.utils.OrangeReflectionUtils;

/**
 * Executor for randomly retrieving a single member from a Redis Hash.
 *
 * <p>This executor provides functionality to get a random key-value pair from a Redis Hash.
 * The operation is useful for scenarios requiring random sampling or display of hash members.
 *
 * <p>The executor supports the following annotation:
 * <ul>
 *   <li>{@link Random} - Marks this as a random member retrieval operation</li>
 * </ul>
 *
 * <p>Key features:
 * <ul>
 *   <li>Returns exactly one random member from the hash</li>
 *   <li>Uses Redis's HRANDFIELD command internally</li>
 *   <li>Supports both simple and complex return types</li>
 *   <li>Automatically handles type conversion of returned values</li>
 * </ul>
 *
 * <p>Return value handling:
 * <ul>
 *   <li>For non-collection return types: returns a single random member</li>
 *   <li>For collection/array return types: returns a collection with one random member</li>
 * </ul>
 *
 * <p>Performance considerations:
 * The HRANDFIELD operation has a time complexity of O(1), making it very efficient
 * regardless of the hash size.
 *
 * <p>Note: For retrieving multiple random members, use {@link OrangeRandomMembersExecutor}.
 * For retrieving multiple distinct random members, use {@link OrangeRandomAndDistinctMembersExecutor}.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRandomMemberExecutor extends OrangeGetMembersExecutor {
	
	/**
	 * The Redis hash operations instance used to execute hash commands.
	 */
	private OrangeRedisHashOperations operations;

	/**
	 * Constructs a new OrangeRandomMemberExecutor with the specified operations and ID generator.
	 *
	 * @param operations the Redis hash operations to use for executing commands
	 * @param idGenerator the generator used for creating executor IDs
	 */
	public OrangeRandomMemberExecutor(OrangeRedisHashOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(operations,idGenerator);
		this.operations = operations;
	}

	/**
	 * Returns the list of annotation classes supported by this executor.
	 * This executor supports the {@link Random} annotation in addition to
	 * the annotations supported by the parent class.
	 *
	 * @return a list of supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		List classes = super.getSupportedAnnotationClasses();
		classes.add(Random.class);
		return classes;
	}

	/**
	 * Returns the context class used by this executor.
	 * This executor uses {@link OrangeHashContext} to handle Redis hash operations.
	 *
	 * @return the class of {@link OrangeHashContext}
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeHashContext.class;
	}

	/**
	 * Executes the random member retrieval operation on the Redis hash.
	 * This method retrieves a single random field-value pair from the hash specified in the context.
	 *
	 * @param context the Redis operation context containing the key information
	 * @param keyType the expected type of the hash key
	 * @param valueType the expected type of the hash value
	 * @return a Map containing a single randomly selected field-value pair
	 * @throws Exception if an error occurs during the Redis operation
	 */
	@Override
	protected Map doGet(OrangeRedisContext context, Type keyType, Type valueType) throws Exception {
		OrangeHashContext ctx = (OrangeHashContext) context;
		return this.operations.randomEntries(ctx.getRedisKey().getValue(), 1L, ctx.getKeyType(), ctx.getValueType(), keyType, valueType);
	}

	/**
	 * Converts the raw Redis result into the appropriate return value for the method.
	 * Handles both collection/array return types and single object return types.
	 *
	 * @param resultMap the raw result map from Redis
	 * @param keyField the field annotated with {@link Key} in the return type
	 * @param valueField the field annotated with {@link Value} in the return type
	 * @param returnArgumentType the generic return type of the method
	 * @param returnClass the actual return class of the method
	 * @return the converted return value
	 * @throws Exception if conversion fails
	 */
	@Override
	protected Object toReturnValue(
		Map resultMap, 
		Field keyField, 
		Field valueField, 
		Type returnArgumentType,
		Class returnClass
	) throws Exception {
		if(Collection.class.isAssignableFrom(returnClass) || returnClass.isArray()) {
			return super.toReturnValue(resultMap, keyField, valueField, returnArgumentType, returnClass);
		}
		Set<Entry> entrySet = resultMap.entrySet();
		for(Entry entry : entrySet) {
			Object obj = returnClass.getConstructor().newInstance();
			OrangeReflectionUtils.setFieldValue(keyField, obj, entry.getKey());
			OrangeReflectionUtils.setFieldValue(valueField, obj, entry.getValue());
			return obj;
		}
		return null;
	}

	/**
	 * Determines the appropriate return argument type based on the context.
	 * For non-collection/array return types, returns the method's generic return type directly.
	 * For collection/array return types, delegates to parent class implementation.
	 *
	 * @param context the Redis operation context
	 * @return the appropriate return argument type
	 */
	@Override
	protected Type getReturnArgumentType(OrangeRedisContext context) {
		Class returnClass = context.getOperationMethod().getReturnType();
		if(Collection.class.isAssignableFrom(returnClass) || returnClass.isArray()) {
			return super.getReturnArgumentType(context);
		}
		return context.getOperationMethod().getGenericReturnType();
	}

	/**
	 * Creates an appropriate collection or array instance for the return value.
	 * For non-collection/array return types, creates a single-element ArrayList as a temporary container.
	 * For collection/array return types, delegates to parent class implementation.
	 *
	 * @param returnType the method's return type
	 * @param size the expected size of the collection/array
	 * @return a new collection or array instance
	 */
	@Override
	protected Object getArrayOrCollectionInstance(Class<?> returnType, int size) {
		if(Collection.class.isAssignableFrom(returnType) || returnType.isArray()) {
			return super.getArrayOrCollectionInstance(returnType, size);
		}
		return new ArrayList<>(1);
	}
	
	
	
	
}