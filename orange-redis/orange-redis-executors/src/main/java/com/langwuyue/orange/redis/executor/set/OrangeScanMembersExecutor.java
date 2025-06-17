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
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.langwuyue.orange.redis.annotation.Count;
import com.langwuyue.orange.redis.annotation.GetMembers;
import com.langwuyue.orange.redis.annotation.ScanPattern;
import com.langwuyue.orange.redis.annotation.zset.PageNo;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisGetAbstractExecutor;
import com.langwuyue.orange.redis.executor.set.context.OrangeScanContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisSetOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisSetOperations.ScanResults;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;
import com.langwuyue.orange.redis.utils.OrangeReflectionUtils;

/**
 * Executor implementation for scanning members in a Redis Set with pattern matching and pagination support.
 * 
 * <p>This executor handles operations annotated with {@link GetMembers}, {@link ScanPattern},
 * {@link Count}, and {@link PageNo} annotations. It provides functionality to scan and retrieve
 * members from a Redis Set that match a specified pattern, with support for pagination and
 * count control.
 *
 * <p>The executor extends {@link OrangeRedisGetAbstractExecutor} to implement the core Redis set
 * scanning operations. It supports returning results in different collection types and can also
 * return a Map containing both the cursor and the matched members.
 *
 * <p>The scanning operation is performed using {@link OrangeRedisSetOperations#scan} which returns
 * both the cursor for the next scan operation and the current batch of matched members.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/set">Orange Redis Set Documentation</a>
 */
public class OrangeScanMembersExecutor extends OrangeRedisGetAbstractExecutor {
	
	private OrangeRedisSetOperations operations;

	/**
	 * Constructs a new OrangeScanMembersExecutor with the specified operations and ID generator.
	 * 
	 * <p>This constructor initializes the executor with the Redis set operations implementation
	 * that will be used to perform the actual scanning operations, and an ID generator
	 * that will be used to generate unique identifiers for the executor instances.
	 *
	 * @param operations the Redis set operations implementation to use for scanning members
	 * @param idGenerator the executor ID generator to use
	 */
	public OrangeScanMembersExecutor(OrangeRedisSetOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Returns the list of annotation classes supported by this executor.
	 * 
	 * <p>This executor supports methods annotated with:
	 * <ul>
	 *   <li>{@link GetMembers} - Indicates that the method retrieves members from a Redis Set</li>
	 *   <li>{@link ScanPattern} - Specifies the pattern to match when scanning members</li>
	 *   <li>{@link Count} - Specifies the count hint for each scan operation</li>
	 *   <li>{@link PageNo} - Specifies the page number for pagination</li>
	 * </ul>
	 *
	 * @return a list containing the supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetMembers.class,ScanPattern.class,Count.class,PageNo.class);
	}

	/**
	 * Performs the actual scan operation on the Redis Set.
	 * 
	 * <p>This method implements the core functionality of scanning members in a Redis Set.
	 * It casts the provided context to {@link OrangeScanContext} to access the scan parameters,
	 * then uses the {@link OrangeRedisSetOperations#scan} method to perform the actual scan operation.
	 *
	 * <p>The scan operation retrieves members from the Redis Set that match the specified pattern,
	 * using the provided count hint and page number for pagination. The result includes both the
	 * cursor for the next scan operation and the current batch of matched members.
	 *
	 * @param context the Redis context containing operation parameters and Redis key
	 * @param valueField the field to store the result, may be null
	 * @param returnArgumentType the type argument of the return value
	 * @return a collection containing the scan results, or null if no results were found
	 * @throws Exception if an error occurs during the scan operation
	 */
	@Override
	protected Collection doGet(OrangeRedisContext context, Field valueField, Type returnArgumentType) throws Exception {
		OrangeScanContext ctx = (OrangeScanContext) context;
		ScanResults scanResults = this.operations.scan(
			context.getRedisKey().getValue(), 
			ctx.getPattern(),
			ctx.getCount(),
			ctx.getPageNo(),
			context.getValueType(), 
			valueField == null ? returnArgumentType : valueField.getGenericType()
		);
		if(scanResults == null) {
			return null;
		}
		return OrangeCollectionUtils.asList(scanResults);
	}

	/**
	 * Converts the scan results to the appropriate return value type.
	 * 
	 * <p>This method handles the conversion of scan results to the expected return type:
	 * <ul>
	 *   <li>If the return type is a {@link Map} or {@link LinkedHashMap}, it returns a map with
	 *       the cursor as the key and the set of matched members as the value</li>
	 *   <li>Otherwise, it extracts just the members from the scan results and converts them
	 *       to the expected collection type using the superclass implementation</li>
	 * </ul>
	 *
	 * @param context the Redis context containing operation parameters
	 * @param result the collection containing the scan results
	 * @param returnArgumentType the type argument of the return value
	 * @param field the field to store the result, may be null
	 * @return the scan results converted to the appropriate return type, or null if the input collection is null
	 * @throws Exception if an error occurs during the conversion process
	 */
	@Override
	protected Object toReturnValue(
		OrangeRedisContext context, 
		Collection result, 
		Type returnArgumentType, 
		Field field
	)throws Exception {
		if(result == null) {
			return null;
		}
		List list = (List) result;
		ScanResults scanResults = (ScanResults)list.get(0);
		Class<?> returnClass = context.getOperationMethod().getReturnType();
		if(returnClass == Map.class || returnClass.isAssignableFrom(LinkedHashMap.class)) {
			Map<Long,Set<Object>> map = new LinkedHashMap<>();
			map.put(scanResults.getCursor(), scanResults.getMembers());
			return map;
		}
		return super.toReturnValue(context, scanResults.getMembers(), returnArgumentType, field);
	}

	/**
	 * Returns the context class used by this executor.
	 * 
	 * <p>This executor uses {@link OrangeScanContext} to store and access
	 * the Redis key, scan pattern, count, and page number information needed for
	 * the scan operation.
	 *
	 * @return the {@link OrangeScanContext} class
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeScanContext.class;
	}
	
	/**
	 * Determines the type argument for the return value based on the method's return type.
	 * 
	 * <p>This method handles special cases for Map return types:
	 * <ul>
	 *   <li>If the return type is a {@link Map} or {@link LinkedHashMap}, it extracts the
	 *       type argument of the map's value (which should be a collection type)</li>
	 *   <li>For collection values in the map, it further extracts the collection's element type</li>
	 *   <li>For other return types, it delegates to the superclass implementation</li>
	 * </ul>
	 *
	 * @param context the Redis context containing the operation method information
	 * @return the type argument for the return value, considering map value types for map returns
	 */
	@Override
	protected Type getReturnArgumentType(OrangeRedisContext context){
		Class<?> returnClass = context.getOperationMethod().getReturnType();
		if(returnClass == Map.class || returnClass.isAssignableFrom(LinkedHashMap.class)) {
			Type mapValueType = OrangeReflectionUtils.getMapValueType(context.getOperationMethod().getGenericReturnType());
			return OrangeReflectionUtils.getCollectionOrArrayArgumentType(mapValueType);
		}
		return super.getReturnArgumentType(context);
	}
	
}