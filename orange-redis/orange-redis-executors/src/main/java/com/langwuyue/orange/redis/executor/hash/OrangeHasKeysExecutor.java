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
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import com.langwuyue.orange.redis.annotation.ContinueOnFailure;
import com.langwuyue.orange.redis.annotation.Multiple;
import com.langwuyue.orange.redis.annotation.hash.HasKeys;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.executor.hash.context.OrangeHashKeysContext;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisHashOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;
import com.langwuyue.orange.redis.utils.OrangeReflectionUtils;

/**
 * Executor for checking if multiple fields exist in a Redis hash.
 *
 * <p>This executor provides functionality to check whether multiple given field names exist
 * in a Redis hash. It can return results in various formats including maps, collections, or arrays.
 *
 * <p>This executor supports the following annotations:
 * <ul>
 *   <li>{@link HasKeys} - Marks a method as a hash fields existence check operation</li>
 *   <li>{@link Multiple} - Indicates that the method accepts multiple hash keys to check</li>
 *   <li>{@link ContinueOnFailure} - Specifies that the operation should continue even if some checks fail</li>
 * </ul>
 *
 * <p>The executor supports different return types:
 * <ul>
 *   <li>Map&lt;K,Boolean&gt; - Maps each key to its existence status</li>
 *   <li>Collection&lt;Boolean&gt; - Returns a collection of boolean values in the same order as input keys</li>
 *   <li>boolean[] - Returns an array of boolean values in the same order as input keys</li>
 * </ul>
 *
 * <p>When used with {@link ContinueOnFailure}, the executor will continue processing remaining keys
 * even if some operations fail, with failed operations returning null in the result.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeHasKeysExecutor extends OrangeRedisAbstractExecutor {
	
	/** Redis hash operations used by this executor */
	private OrangeRedisHashOperations operations;
	
	/** Logger for recording operation results and errors */
	private OrangeRedisLogger logger;

	/**
	 * Constructs a new hash fields existence check executor with the required dependencies.
	 *
	 * @param operations the Redis hash operations component for performing existence checks
	 * @param idGenerator the executor ID generator for creating unique identifiers
	 * @param logger the logger for recording operation results and errors
	 */
	public OrangeHasKeysExecutor(OrangeRedisHashOperations operations,OrangeRedisExecutorIdGenerator idGenerator,OrangeRedisLogger logger) {
		super(idGenerator);
		this.operations = operations;
		this.logger = logger;
	}

	/**
	 * Returns the list of annotation classes supported by this executor.
	 *
	 * @return a list containing {@link HasKeys}, {@link Multiple}, and {@link ContinueOnFailure} annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(HasKeys.class,Multiple.class,ContinueOnFailure.class);
	}

	/**
	 * Returns the context class used by this executor for handling hash fields existence checks.
	 *
	 * @return the class object for {@link OrangeHashKeysContext}
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeHashKeysContext.class;
	}

	/**
	 * Executes the hash fields existence check operation.
	 *
	 * <p>This method checks if multiple fields exist in the Redis hash and
	 * returns the results in the appropriate format based on the method's return type:
	 * <ul>
	 *   <li>Map - Maps each key to its existence status</li>
	 *   <li>Collection - Returns a collection of boolean values</li>
	 *   <li>Array - Returns an array of boolean values</li>
	 * </ul>
	 *
	 * @param context the Redis context containing the hash keys and field information
	 * @return Object the result in the appropriate format (Map, Collection, or Array)
	 * @throws Exception if any error occurs during the operation
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeHashKeysContext ctx = (OrangeHashKeysContext) context;
		List keys = ctx.getHashKeys();
		Class returnClass = ctx.getOperationMethod().getReturnType();
		if(Map.class.isAssignableFrom(returnClass)) {
			Map map = OrangeReflectionUtils.newMap(returnClass);
			doExecute(ctx,map::put);
			return map;
		}
		Object instance = OrangeReflectionUtils.getArrayOrCollectionInstance(returnClass, keys.size());
		if(instance instanceof Collection) {
			Collection collection = (Collection) instance;
			doExecute(ctx,(k,r) -> collection.add(r));
		}else{
			List<Boolean> results = new ArrayList<>();
			doExecute(ctx,(k,r) -> results.add(r));
			for(int i = 0; i < results.size(); i++) {
				Array.set(instance, i, results.get(i));
			}
		}
		return instance;
	}
	
	/**
	 * Performs the actual existence check operations for multiple hash fields.
	 *
	 * <p>This method iterates through each key and checks its existence in the Redis hash.
	 * Results are collected using the provided consumer function. If an operation fails:
	 * <ul>
	 *   <li>If {@link ContinueOnFailure} is specified, it logs a warning and continues processing</li>
	 *   <li>Otherwise, it logs an error and stops processing remaining keys (setting their results to null)</li>
	 * </ul>
	 *
	 * @param ctx the context containing Redis key and hash field information
	 * @param consumer a function that accepts a key and its existence result for collection
	 */
	private void doExecute(OrangeHashKeysContext ctx,BiConsumer<Object,Boolean> consumer) {
		List keys = ctx.getHashKeys();
		boolean alreayBreak = false;
		for(Object key : keys) {
			if(alreayBreak) {
				consumer.accept(key,null);
				continue;
			}
			try {
				Boolean has = this.operations.hasKey(
					ctx.getRedisKey().getValue(),
					key,
					ctx.getKeyType()
				);
				consumer.accept(key,has);
			}catch (Exception e) {
				consumer.accept(key,null);
				if(!ctx.continueOnFailure()) {
					this.logger.error(String.format("An exception occurred during hasKey operation executing.Method:%s", ctx.getOperationMethod()),e);
					alreayBreak = true;
				}else{
					this.logger.warn(String.format("An exception occurred during hasKey operation executing.Method:%s", ctx.getOperationMethod()),e);
				}
				
			}
		}
	}
}