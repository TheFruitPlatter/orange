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
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.AutoRenew;
import com.langwuyue.orange.redis.annotation.global.SetExpiration;
import com.langwuyue.orange.redis.annotation.value.Lock;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisContext.Key;
import com.langwuyue.orange.redis.context.OrangeRedisValueContext;
import com.langwuyue.orange.redis.executor.multiplelocks.OrangeExpirationTimeAutoInitializer;
import com.langwuyue.orange.redis.executor.value.context.OrangeValueLockAutoRenewContext;
import com.langwuyue.orange.redis.listener.OrangeRedisSetIfAbsentListener;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisValueOperations;
import com.langwuyue.orange.redis.timer.OrangeRenewTimerWheel;
import com.langwuyue.orange.redis.timer.OrangeValueLockRenewTask;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeValueLockAutoRenewExpirationExecutor extends OrangeSetIfAbsentExecutor {
	
	private OrangeRenewTimerWheel renewTimerWheel;
	
	private OrangeExpirationTimeAutoInitializer expirationTimeAutoInitializer;
	
	private OrangeRedisLogger logger;

	public OrangeValueLockAutoRenewExpirationExecutor(
		OrangeRedisValueOperations operations,
		OrangeRedisExecutorIdGenerator idGenerator,
		Collection<OrangeRedisSetIfAbsentListener> listeners,
		OrangeRenewTimerWheel renewTimerWheel,
		OrangeExpirationTimeAutoInitializer expirationTimeAutoInitializer,
		OrangeRedisLogger logger
	) {
		super(operations, idGenerator, listeners);
		this.renewTimerWheel = renewTimerWheel;
		this.expirationTimeAutoInitializer = expirationTimeAutoInitializer;
		this.logger = logger;
	}

	@Override
	protected boolean isDeleteInTheEnd(OrangeRedisContext context, Object result) {
		if(result == null) {
			return false;
		}
		OrangeValueLockRenewTask task = (OrangeValueLockRenewTask)result;
		task.setRemove(true);
		return true;
	}

	@Override
	protected Object executeIfAsent(OrangeRedisValueContext context) throws Exception {
		OrangeValueLockAutoRenewContext ctx = (OrangeValueLockAutoRenewContext) context;
		Key key = ctx.getRedisKey();
		AutoRenew autoRenew = ctx.getAutoRenew();
		if(autoRenew.autoInitKeyExpirationTime()) {
			key = this.expirationTimeAutoInitializer.init(key, autoRenew.threshold());
		}
		OrangeValueLockRenewTask task = new OrangeValueLockRenewTask(
			key,
			getOperations(),
			getValue(ctx),
			RedisValueTypeEnum.STRING,
			autoRenew.threshold()
		);
		Boolean result = this.getOperations().setIfAbsent(
			key.getValue(), 
			getValue(ctx), 
			key.getExpirationTime(), 
			key.getExpirationTimeUnit(), 
			RedisValueTypeEnum.STRING
		);
		if(result == null || !result.booleanValue()) {
			throw new OrangeRedisIfAbsentException("False returned");
		}
		renewTimerWheel.addRenewTask(task);
		return task;
	}
	
	

	@Override
	protected Object returnValue(OrangeRedisContext context, Object result) {
		return super.returnValue(context, result != null);
	}

	@Override
	protected void notifyListeners(OrangeRedisValueContext ctx, Object result) {
		super.notifyListeners(ctx, result != null);
	}

	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeValueLockAutoRenewContext.class;
	}

	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(Lock.class, AutoRenew.class,SetExpiration.class);
	}

	@Override
	protected Object getValue(OrangeRedisValueContext ctx) {
		String traceId = logger.getTraceId();
		return traceId != null && !traceId.isEmpty() ? traceId : "1";
	}
}
