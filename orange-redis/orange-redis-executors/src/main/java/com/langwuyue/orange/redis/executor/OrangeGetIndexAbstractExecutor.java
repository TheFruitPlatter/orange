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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.annotation.ContinueOnFailure;
import com.langwuyue.orange.redis.annotation.GetIndexs;
import com.langwuyue.orange.redis.annotation.Multiple;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisMultipleValueContext;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Abstract executor for Redis index retrieval operations.
 * 
 * <p>This abstract class provides the base implementation for executors that handle
 * index retrieval operations in Redis collections. It supports both single and multiple
 * value index retrievals, with flexible return type handling including Maps, Lists,
 * and Arrays.
 *
 * <p>The executor supports the following features:
 * <ul>
 *   <li>Multiple value index retrieval</li>
 *   <li>Configurable error handling with continue-on-failure support</li>
 *   <li>Flexible return types (Map, List, Array)</li>
 *   <li>Automatic error logging</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public abstract class OrangeGetIndexAbstractExecutor extends OrangeRedisAbstractExecutor {
	
	private OrangeRedisLogger logger;

	/**
	 * Constructs a new index retrieval executor.
	 * 
	 * <p>Initializes the executor with the specified ID generator and logger.
	 * The ID generator is used for generating unique identifiers for operations,
	 * while the logger is used for recording execution events and errors.
	 *
	 * @param idGenerator the generator for creating unique operation IDs
	 * @param logger the logger for recording execution events and errors
	 */
	protected OrangeGetIndexAbstractExecutor(OrangeRedisExecutorIdGenerator idGenerator,OrangeRedisLogger logger) {
		super(idGenerator);
		this.logger = logger;
	}

	/**
	 * Executes the index retrieval operation based on the provided context.
	 * 
	 * <p>This method handles different return types for index retrieval operations:
	 * <ul>
	 *   <li>Map: Returns a map of values to their indices</li>
	 *   <li>ArrayList: Returns a list of indices in the order of input values</li>
	 *   <li>Array: Returns an array of indices in the order of input values</li>
	 * </ul>
	 *
	 * @param context the context containing operation parameters and configuration
	 * @return the operation result as either a Map, List, or Array based on the method's return type
	 * @throws OrangeRedisException if the return type is not supported or if the operation fails
	 * @throws Exception if any other error occurs during execution
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		Class returnClass = context.getOperationMethod().getReturnType();
		boolean returnMap = Map.class.isAssignableFrom(returnClass);
		if(returnMap) {
			Map<Object,Long> resultMap = new LinkedHashMap<>();
			doGetIndex(context,resultMap::put);
			// It's hard to know whether the null returned by resultMap.get(key) is due to an exception.
			return resultMap;
		}
		else if(returnClass.isAssignableFrom(ArrayList.class)) {
			List<Long> results = new ArrayList<>();
			doGetIndex(context,(o,t) -> results.add(t));
			return results;
		}
		else if(returnClass.isArray()) {
			List<Long> results = new ArrayList<>();
			doGetIndex(context,(o,t) -> results.add(t));
			return results.toArray();
		}
		throw new OrangeRedisException("The return type of an indexing operation must be a collection, an array, or a map");
	}
	
	/**
	 * Retrieves the index of a specific value in a Redis collection.
	 * 
	 * <p>This abstract method must be implemented by subclasses to perform the actual
	 * Redis index retrieval operation for their specific collection type. It is called
	 * for each value when processing index retrieval operations.
	 *
	 * @param context the Redis operation context containing key information
	 * @param value the value whose index should be retrieved
	 * @return the index of the value in the collection, or null if not found
	 * @throws Exception if the index retrieval operation fails
	 */
	protected abstract Long getIndex(OrangeRedisContext context,Object value) throws Exception;
	
	/**
	 * Returns the context class required for index retrieval operations.
	 * 
	 * <p>This executor uses {@link OrangeRedisMultipleValueContext} to handle
	 * operations that retrieve indices for multiple values. The context provides
	 * support for handling multiple values and their corresponding results.
	 *
	 * @return the {@link OrangeRedisMultipleValueContext} class
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeRedisMultipleValueContext.class;
	}
	
	/**
	 * Returns the list of annotation classes supported by this index retrieval executor.
	 * 
	 * <p>This executor supports the following annotations:
	 * <ul>
	 *   <li>{@link GetIndexs} - Marks methods that retrieve indices from Redis collections</li>
	 *   <li>{@link Multiple} - Indicates operations with multiple values</li>
	 *   <li>{@link ContinueOnFailure} - Controls behavior when individual operations fail</li>
	 * </ul>
	 *
	 * @return a list of supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetIndexs.class,Multiple.class,ContinueOnFailure.class);
	}
	
	/**
	 * Processes index retrieval for multiple values and applies the consumer to each result.
	 * 
	 * <p>This method iterates through all values in the context, retrieves the index for each,
	 * and applies the provided consumer function to the value-index pair. It handles errors
	 * according to the continue-on-failure configuration:
	 * <ul>
	 *   <li>If continue-on-failure is enabled: logs warnings and continues processing</li>
	 *   <li>If continue-on-failure is disabled: throws an exception on the first error</li>
	 * </ul>
	 * <p>The method also validates that returned indices are not null, throwing an exception
	 * if a null index is encountered.
	 *
	 * @param context the context containing the operation parameters and values
	 * @param consumer the function to apply to each value-index pair
	 */
	private void doGetIndex(OrangeRedisContext context,BiConsumer<Object,Long> consumer) {
		OrangeRedisMultipleValueContext ctx = (OrangeRedisMultipleValueContext)context;
		boolean continueOnFailure = ctx.continueOnFailure();
		try {
			ctx.forEach((t,o) -> {
				try {
					Long result = getIndex(context,t);
					if(consumer != null) {
						consumer.accept(o,result);
					}
					if(result == null) {
						throw new OrangeRedisException(
							String.format(
								"Null returned! Operation: %s %n Operatin owner: %s", 
								context.getOperationMethod(),
								context.getOperationOwner()
							)
						);
					}
				}catch (Exception e) {
					// The operation may have been interrupted by a client timeout or network error, 
					// but it was actually completed successfully.
					if(!continueOnFailure) {
						if(e instanceof OrangeRedisException) {
							throw (OrangeRedisException) e;
						}else {
							throw new OrangeRedisException(
								String.format(
									"An exception occurred during get index operation! Operation: %s %n Operatin owner: %s", 
									context.getOperationMethod(),
									context.getOperationOwner()
								),
								e
							);
						}
					}else{
						if(!(e instanceof OrangeRedisException)) {
							this.logger.warn(
								String.format(
									"An exception occurred during get index operation! Operation: %s %n Operatin owner: %s", 
									context.getOperationMethod(),
									context.getOperationOwner()
								),
								e
							);
						}
					}
				}
			});
		}catch (Exception e) {
			this.logger.warn("An exception occurred during get index operation.",e);
		}
	}
}