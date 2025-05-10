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

import com.langwuyue.orange.redis.annotation.AutoRenew;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.global.SetExpiration;
import com.langwuyue.orange.redis.annotation.value.SetValue;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisContext.Key;
import com.langwuyue.orange.redis.context.OrangeRedisValueContext;
import com.langwuyue.orange.redis.executor.multiplelocks.OrangeExpirationTimeAutoInitializer;
import com.langwuyue.orange.redis.executor.value.context.OrangeValueLockAutoRenewContext;
import com.langwuyue.orange.redis.listener.OrangeRedisSetIfAbsentListener;
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

	public OrangeValueLockAutoRenewExpirationExecutor(
		OrangeRedisValueOperations operations,
		OrangeRedisExecutorIdGenerator idGenerator,
		Collection<OrangeRedisSetIfAbsentListener> listeners,
		OrangeRenewTimerWheel renewTimerWheel,
		OrangeExpirationTimeAutoInitializer expirationTimeAutoInitializer
	) {
		super(operations, idGenerator, listeners);
		this.renewTimerWheel = renewTimerWheel;
		this.expirationTimeAutoInitializer = expirationTimeAutoInitializer;
	}

	@Override
	protected boolean isDeleteInTheEnd(OrangeRedisContext context, Boolean result) {
		return result;
	}

	@Override
	protected Boolean executeIfAsent(OrangeRedisValueContext context) throws Exception {
		OrangeValueLockAutoRenewContext ctx = (OrangeValueLockAutoRenewContext) context;
		Key key = ctx.getRedisKey();
		AutoRenew autoRenew = ctx.getAutoRenew();
		if(autoRenew.autoInitKeyExpirationTime()) {
			key = this.expirationTimeAutoInitializer.init(key, autoRenew.threshold());
		}
		OrangeValueLockRenewTask task = new OrangeValueLockRenewTask(
			key,
			getOperations(),
			ctx.getValue(),
			ctx.getValueType(),
			autoRenew.threshold()
		);
		renewTimerWheel.addRenewTask(task);
		return this.getOperations().setIfAbsent(
			key.getValue(), 
			ctx.getValue(), 
			key.getExpirationTime(), 
			key.getExpirationTimeUnit(), 
			ctx.getValueType()
		);
	}

	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeValueLockAutoRenewContext.class;
	}

	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(RedisValue.class,SetValue.class, AutoRenew.class,SetExpiration.class);
	}

}
