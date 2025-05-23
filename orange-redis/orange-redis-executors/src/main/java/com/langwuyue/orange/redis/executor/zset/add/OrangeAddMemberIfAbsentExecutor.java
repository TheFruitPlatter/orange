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
package com.langwuyue.orange.redis.executor.zset.add;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;

import com.langwuyue.orange.redis.OrangeRedisIfAbsentException;
import com.langwuyue.orange.redis.annotation.AddMembers;
import com.langwuyue.orange.redis.annotation.IfAbsent;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.zset.Score;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.executor.zset.context.OrangeAddIfAbsentContext;
import com.langwuyue.orange.redis.executor.zset.context.OrangeScoreIfAbsentContext;
import com.langwuyue.orange.redis.listener.OrangeRedisSetIfAbsentListener;
import com.langwuyue.orange.redis.listener.zset.OrangeAddMemberIfAbsentFailedEvent;
import com.langwuyue.orange.redis.listener.zset.OrangeAddMemberIfAbsentSuccessEvent;
import com.langwuyue.orange.redis.listener.zset.OrangeRemoveMemberFailedEvent;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.ZSetEntry;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeAddMemberIfAbsentExecutor extends OrangeRedisAbstractExecutor {

	private OrangeRedisZSetOperations operations;
	
	private Collection<OrangeRedisSetIfAbsentListener> listeners;
	
	public OrangeAddMemberIfAbsentExecutor(
			OrangeRedisZSetOperations operations,
			OrangeRedisExecutorIdGenerator idGenerator,
			Collection<OrangeRedisSetIfAbsentListener> listeners
	) {
		super(idGenerator);
		this.operations = operations;
		this.listeners = listeners;
	}

	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeAddIfAbsentContext ctx = (OrangeAddIfAbsentContext)context;
		ZSetEntry member = ctx.getMember();
		Boolean success = Boolean.FALSE;
		try {
			success = executeIfAbsent(context,member);
			if(success == null || !success.booleanValue()) {
				throw new OrangeRedisIfAbsentException("False returned");
			}
		}catch (Exception e) {
			// The operation may have been interrupted by a client timeout or network error, but it was actually completed successfully.
			listeners.forEach(t -> 
				t.onFailure(
					context.getRedisKey().getOriginalKey(),
					new OrangeAddMemberIfAbsentFailedEvent(
						context.getArgs(),
						member.getValue(),
						member.getScore(), 
						e
					)
				)
			);
		}
		
		notifyListeners(context,member,success);
		
		return null;
	}
	
	private void notifyListeners(OrangeRedisContext context,ZSetEntry member,Boolean success) {
		OrangeAddIfAbsentContext ctx = (OrangeAddIfAbsentContext)context;
		if(success == null || !(success.booleanValue())) {
			return;
		}
		boolean removeFailed = false;
		Exception removeFailedException = null;
		try {
			listeners.forEach(t -> 
				t.onSuccess(
					context.getRedisKey().getOriginalKey(),
					new OrangeAddMemberIfAbsentSuccessEvent(context.getArgs(),member.getValue(),member.getScore())
				)
			);
		}finally {
			try {
				if(ctx.isDeleteInTheEnd()) {
					Long removed = operations.remove(context.getRedisKey().getValue(), context.getValueType(), member.getValue());	
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
		
		final Exception e = removeFailedException;
		listeners.forEach(t -> 
			t.onRemoveFailed(context.getRedisKey().getOriginalKey(),
				new OrangeRemoveMemberFailedEvent(
					context.getArgs(),
					member.getValue(),
					member.getScore(), 
					e
				)
			)
		);
	}

	protected Boolean executeIfAbsent(OrangeRedisContext ctx,ZSetEntry member) throws Exception {
		 return this.operations.addIfAbsent(
			ctx.getRedisKey().getValue(), 
			member, 
			ctx.getValueType()
		);
	}

	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(AddMembers.class,RedisValue.class,Score.class,IfAbsent.class);
	}

	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeScoreIfAbsentContext.class;
	}

	OrangeRedisZSetOperations getOperations() {
		return operations;
	}
}
