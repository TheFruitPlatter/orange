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
 * @author Liang.Zhong
 * @since 1.0.0
 */
public abstract class OrangeAddMembersAbstractExecutor extends OrangeRedisAbstractExecutor {
	
	private OrangeRedisLogger logger;
	
	protected OrangeAddMembersAbstractExecutor(OrangeRedisExecutorIdGenerator idGenerator,OrangeRedisLogger logger) {
		super(idGenerator);
		this.logger = logger;
	}

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
