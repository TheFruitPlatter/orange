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
package com.langwuyue.orange.redis.executor.set;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.langwuyue.orange.redis.annotation.PopMembers;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisGetAbstractExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisSetOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor implementation for popping a single member from a Redis Set.
 * 
 * <p>This executor is responsible for removing and returning a random member from
 * a Redis Set identified by the specified key. It supports the {@link PopMembers}
 * annotation and extends {@link OrangeRedisGetAbstractExecutor} to handle the retrieval
 * operation.
 * 
 * <p>The executor can return the popped member in various formats based on the method's
 * return type:
 * <ul>
 *   <li>Single value - Returns the popped member directly</li>
 *   <li>Array or Collection - Returns the popped member in the specified collection type</li>
 * </ul>
 * 
 * <p>If the set is empty, the executor returns null for single value return types
 * or an empty collection for array/collection return types.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/set">Orange Redis Set Documentation</a>
 */
public class OrangePopMemberExecutor extends OrangeRedisGetAbstractExecutor {
	
	private OrangeRedisSetOperations operations;

	/**
	 * Constructs a new OrangePopMemberExecutor with the specified operations and ID generator.
	 *
	 * @param operations the Redis Set operations implementation to use for popping members
	 * @param idGenerator the generator for creating unique executor IDs
	 */
	public OrangePopMemberExecutor(OrangeRedisSetOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Returns the list of annotation classes supported by this executor.
	 * 
	 * <p>This executor supports the {@link PopMembers} annotation, which is used
	 * to mark methods that should pop a member from a Redis Set.
	 *
	 * @return a list containing the PopMembers annotation class
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(PopMembers.class);
	}

	/**
	 * Performs the actual pop operation on the Redis Set.
	 * 
	 * <p>This method removes and returns a single random member from the Redis Set
	 * identified by the key in the provided context. It always requests exactly one
	 * member to be popped.
	 *
	 * @param context the Redis context containing the key and other execution parameters
	 * @param valueField the field representing the value type, may be null
	 * @param returnArgumentType the generic return type of the method
	 * @return a collection containing the popped member
	 * @throws Exception if an error occurs during the pop operation
	 */
	@Override
	protected Collection doGet(OrangeRedisContext context, Field valueField, Type returnArgumentType) throws Exception {
		return this.operations.popMember(
			context.getRedisKey().getValue(), 
			1,
			context.getValueType(), 
			valueField == null ? returnArgumentType : valueField.getGenericType()
		);
	}
	
	/**
	 * Executes the pop operation and handles the result based on the return type.
	 * 
	 * <p>This method extends the base execution behavior by adding special handling
	 * for different return types:
	 * <ul>
	 *   <li>For array or collection return types, returns the result as is</li>
	 *   <li>For single value return types, extracts the first element from the result list</li>
	 * </ul>
	 *
	 * @param context the Redis context containing the operation parameters
	 * @return the popped member(s) in the appropriate format based on the return type
	 * @throws Exception if an error occurs during execution
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		Object result = super.execute(context);
		if(result == null) {
			return result;
		}
		Class<?> returnClass = context.getOperationMethod().getReturnType();
		if(returnClass.isArray() || Collection.class.isAssignableFrom(returnClass)) {
			return result;	
		}
		else{
			ArrayList list = ((ArrayList)result);
			return list.isEmpty() ? null : list.get(0);
		}
	}
	
	/**
	 * Determines the return argument type based on the method's return type.
	 * 
	 * <p>This method provides special handling for different return types:
	 * <ul>
	 *   <li>For array or collection return types, delegates to the parent implementation</li>
	 *   <li>For single value return types, returns the method's generic return type directly</li>
	 * </ul>
	 *
	 * @param context the Redis context containing the method information
	 * @return the Type representing the return argument type
	 */
	@Override
	protected Type getReturnArgumentType(OrangeRedisContext context) {
		Class<?> returnClass = context.getOperationMethod().getReturnType();
		if(returnClass.isArray() || Collection.class.isAssignableFrom(returnClass)) {
			return super.getReturnArgumentType(context);	
		}
		else{
			return context.getOperationMethod().getGenericReturnType();
		}
	}

	/**
	 * Creates an appropriate array or collection instance based on the return type.
	 * 
	 * <p>This method handles the creation of the result container:
	 * <ul>
	 *   <li>For array or collection return types, delegates to the parent implementation
	 *       which creates the appropriate collection type</li>
	 *   <li>For single value return types, creates a new ArrayList with capacity 1
	 *       to temporarily hold the single popped member</li>
	 * </ul>
	 *
	 * @param returnClass the class representing the method's return type
	 * @param size the expected size of the collection (typically 1 for this executor)
	 * @return an appropriate collection instance to hold the result
	 */
	@Override
	protected Object getArrayOrCollectionInstance(Class<?> returnClass, int size) {
		if(returnClass.isArray() || Collection.class.isAssignableFrom(returnClass)) {
			return super.getArrayOrCollectionInstance(returnClass, size);
		}
		return new ArrayList<>(1);
	}
}