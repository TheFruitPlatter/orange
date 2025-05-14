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
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import com.langwuyue.orange.redis.OrangeRedisIfAbsentException;
import com.langwuyue.orange.redis.annotation.AddMembers;
import com.langwuyue.orange.redis.annotation.IfAbsent;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.hash.HashKey;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.executor.hash.context.OrangeAddIfAbsentContext;
import com.langwuyue.orange.redis.executor.hash.context.OrangeHashKeyValueIfAbsentContext;
import com.langwuyue.orange.redis.listener.OrangeRedisSetIfAbsentListener;
import com.langwuyue.orange.redis.listener.hash.OrangeAddMemberIfAbsentFailedEvent;
import com.langwuyue.orange.redis.listener.hash.OrangeAddMemberIfAbsentSuccessEvent;
import com.langwuyue.orange.redis.listener.hash.OrangeRemoveMemberFailedEvent;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisHashOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;
import com.langwuyue.orange.redis.utils.OrangeReflectionUtils;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeAddMemberIfAbsentExecutor extends OrangeRedisAbstractExecutor {
	
	private OrangeRedisHashOperations operations;
	
	private Collection<OrangeRedisSetIfAbsentListener> listeners;

	public OrangeAddMemberIfAbsentExecutor(
		OrangeRedisHashOperations operations,
		OrangeRedisExecutorIdGenerator idGenerator,
		Collection<OrangeRedisSetIfAbsentListener> listeners
	) {
		super(idGenerator);
		this.operations = operations;
		this.listeners = listeners;
	}

	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeAddIfAbsentContext ctx = (OrangeAddIfAbsentContext) context;
		Entry member = (Entry) ctx.getMember().entrySet().iterator().next();
		Boolean success = false;
		try {
			success = this.operations.putIfAbsent(
					context.getRedisKey().getValue(), 
					member.getKey(), 
					member.getValue(), 
					ctx.getKeyType(), 
					context.getValueType()
			);
			if(success == null || !success.booleanValue()) {
				throw new OrangeRedisIfAbsentException("False returned");
			}
		}catch (Exception e) {
			// The operation may have been interrupted by a client timeout or network error, but it was actually completed successfully.
			this.listeners.forEach(t -> 
				t.onFailure(
					context.getRedisKey().getOriginalKey(),
					new OrangeAddMemberIfAbsentFailedEvent(context.getArgs(),e)
				)
			);
		}
		
		notifyListeners(context,success,member);
		
		Class<?> returnClass = context.getOperationMethod().getReturnType();
		if(returnClass == Boolean.class || returnClass == boolean.class) {
			return success == null ? Boolean.FALSE : success;	
		}
		if(OrangeReflectionUtils.isInteger(returnClass)) {
			return success == null || !success.booleanValue() ? 0 : 1;	
		}
		return null;
	}
	
	private void notifyListeners(OrangeRedisContext context,Boolean success,Entry member) {
		if(success == null || !success.booleanValue()) {
			return;
		}
		OrangeAddIfAbsentContext ctx = (OrangeAddIfAbsentContext) context;
		boolean removeFailed = false;
		Exception removeFailedException = null;
		try {
			this.listeners.forEach(t -> 
				t.onSuccess(
					context.getRedisKey().getOriginalKey(),
					new OrangeAddMemberIfAbsentSuccessEvent(context.getArgs())
				)
			);
		}finally{
			try {
				if(ctx.isDeleteInTheEnd()) {
					Long removed = operations.removeMembers(context.getRedisKey().getValue(),ctx.getKeyType(),member.getKey());
					removeFailed = removed == null || removed <= 0;
				}
			}catch (Exception e) {
				// Warning, maybe a network error causes the removal to fail.
				// Also, maybe Redis Client timeout, but the operation success.
				removeFailed = true;
				removeFailedException = e;
			}
		}
		
		if(!removeFailed) {
			return;
		}
		final Exception exception = removeFailedException;
		this.listeners.forEach(t -> 
			t.onRemoveFailed(
				context.getRedisKey().getOriginalKey(),
				new OrangeRemoveMemberFailedEvent(context.getArgs(),exception)
			)
		);
	}

	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(AddMembers.class,HashKey.class,RedisValue.class,IfAbsent.class);
	}

	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeHashKeyValueIfAbsentContext.class;
	}

	OrangeRedisHashOperations getOperations() {
		return operations;
	}
}
