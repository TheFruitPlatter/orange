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
package com.langwuyue.orange.redis.executor.value;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;

import com.langwuyue.orange.redis.OrangeRedisIfAbsentException;
import com.langwuyue.orange.redis.annotation.IfAbsent;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.value.SetValue;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisValueContext;
import com.langwuyue.orange.redis.context.OrangeRedisValueIfAbsentContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.listener.OrangeRedisSetIfAbsentListener;
import com.langwuyue.orange.redis.listener.value.OrangeRemoveFailedEvent;
import com.langwuyue.orange.redis.listener.value.OrangeSetIfAbsentFailedEvent;
import com.langwuyue.orange.redis.listener.value.OrangeSetIfAbsentSuccessEvent;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisValueOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;
import com.langwuyue.orange.redis.utils.OrangeReflectionUtils;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeSetIfAbsentExecutor extends OrangeRedisAbstractExecutor {
	
	private OrangeRedisValueOperations operations;
	
	private Collection<OrangeRedisSetIfAbsentListener> listeners;

	public OrangeSetIfAbsentExecutor(
		OrangeRedisValueOperations operations,
		OrangeRedisExecutorIdGenerator idGenerator,
		Collection<OrangeRedisSetIfAbsentListener> listeners
	) {
		super(idGenerator);
		this.operations = operations;
		this.listeners = listeners;
	}

	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeRedisValueContext ctx = (OrangeRedisValueContext)context;
		Object result = null;
		try {
			result = executeIfAsent(ctx);
		}catch (Exception e) {
			// The operation may have been interrupted by a client timeout or network error, but it was actually completed successfully.
			listeners.forEach(t -> 
				t.onFailure(
					context.getRedisKey().getOriginalKey(),
					new OrangeSetIfAbsentFailedEvent(ctx.getArgs(),getValue(ctx), e)
				)
			);
		}
		
		notifyListeners(ctx,result);
		
		return returnValue(context, result);
	}
	
	protected Object getValue(OrangeRedisValueContext ctx) {
		return ctx.getValue();
	}

	protected Object returnValue(OrangeRedisContext context,Object result) {
		Class<?> returnClass = context.getOperationMethod().getReturnType();
		if(returnClass == Boolean.class || returnClass == boolean.class) {
			return result == null ? Boolean.FALSE : result;	
		}
		if(OrangeReflectionUtils.isInteger(returnClass)) {
			return result != null && (((Boolean)result).booleanValue()) ? 1 : 0;	
		}
		return null;
	}
	
	protected void notifyListeners(OrangeRedisValueContext ctx,Object result) {
		if(result == null || !(((Boolean)result).booleanValue())) {
			return;
		}
		boolean delFailed = false;
		Exception delFailedException = null;
		try {
			listeners.forEach(t -> 
				t.onSuccess(
					ctx.getRedisKey().getOriginalKey(),
					new OrangeSetIfAbsentSuccessEvent(ctx.getArgs(), getValue(ctx))
				)
			);
		}finally {
			try {
				if(isDeleteInTheEnd(ctx,result)) {
					Boolean deleted = operations.delete(ctx.getRedisKey().getValue());	
					delFailed = deleted == null || !deleted.booleanValue(); 
				}
			}catch (Exception e) {
				// Warning, maybe a network error causes the removal to fail.
				// Also, maybe Redis Client timeout, but the operation success.
				delFailed = true;
				delFailedException = e;
				
			}
		}
		
		if(!delFailed) {
			return;
		}
		
		final Exception exception = delFailedException;
		listeners.forEach(t -> 
			t.onRemoveFailed(
				ctx.getRedisKey().getOriginalKey(),
				new OrangeRemoveFailedEvent(ctx.getArgs(),getValue(ctx), exception)
			)
		);
	}
	
	protected boolean isDeleteInTheEnd(OrangeRedisContext context,Object result) {
		OrangeRedisValueIfAbsentContext ctx = (OrangeRedisValueIfAbsentContext)context;
		return ctx.isDeleteInTheEnd() && result != null && (Boolean)result;
	}

	protected Object executeIfAsent(OrangeRedisValueContext ctx) throws Exception {
		Boolean result = operations.setIfAbsent(
				ctx.getRedisKey().getValue(), 
				getValue(ctx),
				ctx.getValueType()
		);
		if(result == null || !result.booleanValue()) {
			throw new OrangeRedisIfAbsentException("False returned");
		}
		return result;
	}

	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeRedisValueIfAbsentContext.class;
	}

	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(RedisValue.class,SetValue.class, IfAbsent.class);
	}

	OrangeRedisValueOperations getOperations() {
		return operations;
	}
}
