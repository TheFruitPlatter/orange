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
import java.util.List;
import java.util.Map;

import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.annotation.AddMembers;
import com.langwuyue.orange.redis.annotation.ContinueOnFailure;
import com.langwuyue.orange.redis.annotation.Multiple;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisIterableContext;
import com.langwuyue.orange.redis.context.OrangeRedisMultipleValueContext;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;
import com.langwuyue.orange.redis.utils.OrangeReflectionUtils;

/**
 * Abstract executor for Redis operations that add members to collections (Sets, Lists, etc.).
 * 
 * <p>This executor handles bulk member addition operations with support for:
 * <ul>
 *   <li>Single and multiple member additions</li>
 *   <li>Success/failure tracking per member when returning Map</li>
 *   <li>Configurable failure handling with {@link ContinueOnFailure}</li>
 *   <li>Batch operations through {@link Multiple} annotation</li>
 * </ul>
 * 
 * <p>Key features:
 * <ul>
 *   <li>Supports both single-value and bulk addition operations</li>
 *   <li>Provides detailed operation results through Map return type</li>
 *   <li>Handles operation failures gracefully with configurable behavior</li>
 *   <li>Integrates with logging system for operation tracking</li>
 * </ul>
 * 
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see AddMembers
 * @see Multiple
 * @see ContinueOnFailure
 * @see OrangeRedisMultipleValueContext
 */
public abstract class OrangeAddMembersAbstractExecutor extends OrangeRedisAbstractExecutor {
	
	/**
	 * Logger for recording operation results and errors.
	 */
	private OrangeRedisLogger logger;
	
	/**
	 * Creates a new instance of OrangeAddMembersAbstractExecutor.
	 *
	 * @param idGenerator the generator for creating unique executor IDs
	 * @param logger the logger for recording operation results and errors
	 */
	protected OrangeAddMembersAbstractExecutor(OrangeRedisExecutorIdGenerator idGenerator,OrangeRedisLogger logger) {
		super(idGenerator);
		this.logger = logger;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>Executes the member addition operation with the following logic:
	 * <ol>
	 *   <li>If return type is not Map, performs single addition operation</li>
	 *   <li>For Map return type:
	 *     <ul>
	 *       <li>Creates result map to track success/failure per member</li>
	 *       <li>Iterates through members performing individual additions</li>
	 *       <li>Records success/failure status in result map</li>
	 *       <li>Handles failures according to {@link ContinueOnFailure} setting</li>
	 *     </ul>
	 *   </li>
	 * </ol>
	 *
	 * <p>Error handling:
	 * <ul>
	 *   <li>With {@link ContinueOnFailure(false)}: throws exception on first failure</li>
	 *   <li>With {@link ContinueOnFailure(true)}: logs errors and continues processing</li>
	 *   <li>Network errors and timeouts are logged with detailed context</li>
	 * </ul>
	 *
	 * @param context the Redis operation context
	 * @return Long for single operations, Map<Object, Boolean> for bulk operations
	 * @throws OrangeRedisException if operation fails and continueOnFailure is false
	 * @throws Exception if any unhandled error occurs
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeRedisIterableContext ctx = (OrangeRedisIterableContext)context;
		Class returnClass = context.getOperationMethod().getReturnType();
		if(!Map.class.isAssignableFrom(returnClass)) {
			return doAdd(context);	
		}
		Map resultMap = OrangeReflectionUtils.newMap(returnClass);
		boolean continueOnFailure = ctx.continueOnFailure();
		try {
			ctx.forEach((t,o) -> {
				boolean success = false;
				try {
					Long result = doAdd(context,t);
					success = result != null && result > 0;
					resultMap.put(o, success);
					if(!success) {
						throw new OrangeRedisException(String.format("Add failed,false returned. Operation: %s %n Operatin owner: %s", context.getOperationMethod(),context.getOperationOwner()));
					}
				}catch (Exception e) {
					// The operation may have been interrupted by a client timeout or network error, 
					// but it was actually completed successfully.
					if(!continueOnFailure) {
						if(e instanceof OrangeRedisException) {
							throw (OrangeRedisException)e;
						}
						throw new OrangeRedisException(String.format("An exception occurred during member addition! Operation: %s %n Operatin owner: %s", context.getOperationMethod(),context.getOperationOwner()),e);	
					}else{
						if(!(e instanceof OrangeRedisException)) {
							this.logger.warn(
								String.format(
									"An exception occurred during member addition! Operation: %s %n Operatin owner: %s", 
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
			this.logger.warn("An exception occurred during member addition.",e);
		}
		return resultMap;
	}
	
	protected abstract Long doAdd(OrangeRedisContext ctx) throws Exception;
	
	protected abstract Long doAdd(OrangeRedisContext ctx,Object value) throws Exception;

	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(AddMembers.class,Multiple.class,ContinueOnFailure.class);
	}
	
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeRedisMultipleValueContext.class;
	}
}